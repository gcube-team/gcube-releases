package org.gcube.datatransfer.scheduler.db.model;

import java.util.Calendar;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType;

@PersistenceCapable(table="PERIODICALLY_SCHEDULED")
public class PeriodicallyScheduled implements java.io.Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4097772356378284178L;

	@PrimaryKey	
	private String periodicallyScheduledId;
	
	//the startInstance is the point from when the schedule starts periodically..
	//Calendar startInstance;
	
	//"dd.MM.yy-HH.mm"
	String startInstanceString;
	//the frequency
	FrequencyType frequency;

	public PeriodicallyScheduled(){
	//	this.startInstance=null;
		this.frequency=null;
		this.startInstanceString=null;
	}
	
	public FrequencyType getFrequency() {
		return frequency;
	}
	public void setFrequency(FrequencyType frequency) {
		this.frequency = frequency;
	}

	public String getPeriodicallyScheduledId() {
		return periodicallyScheduledId;
	}
	public void setPeriodicallyScheduledId(String periodicallyScheduledId) {
		this.periodicallyScheduledId = periodicallyScheduledId;
	}

	public String getStartInstanceString() {
		return startInstanceString;
	}

	public void setStartInstanceString(String startInstanceString) {
		this.startInstanceString = startInstanceString;
	}


	
	
	
}
