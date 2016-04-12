package org.gcube.datatransfer.portlets.user.shared.obj;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;




public class TypeOfSchedule implements JsonSerializable{
	
	protected boolean directedScheduled; // if its direct there is no typeOfSchedule
	protected ManuallyScheduled manuallyScheduled;
	protected PeriodicallyScheduled periodicallyScheduled;

	
	public TypeOfSchedule(){		
		this.directedScheduled=false;
		this.manuallyScheduled=new ManuallyScheduled();
		this.periodicallyScheduled=new PeriodicallyScheduled();		
	}
	
	public boolean getDirectedScheduled() {
		return directedScheduled;
	}
	public void setDirectedScheduled(boolean directedScheduled) {
		this.directedScheduled = directedScheduled;
	}


	public ManuallyScheduled getManuallyScheduled() {
		return manuallyScheduled;
	}
	public void setManuallyScheduled(ManuallyScheduled manuallyScheduled) {
		this.manuallyScheduled = manuallyScheduled;
	}


	public PeriodicallyScheduled getPeriodicallyScheduled() {
		return periodicallyScheduled;
	}
	public void setPeriodicallyScheduled(PeriodicallyScheduled periodicallyScheduled) {
		this.periodicallyScheduled = periodicallyScheduled;
	}
	 @Override
	    public String toString() {
	        StringBuffer buffer = new StringBuffer();
	        buffer.append("{");
	        buffer.append("directedScheduled:");
	        buffer.append(directedScheduled + ",");
	        buffer.append("manuallyScheduled:");
	        buffer.append(manuallyScheduled + ",");
	        buffer.append("periodicallyScheduled:");
	        buffer.append(periodicallyScheduled + ",");
	        buffer.append("}");
	        return buffer.toString();
	    }
	
}
