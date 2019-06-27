package org.gcube.portlets.user.performfishanalytics.client.annualcontrollers;

import java.util.List;

import org.gcube.portlets.user.performfishanalytics.client.DataMinerAlgorithms;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;


/**
 * The Class DataMinerInputParameters.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 8, 2019
 */
public class DataMinerInputParameters {
	private PerformFishResponse performFishResponse;
	private List<String> selectedYears;
	private List<KPI> inputKPI;
	private List<KPI> outputKPI;
	private DataMinerAlgorithms chartType;
	private String focusID;
	
	
	public DataMinerInputParameters() {
	}


	public DataMinerInputParameters(PerformFishResponse performFishResponse, List<String> selectedYears,
			List<KPI> inputKPI, List<KPI> outputKPI, DataMinerAlgorithms chartType, String focusID) {
		super();
		this.performFishResponse = performFishResponse;
		this.selectedYears = selectedYears;
		this.inputKPI = inputKPI;
		this.outputKPI = outputKPI;
		this.chartType = chartType;
		this.focusID = focusID;
	}


	public PerformFishResponse getPerformFishResponse() {
		return performFishResponse;
	}


	public void setPerformFishResponse(PerformFishResponse performFishResponse) {
		this.performFishResponse = performFishResponse;
	}


	public List<String> getSelectedYears() {
		return selectedYears;
	}


	public void setSelectedYears(List<String> selectedYears) {
		this.selectedYears = selectedYears;
	}


	public List<KPI> getInputKPI() {
		return inputKPI;
	}


	public void setInputKPI(List<KPI> inputKPI) {
		this.inputKPI = inputKPI;
	}


	public List<KPI> getOutputKPI() {
		return outputKPI;
	}


	public void setOutputKPI(List<KPI> outputKPI) {
		this.outputKPI = outputKPI;
	}


	public DataMinerAlgorithms getChartType() {
		return chartType;
	}


	public void setChartType(DataMinerAlgorithms chartType) {
		this.chartType = chartType;
	}


	public String getFocusID() {
		return focusID;
	}


	public void setFocusID(String focusID) {
		this.focusID = focusID;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataMinerInputParameters [performFishResponse=");
		builder.append(performFishResponse);
		builder.append(", selectedYears=");
		builder.append(selectedYears);
		builder.append(", inputKPI=");
		builder.append(inputKPI);
		builder.append(", outputKPI=");
		builder.append(outputKPI);
		builder.append(", chartType=");
		builder.append(chartType);
		builder.append(", focusID=");
		builder.append(focusID);
		builder.append("]");
		return builder.toString();
	}
	
	
}
