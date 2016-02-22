module WebServer
  module Response
    # Class to handle 403 responses
    class Forbidden < Base
      def initialize(resource, options={})
        super(resource, options)
      	@code = 403
        @content = "403 Page is forbidden"
      end
    end
  end
end
