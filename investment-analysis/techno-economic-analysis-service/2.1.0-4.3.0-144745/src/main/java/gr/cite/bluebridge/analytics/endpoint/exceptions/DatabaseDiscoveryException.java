package gr.cite.bluebridge.analytics.endpoint.exceptions;

public class DatabaseDiscoveryException extends Exception {
	private static final long serialVersionUID = -702769990521199881L;
	
	public DatabaseDiscoveryException(){
		super("Database Endpoint could not be discovered");
	}
	
	public DatabaseDiscoveryException(String Message){
		super(Message);
	}

	public DatabaseDiscoveryException(String Message,Throwable cause){
		super(Message,cause);
	}
}
