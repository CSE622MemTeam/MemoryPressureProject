package edu.buffalo.memlib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.buffalo.memlib.manager.FileOperations;
import edu.buffalo.memlib.manager.MemoryUtil;

public class MainActivity extends Activity {
	List<byte[]> list = SwapObjects.getArrayList();
	TextView VmUsage;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// SwappableDSAPP section
		setContentView(R.layout.activity_main);
		
		TextView title   = (TextView)findViewById(R.id.title);
		TextView summary = (TextView)findViewById(R.id.summary);
		VmUsage = (TextView)findViewById(R.id.VMUsage);
		
		SimpleDateFormat simpleDate =new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		
		title.setText(R.string.AppInfo);
		summary.setText(R.string.AppTitle);
		
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
		    Toast.makeText(this, "External SD card not mounted", Toast.LENGTH_LONG).show();
		}
		else {
			FileOperations.createFile();
			
			try {
				FileOperations.write(simpleDate.format(new Date(0))+"\n");
				FileOperations.writeHeaders();
				MemoryUtil.dumpString();
			} catch (IOException e) {
				Log.e("ERROR_622", "Cannot write to file");
				e.printStackTrace();
			}
		}
		
		updateUsage();
		MemoryUtil.buildTable();
		
		Button tenButton = (Button) findViewById(R.id.button_10MB);
		tenButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	    	   allocateMemory(10<<20); 
	    	   updateUsage();
	        }
	    });
	    
	    Button oneButton = (Button)findViewById(R.id.button_1MB);
	    oneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				allocateMemory(1<<20);
				updateUsage();
			}
		});
	    
	    Button pushButton = (Button)findViewById(R.id.button2);
	    pushButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    list.size();
				updateUsage();
			}
		});
	    
	    Button popButton = (Button)findViewById(R.id.button1);
	    popButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SwapReference.swapOutLeastUsed();
				System.gc();
		        Runtime.getRuntime().gc();
				updateUsage();
			}
		});
		
		// Debug section
		SwapManager.initialize();
		testMemoryUtil();
		testFileOperations();
		testSwapObjects();
	}
	
	private void updateUsage() {
		Runtime info = Runtime.getRuntime(); 
		long max_memory  = info.maxMemory();
		long cur_memory  = (info.totalMemory() - info.freeMemory());
		VmUsage.setText("VM memory: " + 
		                MemoryUtil.bytesToString(cur_memory) +
		                " / " + 
		                MemoryUtil.bytesToString(max_memory));
	}

	/** Will allocate bytes MB memory. */
	private void allocateMemory(int bytes) {
		list.add(new byte[bytes]);
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
