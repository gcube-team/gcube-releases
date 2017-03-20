package org.gcube.accounting.datamodel.usagerecords;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.basetypes.AbstractServiceUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ServiceUsageRecord extends AbstractServiceUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1941140440484309668L;

	public ServiceUsageRecord(){
		super();
	}
	
	public ServiceUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}
}
