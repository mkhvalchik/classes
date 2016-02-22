<?php

class DatabaseManager {

    private $conn;

    public function connect() {
        // Requires the decryption script from the deployment server
        require_once '../.decrypt.php';
        // Grab credentials
        $login = get_credentials();
        // Populate login info
        $servername = $login['servername'];
        $dbname = $login['dbname'];
        $username = $login['username'];
        $password = $login['password'];

      // Create connection
      $this->conn = mysqli_connect($servername, $username, $password, $dbname);

      // Set default time zone to California time = "America/Los_Angeles"
      date_default_timezone_set("America/Los_Angeles");
    }

    public function disconnect() {
      mysqli_close($this->conn);
    }

    public function get_status() {
        // Check connection
      if (!$this->conn) {
        return 'ERROR:' . mysqli_connect_error();
      }
      else {
        return 'OK';
      }
    }

    /** Returns a restaurant from the database based on its ID.
     *
     * @param   integer $id a restaurant ID number
     * @return  mixed   an associate array representing a restaurant if found,
     *                  or false otherwise
     */
    public function get_restaurant_by_id($id) {
        $query = "SELECT * "
                . "FROM RestaurantsPrimary "
                . "WHERE ID = $id";
        $result = mysqli_query($this->conn, $query);
        if (mysqli_num_rows($result) > 0) {
            // Create associative array from mysqli_result object
            $restaurant = mysqli_fetch_assoc($result);
            $attr = $this->get_tags_helper($restaurant['ID']);
            // Add attributes
            $restaurant['Tags'] = $attr;
            return array_map("to_entity", $restaurant);
        }
        return false;
    }

    public function get_restaurants($criteria_string) {
      $criteria_array = preg_split('/\s+/', mysqli_real_escape_string($this->conn, $criteria_string));

      $before_intersection = array();
      $after_intersection = array();
      foreach ($criteria_array as $criteria) {
        $tmp = array();
        $sql = "SELECT *
                FROM RestaurantsPrimary
                WHERE name LIKE '%$criteria%' OR cuisine LIKE '%$criteria%'
                union
                SELECT RestaurantsPrimary.*
                FROM Cities
                INNER JOIN RestaurantsPrimary ON Cities.ID = RestaurantsPrimary.CityID
                WHERE Cities.Name LIKE '%$criteria%'
                union
                SELECT RestaurantsPrimary.*
                FROM (Metros INNER JOIN CitiesMetros ON Metros.ID = CitiesMetros.MetroID) INNER JOIN
                RestaurantsPrimary ON CitiesMetros.CityID = RestaurantsPrimary.CityID
                WHERE Metros.Name LIKE '%$criteria%' ";
        $result = mysqli_query($this->conn, $sql);
        if (mysqli_num_rows($result) > 0) {
          // Create array of associated arrays e.g. $ret[0][Name] = 'Red Tavern'
          while($row = mysqli_fetch_assoc($result)) {
            $attr = $this->get_tags_helper($row['ID']);
            // Adding attributes to associated array
            $row['Tags'] = $attr;

            // Add city name to list
            $city = $this->get_city_by_id($row['CityID']);
            $row['City'] = $city;

            $tmp[] = $row;
          }
          $before_intersection[] = $tmp;
        }
      }
      if(!empty($before_intersection)) {
        foreach ($before_intersection[0] as $first_rest) {
          $cnt = 0;
          foreach ($before_intersection as $basket) {
            foreach ($basket as $other_rest) {
              if($first_rest['ID'] == $other_rest['ID']) {
                $cnt += 1;
              }
            }
          }
          if($cnt == sizeof($before_intersection)) {
            $after_intersection[] = array_map("to_entity", $first_rest);
          }
        }
      }
      return $after_intersection;
    }

    public function get_tags_helper($id) {
      $ret = array();
      $sql = "SELECT *
              FROM RestaurantsTags
              WHERE RestaurantID = '$id' ";
      $result = mysqli_query($this->conn, $sql);
      if (mysqli_num_rows($result) > 0) {
        while($row = mysqli_fetch_assoc($result)) {
          $ret[] = $row['Tag'];
        }
      }
      return $ret;
    }

