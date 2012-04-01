#!/usr/bin/env ruby

class Tree

  def initialize
   @nfiles = @ndirs = 0
  end

  def display(dirname)
    dive dirname, ""
    printf("\n%d %s, %d %s\n",
           @ndirs,  (@ndirs == 1 ? "directory" : "directories"),
           @nfiles, (@nfiles == 1 ? "file" : "files"))
  end

  private

  def dive(dirname, prefix)
    dir = Dir.new(dirname)
    Dir.chdir dirname
    dirname = Dir.pwd  # ensure get full path to dir

    sorted_entries(dir).each do |e|
      display_dir(e, prefix, dirname) or
        display_link(e, prefix)       or
        display_file(e, prefix)
    end
  end

  def display_dir(e, prefix, dirname)
    if File.ftype(e) == "directory"
      @ndirs += 1
      print "#{prefix}|-- ", e, "/\n"
      dive(e, prefix + "    ")
      Dir.chdir dirname
      true
    end
  end
  
  def display_file(e, prefix)
    @nfiles += 1
    print "#{prefix}|-- ", e, "\n"
    true
  end
  
  def display_link(e, prefix)
    if File.ftype(e) == "link"
      @nfiles += 1
      puts "#{prefix}|-- #{e} -> #{File.readlink(e)}"
      true
    end
  end
  
  def sorted_entries(dir)
    dlist = dir.select { |e| e !~ /^\./ }
    dlist.sort do |a,b|
      a = a.gsub(/[^0-9a-zA-Z]/, '')
      b = b.gsub(/[^0-9a-zA-Z]/, '')
      a.downcase <=> b.downcase
    end
  end
  
end

# ---[ MAIN METHOD ]--- #

def main(dirname)
  puts dirname
  t = Tree.new
  t.display(dirname)
rescue => ex
  puts "[error]: #{ex}"
end


if __FILE__ == $0
  main(ARGV.first || '.')
end
