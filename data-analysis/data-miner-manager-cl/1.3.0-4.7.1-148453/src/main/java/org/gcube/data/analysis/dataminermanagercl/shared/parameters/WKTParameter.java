/**
 * 
 */
package org.gcube.data.analysis.dataminermanagercl.shared.parameters;

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
	 *            wkt geometry type
	 * @param defaultValue
	 *            default value
	 */
	public WKTParameter(String name, String description, WKTGeometryType wktGeometryType, String defaultValue) {
		super(name, ParameterType.WKT, description);
		this.wktGeometryType = wktGeometryType;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * 
	 * @return the WKT Geometry Type
	 */
	public WKTGeometryType getWktGeometryType() {
		return wktGeometryType;
	}

	/**
	 * 
	 * @param wktGeometryType
	 *            set the WKT Geometry Type
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
