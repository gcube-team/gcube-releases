package it.eng.edison.usersurvey_portlet.client.util;

public enum SurveyOptions {
	SELECT("Actions ..."),
	GET_LINK("Invite users to take this survey"),
	MODIFY("Edit Survey"),
	DELETE("Delete"),
	STATISTICS("Statistics");

	String label;
	SurveyOptions(String s) {
		label = s;
	}
	public String getDisplayLabel() {
		return label;
	} 

}
