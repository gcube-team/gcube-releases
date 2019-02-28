package org.gcube.datatransfer.scheduler.library.obj;

import java.util.Calendar;

import org.gcube.datatransfer.common.scheduler.Types.FrequencyType;



public class PeriodicallyScheduled{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//the startInstance is the point from when the schedule starts periodically..
	Calendar startInstance;
	
	//"dd.MM.yy-hh.mm"
	String startInstanceString; 
	
	//the frequency
	FrequencyType frequency;

	public PeriodicallyScheduled(){
		this.startInstance=null;
		this.startInstanceString=null;
		this.frequency=null;
	}
	
	public FrequencyType getFrequency() {
		return frequency;
	}
	public void setFrequency(FrequencyType frequency) {
		this.frequency = frequency;
	}
	public Calendar getStartInstance() {
		return startInstance;
	}
	public void setStartInstance(Calendar startInstance) {
		this.startInstance = startInstance;
	}

	public String getStartInstanceString() {
		return startInstanceString;
	}

	public void setStartInstanceString(String startInstanceString) {
		this.startInstanceString = startInstanceString;
	}
	
	
	
}
