package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;

/**
 * Data type.
 * @see org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.DataType
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum DataTypeWrapper {

	String,
	Time,
	Time_Interval,
	Times_ListOf,
	Text,
	Boolean,
	Number,
	GeoJSON;

	/**
	 * Value as String.
	 * @return the string
	 */
	public String value() {
		return name();
	}
}
