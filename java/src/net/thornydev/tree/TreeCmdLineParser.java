package net.thornydev.tree;

import java.io.PrintStream;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Command Line argument and options parser specifically
 * for the TreeCmd classes (either Tree6Cmd or Tree7Cmd).
 * 
 * @author Michael Peterson
 */
public class TreeCmdLineParser {

  // the options (switches) specified by the user
  EnumSet<TreeCmdOptions> options;
  String startDir = ".";  // default is curr dir
  
  public void showHelp() {
    PrintStream out = System.out;
    out.println("Usage: tree [OPTIONS] [DIRECTORY]");
    out.println("  DIRECTORY is optional and defaults to current dir.");
    out.println("  OPTIONS:");
    out.println("  -a    : Show all files, including files starting with .");
    out.println("  -d    : Show dirs only");
    out.println("  -i    : No indentation");
    out.println("  -h    : Show this help screen and exit.");
  }
  
  // Support
  // -a all files
  // -d dirs only
  // -i no indentation lines
  // -h help screen
  // Last arg is optional directory
  public void parse(String[] args) {
    
    Set<TreeCmdOptions> optSet = new HashSet<TreeCmdOptions>();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-a")) {
        optSet.add(TreeCmdOptions.ALL_FILES);
      } else if (args[i].equals("-d")) {
        optSet.add(TreeCmdOptions.DIRS_ONLY);
      } else if (args[i].equals("-i")) {
        optSet.add(TreeCmdOptions.NO_INDENTATION);
      } else if (args[i].equals("-h")) {
        showHelp();
        System.exit(0);
      } else if (i == args.length - 1) {
        startDir = args[i];
      } else {
        throw new IllegalArgumentException("Command line option " + args[i] + " not recognized.");
      }
    }
    if (optSet.size() == 0) {
      options = EnumSet.noneOf(TreeCmdOptions.class);
    } else {
      options = EnumSet.copyOf(optSet);
    }
  }

  public String getStartDirectory() {
    return startDir;
  }

  public EnumSet<TreeCmdOptions> getOptions() {
    return options;
  }

}
