package org.gcube.portlets.widgets.dataminermanagerwidget.client.dataspace;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public enum DataSpacePropertiesType {
	COMPUTATION_ID("computation_id"), DATA_DESCRIPTION("data_description"), DATA_TYPE(
			"data_type"), OPERATOR_NAME("operator_name"), VRE("VRE");
	/**
	 * @param text
	 */
	private DataSpacePropertiesType(final String id) {
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

	public static DataSpacePropertiesType getFromId(String id) {
		for (DataSpacePropertiesType prop : values()) {
			if (prop.id.compareToIgnoreCase(id) == 0) {
				return prop;
			}
		}
		return null;
	}

}
