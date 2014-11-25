package edu.buffalo.memlib;

import java.util.io.*;

import edu.buffalo.memlib.swap.Swap;

/**
 * Global configuration interface.
 */
public final class SwapLib {
  public static void setSwapLocation(String path) {
    setSwapLocation(new File(path));
  }

  public static void setSwapLocation(File dir) {
    Swap.setRoot(dir);
  }
}
