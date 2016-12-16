/**
 * 
 */
package org.gcube.data.analysis.dataminermanagercl.shared.parameters;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public abstract class Parameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -555286289487491703L;

	protected String name;
	protected String description;
	protected ParameterType typology;
	protected String value;

	/**
	 * 
	 */
	public Parameter() {
		super();
	}

	
	/**
	 * @param name
	 * @param type
	 * @param description
	 * @param defaultValue
	 * @param value
	 */
	public Parameter(String name, ParameterType type, String description) {
		super();
		this.name = name;
		this.typology = type;
		this.description = description;
	}

	public void setValue(String value){
		this.value=value;
	}

	public String getValue(){
		return value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the typology
	 */
	public ParameterType getTypology() {
		return typology;
	}

	/**
	 * @param typology
	 *            the typology to set
	 */
	public void setTypology(ParameterType typology) {
		this.typology = typology;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Parameter [name=" + name + ", description=" + description
				+ ", typology=" + typology + ", value=" + value + "]";
	}

	

}
