package org.gcube.common.core.informationsystem;

import org.gcube.common.core.informationsystem.client.ISClient;

/**
 * Generic exception raised by the {@link ISClient}. 
 *  
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 */
public class ISException extends Exception {
	private static final long serialVersionUID = 1L;
	public ISException() {
		super(); 
	}
	public ISException(String string) {
		super(string);
	}
	public ISException(Exception e) {
		super(e);
	}
}
