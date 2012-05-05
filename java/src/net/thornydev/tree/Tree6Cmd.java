package net.thornydev.tree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * A relatively simple implementation of the Unix/Linux 'tree'
 * command. This class relies on a PathPrinter and TreeCmdLineParser.
 * It encompasses the logic of recursing down a directory hierarchy
 * printing out all the contents of those directories - minus what
 * any applied filters remove.
 * 
 * The "6" in the name means that this version can run with Java 6,
 * as it does not rely on the Java7 NIO2 library.
 *
 * @author Michael Peterson
 */
public class Tree6Cmd {

  private final PathPrinter out;
  private final EnumSet<TreeCmdOptions> options;
  private final FileFilter filter;

  private int nfiles = 0;
  private int ndirs  = 0;

  /**
   * @param options EnumSet of TreeCmdOptions from the TreeCmdLineParser
   */
  public Tree6Cmd(final EnumSet<TreeCmdOptions> options) {
    this.options = options;
    out = new PathPrinter(!options.contains(TreeCmdOptions.NO_INDENTATION));

    filter = new FileFilter() {
        public boolean accept(File file) {
          if (options.contains(TreeCmdOptions.DIRS_ONLY) && !file.isDirectory()) {
            return false;
          }
          if (!options.contains(TreeCmdOptions.ALL_FILES)) {
            return !(file.getName().startsWith("."));
          }
          return true;
        }
      };
  }

  /**
   * Call with the top level directory name to recurse into to display
   * all its contents (in accordance with the options provided)
   * @param dirname top level directory
   * @throws Exception
   */
  public void display(final String dirname) throws Exception {
    File path = checkIsDir(dirname);
    printTopDirName(dirname);
    dive(path, "");
    printSummary();
  }


  // prints the "N directories, M files" note
  private void printSummary() {
    out.printf("%n%d %s", ndirs, (ndirs == 1 ? "directory" : "directories"));

    if (!options.contains(TreeCmdOptions.DIRS_ONLY)) {
      out.printf(", %d %s", nfiles, (nfiles == 1 ? "file" : "files"));
    }
    out.println();
  }

  /**
   * Checks whether the file is a symlink using tricks required
   * with the File class, rather than the functionality in the
   * Java 7 Path class.
   */
  boolean isSymlink(File file) throws IOException {
    File canon;
    if (file.getParent() == null) {
      canon = file;
    } else {
      File canonDir = file.getParentFile().getCanonicalFile();
      canon = new File(canonDir, file.getName());
    }
    return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
  }
  
  /**
   * Recursively called with each new dir path (File) encountered
   * @param path File representing a directory
   * @param spaces String of spaces for appropriate level of indentation
   *        in the printout
   */
  private void dive(File path, String spaces) throws Exception {
    for (File entry: getEntriesInPath(path)) {
      if (entry.isDirectory()) {
        out.p(entry.getName(), spaces);
        dive(entry, spaces + "    ");
        ++ndirs;
      } else if (isSymlink(entry)) {
        // TODO: not sure this is how to get symlink path in Java 6
        out.p(entry.getName() + " -> " + entry.getCanonicalPath(), spaces);
        ++nfiles;
      } else if (entry.isFile()){
        out.p(entry.getName(), spaces);
        ++nfiles;
      } else {
        out.printf("[error]: %s:%s%n", entry.getName(), entry);
      }
    }
  }


  private File[] getEntriesInPath(File path) throws Exception {
    File[] paths = path.listFiles(filter);
    Arrays.sort(paths);
    return paths;
  }

  private void printTopDirName(String dirname) {
    out.println(dirname);
  }


  private File checkIsDir(String dirname) {
    File path = new File(dirname);
    if (path.isDirectory()) {
      return path;
    } else {
      throw new RuntimeException("[error]: Path " + dirname + " is not a directory.");
    }
  }

  /* ---[ MAIN ]--- */

  // see the TreeCmdLineParser for what options are supported
  public static void main(String[] args) {
    try {
      TreeCmdLineParser p = new TreeCmdLineParser();
      p.parse(args);

      Tree6Cmd t = new Tree6Cmd(p.getOptions());
      t.display(p.getStartDirectory());
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

}

