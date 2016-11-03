package org.gcube.portlets.admin.policydefinition.services.restful;

public class RestfulResponseException extends Exception {

	private static final long serialVersionUID = -8074119037509968140L;
	
	public RestfulResponseException(Throwable throwable) {
        super(throwable);
    }

	public RestfulResponseException(int statusCode, String message) {
        super("Status code '"+statusCode+"': "+message);
    }

    public RestfulResponseException(int statusCode, String message, Throwable throwable) {
        super("Status code '"+statusCode+"': "+message, throwable);
    }

}
