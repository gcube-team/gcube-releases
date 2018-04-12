/**
 * 
 */
package org.gcube.portlets.user.tdwx.shared.model;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum ValueType implements Serializable {
	STRING, 
	INTEGER, 
	BOOLEAN, 
	DOUBLE, 
	LONG, 
	DATE,
	GEOMETRY;
}
