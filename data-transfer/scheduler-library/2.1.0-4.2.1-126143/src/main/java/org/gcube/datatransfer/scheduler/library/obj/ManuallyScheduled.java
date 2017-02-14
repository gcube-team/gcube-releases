package org.gcube.datatransfer.scheduler.library.obj;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ManuallyScheduled{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//DateFormat formatter = new SimpleDateFormat("dd.MM.yy-hh.mm");
	//"dd.MM.yy-hh.mm"
	String instanceString; 
	
	public Calendar calendar;
	
	public ManuallyScheduled(){
		this.instanceString=null;
		this.calendar=null;
	}	
	
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	public String getInstanceString() {
		return instanceString;
	}
	public void setInstanceString(String instanceString) {
		this.instanceString = instanceString;
	}



}
