package com.buffalo.edu.mempressureTest;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;



public class MainActivity extends ActionBarActivity {

	public static long MemIncrement;
	public static int timeInterval;
	public static List<byte[]> dummyList= new LinkedList<byte[]>();
	public static long maxIterataions = 0;
	public static long curIterataions = 0;
	public static Runtime info = Runtime.getRuntime();
	public long max = info.maxMemory();
	
	String a, b;
	
	boolean throughConsole;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	Intent intent = getIntent();
	Bundle bundle = intent.getExtras();
	
	if(bundle !=null)
	{
		MemIncrement = Integer.parseInt(bundle.getString("-m", "10"));
		timeInterval = Integer.parseInt(bundle.getString("-t", "10"));
	
		MemIncrement = MemIncrement*1000000; //increment
		timeInterval = timeInterval*1000; //miliseconds
		
	}
	
	/**Command line parameters through ADB*/
	if(MemIncrement==0 || timeInterval==0)
			throughConsole = false;
	else
			throughConsole = true;
	
	if(throughConsole)
	{
		maxIterataions = max/MemIncrement;
	
		Log.e("MemPressureTest", "Increments= "+maxIterataions+" Interval = " + timeInterval);
		Log.e("MaxMemory", "Max Memory is "+max);
	
		/**Start thread*/
		Thread thread = new Thread(new runThis());
	
		thread.setDaemon(true);
		thread.start();
		
	}
	}
}
