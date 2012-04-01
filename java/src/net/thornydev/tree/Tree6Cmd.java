package net.thornydev.tree;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

public class Tree6Cmd {

	private final PathPrinter out;
	private final EnumSet<TreeCmdOptions> options;

	private int nfiles = 0;
	private int ndirs  = 0;

	public Tree6Cmd(final EnumSet<TreeCmdOptions> options) {
		this.options = options;
		out = new PathPrinter(!options.contains(TreeCmdOptions.NO_INDENTATION));

		// TODO: need to set up filter
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
	 * Recursively called with each new Dir Path encountered
	 * @param path Path representing a directory
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
		File[] paths = path.listFiles();
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

