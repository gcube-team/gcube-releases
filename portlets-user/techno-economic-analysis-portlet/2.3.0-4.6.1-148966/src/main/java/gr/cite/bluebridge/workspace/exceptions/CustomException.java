package gr.cite.bluebridge.workspace.exceptions;

public class CustomException extends Exception {

	private static final long serialVersionUID = 2265219993758336852L;
	private int statusCode;
	
	public CustomException(int statusCode, String message){
		super(message);
		this.statusCode = statusCode;
	}
	
	public CustomException(int statusCode, String message, Throwable cause){
		super(message, cause);
		this.statusCode = statusCode;
	}

	public CustomException(String message){
		super(message);
	}

	public CustomException(String message,Throwable cause){
		super(message, cause);
	}		
	
	public int getStatusCode() {
		return statusCode;
	}
}
