require_relative 'response/base'
require "base64"
require "digest"

module WebServer
  module Response
    DEFAULT_HTTP_VERSION = "HTTP/1.1"

    RESPONSE_CODES = {
      200 => 'OK',
      201 => 'Successfully Created',
      304 => 'Not Modified',
      400 => 'Bad Request',
      401 => 'Unauthorized',
      403 => 'Forbidden',
      404 => 'Not Found',
      500 => 'Internal Server Error'
    }.freeze

    def self.default_headers
      {
        'Date' => Time.now.strftime('%a, %e %b %Y %H:%M:%S %Z'),
        'Server' => 'Kevin Soncuya and Marie Khvalchik CSC 667'
      }
    end

    module Factory
      def self.create(resource)
        if resource.request.http_method == ''
          return Response::BadRequest.new(resource)
        end
        path = resource.resolve
        user = nil
        if resource.protected?
          # check if user sent the password
          if resource.request.headers.has_key? "AUTHORIZATION"
            # getting the corresponding header
            s = resource.request.headers["AUTHORIZATION"]
            if s[0] == ' '
              s[0] = ''
            end
            # extracting password and username and decoding it
            pwd = s.split(' ')[1]
            pwd = pwd.split('\r')[0]
            pwd = Base64.decode64(pwd).split(":")
            if pwd.length < 2
              return Response::Unauthorized.new(resource)
            end
            # checking whether we could allow access to any user
            if resource.htaccess.require_user != "valid-user" && resource.htaccess.require_user != pwd[0]
              return Response::Forbidden.new(resource)
            end
            # hashing password
            pwd[1] = Digest::SHA1.base64digest(pwd[1])
            pwd_found = false
            # checking password against the pwd file
            File.readlines(resource.htaccess.auth_user_file).each do |line|
              if line.split("\n")[0] == pwd[0] + ":{SHA}" + pwd[1]
                pwd_found = true
              end
            end
            user = pwd[0]
            if !pwd_found
              return Response::Forbidden.new(resource, user: user)
            end
          else
            return Response::Unauthorized.new(resource)
          end
        end

        # if we can't find file and request is not PUT, return 404
        if !(File.file?(path)) and resource.request.http_method != 'PUT'
          return Response::NotFound.new(resource, user: user)
        end
        # if it is script, we execute it
        if resource.script_aliased?
          if resource.request.http_method == 'POST'
            # parsing body of the POST as a set of params
            resource.request.body.split('&').each do |param_value|
              param_value = param_value.split("=")
              if param_value.length == 2
                path = path + " " + param_value[1]
              end
            end
          else
            resource.request.params.each do |key, value|
              path = path + " " + value
            end
          end
          out = IO.popen(path, 'r').read
          return Response::Base.new(resource, content: out, user: user)
        end
        # handling get request
        if resource.request.http_method == 'GET'
          # checking file mod time
          m = File.mtime(path)
          # if client can accept this modification time, send not-modified
          if resource.request.headers.has_key? "IF-MODIFIED-SINCE"
            s = resource.request.headers["IF-MODIFIED-SINCE"]
            if s[0] == ' '
              s[0] = ''
            end
            m1 = Time.httpdate(s)
            if m >= m1
              return Response::NotModified.new(resource, user: user)
            end
          end
          out = File.read(path)
          # return the file content
          return Response::Base.new(resource, content: out, modification_date: m, user: user)
        end
        # put just creates new file
        if resource.request.http_method == 'PUT'
          out_file = File.new(path, "w")
          if out_file
            out_file.puts(resource.request.body)
            out_file.close
            return Response::SuccessfullyCreated.new(resource, user: user)
          end
          return Response::ServerError.new(resource, content: "Can't create file", user: user)
        end
        # head is the same as get, but without body
        if resource.request.http_method == 'HEAD'
          return Response::Base.new(resource, user: user)
        end
        # Response::Base.new(resource)
      end

      def self.error(resource, error_object)
        Response::ServerError.new(resource, exception: error_object)
      end
    end
  end
end
