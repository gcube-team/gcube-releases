package org.gcube.common.portal.mailing.templates;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;

public abstract class AbstractTemplate {
	
	private String gatewayName;
	private String gatewayURL;
	private GroupManager gm;
	
	public AbstractTemplate(String gatewayName, String gatewayURL) {
		super();
		this.gatewayName = gatewayName;
		this.gatewayURL = gatewayURL;
		this.gm = new LiferayGroupManager();
	}
	
	public String getGatewayLogoURL() {
		long gatewayGroupId;
		long gatewayLogoId = 0;
		try {
			gatewayGroupId = gm.getGroupId(gatewayName);
			gatewayLogoId = gm.getGroup(gatewayGroupId).getLogoId();
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return gatewayURL + gm.getGroupLogoURL(gatewayLogoId);
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public String getGatewayURL() {
		return gatewayURL;
	}
	
	public GroupManager getGroupManagerImpl() {
		return gm;
	}
}
