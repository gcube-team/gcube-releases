package org.gcube.portlets.admin.fhn_manager_portlet.server;

import lombok.Data;
import lombok.NonNull;

@Data	
public class UserInformation{
	@NonNull
	private String userName;
	@NonNull
	private String context;
	@NonNull
	private String token;
	@Override
	public String toString() {
		return "UserInformation [userName=" + userName + ", context=" + context + ", token=***]";
	}				
}