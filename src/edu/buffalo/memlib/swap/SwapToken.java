package edu.buffalo.memlib.swap;

import java.util.Random;

/** A token which can be used to recover a swapped object. */
class SwapToken {
    private long token;
    private boolean internal;
    private Random random;

    public SwapToken() {
    	this.internal = true;
    	random = new Random();
    	this.token = tokenGenerator();
    }
    
    public SwapToken(boolean internal) { 
    	this.internal = internal;
    	random = new Random();
    	this.token = tokenGenerator();
    }
    
    public long getTokenValue() {
    	return token;
    }
    
    public boolean isInternal() {
    	return internal;
    }
    
    private long tokenGenerator() {
    	while (true) {
        	long token = random.nextLong();
        	if (!SwapUtil.fileExists(token, internal))
        		return token;
    	}
    }
}
