package it.eng.edison.usersurvey_portlet.client.util;

public enum SurveyOptions {
	SELECT("Actions ..."),
	GET_LINK("Send to users"),
	MODIFY("Edit "),
	DELETE("Delete"),
	STATISTICS("Results");

	String label;
	SurveyOptions(String s) {
		label = s;
	}
	public String getDisplayLabel() {
		return label;
	} 

}
