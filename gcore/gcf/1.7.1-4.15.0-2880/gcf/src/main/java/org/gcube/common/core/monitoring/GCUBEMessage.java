package org.gcube.common.core.monitoring;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This class models a base GCUBEMessage 
 * 
 * @author Andrea Manzi (CERN)
 *
 */
public abstract class GCUBEMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	protected String topic;
	protected String sourceGHN;
	protected String time;
	private SimpleDateFormat sdf;
	protected String scope;
	
	
	/**
	 * Constructor
	 */
	public GCUBEMessage(){
		 sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		
	}
	/**
	 * Get the time the message has been created
	 * @return Time the message has been created
	 */
	public String getTime() {
		return time;
	}
	
	/**
	 * Set the message scope
	 * @param scope the message scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Get the message scope
	 * @return the message scope
	 */
	public String getScope() {
		return scope;
	}
	
	/**
	 * Set the message creation time 
	 * @param time String representation of the time
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * Result of the Monitoring message
	 */
	protected ResultType result;

	/**
	 * get the Result of the Monitoring message
	 * @return the result
	 */
	public ResultType getResult() {
		return result;
	}

	/**
	 * Set the result of the monitoring message
	 * 
	 * @param result the result
	 */
	public void setResult(ResultType result) {
		this.result = result;
	}

	/** Result types */
	public static enum ResultType {
    	OK("OK"),WARNING("WARNING"),CRITICAL("CRITICAL"),UNKNOWN("UNKNOWN");
    	String type;
    	ResultType(String type) {this.type = type;}
		public String toString() {return this.type;}
    };

    /**
     * Get the GHN name source of the message
     * @return the Source GHN
     */
	public String getSourceGHN() {
		return sourceGHN;
	}

	/**
	 * Set the GHNName source of the message
	 * @param sourceGHN the source GHN
	 */
	public void setSourceGHN(String sourceGHN) {
		this.sourceGHN = sourceGHN;
	}

	/**
	 * Get the message topic name
	 * @return the message topic name 
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Set the message topic name
	 * @param topic the topic name
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Set  message creation time to the current time
	 * 
	 */
	public void setTimeNow() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone(("GMT")));
		this.setTime(sdf.format(calendar.getTime()));
	}
}
