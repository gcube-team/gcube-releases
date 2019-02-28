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
public class ReportElementData implements Serializable {

	private static final long serialVersionUID = -4942929709611742287L;
	private String label;
	private String category;
	private String xAxis;
	private String yAxis;

	private SeriesData[] serieses;

	public ReportElementData() {
		super();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getxAxis() {
		return xAxis;
	}

	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}

	public String getyAxis() {
		return yAxis;
	}

	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}

	public SeriesData[] getSerieses() {
		return serieses;
	}

	public void setSerieses(SeriesData[] serieses) {
		this.serieses = serieses;
	}

	@Override
	public String toString() {
		return "ReportElementData [label=" + label + ", category=" + category + ", xAxis=" + xAxis + ", yAxis=" + yAxis
				+ ", serieses=" + Arrays.toString(serieses) + "]";
	}

}
