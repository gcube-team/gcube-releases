package org.gcube.datatransfer.portlets.user.shared.obj;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;

public class ManuallyScheduled implements JsonSerializable{
	

	//DateFormat formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
	//"dd.MM.yy-HH.mm"
	String instanceString; 
		
	public ManuallyScheduled(){
		this.instanceString="";
	}	

	public String getInstanceString() {
		return instanceString;
	}
	public void setInstanceString(String instanceString) {
		this.instanceString = instanceString;
	}


}
