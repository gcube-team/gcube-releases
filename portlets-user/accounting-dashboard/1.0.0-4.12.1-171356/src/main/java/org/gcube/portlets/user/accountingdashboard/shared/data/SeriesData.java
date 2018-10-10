package org.gcube.portlets.user.accountingdashboard.shared.data;

import java.io.Serializable;
import java.util.Arrays;

import jsinterop.annotations.JsType;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
@JsType
public class SeriesData implements Serializable {

	private static final long serialVersionUID = 3308676516412447011L;
	private String label;
	private RecordData[] dataRow;

	public SeriesData() {
		super();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public RecordData[] getDataRow() {
		return dataRow;
	}

	public void setDataRow(RecordData[] dataRow) {
		this.dataRow = dataRow;
	}

	@Override
	public String toString() {
		return "SeriesData [label=" + label + ", dataRow=" + Arrays.toString(dataRow) + "]";
	}

}
