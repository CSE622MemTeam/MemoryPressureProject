package edu.buffalo.memlib.swap;

/**
 * This class is a placeholder for now. It represents something that associated
 * swap tokens with swap files and vice versa and provides a nice interface to
 * access them.
 */
class Swapper {
  /** Put an object into swap, returning a token. */
  static SwapToken put(Object object) { return null; }

  /** Recover an object from swap using its swap token. */
  static <T> T get(SwapToken<T> token) { return null; }

  /** Free a swapped object. */
  static <T> void free(SwapToken<T> token) { }
}
