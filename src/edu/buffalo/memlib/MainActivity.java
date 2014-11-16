package edu.buffalo.memlib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.os.Bundle;
import android.util.Log;

import edu.buffalo.memlib.swap.*;
import edu.buffalo.memlib.util.*;

public class MainActivity extends SwapActivity {
    List<byte[]> byteList= new LinkedList<byte[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	showAvailableMemory();
    }
    
    private void showAvailableMemory() {
	ActivityManager activityManager =  (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	MemoryInfo memoryInfo = new MemoryInfo();
	activityManager.getMemoryInfo(memoryInfo);
	Log.i("memory footprint", "" + memoryInfo.availMem / memoryInfo.totalMem + "%");
	Log.i("TOTAL MEMORY", "" + (MemUtil.getTotalMem() >> 20));
	Log.i("AVAILABLE MEMORY", "" + (MemUtil.getAvailableMem() >> 20));
	Log.i("FREE MEMORY", "" + (MemUtil.getFreeMem() >> 20));
	Log.i("THRESHOLD MEMORY", "" + (memoryInfo.threshold >> 20));

	Log.i("max heap", "" + (MemUtil.getMaxHeap() >>20));
	Log.i("current heap", "" + (MemUtil.getCurrentHeap() >>20)); 
	Log.i("used heap", "" + (MemUtil.getUsedHeap() >>20));   
	allocateMemory(5<<20);
	Log.i("max heap", "" + (MemUtil.getMaxHeap() >>20));
	Log.i("current heap", "" + (MemUtil.getCurrentHeap() >>20)); 
	Log.i("used heap", "" + (MemUtil.getUsedHeap() >>20));   
	
	String string = "hello world!";

	FileOutputStream fos;
	try {
	    fos = SwapUtil.getFileOutputStream(5, false);
	    fos.write(string.getBytes());
	    fos.close();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	

	System.out.println(SwapUtil.fileExists(5, false));
	
	FileInputStream fis;
	try {
	    fis = SwapUtil.getFileInputStream(5, false);
	    StringBuilder builder = new StringBuilder();
	    int ch;
	    while((ch = fis.read()) != -1){
		builder.append((char)ch);
	    }

	    System.out.println(builder.toString());
	    fis.close();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		SwapUtil.deleteFile(5, false);
		System.out.println(SwapUtil.fileExists(5, false));
		
		SwapVector<String> vector = new SwapVector<String>();
		vector.add("cat");
		vector.add("dog");
		vector.swapOut(false);
		System.out.println(vector.get(0));
		System.out.println(vector.get(1));
    }
    
    private void allocateMemory(int bytes) {
	byteList.add(new byte[bytes]);
    }
}
