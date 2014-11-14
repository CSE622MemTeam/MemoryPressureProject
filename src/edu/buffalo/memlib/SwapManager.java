package edu.buffalo.memlib;


public final class SwapManager {
    public LeastRecentlyUsed policy;
    
    public SwapManager() {
        policy = new LeastRecentlyUsed();
    }
}
