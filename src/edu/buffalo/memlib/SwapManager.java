package edu.buffalo.memlib;

import android.util.Log;
import edu.buffalo.memlib.manager.MemoryUtil;

/**
 * Application-wide daemon thread. Periodically monitors heap utilization and
 * swaps objects as necessary.
 */
final class SwapManager {
    /** Set to true when daemon thread has started. */
    private static boolean initialized = false;

    /** Start this thread to start monitoring. */
    private static Thread daemon = new Thread("swap-manager") {
        public void run() { monitor(); }
    };

    private static Policy policy = new Policy();

    /** Set the global policy used by the swap manager. */
    public static synchronized void setPolicy(Policy policy) {
        SwapManager.policy = policy;
    }

    /** Initialize the swap manager thread. No effect if already started. */
    public static void initialize() {
        if (!initialized) try {
            daemon.setDaemon(true);
            daemon.start();
            
            initialized = true;
        } catch (Exception e) {
            // Must have already started...
        }
    }

    /** The monitoring code. Will be run in a separate thread. */
    private static void monitor() {
        while (true) synchronized (daemon) {
            try {
                daemon.wait(policy.heapAnalysisInterval);
            } catch (InterruptedException ie) {
                // This should actually never happen...
            } finally {
                analyze();
            }
        }
    }

    /** Force early heap analysis. */
    public static void force() {
        synchronized (daemon) {
            daemon.notifyAll();
        };
    }

    /** Analyze memory usage and swap if necessary. */
    private static void analyze() {
        double target  = policy.fgHeapOptUsage;
        double trigger = policy.fgHeapMaxUsage;

        if (MemoryUtil.isBackgrounded()) {
            target  = policy.bgHeapOptUsage;
            trigger = policy.bgHeapMaxUsage;
        }

        if (MemoryUtil.heapUsage() > trigger)
            swapUntil(target);
    }

    /**
     * Swap until target utilization is reached (or there is nothing to swap).
     */
    private static void swapUntil(double target) {
        while (MemoryUtil.heapUsage() > target &&
               SwapReference.swapOutLeastUsed())
            Thread.yield();  // Precaution - don't overutilize CPU.
    }

    private SwapManager() { /* Don't make me. */ }
}
