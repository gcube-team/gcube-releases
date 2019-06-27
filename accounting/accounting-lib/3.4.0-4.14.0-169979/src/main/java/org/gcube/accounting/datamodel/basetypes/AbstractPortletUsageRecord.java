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
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmptyIfNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class AbstractPortletUsageRecord extends BasicUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8339324883678974869L;
	
	//@DeprecatedWarning @MoveToConsumerId
	//protected static final String USER_ID = "userId";
	
	@RequiredField @NotEmpty
	public static final String PORTLET_ID = "portletId";
	@RequiredField @NotEmpty
	public static final String OPERATION_ID = "operationId";
	@NotEmptyIfNotNull
	public static final String MESSAGE = "message";
	
	public AbstractPortletUsageRecord(){
		super();
	}
	
	public AbstractPortletUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}
	
	private static final String ABSTRACT_TO_REPLACE = "Abstract";
	
	@Override
	public String getRecordType() {
		return AbstractPortletUsageRecord.class.getSimpleName().replace(ABSTRACT_TO_REPLACE, "");
	}
	
	@JsonIgnore
	public String getPortletId() {
		return (String) this.resourceProperties.get(PORTLET_ID);
	}
	
	public void setPortletId(String portletId) throws InvalidValueException {
		setResourceProperty(PORTLET_ID, portletId);
	}
	
	@JsonIgnore
	public String getOperationId() {
		return (String) this.resourceProperties.get(OPERATION_ID);
	}

	public void setOperationId(String operationId) throws InvalidValueException {
		setResourceProperty(OPERATION_ID, operationId);
	}
	
	@JsonIgnore
	public String getMessage() {
		return (String) this.resourceProperties.get(MESSAGE);
	}

	public void setMessage(String message) throws InvalidValueException {
		setResourceProperty(MESSAGE, message);
	}

}
