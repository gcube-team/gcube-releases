package org.gcube.datatransfer.scheduler.library.obj;




public class TypeOfSchedule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean directedScheduled; // if its direct there is no typeOfSchedule
	protected ManuallyScheduled manuallyScheduled;
	protected PeriodicallyScheduled periodicallyScheduled;

	
	public TypeOfSchedule(){		
		this.directedScheduled=false;
		this.manuallyScheduled=null;
		this.periodicallyScheduled=null;		
	}
	
	public boolean isDirectedScheduled() {
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

	
}
