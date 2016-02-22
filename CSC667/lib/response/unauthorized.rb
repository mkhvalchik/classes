module WebServer
  module Response
    # Class to handle 401 responses
    class Unauthorized < Base
      def initialize(resource, options={})
        super(resource, options)
        @code = 401
      end

      def header
        super + "WWW-Authenticate: " + @resource.htaccess.auth_type +
        " realm=\"" + @resource.htaccess.auth_name + "\"\r\n"
      end
    end
  end
end
