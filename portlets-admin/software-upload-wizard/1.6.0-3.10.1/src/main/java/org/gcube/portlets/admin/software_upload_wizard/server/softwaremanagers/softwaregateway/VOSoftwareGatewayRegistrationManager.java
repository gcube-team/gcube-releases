package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class VOSoftwareGatewayRegistrationManager implements ISoftwareGatewayRegistrationManager {

	ISoftwareGatewayRegistrationManager decoratedSGRegistrationManager;

	private static final Logger log = LoggerFactory.getLogger(VOSoftwareGatewayRegistrationManager.class);

	@Inject
	public VOSoftwareGatewayRegistrationManager(
			@DefaultSG ISoftwareGatewayRegistrationManager decoratedSGRegistrationManager) {
		super();
		this.decoratedSGRegistrationManager = decoratedSGRegistrationManager;
	}

	@Override
	public void registerProfile(String serviceProfileContent, String scope) throws Exception {
		int charMatches = StringUtils.countMatches(scope.toString(), "/");
		log.trace("Modifing scope \"" + scope.toString() + "\" according to SG rules.");
		if (charMatches == 0 || charMatches > 3)
			throw new Exception("Software gateway cannot accept scope: " + scope.toString());
		String newScope = null;
		if (charMatches > 0 && charMatches < 3) {
			log.debug("Scope does not need to be modified");
			newScope = scope;
		} else if (charMatches == 3) {
			newScope = scope.substring(0, scope.lastIndexOf("/"));
			log.debug("Scope modification applied: '" + scope.toString() + "'" + " --> '" + newScope.toString() + "'");
		}
		decoratedSGRegistrationManager.registerProfile(serviceProfileContent, newScope);
	}
}
