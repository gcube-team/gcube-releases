/**
 * 
 */
package org.gcube.accounting.datamodel.basetypes;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.RequiredField;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmpty;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class AbstractJobUsageRecord extends BasicUsageRecord {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8648691183939346858L;
	
	
	/**
	 * KEY for : hostname:port of the Hosting Node receiving the service call
	 */
	@RequiredField @NotEmpty
	public static final String HOST = "host";
	
	/**
	 * KEY for : Duration
	 */
	@RequiredField @ValidLong
	public static final String DURATION = "duration";
	
	
	/**
	 * KEY for : Service Class
	 */
	@RequiredField @NotEmpty
	public static final String SERVICE_CLASS = "serviceClass";
	
	/**
	 * KEY for : Service Class
	 */
	@RequiredField @NotEmpty
	public static final String SERVICE_NAME = "serviceName";
	
	/**
	 * KEY for : Job Name
	 */
	@RequiredField
	public static final String JOB_NAME = "jobName";
	
	/**
	 * KEY for : callerQualifier
	 * 
	 */
	@RequiredField @NotEmpty
	public static final String CALLER_QUALIFIER = "callerQualifier";
	
	
	public AbstractJobUsageRecord(){
		super();
	}
	
	public AbstractJobUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}
	
	private static final String ABSTRACT_TO_REPLACE = "Abstract";
	
	@Override
	public String getRecordType() {
		return AbstractJobUsageRecord.class.getSimpleName().replace(ABSTRACT_TO_REPLACE, "");
	}

	@JsonIgnore
	public String getJobName() {
		return (String) this.resourceProperties.get(JOB_NAME);
	}

	public void setJobName(String jobName) throws InvalidValueException {
		setResourceProperty(JOB_NAME, jobName);
	}
	
	@JsonIgnore
	public String getHost() {
		return (String) this.resourceProperties.get(HOST);
	}

	public void setHost(String host) throws InvalidValueException {
		setResourceProperty(HOST, host);
	}
	
	@JsonIgnore
	public String getServiceClass() {
		return (String) this.resourceProperties.get(SERVICE_CLASS);
	}

	public void setServiceClass(String serviceClass) throws InvalidValueException {
		setResourceProperty(SERVICE_CLASS, serviceClass);
	}
	
	@JsonIgnore
	public String getServiceName() {
		return (String) this.resourceProperties.get(SERVICE_NAME);
	}

	public void setServiceName(String serviceName) throws InvalidValueException {
		setResourceProperty(SERVICE_NAME, serviceName);
	}
	
	@JsonIgnore
	public Long getDuration() {
		return (Long) this.resourceProperties.get(DURATION);
	}

	public void setDuration(Long duration) throws InvalidValueException {
		setResourceProperty(DURATION, duration);
	}
	
	@JsonIgnore
	public String getCallerQualifier() {
		return (String) this.resourceProperties.get(CALLER_QUALIFIER);
	}
	
	public void setCallerQualifier(String callerQualifier) throws InvalidValueException {
		setResourceProperty(CALLER_QUALIFIER, callerQualifier);
	}

}
