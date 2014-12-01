package edu.buffalo.memlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import edu.buffalo.memlib.manager.FileOperations;
import edu.buffalo.memlib.manager.MemoryUtil;

public class DebugActivity extends Activity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.debug);
        SwapManager.initialize();
        testMemoryUtil();
        testFileOperations();
        testSwapObjects();
        
        try {
        	Process process = Runtime.getRuntime().exec("logcat -t 25 Test:V dalvikvm:S libEGL:S OpenGLRenderer:S");
        	BufferedReader bufferedReader = new BufferedReader(
        			new InputStreamReader(process.getInputStream()));

        	StringBuilder log=new StringBuilder();
        	String line = ""; 
        	while ((line = bufferedReader.readLine()) != null) {
        		log.append(line + "\n");
        	}   
        	TextView tv = (TextView)findViewById(R.id.textView1);
        	tv.setText("");
        	tv.setText(log.toString());
        } catch (IOException e) {
        	e.printStackTrace();
        }
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
        
        Queue<Integer> queue = SwapObjects.getOne(Queue.class, new ArrayDeque<Integer>());
        queue.add(9);
        queue.add(10);
        Log.d("Test", "getOne Queue " + queue);
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
