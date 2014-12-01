package edu.buffalo.memlib;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import edu.buffalo.memlib.manager.FileOperations;
import edu.buffalo.memlib.manager.MemoryUtil;

public class MainActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SwapManager.initialize();
		testMemoryUtil();
		testFileOperations();
		
		testSwapObjects();
		SwapDirectory.clear();
	}
	
	/** Test the SwapObjects. */
	public void testSwapObjects() {
		Set<Integer> hset = SwapObjects.getHashSet();
		hset.add(1);
		hset.add(2);
		Log.d("Test", "HashSet " + hset);
		
		List<Integer> alist = SwapObjects.getArrayList();
		alist.add(3);
		alist.add(4);
		Log.d("Test", "ArrayList " + alist);
		
		Map<Integer, String> hmap = SwapObjects.getHashMap();
		hmap.put(5, "Hello");
		hmap.put(6, "World");
		Log.d("Test", "HashMap " + hmap);

		List<Integer> llist = SwapObjects.getLinkedList();
		llist.add(7);
		llist.add(8);
		Log.d("Test", "LinkedList " + llist);
	}
	
	/** Tests every MemoryUtil public methods. */
	public void testMemoryUtil() {
		MemoryUtil.buildTable();
		MemoryUtil.updateMemoryStatus();
		MemoryUtil.dumpString();
		Log.d("Test", "getAvailableMem " + MemoryUtil.bytesToString(MemoryUtil.getAvailableMem(this)) + "\n" +
		              "getThreshold " + MemoryUtil.bytesToString(MemoryUtil.getThreshold(this)) + "\n" +
		              "getCurrentHeap " + MemoryUtil.bytesToString(MemoryUtil.getCurrentHeap()) + "MB \n" +
		              "getFreeMem " + MemoryUtil.bytesToString(MemoryUtil.getFreeMem()) + "\n" +
		              "getMaxHeap " + MemoryUtil.bytesToString(MemoryUtil.getMaxHeap()) + "\n" +
		              "getTotalMem " + MemoryUtil.bytesToString(MemoryUtil.getTotalMem()) + "\n" +
		              "getUsedHeap " + MemoryUtil.bytesToString(MemoryUtil.getUsedHeap()) + "\n" +
		              "isBackgrounded " + MemoryUtil.isBackgrounded() + "\n" +
		              "heapUsage " + MemoryUtil.heapUsage() + "\n" +
		              "getMemoryState " + MemoryUtil.getMemoryState());
	}
	
	/** Tests FileOperations public methods. */
	public void testFileOperations() {
		FileOperations.createFile();
		FileOperations.writeHeaders();
		try {
			FileOperations.write("Hello World!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
