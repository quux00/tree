## Tree

This repository is a start towards implementating the Unix/Linux `tree` command in multiple programming languages.  You can learn more about the native tree command here: [http://www.computerhope.com/unix/tree.htm](http://www.computerhope.com/unix/tree.htm)

So far, I have created a very simple Ruby version and two Java versions - one that will work with Java 6 (and earlier) and one that will work only with Java 7, as it uses the new Java7 NIO2 [Path](http://docs.oracle.com/javase/7/docs/api/java/nio/file/Path.html) interface.

I initially did this project to have a version of `tree` on Windows with either Ruby or Java.

Over time, it would be a good exercise to do this in more languages - it is a good command to learn how files and directories are handled in a language.

## Java version

You can build the Java 6 version with:

    ant compile6

You can build the Java 7 version with:

    ant compile7

Once done, run:

    ant jar
    
This will create a tree.jar file in the dist directory.

I have provided two simple shell scripts to run it - you'll need to install the jar somewhere and then modify the appropriate shell script.  If you do this with a .bat file on Windows, feel free to send it to me.

The real tree command supports quite a number of command line switches.  My Java version only supports a few so far:

    $ ./tree.sh -h
    Usage: tree [OPTIONS] [DIRECTORY]
    DIRECTORY is optional and defaults to current dir.
    OPTIONS:
    -a    : Show all files, including files starting with .
    -d    : Show dirs only
    -i    : No indentation
    -h    : Show this help screen and exit.


## Ruby version

The Ruby version depends on no gems, so you can just run it.  Currently, the only switch it supports is `-h` (or `--help`).

