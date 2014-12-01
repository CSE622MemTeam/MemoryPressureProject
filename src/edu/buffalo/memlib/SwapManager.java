package edu.buffalo.memlib;

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
        while (true) try {
            Thread.sleep(policy.heapAnalysisInterval);
            analyzeAndCollect();
        } catch (InterruptedException ie) {
            // If we're interrupted, don't collect, just reset.
        }
    }

    /** Force heap analysis. */
    public static synchronized void analyzeAndCollect() {
        if (shouldSwap())
            System.gc();
        if (shouldSwap())
            swapUntilOptimum();

        // Interrupt the daemon to reset its timer.
        daemon.interrupt();
    }

    /** Determine if we should swap. */
    private static synchronized boolean shouldSwap() {
        double trigger = MemoryUtil.isBackgrounded() ?
            policy.bgHeapMaxUsage : policy.fgHeapMaxUsage;
        System.out.println("Trigger: "+(MemoryUtil.heapUsage() > trigger));
        return MemoryUtil.heapUsage() > trigger;
    }

    /** Swap until we've reached optimal heap utilization. */
    private static synchronized void swapUntilOptimum() {
        double target = MemoryUtil.isBackgrounded() ?
            policy.bgHeapOptUsage : policy.fgHeapOptUsage;
        swapUntil(target);
    }

    /**
     * Swap until target utilization is reached (or there is nothing to swap).
     */
    private static synchronized void swapUntil(double target) {
        while (MemoryUtil.heapUsage() > target) {
            // Swap out, and stop if there's nothing left to swap.
            if (!SwapReference.swapOutLeastUsed())
                return;
            System.gc();
            System.out.println("Swapped something out! "+MemoryUtil.heapUsage());
        }
    }

    private SwapManager() { /* Don't make me. */ }
}
