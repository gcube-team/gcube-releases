package org.gcube.portlets.user.gcubeloggedin.shared;


@SuppressWarnings("serial")
public class VREClient extends VObject {

	public VREClient() {
		super();
	}

	public VREClient(String name, String groupName, String description, String imageURL, String friendlyURL, 
			UserBelongingClient userBelonging, boolean isMandatory, boolean isUponRequest, boolean isManager) {
		super(name, groupName, description, imageURL, friendlyURL, userBelonging, isMandatory, isUponRequest, isManager);
	}

	
}