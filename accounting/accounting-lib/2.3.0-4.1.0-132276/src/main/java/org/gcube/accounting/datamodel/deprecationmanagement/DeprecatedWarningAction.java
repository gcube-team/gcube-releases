/**
 * 
 */
package org.gcube.accounting.datamodel.deprecationmanagement;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class DeprecatedWarningAction implements FieldAction {
	
	private static Logger logger = LoggerFactory.getLogger(DeprecatedWarningAction.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException  {
		logger.trace("The field {} is deprecated for {}. Anyway the field will be included in the SingleUsageRecord. The field can be lost during aggregation.", 
				key, record.getClass().getSimpleName());
		return value;
	}
	
}
