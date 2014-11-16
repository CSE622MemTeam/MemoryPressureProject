package edu.buffalo.memlib.swap;

/** A token which can be used to recover a swapped object. */
class SwapToken {
    private long token;
    private boolean internal;

    public SwapToken(long token) {
    	this.token = token;
    	this.internal = true;
    }
    
    public SwapToken(long token, boolean internal) { 
    	this.token = token;
    	this.internal = internal;
    }
    
    public long getTokenValue() {
    	return token;
    }
    
    public boolean isInternal() {
    	return internal;
    }
}
