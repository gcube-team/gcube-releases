package org.gcube.data.publishing.gCatFeeder.model;


public class ControllerConfiguration {

	public static enum PublishingPolicy{
		FAIL,UPDATE,SKIP
	}
	
	private PublishingPolicy onClash=PublishingPolicy.UPDATE; 
	
	public PublishingPolicy getOnClash() {
		return onClash;
	}
	
	public void setOnClash(PublishingPolicy onClash) {
		this.onClash = onClash;
	}
}
