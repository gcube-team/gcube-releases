package org.gcube.portlets.user.dataminermanager.client.computations;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public enum ComputationsPropertiesType {
	OPERATOR_NAME("operator_name"), START_DATE("start_date"), END_DATE(
			"end_date"), STATUS("status"), EXECUTION_TYPE("execution_platform"), VRE(
			"VRE");

	// COMPUTATION_ID("computation_id"),
	// OPERATOR_DESCRIPTION("operator_description"),
	// ERROR("error"),
	// EXECUTION_PLATFORM("execution_platform")
	/**
	 * @param text
	 */
	private ComputationsPropertiesType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

	public String getLabel() {
		return id;
	}

	public static ComputationsPropertiesType getFromId(String id) {
		for (ComputationsPropertiesType prop : values()) {
			if (prop.id.compareToIgnoreCase(id) == 0) {
				return prop;
			}
		}
		return null;
	}

}
