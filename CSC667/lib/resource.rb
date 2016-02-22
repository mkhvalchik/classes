module WebServer
  class Resource
    attr_reader :request, :conf, :mimes, :htaccess

    def initialize(request, httpd_conf, mimes)
      @request = request
      @conf = httpd_conf
      @mimes = mimes
    end

    # If uri is aliased, we modify uri
    def modify_uri
      uri = @request.uri
      @conf.aliases.each do |a|
        if uri.include? a
          uri = uri.sub(a, @conf.alias_path(a))
        end
      end
      if uri != @request.uri
        return uri
      end
      @conf.script_aliases.each do |a|
        if uri.include? a
          uri = uri.sub(a, @conf.script_alias_path(a))
        end
      end
      uri
    end

    # This function creates an absolute path.
    def resolve
      file_location = modify_uri
      # Adding doc root if path is not aliased. Handling pathname joining
      if(file_location == @request.uri)
        file_location = File.join(@conf.document_root, file_location)
      end
      # If the path is directory, add dir index to serve index file
      if (!File.file?(file_location) and @request.extension == '')
        file_location = File.join(file_location, @conf.directory_index)
        @request.extension = File.extname(file_location)
        if @request.extension != ""
          @request.extension = @request.extension.split('.')[1]
        end
      end
      file_location
    end

    # Check if user asked for a script
    def script_aliased?
      uri = @request.uri
      @conf.script_aliases.each do |a|
        if uri.include? a
          return true
        end
      end
      false
    end

    def protected?
      dir = resolve
      secured = false
      while dir != "/" do
        if File.exists?(File.join(dir, "/.htaccess"))
          @htaccess = Htaccess.new(File.read(File.join(dir, "/.htaccess")))
          secured = true
        end
        # Split does splitting of /path/dir/file into /path/dir and file
        dir, base = File.split(dir)
      end
      return secured
    end
  end
end
