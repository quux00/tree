package net.thornydev;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class Tree7Cmd {

	private final DirectoryStream.Filter<Path> filter;
	private final PathPrinter out;
	private final EnumSet<TreeCmdOptions> options;

	private int nfiles = 0;
	private int ndirs  = 0;
	
	public static class PathPrinter {
		public static final String SYMBOL = "|-- "; 
		private PrintStream out = System.out;
		private boolean showIndentation;
		
		public PathPrinter(boolean showIndentation) {
			this.showIndentation = showIndentation;
		}
		
		/**
		 * Prints the Path with the symbol in front and the
		 * specified number of spaces, followed by the path
		 * @param path
		 */
		public void p(Object path, String spaces) {
			if (showIndentation) {
				if (!spaces.equals("")) {
					out.print("|");
					out.print(spaces.substring(1));
				}
				out.print(SYMBOL);
			}
			out.println(path.toString());
		}
		
		// regular pass through to System.out.println
		public void println(Object o) {
			out.println(o);
		}
		
		// regular pass through to System.out.println
		public void println() {
			out.println();
		}
		
		// regular pass through to System.out.printf		
		public void printf(String format, Object... args) {
			out.printf(format, args);
		}
	}
	
	
	/* ---[ Tree7Cmd class ]--- */
	
	public Tree7Cmd(final EnumSet<TreeCmdOptions> options) {
		this.options = options;
		out = new PathPrinter(!options.contains(TreeCmdOptions.NO_INDENTATION));

		// filter on the Directory to determine which dir entries not to show
		filter = new DirectoryStream.Filter<Path>() {
	         public boolean accept(Path file) throws IOException {
	        	 if (options.contains(TreeCmdOptions.DIRS_ONLY) && !Files.isDirectory(file)) {
	        		 return false;
	        	 }
	        	 if (!options.contains(TreeCmdOptions.ALL_FILES)) {
	        		 return !(file.getFileName().toString().startsWith("."));
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
		Path path = checkIsDir(dirname);
		printTopDirName(dirname);
		dive(path, "");
		printSummary();
	}

	

	// prints the "N directories, M files" note
	private void printSummary() {
		out.printf("%n%d %s", ndirs, (ndirs == 1 ? "directory" : "directories"));
		
		if (!options.contains(TreeCmdOptions.DIRS_ONLY)) {
			out.printf("%d %s", nfiles, (nfiles == 1 ? "file" : "files"));
		}
		out.println();
	}

	/**
	 * Recursively called with each new Dir Path encountered
	 * @param path Path representing a directory
	 */
	private void dive(Path path, String spaces) throws Exception {
		for (Path entry: getEntriesInPath(path)) {
			if (Files.isDirectory(entry)) {
				out.p(entry.getFileName(), spaces);
				dive(entry, spaces + "    ");
				++ndirs;
			} else if (Files.isSymbolicLink(entry)) {
				Path p = Files.readSymbolicLink(entry);
				out.p(entry.getFileName() + " -> " + p.toString(), spaces);
				++nfiles;
			} else if (Files.isRegularFile(entry)){
				out.p(entry.getFileName(), spaces);
				++nfiles;
			} else {
				out.printf("[error]: %s:%s%n", entry.getFileName(), entry);
			}
		}
	}

	private List<Path> getEntriesInPath(Path path) throws Exception {
		List<Path> paths = new ArrayList<Path>();
		// try-with-resources idiom
		// Note; there doesn't appear to be a way to sort a DirectoryStream, so I 
		// copy the entries into a List and then sort it below
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter)) {
			for (Path entry: stream) {
				paths.add(entry);
			}
		}
		
		Collections.sort(paths);
		return paths;
	}
	
	private void printTopDirName(String dirname) {
		out.println(dirname);
	}


	private Path checkIsDir(String dirname) {
		try {
			Path path = Paths.get(dirname).toRealPath();
			if (Files.isDirectory(path)) return path;
			else throw new RuntimeException("[error]: Path " + dirname + " is not a directory.");
		} catch (IOException e) {
			throw new RuntimeException("[error]: Path " + dirname + " does not exist.");
		}
	}

	/* ---[ MAIN ]--- */

	// see the TreeCmdLineParser for what options are supported
	public static void main(String[] args) {
		try {
			TreeCmdLineParser p = new TreeCmdLineParser();
			p.parse(args);

			Tree7Cmd t = new Tree7Cmd(p.getOptions());
			System.out.println(p.getStartDirectory());
			t.display(p.getStartDirectory());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
