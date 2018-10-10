package org.gcube.common.informationsystem.publisher.impl.utils;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;

/**
 * 
 * Helper Class
 * 
 * @author Manuele Simi
 *
 */
public class Helper {

	/**
	 * Converts an EndpontReferenceType object into a String object
	 * 
	 * @param epr the epr
	 * @return the string representation of the epr
	 */
	public static String EPR2String(EndpointReferenceType epr) {
		
		String ret = "";
		ret += epr.getAddress().toString();
		try {
		ReferencePropertiesType prop = epr.getProperties();
		if (prop != null) {
			MessageElement[] any = prop.get_any();
			if (any.length > 0) 
				ret += "/" + any[0].getValue();
		}
		} catch (NullPointerException npe) {
			//nothing to do, the caller is a singleton service
		}
		return ret;
	}

}
