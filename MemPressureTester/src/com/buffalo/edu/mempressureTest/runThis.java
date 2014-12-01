package com.buffalo.edu.mempressureTest;


public class runThis implements Runnable{

	@Override
	public void run() {
		 
			try {		
					
				while(MainActivity.curIterataions<(MainActivity.maxIterataions)-1)
				{	
					MainActivity.dummyList.add(new byte[(int) MainActivity.MemIncrement]);
				    MainActivity.curIterataions += 1;
				
				    Thread.sleep(MainActivity.timeInterval, 0);
				}	
				
			} catch (InterruptedException e) {
				/**Do nothing*/
				e.printStackTrace();
			}			
		
	}
	

}
