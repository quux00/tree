package net.thornydev.tree;

import java.io.PrintStream;

/**
 * Wrapper around STDOUT that provides a few simple
 * "print" commands that print proper indentation
 * and tree hierarchy symbols as part of the printout.
 */
public class PathPrinter {
  public static final String SYMBOL = "|-- "; 
  private PrintStream out = System.out;
  private boolean showIndentation;
  
  public PathPrinter(boolean showIndentation) {
    this.showIndentation = showIndentation;
  }
  
  /**
   * Prints the Path with the symbol in front and the
   * specified number of spaces, followed by the path.
   * 
   * @param path any Object with a useful toString() method
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
