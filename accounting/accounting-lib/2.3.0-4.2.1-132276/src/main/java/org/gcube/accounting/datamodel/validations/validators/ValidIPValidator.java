package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidIPValidator implements FieldAction {
	
	private static Logger logger = LoggerFactory.getLogger(ValidIPValidator.class);
	
	private static final String ERROR = "Not valid IP Address";
	
	private static Pattern IPV4_PATTERN = null;
	private static Pattern IPV6_PATTERN = null;
	
	private static final String ipv4Regex = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Regex = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";

	static {
		try {
			IPV4_PATTERN = Pattern.compile(ipv4Regex,
					Pattern.CASE_INSENSITIVE);
			IPV6_PATTERN = Pattern.compile(ipv6Regex,
					Pattern.CASE_INSENSITIVE);
		} catch(Exception e) {
			logger.error("Unable to compile pattern", e);
		}
	}

	protected static boolean isIpAddress(String ipAddress) {
		Matcher ipV4Matcher = IPV4_PATTERN.matcher(ipAddress);
		if (ipV4Matcher.matches()) {
			return true;
		}
		
		Matcher ipV6Matcher = IPV6_PATTERN.matcher(ipAddress);
		return ipV6Matcher.matches();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		try {
			if(isIpAddress((String) value)){
				return (String) value;
			}
		}catch (Exception e) {
			throw new InvalidValueException(ERROR, e);
		}
		throw new InvalidValueException(ERROR);
	}

}
