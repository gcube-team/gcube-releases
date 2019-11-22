package org.gcube.portlets.user.workspace.client.util;

import com.google.gwt.user.client.Timer;

public class CountTimer {
	
	public int time;
	Timer timer;
	
	public CountTimer(int milliseconds) {
		
		time = 0;
		
		timer = new Timer() {
	        @Override
	        public void run()
	        {
	      	  time++;
	        }
	    };
	    
	    timer.scheduleRepeating(milliseconds);
	}
	
	
	public int getTime(){
		timer.cancel();
		return time;
	}

	
}
