package edu.buffalo.memlib;

import java.io.File;
import java.util.Scanner;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.util.Log;

public final class MemUtil extends Activity {
    
    public static long getMaxHeap() {
	return Runtime.getRuntime().maxMemory();
    }
    
    public static long getCurrentHeap() {
	return Runtime.getRuntime().totalMemory();
    }
    
    public static long getUsedHeap() {
	return getCurrentHeap() - Runtime.getRuntime().freeMemory();
    }
    
    public static long getTotalMem() {
	return scanProcForField("/proc/meminfo", "MemTotal");
    }
    
    public static long getFreeMem() {
	return scanProcForField("/proc/meminfo", "MemFree");
    }
    
    public static long getAvailableMem(Activity activity) {
	ActivityManager activityManager =  (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
	MemoryInfo memoryInfo = new MemoryInfo();
	activityManager.getMemoryInfo(memoryInfo);
	return memoryInfo.availMem;
    }
    
    public static long getThreshold(Activity activity) {
	ActivityManager activityManager =  (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
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
