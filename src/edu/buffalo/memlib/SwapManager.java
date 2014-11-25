package edu.buffalo.memlib;

import android.util.Log;

public class SwapManager extends Thread {
	private static volatile SwapManager instance;
	private static Policy = new Policy();

	private SwapManager() {
	}

	public static synchronized void initialize() {
		if (instance == null)
			instance = new SwapManager();
	}

	public void run() {
		// TODO: bwross
		while (true) try {
			Thread.sleep
		}
	}
}
