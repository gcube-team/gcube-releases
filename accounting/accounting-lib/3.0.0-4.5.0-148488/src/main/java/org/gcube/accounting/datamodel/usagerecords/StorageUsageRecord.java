package org.gcube.accounting.datamodel.usagerecords;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value="NotAggregateStorageUsageRecord")
public class StorageUsageRecord extends AbstractStorageUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8174719617939936365L;
	
	public StorageUsageRecord() {
		super();
	}
	
	public StorageUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}

}
