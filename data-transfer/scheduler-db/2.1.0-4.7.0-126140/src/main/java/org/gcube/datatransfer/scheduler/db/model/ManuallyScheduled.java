package org.gcube.datatransfer.scheduler.db.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="MANUALLY_SCHEDULED")
public class ManuallyScheduled implements Serializable{

	private static final long serialVersionUID = -4127682520324924886L;

	@PrimaryKey	
	private String manuallyScheduledId;
	
	//public Calendar calendar;
	
	//"dd.MM.yy-HH.mm"
	public String  calendarString;
	
	public ManuallyScheduled(){
		//this.calendar=null;
		this.calendarString=null;
	}
	

	public String getManuallyScheduledId() {
		return manuallyScheduledId;
	}

	public void setManuallyScheduledId(String manuallyScheduledId) {
		this.manuallyScheduledId = manuallyScheduledId;
	}

	public String getCalendarString() {
		return calendarString;
	}

	public void setCalendarString(String calendarString) {
		this.calendarString = calendarString;
	}

	





}
