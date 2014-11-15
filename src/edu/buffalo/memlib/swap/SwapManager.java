package edu.buffalo.memlib.swap;

import edu.buffalo.memlib.policy.*;

public final class SwapManager {
	public LeastRecentlyUsed policy;

	public SwapManager() {
		policy = new LeastRecentlyUsed();
	}
}
