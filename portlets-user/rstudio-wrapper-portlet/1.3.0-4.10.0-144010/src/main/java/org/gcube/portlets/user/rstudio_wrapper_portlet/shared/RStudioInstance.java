package org.gcube.portlets.user.rstudio_wrapper_portlet.shared;

public class RStudioInstance {
	String context;
	String hostedOn;
	
	public RStudioInstance(String[] splits) {
		this.context = splits[0];
		this.hostedOn = splits[1];
	}
	
	public RStudioInstance(String context, String hostedOn) {
		this.hostedOn = hostedOn;
		this.context = context;
	}
	public String getHostedOn() {
		return hostedOn;
	}
	public void setHostedOn(String hostedOn) {
		this.hostedOn = hostedOn;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
}
