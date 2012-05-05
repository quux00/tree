#!/usr/bin/env ruby

# 
# Simple implementation of the Unix tree command
# in Ruby. This version takes only one cmd-line
# arg - the directory to display, which is optional
# as it defaults to the current directory.
# 
# The only switch it currently takes is -h or
# --help, so it is no where near as full featured
# as the C-version for Unix/Linux.
#
# Author: Michael Peterson
# Date: April 2012
# 
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

def help
  puts "Displays contents of DIR and all subdirs in tree form"
  puts "USAGE:"
  puts "tree.rb [OPTIONS] [DIR]"
  puts "  DIR is optional - defaults to current directory"
  puts "  -h, --help:  show this help"
end

if __FILE__ == $0
  ARGV.each do |arg|
    if arg == '-h' || arg == '--help'
      help
      exit
    end
  end
  main(ARGV.first || '.')
end
