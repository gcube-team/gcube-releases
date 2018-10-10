/**
 * 
 */
package org.gcube.data.tr.requests;

import javax.xml.bind.annotation.XmlEnum;

/**
 * The access mode to a source.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlEnum
public enum Mode {

	READABLE,WRITABLE,FULLACESSS;
}
