package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * <code> SerializableAttributeArea </code> class 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version April 2011 (1.0) 
 */
public class AttributeArea implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2707392337121812931L;
	/**
	 * 
	 */
	private String attrName;
	/**
	 * 
	 */
	private ArrayList<Attribute> values;
	/**
	 * 
	 */
	public AttributeArea() {
		super();
	}
	/**
	 * 
	 * @param attrName .
	 * @param values .
	 */
	public AttributeArea(String attrName, ArrayList<Attribute> values) {
		super();
		this.attrName = attrName;
		this.values = values;
	}
	/**
	 * 
	 * @return name
	 */
	public String getAttrName() {
		return attrName;
	}
	/**
	 * 
	 * @param attrName n
	 */
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	/**
	 * 
	 * @return the list
	 */
	public ArrayList<Attribute> getValues() {
		return values;
	}
	/**
	 * 
	 * @param values -
	 */
	public void setValues(ArrayList<Attribute> values) {
		this.values = values;
	}
	
	@Override
	public String toString() {
		return "AttributeArea [attrName=" + attrName + ", values=" + values
				+ "]";
	}
	
}
