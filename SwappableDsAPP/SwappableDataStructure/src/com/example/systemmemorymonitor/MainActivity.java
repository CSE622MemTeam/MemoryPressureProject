package com.example.systemmemorymonitor;

import java.io.FileOutputStream;
import java.io.IOException;
import edu.buffalo.memlib.policy.LeastRecentlyUsed;
import edu.buffalo.memlib.policy.Policy;
import edu.buffalo.memlib.swap.*;
import edu.buffalo.memlib.util.*;
import edu.buffalo.memlib.manager.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import com.example.systemmemorymonitor.*;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	SwapVector<byte[]> vector = new SwapVector<byte[]>();
	//static LeastRecentlyUsed LRUPolicy=new LeastRecentlyUsed();
	
	public static Policy publicPolicy = new LeastRecentlyUsed();
	
	private TextView title;
	private TextView summary;
	private TextView VmUsage;
	
	private String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private SimpleDateFormat simpleDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		title   = (TextView)findViewById(R.id.title);
		summary = (TextView)findViewById(R.id.summary);
		VmUsage = (TextView)findViewById(R.id.VMUsage);
		
		simpleDate=new SimpleDateFormat(DATE_FORMAT, Locale.US);
								
		title.setText(R.string.AppInfo);
		summary.setText(R.string.AppTitle);
		
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
		    Toast.makeText(this, "External SD card not mounted", Toast.LENGTH_LONG).show();
		}
		else
		{
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
       showAvailableMemory();
      }
    });
    
    Button oneButton = (Button)findViewById(R.id.button_1MB);
    oneButton.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View v) {
			allocateMemory(1<<20);
			updateUsage();
			//showAvailableMemory();
		}
	});
    
    Button pushButton = (Button)findViewById(R.id.button2);
    pushButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			publicPolicy.push(vector);
			
		}
	});
    
    Button popButton = (Button)findViewById(R.id.button1);
    popButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Swappable next = publicPolicy.pop();
			next.swapOut(false);
			System.gc();
			updateUsage();
			//showAvailableMemory();
			
		}
	});
    
}
	
	


private void allocateMemory(int bytes)
{
	/**Will allocate bytes MB memory*/
	vector.add(new byte[bytes]);
}

private void updateUsage()
{
	Runtime info = Runtime.getRuntime(); 
	long max_memory   = info.maxMemory();
	long cur_memory   = (info.totalMemory() - info.freeMemory());
	
	VmUsage.setText("VM memory: "+pretty(cur_memory)+" / "+pretty(max_memory));
}

private static String pretty(long bytes) {
    if (bytes < 1024)
      return bytes+"B";
    if ((bytes>>10) < 1024)
      return (bytes>>10)+"KB";
    if ((bytes>>20) < 1024)
      return (bytes>>20)+"MB";
    return (bytes>>30)+"GB";
  }


private void showAvailableMemory() {
	ActivityManager activityManager =  (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	MemoryInfo memoryInfo = new MemoryInfo();
	activityManager.getMemoryInfo(memoryInfo);
	//Log.i("memory footprint", "" + memoryInfo.availMem / memoryInfo.totalMem + "%");
	Log.i("TOTAL MEMORY", "" + (MemUtil.getTotalMem() >> 20));
	//Log.i("AVAILABLE MEMORY", "" + (MemUtil.getAvailableMem() >> 20));
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
}	


}