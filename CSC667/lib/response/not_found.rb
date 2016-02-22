module WebServer
  module Response
    # Class to handle 404 errors
    class NotFound < Base
      def initialize(resource, options={})
        super(resource, options)
        @code = 404
        @content = "404 Page Not Found"
      end

    end
  end
end
