package org.gcube.dataanalysis.statistical_manager_wps_algorithms.output;


public class StringOutputManagement extends OutputManagement<String> {

	public StringOutputManagement() {
		super();
	}
	@Override
	public String getFormattedOutput() {
		StringBuilder sb = new StringBuilder();
		sb.append(deleteCharacter() + "\n") ;

		return sb.toString();

	}
	
	public String deleteCharacter()
	{
		return value.replace("&", "").replace("[", "").replace("]", "");
	}
}
