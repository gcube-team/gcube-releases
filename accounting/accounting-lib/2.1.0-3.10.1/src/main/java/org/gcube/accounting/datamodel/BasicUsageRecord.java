/**
 * 
 */
package org.gcube.accounting.datamodel;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.backwardcompatibility.MoveToRecordType;
import org.gcube.accounting.datamodel.validations.annotations.ValidOperationResult;
import org.gcube.accounting.datamodel.validations.validators.ValidOperationResultValidator;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.AbstractRecord;
import org.gcube.documentstore.records.implementation.RequiredField;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class BasicUsageRecord extends AbstractRecord implements UsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -2060728578456796388L;
	
	private static Logger logger = LoggerFactory.getLogger(BasicUsageRecord.class);

	@Deprecated @MoveToRecordType
	private static final String USAGE_RECORD_TYPE = "usageRecordType";
	
	@RequiredField @NotEmpty
	public static final String CONSUMER_ID = UsageRecord.CONSUMER_ID;
	
	@RequiredField @NotEmpty
	public static final String SCOPE = UsageRecord.SCOPE;
	
	@RequiredField @ValidOperationResult
	public static final String OPERATION_RESULT = UsageRecord.OPERATION_RESULT;
	
	/*
	public static String getScopeFromToken(){
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String scope = authorizationEntry.getContext();
		return scope;
	}
	*/
	
	/**
	 * Initialize variable
	 */
	protected void init() {
		super.init();
		// Backward compatibility
		try {
			//this.setScope(getScopeFromToken());
			this.setScope(ScopeProvider.instance.get());
		} catch(Exception e) {
			logger.warn("Unable to automaticcally set the scope using scope provider. The record will not be valid if the scope will not be explicitly set.");
		}
	}
	
	public BasicUsageRecord(){
		super();
	}

	public BasicUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConsumerId() {
		return (String) this.resourceProperties.get(CONSUMER_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConsumerId(String consumerId) throws InvalidValueException {
		setResourceProperty(CONSUMER_ID, consumerId);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getScope() {
		return (String) this.resourceProperties.get(SCOPE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScope(String scope) throws InvalidValueException {
		setResourceProperty(SCOPE, scope);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OperationResult getOperationResult(){
		try {
			return (OperationResult) new ValidOperationResultValidator().
					validate(OPERATION_RESULT, this.resourceProperties.get(OPERATION_RESULT), null);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws InvalidValueException 
	 */
	@Override
	public void setOperationResult(OperationResult operationResult) throws InvalidValueException {
		setResourceProperty(OPERATION_RESULT, operationResult);
	}

}
