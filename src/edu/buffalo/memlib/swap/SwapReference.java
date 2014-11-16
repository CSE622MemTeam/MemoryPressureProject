package edu.buffalo.memlib.swap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** A swappable reference to an object. */
public class SwapReference<T> {
  /**
   * The referent or swap token. This will either be a SwapToken object, or
   * something else (including null).
   */
  private Object object;

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
    return (T) object;
  }

  /**
   * Set the referent. If the current referent is swapped out, its swap slot
   * will be freed.
   */
  public synchronized void set(T object) {
    if (isSwappedOut()) {
    	SwapToken token = (SwapToken) object;
    	SwapUtil.deleteFile(token.getTokenValue(), token.isInternal());
    }
//      Swapper.free(token());
    this.object = object;
  }

  /** Bring the referent in from swap. No effect if already swapped in. */
  public synchronized void swapIn() {
    if (isSwappedOut()) {
    	SwapToken token = (SwapToken) object;
    	try {
			ObjectInputStream ois = SwapUtil.getObjectInputStream(token.getTokenValue(), token.isInternal());
			object = ois.readObject();
			ois.close();
			SwapUtil.deleteFile(token.getTokenValue(), token.isInternal());
		} catch (IOException e) {
			e.printStackTrace();
		} 
    	catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    }
//      object = Swapper.get(token());
  }

  /** Swap the referent out. No effect if already swapped out. */
  public synchronized void swapOut(boolean internal) {
    if (object != null && !isSwappedOut()) {
    	SwapToken token = new SwapToken(internal);
    	try {
			ObjectOutputStream oos = SwapUtil.getObjectOutputStream(token.getTokenValue(), token.isInternal());
			oos.writeObject(object);
			oos.close();
			object = token;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
//      object = (T) Swapper.put(object);
  }

  /** Returns whether or not the referent is in swap. */
  private boolean isSwappedOut() {
	  return object instanceof SwapToken;
  }

//  /**
//   * Get the swap token if we have one.
//   *
//   * @throws IllegalStateException If the referent is swapped in.
//   */
//  private SwapToken token() {
//    if (!isSwappedOut())
//      throw new IllegalStateException();
//    return (SwapToken) object;
//  }
}
