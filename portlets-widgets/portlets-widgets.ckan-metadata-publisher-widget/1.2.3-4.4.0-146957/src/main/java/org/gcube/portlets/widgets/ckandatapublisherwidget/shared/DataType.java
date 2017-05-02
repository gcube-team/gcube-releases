package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

/**
 * Data type.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum DataType {

	String,
	Time,
	Time_Interval,
	Times_ListOf,
	Text,
	Boolean,
	Number;

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return name();
    }
}
