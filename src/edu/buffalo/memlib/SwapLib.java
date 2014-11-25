package edu.buffalo.memlib;

import java.util.io.*;

import edu.buffalo.memlib.swap.Swap;

/**
 * Global configuration interface.
 */
public final class SwapLib {
  public static void setPolicy(Policy policy) {
    Swap.setRoot(policy.swapPath);
    SwapManager.setPolicy(policy);
  }
}
