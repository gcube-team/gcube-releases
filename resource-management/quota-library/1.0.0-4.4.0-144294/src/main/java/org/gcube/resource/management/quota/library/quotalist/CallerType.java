package org.gcube.resource.management.quota.library.quotalist;
public enum CallerType {
	USER(1),
	ROLE(2),
	SERVICE(3);   
	@SuppressWarnings("unused")
	private int value;
	private CallerType(int value){
		this.value=value;
	}
}
