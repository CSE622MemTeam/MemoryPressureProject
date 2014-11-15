package edu.buffalo.memlib.swap;


public final class SwapManager {
    public LeastRecentlyUsed policy;
    
    public SwapManager() {
	policy = new LeastRecentlyUsed();
    }
}
