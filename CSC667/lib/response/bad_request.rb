module WebServer
  module Response
    # Class to handle 400 responses
    class BadRequest < Base
      def initialize(resource, options={})
        super(resource, options)
      	@code = 400
        @content = "400 Bad Request"
      end
    end
  end
end
