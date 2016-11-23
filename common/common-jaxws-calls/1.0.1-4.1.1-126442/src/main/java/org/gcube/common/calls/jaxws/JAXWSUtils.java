package org.gcube.common.calls.jaxws;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Library-wide utilities.
 * 
 * @author Fabio Simeoni
 * 
 */
public class JAXWSUtils {

	@XmlRootElement
	public static class Empty{}
	
	public static final Empty empty = new Empty(); 
	
	static void notNull(String message,Object o) {
		if (o==null)
			throw new IllegalArgumentException(o+" cannot be null");
	}

}
