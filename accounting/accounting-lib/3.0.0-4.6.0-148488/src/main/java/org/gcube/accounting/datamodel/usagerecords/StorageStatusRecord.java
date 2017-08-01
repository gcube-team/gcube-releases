package org.gcube.accounting.datamodel.usagerecords;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.basetypes.AbstractStorageStatusRecord;
import org.gcube.documentstore.exception.InvalidValueException;

import com.fasterxml.jackson.annotation.JsonTypeName;
/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 *
 */
@JsonTypeName(value="NotAggregateStorageStatusRecord")
public class StorageStatusRecord extends AbstractStorageStatusRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8174719617939936365L;
	
	public StorageStatusRecord() {
		super();
	}
	
	public StorageStatusRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}
}
