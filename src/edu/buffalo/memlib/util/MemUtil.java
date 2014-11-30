package edu.buffalo.memlib.util;

import java.io.File;
import java.util.Scanner;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.util.Log;

public final class MemUtil extends Activity {
	
	/** Maximum heap space allowed for this application. */
	public static long getMaxHeap() {
		return Runtime.getRuntime().maxMemory();
	}

	/** Current allocated heap space. */
	public static long getCurrentHeap() {
		return Runtime.getRuntime().totalMemory();
	}

	/** Current used heap space. */
	public static long getUsedHeap() {
		return getCurrentHeap() - Runtime.getRuntime().freeMemory();
	}

	/** Total system memory. */
	public static long getTotalMem() {
		return scanProcForField("/proc/meminfo", "MemTotal");
	}

	/** Available system memory. */
	public static long getFreeMem() {
		return scanProcForField("/proc/meminfo", "MemFree");
	}
	
	
	/**
	 * Check if the application is backgrounded.
	 * 
	 * @return true if backgrounded, otherwise false
	 */
	public static boolean isBackgrounded() {
		File file = new File("/proc/" + android.os.Process.myPid() + "/oom_adj");
		long oom_adj = -1;
		
		try {
			Scanner scanner = new Scanner(file);
			oom_adj = scanner.nextLong();
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return oom_adj != 0 ? true : false;
	}

	/** 
	 *  The available memory on the system. This number should not be considered absolute: due 
	 *  to the nature of the kernel, a significant portion of this memory is actually in use and 
	 *  needed for the overall system to run well.
	 */
	public static long getAvailableMem(Context context) {
		ActivityManager activityManager =  (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	/** 
	 *  The threshold of availMem at which we consider memory to be low and start killing 
	 *  background services and other non-extraneous processes. 
	 */
	public static long getThreshold(Context context) {
		ActivityManager activityManager =  (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo.threshold;
	}

	
	private static long scanProcForField(String path, String field) {
		File file = new File(path);
		Scanner scanner = null;
		field += ":";

		try {
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String token = scanner.next();
				if (token.equals(field))
					return scanner.nextLong() << 10; // It's in KB...
				scanner.nextLine();
			}
		} catch (Exception e) {
			// Should we propagate this? Fall through for now.
			Log.e("MemInfo", "Error scanning " + file + " for " + field, e);
		} finally {
			if (scanner != null)
				scanner.close();
		}
		return -1;
	}
}
