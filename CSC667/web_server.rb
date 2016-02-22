require 'socket'
Dir.glob('lib/**/*.rb').each do |file|
  require file
end

module WebServer
  class Server
    HOST = 'localhost'

    def initialize(options={})
      # Set up WebServer's configuration files and logger here
      # Do any preparation necessary to allow threading multiple requests
      httpd_conf_file = File.open("./config/httpd.conf")
      mime_types_file = File.open("./config/mime.types")

      httpd_file = httpd_conf_file.read
      @http_conf = HttpdConf.new(httpd_file)

      mime_file = mime_types_file.read
      @mimes = MimeTypes.new(mime_file)

      @log_file = File.new(@http_conf.log_file)
      out_log = File.read(@log_file)
      @logger = Logger.new(@http_conf.log_file, echo: out_log)
    end

    def start
      # Begin your 'infinite' loop, reading from the TCPServer, and
      # processing the requests as connections are made
      server_thread = Thread.start do 
        server = TCPServer.new(HOST, @http_conf.port)
        puts "Server is up"

        loop do
          Thread.start(server.accept) do |socket|
            #pass socket into new worker object
            # socket = server.accept
            worker = Worker.new(socket)
            #create request object per worker processing the request
            request = worker.process_request
            #create resource based from request and configs
            resource = Resource.new(request, @http_conf, @mimes)
            #create appropriate response object per worker processing response
            response = worker.process_response(resource)
            #pass request and response to logger
            @logger.log(request, response)
            #server prints out response to the browser
            socket.print response.to_s
            socket.close 
            @logger.close
          end# end sockets in thread
        end #end loop do
      end #end server thread
      server_thread.join
    end

    private
  end
end

WebServer::Server.new.start
