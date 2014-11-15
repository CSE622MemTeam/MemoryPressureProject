package edu.buffalo.memlib.swap;

/** A token which can be used to recover a swapped object. */
class SwapToken<T> {
  private long token;

  SwapToken(long token) { this.token = token; }
}
