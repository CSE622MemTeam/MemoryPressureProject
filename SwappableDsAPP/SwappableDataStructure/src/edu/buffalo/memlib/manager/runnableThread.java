package edu.buffalo.memlib.manager;

import android.util.Log;

public  class runnableThread implements Runnable{

	long time=1000;

	@Override
	public void run() {

		while(true)
		{	
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
					
			/**Un-comment to track memory*/
			//MemoryUtil.dumpString();
			MemoryUtil.updateMemoryStatus();

			try {
				Thread.sleep(time, 0);
			} catch (InterruptedException e) {
				Log.e("cse_622", "Thread could not sleep");
				e.printStackTrace();
			}
		}


	}


}
