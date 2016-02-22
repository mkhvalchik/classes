require_relative 'configuration'

# Parses, stores and exposes the values from the mime.types file
module WebServer
  class MimeTypes < Configuration
    def initialize(mime_file_content)
      @mime_types = Hash.new
      mime_file_content.each_line do |line|
        line_array = line.split(" ")
        if line_array.length.eql? 0 or line_array.length.eql? 1
          next
        end
        if line != "" and line[0] == "#"
          next
        end

        (1..line_array.length).each do |i|
          @mime_types[line_array[i]] = line_array[0]
        end
      end
    end

    # Returns the mime type for the specified extension
    def for_extension(extension)
      if @mime_types[extension].eql? nil
        return 'text/plain'
      end
      @mime_types[extension]
    end
  end
end
