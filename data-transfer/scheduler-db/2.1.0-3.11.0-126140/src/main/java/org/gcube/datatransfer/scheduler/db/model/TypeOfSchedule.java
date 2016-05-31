package org.gcube.datatransfer.scheduler.db.model;



import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="TYPE_OF_SCHEDULE")
public class TypeOfSchedule implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3191765061718224983L;

	@PrimaryKey	
	private String TypeOfScheduleId;
	
	protected boolean directedScheduled; // if its direct there is no typeOfSchedule
	protected String manuallyScheduledId;
	protected String periodicallyScheduledId;

	
	public TypeOfSchedule(){		
		this.directedScheduled=false;
		this.manuallyScheduledId=null;
		this.periodicallyScheduledId=null;		
	}

	
	public boolean isDirectedScheduled() {
		return directedScheduled;
	}
	public void setDirectedScheduled(boolean directedScheduled) {
		this.directedScheduled = directedScheduled;
	}

	public String getManuallyScheduledId() {
		return manuallyScheduledId;
	}
	public void setManuallyScheduledId(String manuallyScheduledId) {
		this.manuallyScheduledId = manuallyScheduledId;
	}

	public String getPeriodicallyScheduledId() {
		return periodicallyScheduledId;
	}


	public void setPeriodicallyScheduledId(String periodicallyScheduledId) {
		this.periodicallyScheduledId = periodicallyScheduledId;
	}


	public String getTypeOfScheduleId() {
		return TypeOfScheduleId;
	}
	public void setTypeOfScheduleId(String typeOfScheduleId) {
		TypeOfScheduleId = typeOfScheduleId;
	}

	
}
