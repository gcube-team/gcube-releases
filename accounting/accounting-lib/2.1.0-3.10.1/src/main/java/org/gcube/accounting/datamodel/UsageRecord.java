package org.gcube.accounting.datamodel;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;

public interface UsageRecord extends Record {
	
	public enum OperationResult implements Serializable, Comparable<OperationResult> {
		SUCCESS, FAILED
	}
	
	/**
	 * KEY for : The Operation Result of the accounted operation.
	 * The value is expressed as 
	 * {@link #org.gcube.accounting.datamodel.UsageRecord.OperationResult}
	 */
	public static final String OPERATION_RESULT = "operationResult";
	
	/**
	 * KEY for : The user (or the Consumer Identity, that in the S2S 
	 * communication is another service).
	 */
	public static final String CONSUMER_ID = "consumerId";
	
	/**
	 * KEY for : The scope
	 */
	public static final String SCOPE = "scope";
	
	/**
	 * @return the Operation Result of the accounted operation.
	 */
	public OperationResult getOperationResult();
	
	/**
	 * Set the Operation Result related to the accounted Usage Record
	 * @param operationResult the Operation Result to set
	 * @throws InvalidValueException 
	 */
	public void setOperationResult(OperationResult operationResult) throws InvalidValueException;
	
	/**
	 * Return the user (or the Consumer Identity, that in the S2S 
	 * communication is another service)
	 * @return Consumer ID
	 */
	public String getConsumerId();

	/**
	 * Set the user (or the Consumer Identity, that in the S2S 
	 * communication is another service)
	 * @param consumerId Consumer ID
	 * @throws InvalidValueException
	 */
	public void setConsumerId(String consumerId) throws InvalidValueException;
	
	/**
	 * Return the scope of this {#UsageRecord} 
	 * @return The scope of this {#UsageRecord} 
	 */
	public String getScope();
	
	/**
	 * Set the scope of the {#UsageRecord} 
	 * @param scope the scope of the {#UsageRecord}
	 * @throws InvalidValueException
	 */
	public void setScope(String scope) throws InvalidValueException;
	
	
	

}