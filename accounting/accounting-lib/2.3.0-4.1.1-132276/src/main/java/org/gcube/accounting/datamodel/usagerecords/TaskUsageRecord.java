package org.gcube.accounting.datamodel.usagerecords;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.basetypes.AbstractTaskUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;

public class TaskUsageRecord extends AbstractTaskUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 5053135599013854281L;

	public TaskUsageRecord(){
		super();
	}
	
	public TaskUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}
}
