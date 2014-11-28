class PropertyFileHandler
  attr :file, :properties

  #Takes a file and loads the properties in that file
  def initialize file
    @file = file
    @properties = {}
    IO.foreach(@file) do |line|
      @properties[$1.strip] = $2 if line =~ /([^=]*)=(.*)\/\/(.*)/ || line =~ /([^=]*)=(.*)/
    end
  end

  #Helpfull to string
  def to_s
    output = "File Name #{@file} \n"
    @properties.each {|key,value| output += " #{key}= #{value} \n" }
    print output
  end

  #Read a property
  def read_property (key)
    # Return value without quotes
    if @properties[key]
      return @properties[key].tr('"', '')
    else
      print "No property set for key " + key
      return ""
    end
  end

end