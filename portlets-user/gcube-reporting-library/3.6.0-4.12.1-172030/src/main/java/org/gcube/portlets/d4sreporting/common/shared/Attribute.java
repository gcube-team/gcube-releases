 package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
/**
 * <code> Attribute </code> class 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class Attribute implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3782430443598241309L;
	/**
	 * 	
	 */
	private String name;
	/**
	 * 
	 */
	private Boolean value;
	/**
	 * 	
	 */
	private String optionalValue;
	/**
	 * 
	 */
	public Attribute() {
		super();
	}
	/**
	 * 
	 * @param name
	 * @param value
	 */
	public Attribute(String name, Boolean value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public Attribute(String name, Boolean value, String optionalValue) {
		super();
		this.name = name;
		this.value = value;
		this.optionalValue = optionalValue;
	}
	/**
	 * 
	 * @return .
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name .
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return .
	 */
	public Boolean getValue() {
		return value;
	}
	/**
	 * 
	 * @param value .
	 */
	public void setValue(Boolean value) {
		this.value = value;
	}
	
	public String getOptionalValue() {
		return optionalValue;
	}
	public void setOptionalValue(String optionalValue) {
		this.optionalValue = optionalValue;
	}
	
	@Override
	public String toString() {
		return "Attribute [name=" + name + ", value=" + value
				+ ", optionalValue=" + optionalValue + "]";
	}
	
	
}
