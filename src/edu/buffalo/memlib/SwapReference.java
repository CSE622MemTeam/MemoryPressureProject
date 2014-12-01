package edu.buffalo.memlib;

import java.io.IOException;

/** A swappable reference to an object. */
public class SwapReference<T> {
    /**
     * The referent or swap token. This will either be a SwapToken object, or
     * something else (including null).
     */
    private Object object;

    /** Neighbor elements in reference list. */
    private SwapReference<?> next, prev;

    /** Head and tail of global reference list. Head == LRU. */
    private static SwapReference<?> head, tail;

    /** Create a SwapReference with a null referent. */
    public SwapReference() { this(null); }

    /** Create a SwapReference referring to the given object. */
    public SwapReference(T object) {
        SwapManager.initialize();
        synchronized (SwapManager.class) { set(object); }
    }

    /**
     * Get the referent. If it is swapped out, it will be swapped in.
     */
    @SuppressWarnings("unchecked")
    public synchronized T get() {
        if (isSwappedOut()) {
            swapIn();
            SwapManager.analyzeAndCollect();
        } else {
            updateAccessList();
        } return (T) object;
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
        SwapManager.analyzeAndCollect();
        updateAccessList();
    }

    /** Bring the referent in from swap. No effect if already swapped in. */
    public synchronized void swapIn() {
        updateAccessList();

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
    @SuppressWarnings("unchecked")
    public synchronized void swapOut() {
        if (object != null && !isSwappedOut()) try {
            object = (T) Swap.swapOut(object);
            updateAccessList();
            System.gc();
        } catch (IOException e) {
            // Couldn't swap object. Let's treat this like an OOM error for now.
            // Really we should throw some checked exception to force the caller to
            // deal with swap failing.
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
    @SuppressWarnings("unchecked")
    private Swap.Token<T> token() {
        return (Swap.Token<T>) object;
    }

    /**
     * Swap out the least recently used SwapReference. Returns true if there was
     * something to swap out; false otherwise.
     */
    static synchronized boolean swapOutLeastUsed() {
        if (head == null)
            return false;
        head.swapOut();
        return true;
    }

    /**
     * Call this whenever the SwapReference is accessed or changed. This updates
     * the SwapReference's position in the global reference list, or removes it
     * if it has been swapped out (or is null).
     */
    private synchronized void updateAccessList() {
        synchronized (SwapReference.class) {
            // Remove from list if necessary.
            if (prev != null) prev.next = next;
            if (next != null) next.prev = prev;
            if (head == this) head = next;
            if (tail == this) tail = prev;
            prev = next = null;

            // Don't (re)insert if unnecessary.
            if (object == null || isSwappedOut())
                return;

            // Append to list.
            if (tail != null) {
                tail.next = this;
                this.prev = tail;
                tail = this;
            } else {
                head = tail = this;
            }
        }
    }
}
