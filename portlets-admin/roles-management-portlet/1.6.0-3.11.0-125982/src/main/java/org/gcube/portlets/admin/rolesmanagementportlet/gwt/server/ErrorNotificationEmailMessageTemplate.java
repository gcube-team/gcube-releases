package org.gcube.portlets.admin.rolesmanagementportlet.gwt.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ErrorNotificationEmailMessageTemplate {
	
	private String message;
	private String cause;
	private String stackTrace;
	private String username;
	
	public ErrorNotificationEmailMessageTemplate(Throwable caught, String username) {
		if (caught.getMessage() != null)
			this.message = caught.getMessage();
		else
			this.message = "An unexpected error occured while using the roles management portlet. The exception was thrown without any message";
		
		if (caught.getCause() != null)
			if (caught.getCause().getMessage() != null)
				this.cause = caught.getCause().getMessage();
			else
				this.cause = "The exception was thrown without any cause";
		else
			this.cause = "The exception was thrown without any cause";
		
		this.username = username;
		this.stackTrace = stackTraceAsString(caught);
	}
	
	public String createBodyMessage() {
		String bodyMsg = "<p style=\"font-size:medium\"><b>USER: </b>" + this.username + " has encoutered a problem while using the portlet.</p><br>";
		bodyMsg += "<p style=\"font-size:medium\"><b>Error message: </b>" + this.message + "</p><br>";
		bodyMsg += "<p style=\"font-size:medium\"><b>Error cause: </b>" + this.cause + "</p><br>";
		bodyMsg += "<p style=\"font-size:medium\"><b>Exception's stack trace: </b>" + this.stackTrace + "</p><br>";
		bodyMsg += "<p style=\"font-size:small\">This is an automatic message sent to inform you about the produced error.<br>You have received this message because you are a member of the D4Science-II support team.</p>";
			
		return bodyMsg;
	}
	
	private static String stackTraceAsString(Throwable caught) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		caught.printStackTrace(printWriter);
		return writer.toString();
	}

}