    /*
    For a given timestamp returns tables which are available in
    [timestamp - variance * 1800; timestamp + variance * 1800] time interval.
    1800 is number of seconds in half an hour. A table is considered as available
    at time x if it is not reserved at time interval [x, x + 3600].

    For instance getTables(1, 1445828400, 3, 2);
    (i.e. find tables which are available
    on 10/25 at 7:00 pm, 7:30 pm, 8:00 pm, 8:30 pm, 9:00 pm, 9:30 pm)
    will return:
    Array
    (
        [0] => Array
            (
                [0] => Array
                    (
                        [ID] => 3
                        [RestaurantID] => 1
                        [Capacity] => 4
                    )

                [Timeslot] => 1445824800
            )

        [1] => Array
            (
                [0] => Array
                    (
                        [ID] => 3
                        [RestaurantID] => 1
                        [Capacity] => 4
                    )

                [Timeslot] => 1445826600
            )

        [2] => Array
            (
                [0] => Array
                    (
                        [ID] => 3
                        [RestaurantID] => 1
                        [Capacity] => 4
                    )

                [Timeslot] => 1445828400
            )

        [3] => Array
            (
                [0] => Array
                    (
                        [ID] => 3
                        [RestaurantID] => 1
                        [Capacity] => 4
                    )

                [Timeslot] => 1445830200
            )

        [4] => Array
            (
                [0] => Array
                    (
                        [ID] => 3
                        [RestaurantID] => 1
                        [Capacity] => 4
                    )

                [Timeslot] => 1445832000
            )

    )
    */
    public function get_tables($rest_id, $timeslot, $numpeople, $variance) {
      $ret = array();
      for ($i = -$variance; $i <= $variance; $i++) {
        $tmp = $this->get_tables_helper($rest_id, $timeslot+$i * 1800, $numpeople);
        if (!empty($tmp)) {
          $tmp['Timeslot'] = $timeslot+$i * 1800;
          $ret[] = $tmp;
        }
      }
      return $ret;
    }

    public function get_tables_helper($rest_id, $timeslot, $numpeople) {
      $ret = array();
      $timeslot_next = $timeslot + 1800;
      $sql = "SELECT a.*
              FROM (SELECT * FROM TablesPrimary WHERE RestaurantID = $rest_id and Capacity >= $numpeople) AS a
              LEFT JOIN (SELECT * FROM Reservations WHERE TimeSlot = $timeslot OR TimeSlot = $timeslot_next)  AS b
              ON a.ID = b.TableID
              WHERE b.TableID is NULL
              ORDER BY a.Capacity asc";
      $result = mysqli_query($this->conn, $sql);
      if (mysqli_num_rows($result) > 0) {
        while($row = mysqli_fetch_assoc($result)) {
          $ret[] = $row;
        }
      }
      return $ret;
    }

    /*
    This function reserves a table for given arguments and returns the unique
    reservation id in form of string.
    Returns NULL in case if operation was not successful (somebody already reserved
    the table)
    */
    public function reserve_table($name, $tel_num, $email, $timeslot, $table_id, $guests, $special_insts) {
      $sql = "INSERT INTO Reservations (Name, TelNumber, Email, Timeslot, TableID, Guests, Status, SpecialInstructions)
              VALUES ('$name', '$tel_num', '$email', $timeslot, $table_id, $guests, 'booked', '$special_insts')";
      if(mysqli_query($this->conn, $sql)) {
        return $table_id . ":" . $timeslot;
      }
      return null;
     }

    public function get_role($email) {
      $sql = "SELECT Role
              FROM EmployeeProfiles
              WHERE email = '$email' ";

      $result = mysqli_query($this->conn, $sql);
      if (mysqli_num_rows($result) > 0) {
        while($row = mysqli_fetch_assoc($result)) {
          return $row['Role'];
        }
      }
      return null;
    }

