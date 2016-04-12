package org.gcube.datatransfer.portlets.user.shared.obj;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;



public class PeriodicallyScheduled implements JsonSerializable{

	//the startInstance is the point from when the schedule starts periodically..
	//"dd.MM.yy-HH.mm"
	String startInstanceString; 
	
	//the frequency
	String frequency;

	public PeriodicallyScheduled(){
		this.startInstanceString="";
		this.frequency="";
	}
	

	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getStartInstanceString() {
		return startInstanceString;
	}

	public void setStartInstanceString(String startInstanceString) {
		this.startInstanceString = startInstanceString;
	}
	

	
}
