package edu.buffalo.memlib.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

public final class FileOperations {
	
	public static String sd_path;
	public static int    pid= 0;
	public static String file_path=null;
	
	public FileOperations()
	{
		
	}
		
	/**creates a time-stamped csv file. Call before starting the thread*/
	public static Boolean createFile()
	{
		SimpleDateFormat fileDate = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.US);
		
		String timeStamp = fileDate.format(new Date());
		
		pid       = android.os.Process.myPid();
		sd_path   = Environment.getExternalStorageDirectory().toString();		
		file_path = sd_path+"/memory_usage_"+timeStamp+".csv";
		File file = new File(file_path);
		
		if(!file.exists())
		{
			try {
				file.createNewFile();
				Log.d("cse622_debug", "Created a file /mnt/sdcard/");
			} catch (IOException e) {
				Log.e("cse622_error", "Cannot create file memory_usage");
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	/**Writes the column headers for the csv file.Call before starting the thread*/
	public static Boolean writeHeaders()
	{   
		String Headers = "PID, VMRss, MemFree, MemTotal, FreePages, oomValue, curMem(in bytes), state\n";
		
		try {
			FileOperations.write(Headers);
		} catch (IOException e) {
			Log.e("cse622_error", "Cannot write to file memory_usage");
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	/**Write anything to the csv file*/
	public static Boolean write(String content) throws IOException
	{
		File file        = new File(file_path);	
			
		try {
			 FileWriter filewriter = new FileWriter(file.getAbsoluteFile(),true);
			 BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
			 
			 bufferedwriter.write(content);
			 
			 bufferedwriter.close();
			 return true;
			 
		} catch (IOException e) {
			Log.e("cse622_error", "Cannot Open file memory_usage");
			e.printStackTrace();
			return false;
		}
		
		
	}
	
}
