package org.gcube.accounting.datamodel.usagerecords;

import java.io.Serializable;
import java.util.Map;

import org.gcube.accounting.datamodel.basetypes.AbstractPortletUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;

public class PortletUsageRecord extends AbstractPortletUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -6639325322297348702L;

	public PortletUsageRecord(){
		super();
	}
	
	public PortletUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}
	
}
