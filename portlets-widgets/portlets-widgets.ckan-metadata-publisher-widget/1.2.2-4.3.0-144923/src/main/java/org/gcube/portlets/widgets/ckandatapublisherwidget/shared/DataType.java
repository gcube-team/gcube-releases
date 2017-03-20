package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

/**
 * The Enum DataType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 12, 2016
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
