package org.gcube.accounting.datamodel.usagerecords;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.basetypes.AbstractJobUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class JobUsageRecord extends AbstractJobUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 5589190770192442225L;

	public JobUsageRecord(){
		super();
	}
	
	public JobUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}
}
