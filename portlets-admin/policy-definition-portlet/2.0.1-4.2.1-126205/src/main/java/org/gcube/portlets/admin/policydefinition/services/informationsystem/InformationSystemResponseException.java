package org.gcube.portlets.admin.policydefinition.services.informationsystem;

public class InformationSystemResponseException extends Exception {

	private static final long serialVersionUID = -8074119037509968140L;
	
	public InformationSystemResponseException(Throwable throwable) {
        super(throwable);
    }

	public InformationSystemResponseException(String message) {
        super(message);
    }

    public InformationSystemResponseException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
