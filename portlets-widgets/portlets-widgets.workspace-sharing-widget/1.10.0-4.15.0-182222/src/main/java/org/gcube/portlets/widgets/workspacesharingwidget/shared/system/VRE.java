package org.gcube.portlets.widgets.workspacesharingwidget.shared.system;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VRE extends ResearchEnvironment implements Serializable {
	
	public VRE() {
		super();
	}

	public VRE(String vreName, String description, String imageURL,
			String vomsGroupName, String friendlyURL,
			UserBelonging userBelonging) {
		super(vreName, description, imageURL, vomsGroupName, friendlyURL, userBelonging);		
	}
}
