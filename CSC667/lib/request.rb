# The Request class encapsulates the parsing of an HTTP Request
require 'uri'

module WebServer
  class Request
    attr_accessor :http_method, :uri, :version, :headers, :body, :params, :extension, :request_line, :socket

    # Request creation receives a reference to the socket over which
    # the client has connected
    def initialize(socket)
      # Perform any setup, then parse the request
      @socket = socket
      @http_method = ''
      @uri = ''
      @extension = ''
      @headers = Hash.new
      @params = Hash.new
      #parse request line
      @request_line = @socket.gets
      if @request_line == nil
        return
      end
      parse_request_line
      #read and parse headers
      while header_line = @socket.gets
        break if header_line.length <= 2
        parse_header(header_line)
      end
      # read and parse body
      if @headers.has_key? "CONTENT-LENGTH"
        body_line = @socket.read(@headers["CONTENT-LENGTH"].to_i)
        parse_body(body_line)
      end
      #parse query strings
      parse_params
    end

    # I've added this as a convenience method, see TODO (This is called from the logger
    # to obtain information during server logging)
    def user_id
      # TODO: This is the userid of the person requesting the document as determined by
      # HTTP authentication. The same value is typically provided to CGI scripts in the
      # REMOTE_USER environment variable. If the status code for the request (see below)
      # is 401, then this value should not be trusted because the user is not yet authenticated.
      '-'
    end

    # Parse the request from the socket - Note that this method takes no
    # parameters
    def parse
    end

    #parses method uri http version
    def parse_request_line
      request_line_array = @request_line.split(" ")
      if request_line_array.length < 3
        @http_method = ''
      end
      @http_method = request_line_array[0]
      @uri = request_line_array[1]
      @version = request_line_array[2]
    end

    #headers
    def parse_header(header_line)
      header_hash = header_line.split(":", 2)
      if (header_hash.length < 2)
        return
      end
      @headers[header_hash[0].upcase.strip] = header_hash[1].strip
      env_headers(header_hash[0].upcase.strip, header_hash[1].strip)
    end

    def env_headers(key, value)
      if key.include? "-"
        key.gsub!("-","_")
      end
      env_var = "HTTP_" + key
      ENV["#{env_var}"] = value
    end

    def remote_ip
      return "127.0.0.1"
    end

    #request body
    def parse_body(body_line)
      @body = body_line.strip
    end

    #query string
    def parse_params
      @extension = File.extname(URI.parse(@uri).path)
      if @extension != ""
        @extension = @extension.split('.')[1]
      end
      if @uri.include? "?"
        split_uri = @uri.split('?')
        @uri = split_uri[0]
        params = split_uri[1]
        split_params = params.split('&')
        split_params.each do |p|
          @params[p.split('=')[0]] = p.split('=')[1]
        end
      end
    end
  end
end
