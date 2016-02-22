require_relative 'configuration'

# Parses, stores, and exposes the values from the httpd.conf file
module WebServer
  class HttpdConf < Configuration
    attr_reader :server_root, :document_root, :directory_index, :port, :log_file, :access_file_name, :aliases, :script_aliases

    def initialize(httpd_file_content)
      @server_root = ''
      @document_root = ''
      @directory_index = ''
      @port = ''
      @log_file = ''
      @access_file_name = ''
      # These are map keys for corresponding maps
      @aliases = Array.new
      @script_aliases = Array.new
      # These are maps which map the paths to its alias
      @alias_paths = Hash.new
      @script_alias_paths = Hash.new
      # This loop takes the content and slices it in lines of strings
      # Delimeter " " works for both whitespace and tab
      httpd_file_content.each_line do |line|
        line_array = line.split(" ")
        if line_array.length.eql? 0
          next
        end
        # For each case where the following element is embeded by quotes
        # we try to remove quotes. We use escaping with '\' to escape '"' sign
        if line_array[0].eql? "ServerRoot"
          @server_root = line_array[1].tr("\"","")
        elsif line_array[0].eql? "DocumentRoot"
          @document_root = line_array[1].tr("\"","")
        # We convert string to integer.
        elsif line_array[0].eql? "Listen"
          @port = line_array[1].tr("\"","").to_i()
        elsif line_array[0].eql? "LogFile"
          @log_file = line_array[1].tr("\"","")
        elsif line_array[0].eql? "ScriptAlias"
          @script_aliases.push(line_array[1].tr("\"",""))
          @script_alias_paths[line_array[1].tr("\"","")] = line_array[2].tr("\"","")
        elsif line_array[0].eql? "Alias"
          @aliases.push(line_array[1].tr("\"",""))
          @alias_paths[line_array[1].tr("\"","")] = line_array[2].tr("\"","")
        elsif line_array[0].eql? "DirectoryIndex"
          @directory_index = line_array[1].tr("\"","")
        elsif line_array[0].eql? "AccessFileName"
          @access_file_name = line_array[1].tr("\"","")
        end
      end
    end

    # Returns the aliased path for a given ScriptAlias directory
    def script_alias_path(path)
      @script_alias_paths[path]
    end

    # Returns the aliased path for a given Alias directory
    def alias_path(path)
      @alias_paths[path]
    end
  end
end
