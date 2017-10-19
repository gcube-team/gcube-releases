package gr.cite.geoanalytics.util.http;

import org.springframework.http.HttpStatus;

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
		statusCode = 500;
	}

	public CustomException(String message,Throwable cause){
		super(message, cause);
		statusCode = 500;
	}		
	
	public CustomException(HttpStatus statusCode, String message) {
		super(message);
		this.statusCode = Integer.parseInt(statusCode.toString());
	}
	
	public CustomException(HttpStatus statusCode, String message, Exception e) {
		super(message, e);
		this.statusCode = Integer.parseInt(statusCode.toString());
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}

