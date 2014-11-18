package edu.buffalo.memlib.swap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public abstract class SwapActivity extends Activity {
	private static Context swapContext;
	private static boolean internal = true;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swapContext = this.getApplicationContext();
    }
	
	protected void onCreate(Bundle savedInstanceState, boolean internal) {
        super.onCreate(savedInstanceState);
        SwapActivity.swapContext = this.getApplicationContext();
        SwapActivity.internal = internal;
    }
	
	public static Context getSwapContext() {
		return swapContext;
	}
	
	public static boolean isInternal() {
		return internal;
	}
}
