package org.gcube.informationsystem.notifier.impl.entities;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.informationsystem.notifier.util.EPR;

public class Producer extends EPR {

	private boolean isRegistrationFinished;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Producer(EndpointReferenceType epr){
		super(epr);
		this.isRegistrationFinished=false;
	}
	
	public synchronized void setSubscriptionFinished(){this.isRegistrationFinished=true;}
	
	public boolean isSubscribed(){return this.isRegistrationFinished;}
	

}
