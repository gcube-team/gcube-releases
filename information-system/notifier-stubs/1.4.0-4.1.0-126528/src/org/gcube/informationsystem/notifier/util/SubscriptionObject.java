package org.gcube.informationsystem.notifier.util;

import java.io.Serializable;

import javax.xml.namespace.QName;


public abstract class SubscriptionObject<PRODUCER extends EPR,CONSUMER extends EPR> implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1618005678297481849L;
	
	private CONSUMER consumer;
	private PRODUCER producer;
	private  EPR notificationResourceEnpoint;
	
	private boolean subscribed;
	
	public CONSUMER getConsumer(){
		return this.consumer;
	}
	
	public PRODUCER getProducer(){
		return this.producer;
	}
	
	public void setConsumer(CONSUMER consumer){
		this.consumer= consumer;
	}
	
	public void setProducer(PRODUCER producer){
		this.producer= producer;
	}
	
	public EPR getNotificationResourceEnpoint(){
		return this.notificationResourceEnpoint;
	}
	
	public void setNotificationResourceEnpoint(EPR notificationResourceEndpoint){
		this.notificationResourceEnpoint=notificationResourceEndpoint;
	}
	
	public boolean isSubscribed(){
		return this.subscribed;
	}
	
	public void setSubscribed(boolean subscribed){
		this.subscribed= subscribed;
	}
	
	
	public abstract void subscribe(QName topic) throws Exception;
		
		
	
	public abstract void removeSubscription() throws Exception;
	
}
