require 'time'

module WebServer
  module Response
    # Provides the base functionality for all HTTP Responses
    # (This allows us to inherit basic functionality in derived responses
    # to handle response code specific behavior)
    class Base
      attr_reader :version, :code, :body, :content, :user

      def initialize(resource, options={})
        @resource = resource
        @version = @resource.request.version
        @code = 200
        @body = @resource.request.body
        if (options.has_key?(:content))
          @content = options[:content]
        else
          @content = ""
        end
        if (options.has_key?(:modification_date))
          @modification_date = options[:modification_date]
        else
          @modification_date = nil
        end
        if (options.has_key?(:user))
          @user = options[:user]
        else
          @user = nil
        end
        @content_type = @resource.mimes.for_extension(@resource.request.extension)
        if resource.script_aliased?
          @content_type = "text/html"
        end
      end

      def to_s
        if content != ""
          return header + "\n" + @content
        end
        return header
      end

      def header
        h = @version + " " + @code.to_s + " " + RESPONSE_CODES[@code] + "\r\n" +
               "Server: " + Response.default_headers["Server"] + "\r\n" +
               "Date: " + Response.default_headers["Date"] + "\r\n" +
               "Content-Type: " + @content_type.to_s + "\r\n" +
               "Content-Length: " + content_length.to_s + "\r\n"
        if @modification_date != nil
          h = h + "Last-Modified: " + @modification_date.httpdate + "\r\n" +
              "Expires: " +  (@modification_date + (365 * 3600 * 24)).httpdate + "\r\n" +
              "Cache-Control: max-age=31536000\r\n"
        end
        return h + "Connection: close\r\n"
      end

      def content_length
        @content.length
      end

    end
  end
end
