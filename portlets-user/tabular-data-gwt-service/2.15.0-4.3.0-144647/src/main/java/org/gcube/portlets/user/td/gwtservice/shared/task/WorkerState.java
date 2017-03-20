package org.gcube.portlets.user.td.gwtservice.shared.task;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public enum WorkerState {

	PENDING("Pending"), 
	INITIALIZING("Initializing"), 
	VALIDATING_DATA("Validating Data"), 
	IN_PROGRESS("In Progress"), 
	SUCCEDED("Succeded"), 
	FAILED("Failed"),
	ABORTED("Aborted");

	/**
	 * @param text
	 */
	private WorkerState(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}
	
	
	public static WorkerState get(String state) {
		for(WorkerState ws:values()){
			if(ws.id.compareTo(state)==0){
				return ws;
			}
		}
		
		return WorkerState.FAILED;
	}
	

}
