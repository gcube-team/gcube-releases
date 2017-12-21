package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;

/**
 * The <code> Metadata </code> class represents a metadata that can be associated to any object
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2009 (1.4) 
 */
public class Metadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6451088310712823335L;
	/**
	 * 
	 */
	private String attribute;
	/**
	 * 
	 */
	private String value;

	/**
	 * 
	 */
	public Metadata() {
		super();
	}

	/**
	 * 
	 * @param attribute .
	 * @param value .
	 */
	public Metadata(String attribute, String value) {
		super();
		this.attribute = attribute;
		this.value = value;
	}

	/**
	 * 
	 * @return .
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * 
	 * @param attribute .
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * 
	 * @return .
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value .
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
