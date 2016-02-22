module WebServer
  class Logger

    # Takes the absolute path to the log file, and any options
    # you may want to specify.  I include the option :echo to
    # allow me to decide if I want my server to print out log
    # messages as they get generated
    def initialize(log_file_path, options={})
      @log_file_path = log_file_path
      @log_file
      if (options.has_key?(:echo))
        @print_log = options[:echo]
      else
        @print_log = ""
      end
    end

    # Log a message using the information from Request and
    # Response objects
    def log(request, response)
      if response.user == nil
        user_id = "-"
      else
        user_id = response.user
      end
      @log_file = File.open(@log_file_path, 'a')
      @log_file.write(request.socket.peeraddr[3] + " - " +
                      user_id + " [" +
                      Time.now.to_s + "] " +
                      request.request_line + " " +
                      response.code.to_s + " " +
                      response.content_length.to_s + "\n")
    end

    # Allow the consumer of this class to flush and close the
    # log file
    def close
      @log_file.close
    end
  end
end
