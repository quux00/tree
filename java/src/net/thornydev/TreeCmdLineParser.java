package net.thornydev;

import java.io.PrintStream;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class TreeCmdLineParser {

	EnumSet<TreeCmdOptions> options;
	String startDir;
	
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
	}

	public String getStartDirectory() {
		// TODO: update me
		return ".";
	}

	public EnumSet<TreeCmdOptions> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
