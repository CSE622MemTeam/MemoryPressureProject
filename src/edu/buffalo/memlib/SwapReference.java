//package edu.buffalo.memlib;

import java.io.*;
import java.lang.ref.*;

public class SwapReference<T extends Serializable> {
  private static final ReferenceQueue<Serializable> queue =
    new ReferenceQueue<Serializable>();
  private static final SwapFile swap = SwapFile.instance();

  /** Thread which swaps objects when they're being freed. */
  private static final Thread swapThread = new Thread("swap-queue-thread") {
    {
      setDaemon(true);
      start();
    }

    public void run() {
      while (true) try {
        InternalReference ref = (InternalReference) queue.remove();
        ref.swapOut();
      } catch (InterruptedException e) {
        // This will never happen...
      }
    }
  };

  private InternalReference<T> ref;

  private interface InternalReference<T1> {
    T1 get();
    void free();
    void swapIn();
    void swapOut();
  }

  /** A reference to an object still on the heap. */
  private final class LiveReference extends SoftReference<T>
  implements InternalReference<T> {
    private boolean freed = false;

    LiveReference(T object) { super(object, queue); }

    public void free() {
      // Synchronized - called in finalizer.
      if (!freed) synchronized (SwapReference.this) {
        clear();
        freed = true;
      }
    }

    public void swapIn() { }

    public void swapOut() {
      // Synchronized - called in swapThread.
      if (!freed) synchronized (SwapReference.this) {
        try {
          T t = get();
          clear();
          long token = swap().put(t);
          ref = new DeadReference(token);
        } catch (Exception e) {
          e.printStackTrace();
          SwapReference.this.clear();
        }
      }
    }

    public void finalize() { free(); }
  }

  /** A reference to an object which has been swapped out. */
  private final class DeadReference implements InternalReference<T> {
    private long token;

    DeadReference(long token) { this.token = token; }

    public T get() {
      if (token < 0) {
        return null;
      } try {
        T t = (T) swap().get(token);
        free();
        return t;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    public void free() {
      // Synchronized - called in finalizer.
      synchronized (SwapReference.this) {
        swap().free(token);
        token = -1;
      }
    }

    public void swapIn() {
      if (token < 0) {
        SwapReference.this.clear();
      } else try {
        set((T) swap().get(token));
      } catch (Exception e) {
        e.printStackTrace();
        clear();
      }
    }

    public void swapOut() { }
  }

  /** Used when this SwapReference is set to null. */
  private final class NullReference implements InternalReference<T> {
    public T get() { return null; }
    public void free() { }
    public void swapIn() { }
    public void swapOut() { }
  }

  /**
   * Creates a new swap reference that refers to the given object. The object
   * is serialized and written to permanent storage when the garbage collector
   * is has determined it should be freed.
   *
   * @param object object the swap reference will refer to
   */
  public SwapReference(T object) {
    set(object);
  }

  /** Set this reference object's referent. */
  public synchronized void set(T object) {
    if (ref != null)
      ref.free();
    ref = (object == null) ?
      new NullReference() : new LiveReference(object);
  }

  /** Returns this reference object's referent. */
  public synchronized T get() { return ref.get(); }

  /** Clears this reference object. */
  public synchronized void clear() { set(null); }

  /** Force the referent to be swapped out. */
  public synchronized void swapOut() { ref.swapOut(); }

  /** Swap the referent back in. */
  public synchronized void swapIn() { ref.swapIn(); }

  /**
   * Get the swap file used by this reference. By default, it's the global swap
   * file. Subclasses can override this to use a different swap file.
   */
  SwapFile swap() { return SwapFile.instance(); }

  public void finalize() { ref.free(); }

  public static void main(String[] args) {
    final int mb = 32;
    SwapReference reference = new SwapReference(
      new java.util.LinkedList<byte[]>() {{
        for (int i = 0; i < mb; i++) add(new byte[1<<20]);
      }}
    );

    System.out.println(reference.ref);
    System.out.println(Runtime.getRuntime().freeMemory());
    System.out.println();

    while (true) {
      reference.swapOut();
      System.gc();

      System.out.println(reference.ref);
      System.out.println(Runtime.getRuntime().freeMemory());
      System.out.println();

      reference.swapIn();
      System.gc();
      System.out.println(reference.ref);
      System.out.println(Runtime.getRuntime().freeMemory());
      System.out.println("---------");
    }
  }
}
