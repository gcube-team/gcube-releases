package gr.cite.gaap.datatransferobjects.plugin;


public class FunctionResponse {
	
	private String executionID;
	
	
	public FunctionResponse(String executionID){
		this.executionID = executionID;
	}
	
	public String getExecutionID() {
		return executionID;
	}

	public void setExecutionID(String executionID) {
		this.executionID = executionID;
	}

	
}