    /**
     * Returns the list of all future reservations for a specified timeframe.
     * Reservations have the following properties:
     *  - Name (name under whom the reservation was made)
     *  - TelNumber (respective phone number)
     *  - Email (respective email)
     *  - Timeslot (timeslot for reservation as a Unix timestamp)
     *  - TableID (unique table ID number)
     *  - ReservationID (verification code for reservation)
     *  - Guests (number of all people attending)
     *  - SpecialInstructions (any special instructions)
     *
     * @param   string  $range_type specifies "day", "month", or "year" to grab
     *                  reservations for the current day, month, or year respectively.
     *                  Default value = "day"; default is used if $range_type is
     *                  not recognized.
     * @return  array   an array of reservations as associative arrays
     */
    public function get_reservations ($range_type = "day") {
        $range_begin = 0;
        $range_end = 0;
        // Log current time
        switch ($range_type) {
            case "month":
                // Create month begin
                $month_begin = new DateTime('first day of this month');
                $month_begin->setTime(0,0,0);
                // Create month end
                $month_end = new DateTime('last day of this month');
                $month_end->setTime(23,59,59);
                // Parse timestamps
                $range_begin = $month_begin->getTimestamp();
                $range_end = $month_end->getTimestamp();
                break;
            case "year":
                // Parse today's date/time
                $today = new DateTime();
                // Create year begin
                $year = new DateTime();
                $year->setDate($today->format('Y'), 1, 1);
                $year->setTime(0, 0, 0);
                $range_begin = $year->getTimestamp();
                // Create year end
                $year->setDate($today->format('Y'), 12, 31);
                $year->setTime(23, 59, 59);
                $range_end = $year->getTimestamp();
                break;
            default:
                // Create current date/time
                $now = new DateTime();
                // Set time to beginning of day
                $now->setTime(0, 0, 0);
                $range_begin = $now->getTimestamp();
                // Set time to end of day
                $now->setTime(23, 59, 59);
                $range_end = $now->getTimestamp();
        }

        $query = "SELECT * "
                . "FROM Reservations "
                . "WHERE Timeslot BETWEEN $range_begin AND $range_end";

        // Pass query, get result
        $result = mysqli_query($this->conn, $query);

        // Create and populate return array
        $ret = array();

        if (mysqli_num_rows($result) > 0) {
            // Create array of associated arrays
            while($row = mysqli_fetch_assoc($result)) {
                $row['ReservationID'] = $row['TableID'] . ":" . $row['Timeslot'];
                $ret[] = $row;
            }
        }
        return $ret;
    }

    /**
     * Sets the "status" column value for an entry in the Reservation table.
     * A unique reservation is identified by its table ID and timeslot.
     *
     * @param   integer $table_id   the table ID of the reservation
     * @param   integer $timeslot   a Unix timestamp denoting the reservation time
     * @param   string  $status     the new status of the reservation
     * @return  boolean true if successful, false otherwise
     */
    public function set_reservation_status ($table_id, $timeslot, $status) {
        // Form query
        $query = "UPDATE Reservations "
                . "SET Status=\"$status\" "
                . "WHERE TableID=$table_id AND Timeslot=$timeslot";

        // Send database request
        return mysqli_query($this->conn, $query);
    }

    /** Returns a city name from the database based on its ID.
     *
     * @param   integer $id a CityID number
     * @return  mixed   a string containing a city name if found, false if not
     */
    public function get_city_by_id($id) {
        $query = "SELECT Name "
                . "FROM Cities "
                . "WHERE ID = $id";
        // Submit query
        $result = mysqli_query($this->conn, $query);
        // Check for successful results
        if (mysqli_num_rows($result) > 0) {
            // Return the entry name
            $city = mysqli_fetch_assoc($result);
            return $city['Name'];
        }
        return false;
    }

    /**
     * Returns an array of all cities in the database.
     *
     * @return  an associative array mapping a city ID key to a city name value.
     */
    public function get_all_cities () {
        $cities = array();
        $query = "SELECT * FROM Cities";
        $result = mysqli_query($this->conn, $query);
        if (mysqli_num_rows($result) > 0) {
            while($entry = mysqli_fetch_assoc($result)) {
                $id = $entry['ID'];
                $cities["$id"] = $entry['Name'];
            }
        }
        return $cities;
    }
}

/**
 * Formats a string to display HMTL entities properly.
 *
 * @param   string  $string
 * @return  a string formatted to display HTML entities
 */
function to_entity ($string) {
    if (is_string($string)) {
        return htmlentities($string);
    }
}

// Testing reserve_table
//$obj = new DatabaseManager;
//$obj->connect();
//$obj->reserve_table('Miam Tiu','6565558963', 'miam@gmail.com', 88896663, 21);
//echo $obj-> get_role('abc@gmail.com');
