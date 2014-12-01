package edu.buffalo.memlib;

/** Global configuration API. */
public final class SwapLib {
    /** Configure the library with a policy file. */
    public static void setPolicy(Policy policy) {
        Swap.setRoot(policy.swapPath);
        SwapManager.setPolicy(policy);
    }
}
