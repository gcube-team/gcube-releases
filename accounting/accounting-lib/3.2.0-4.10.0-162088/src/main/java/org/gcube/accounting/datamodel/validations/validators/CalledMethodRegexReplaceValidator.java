package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.gcube.accounting.datamodel.basetypes.AbstractServiceUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CalledMethodRegexReplaceValidator implements FieldAction {
	
	private static Logger logger = LoggerFactory.getLogger(CalledMethodRegexReplaceValidator.class);
	
	protected static final List<RegexReplace> regexReplaceList;
	
	public static List<RegexReplace> getRegexReplacelist() {
		return regexReplaceList;
	}

	public static RegexReplace addRegexReplace(String serviceClass, String serviceName, String regex, String replace) {
		RegexReplace regexReplace = new RegexReplace(serviceClass, serviceName, regex, replace);
		regexReplaceList.add(regexReplace);
		return regexReplace;
	}
	
	public static RegexReplace addRegexReplace(RegexReplace regexReplace) {
		regexReplaceList.add(regexReplace);
		return regexReplace;
	}
	
	static {
		regexReplaceList = new ArrayList<>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		
		if(!(record instanceof AbstractServiceUsageRecord)) {
			throw new RuntimeException(record.toString() + "is not an instace of " + AbstractServiceUsageRecord.class.getSimpleName());
		}
		
		if(!(value instanceof String)) {
			throw new InvalidValueException(value.toString() + "is not a " + String.class.getSimpleName());
		}
		
		String stringValue = (String) value;
		
		AbstractServiceUsageRecord serviceUsageRecord = (AbstractServiceUsageRecord) record;
		
		String serviceClass = serviceUsageRecord.getServiceClass();
		if(serviceClass==null) {
			logger.debug("{} is not already set. The check will be postponed to validation phase", AbstractServiceUsageRecord.SERVICE_CLASS);
			return value;
		}
		
		String serviceName = serviceUsageRecord.getServiceName();
		if(serviceName==null) {
			logger.debug("{} is not already set. The check will be postponed to validation phase", AbstractServiceUsageRecord.SERVICE_NAME);
			return value;
		}
		
		for(RegexReplace regexReplace : getRegexReplacelist()) {
			if(serviceClass.compareTo(regexReplace.getServiceClass())==0 && serviceName.compareTo(regexReplace.getServiceName())==0) {
				Matcher matcher = regexReplace.regexPattern.matcher(stringValue);
				if(matcher.matches()) {
					// TODO allow regex replace using matcher
					return regexReplace.getReplace();
				}
			}
		}
		
		return value;
	}

}
