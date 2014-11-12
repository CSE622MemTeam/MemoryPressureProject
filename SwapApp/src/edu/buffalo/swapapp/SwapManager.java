package edu.buffalo.swapapp;


public final class SwapManager {
    public LeastRecentlyUsed policy;
    
    public SwapManager() {
        policy = new LeastRecentlyUsed();
    }
}
