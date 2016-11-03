/**
 * 
 */
package org.gcube.accounting.datamodel.basetypes;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.datamodel.backwardcompatibility.MoveToConsumerId;
import org.gcube.accounting.datamodel.deprecationmanagement.DeprecatedWarning;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.RequiredField;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmpty;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmptyIfNotNull;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class AbstractPortletUsageRecord extends BasicUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8339324883678974869L;
	
	@DeprecatedWarning @MoveToConsumerId
	protected static final String USER_ID = "userId";
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String giveMeRecordType() {
		return AbstractPortletUsageRecord.class.getSimpleName().replace(ABSTRACT_TO_REPLACE, "");
	}
	
	public String getPortletId() {
		return (String) this.resourceProperties.get(PORTLET_ID);
	}

	public void setPortletId(String portletId) throws InvalidValueException {
		setResourceProperty(PORTLET_ID, portletId);
	}
	
	public String getOperationId() {
		return (String) this.resourceProperties.get(OPERATION_ID);
	}

	public void setOperationId(String operationId) throws InvalidValueException {
		setResourceProperty(OPERATION_ID, operationId);
	}
	
	public String getMessage() {
		return (String) this.resourceProperties.get(MESSAGE);
	}

	public void setMessage(String message) throws InvalidValueException {
		setResourceProperty(MESSAGE, message);
	}
}
