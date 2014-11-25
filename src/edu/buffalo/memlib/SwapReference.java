package edu.buffalo.memlib.swap;

import java.io.*;

/** A swappable reference to an object. */
public class SwapReference<T> {
  /**
   * The referent or swap token. This will either be a SwapToken object, or
   * something else (including null).
   */
  private T object;

  private SwapReference<?> next, prev;
  static SwapReference<?> head, tail;

  /** Create a SwapReference with a null referent. */
  public SwapReference() { this(null); }

  /** Create a SwapReference referring to the given object. */
  public SwapReference(T object) {
    this.object = object;
    insertIntoList(this);
  }

  private static synchronized insertIntoList(SwapReference<?> ref) {
    // TODO: bwross
  }

  /**
   * Get the referent. If it is swapped out, it will be swapped in.
   */
  public synchronized T get() {
    swapIn();
    return object;
  }

  /**
   * Set the referent. If the current referent is swapped out, its swap slot
   * will be freed.
   */
  public synchronized void set(T object) {
    if (isSwappedOut()) try {
      Swap.free(token());
    } catch (IOException ioe) {
      // Trouble when freeing. Just ignore.
    }

    this.object = object;
  }

  /** Bring the referent in from swap. No effect if already swapped in. */
  public synchronized void swapIn() {
    if (isSwappedOut()) try {
      object = Swap.swapIn(token());
    } catch (IOException e) {
      // Couldn't recover object. Let's treat this like an OOM error.
      throw new OutOfMemoryError("Swapping in");
    } catch (ClassNotFoundException e) {
      // This should never happen if the framework is working and being used
      // right. Therefore, if it does happen, it's a programmer error.
      throw new Error("Corrupt swap file", e);
    }
  }

  /** Swap the referent out. No effect if already swapped out. */
  public synchronized void swapOut() {
    if (object != null && !isSwappedOut()) try {
      object = (T) Swap.swapOut(object);
    } catch (IOException e) {
      // Couldn't swap object. Let's treat this like an OOM error.
      throw new OutOfMemoryError("Swapping out");
    }
  }

  /** Returns true if the referent is swapped out. */
  public synchronized final boolean isSwappedOut() {
    return object instanceof Swap.Token;
  }

  /**
   * Returns the swap token if we're swapped out. It is an error to call this
   * if the referent is not swapped out.
   */
  private Swap.Token<T> token() {
    return (Swap.Token<T>) object;
  }
}
