module WebServer
  class Htaccess

  	attr_accessor :htaccess_valid_user, :htaccess_user

  	def initialize(htaccess_file)
      @htaccess_file = htaccess_file
      #These are map keys for the htaccess file
      @htaccess_hash = Hash.new
      @users_array = Array.new

      line_array = @htaccess_file.split("\n")
      line_array.each do |line|
        parse_line_array = line.split(" ",2)

        if parse_line_array.length.eql? 0
          next
        end

        if parse_line_array[0].eql? "AuthUserFile"
          @htaccess_hash[parse_line_array[0].tr("\"","")] = parse_line_array[1].tr("\"","")
        elsif parse_line_array[0].eql? "AuthType"
          @htaccess_hash[parse_line_array[0].tr("\"","")] = parse_line_array[1].tr("\"","")
        elsif parse_line_array[0].eql? "AuthName"
          @htaccess_hash[parse_line_array[0].tr("\"","")] = parse_line_array[1].tr("\"","")
        elsif parse_line_array[0].eql? "Require"
          @htaccess_hash[parse_line_array[0].tr("\"","")] = parse_line_array[1].tr("\"","")
        end
      end
  	end

  	#returns the AuthUserFile string
  	def auth_user_file
  		return @htaccess_hash['AuthUserFile']
  	end

  	#returns "Basic"
  	def auth_type
  		return @htaccess_hash['AuthType']
  	end

  	#returns AuthName string
  	def auth_name
  		return @htaccess_hash['AuthName']
  	end

  	#returns the require string
  	def require_user
  		return @htaccess_hash['Require']
  	end

  	#returns true for valid users with valid credentials
  	def authorized?(encrypted_string)

  	end

  	#returns an array of users from the htpasswd file
  	def users
      return @users_array
  	end

  end
end
