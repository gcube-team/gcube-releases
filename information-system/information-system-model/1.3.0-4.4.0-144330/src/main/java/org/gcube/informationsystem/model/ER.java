/**
 * 
 */
package org.gcube.informationsystem.model;

import org.gcube.informationsystem.model.embedded.Header;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Basic Interface for all Entity and Relations
 */
//@JsonDeserialize(as=ERImpl.class) Do not uncomment to manage subclasses
public interface ER extends ISManageable {

	public static final String NAME = "ER"; //ER.class.getSimpleName();
	
	public static final String HEADER_PROPERTY = "header";

	public Header getHeader();
	
	public void setHeader(Header header);
}
