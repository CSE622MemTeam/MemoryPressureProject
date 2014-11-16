package edu.buffalo.memlib.swap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public abstract class SwapActivity extends Activity {
	private static Context swapContext;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swapContext = this.getApplicationContext();
    }
	
	public static Context getSwapContext() {
		return swapContext;
	}
}
