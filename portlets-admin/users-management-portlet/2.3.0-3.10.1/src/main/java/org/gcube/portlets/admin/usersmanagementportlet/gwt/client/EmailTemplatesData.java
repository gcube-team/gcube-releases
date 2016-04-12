package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.HashMap;

public class EmailTemplatesData {

	private HashMap<String, String> templates = new HashMap<String, String>();
	
	public EmailTemplatesData() {
		super();
		templates.put("Start of Ecosystem Downtime", "This is a downtime announcement for the D4Science Ecosystem.<br>" + 

				 "<ul><li> Affected Scopes: <b>X</b></li>  <li>Duration: <b>X</b></li> <li>Motivation: <b>X</b></li></ul><br>" +
				 "We will keep you informed. Thanks for your comprehension.<br><br>Best Regards,<br>the Service Activity Team <br><br>" +
				 "You have received this message because you are a member of <a href=\"http://portal.d4science.research-infrastructures.eu\">D4Science portal</a> .");
		
		
		templates.put("End of Ecosystem Downtime", "The scheduled downtime is now completed.<br>" +

				 "All VOs and VREs are again accessible.<br><br>" +
				 "Best Regards,<br>the Service Activity Team  <br><br>" +
				 "You have received this message because you are a member of <a href=\"http://portal.d4science.research-infrastructures.eu\">D4Science portal</a> .");
		
		templates.put("New Functionally Available ", 

				 "With the deployment of gCube release <b>XXX</b>, the following functionality is now available from the portal:<br>- <b>XXX</b><br><br>" +
		 		"Best Regards,<br>the Service Activity Team  <br><br>" +
				 "You have received this message because you are a member of <a href=\"http://portal.d4science.research-infrastructures.eu\">D4Science portal</a> .");
	}
	
	public HashMap<String, String> getEmailTemplates() {
		return this.templates;
	}
	
}
