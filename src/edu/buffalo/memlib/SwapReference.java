//package edu.buffalo.memlib;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/** A swappable reference to an object. */
public class SwapReference<T> {
  /**
   * The referent or swap token. This will either be a SwapToken object, or
   * something else (including null).
   */
  private T object;

  /** Create a SwapReference with a null referent. */
  public SwapReference() { this(null); }

  /** Create a SwapReference referring to the given object. */
  public SwapReference(T object) { this.object = object; }

  /**
   * Get the referent. If it is swapped out, it will be swapped in.
   */
  public synchronized T get() {
    if (isSwappedOut())
      swapIn();
    return object;
  }

  /**
   * Set the referent. If the current referent is swapped out, its swap slot
   * will be freed.
   */
  public synchronized void set(T object) {
    if (isSwappedOut())
      Swapper.free(token());
    this.object = object;
  }

  /** Bring the referent in from swap. No effect if already swapped in. */
  public synchronized void swapIn() {
    if (isSwappedOut())
      object = Swapper.get(token());
  }

  /** Swap the referent out. No effect if already swapped out. */
  public synchronized void swapOut() {
    if (object != null && !isSwappedOut())
      object = (T) Swapper.out(object);
  }

  /** Returns whether or not the referent is in swap. */
  private boolean isSwappedOut() {
    return object instanceof SwapToken;
  }

  /**
   * Get the swap token if we have one.
   *
   * @throws IllegalStateException If the referent is swapped in.
   */
  private SwapToken<T> token() {
    if (!isSwappedOut())
      throw new IllegalStateException();
    return (SwapToken<T>) object;
  }
}

/**
 * A token which can be used to recover a swapped object. This class will be
 * moved elsewhere later. For now, it's here only to demonstrate the concept.
 */
class SwapToken<T> {
  private long token;

  SwapToken(long token) { this.token = token; }
}
