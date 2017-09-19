/**
 *
 */
package org.gcube.datatransfer.resolver;

import javax.servlet.ServletException;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 29, 2017
 */
public class BadRequestException extends ServletException {

	private static final long serialVersionUID = 567117691399245474L;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String arg0){
		super(arg0);
	}
}
