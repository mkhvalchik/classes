require_relative 'request'
require_relative 'response'

# This class will be executed in the context of a thread, and
# should instantiate a Request from the client (socket), perform
# any logging, and issue the Response to the client.
module WebServer
  class Worker
    # Takes a reference to the client socket and the logger object
    def initialize(client_socket, server=nil)
    	@socket = client_socket
    end

    # Process the requests
    def process_request
      return Request.new(@socket)
    end

    # Process the responses
    def process_response(resource)
      return Response::Factory.create(resource)      
    end
  end
end
