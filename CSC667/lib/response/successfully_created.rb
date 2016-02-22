module WebServer
  module Response
    # Class to handle 201 responses
    class SuccessfullyCreated < Base
      def initialize(resource, options={})
        super(resource, options)
        @code = 201
        @content = "Created new file"
      end
    end
  end
end
