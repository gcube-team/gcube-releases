/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class WKTParameter extends Parameter {

	private static final long serialVersionUID = 1673874854501249519L;
	private WKTGeometryType wktGeometryType;
	private String defaultValue;

	/**
	 * 
	 */
	public WKTParameter() {
		super();
		this.typology = ParameterType.WKT;
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param wktGeometryType
	 *            WKT Geometry type
	 * @param defaultValue
	 *            default value
	 */
	public WKTParameter(String name, String description, WKTGeometryType wktGeometryType, String defaultValue) {
		super(name, ParameterType.WKT, description);
		this.wktGeometryType = wktGeometryType;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the default value to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * 
	 * @return the WKT Geometry type
	 */
	public WKTGeometryType getWktGeometryType() {
		return wktGeometryType;
	}

	/**
	 * 
	 * @param wktGeometryType
	 *            set the WKT Geometry type
	 */
	public void setWktGeometryType(WKTGeometryType wktGeometryType) {
		this.wktGeometryType = wktGeometryType;
	}

	@Override
	public String toString() {
		return "WKTParameter [wktGeometryType=" + wktGeometryType + ", defaultValue=" + defaultValue + ", value="
				+ value + ", name=" + name + ", description=" + description + ", typology=" + typology + "]";
	}

}
