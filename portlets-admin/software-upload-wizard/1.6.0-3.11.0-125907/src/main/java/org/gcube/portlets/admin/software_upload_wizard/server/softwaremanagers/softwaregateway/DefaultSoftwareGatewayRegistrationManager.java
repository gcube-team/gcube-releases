package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway;

import org.gcube.vremanagement.softwaregateway.client.SGRegistrationLibrary;
import org.gcube.vremanagement.softwaregateway.client.proxies.Proxies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSoftwareGatewayRegistrationManager implements ISoftwareGatewayRegistrationManager {

	private static final Logger log = LoggerFactory.getLogger(DefaultSoftwareGatewayRegistrationManager.class);

	@Override
	public void registerProfile(String serviceProfileContent, String scope) throws Exception {
		log.trace("Contacting Software Gateway using scope: '" + scope.toString() + "'");
		log.trace("Registering service profile:\n" + serviceProfileContent);
		String storeResult = null;
		try {
			SGRegistrationLibrary sgClient = Proxies.registrationService().build();

			// GCUBEScope gcubeScope = new GCUBEScope();
			// gcubeScope.setScope(scope);
			storeResult = sgClient.register(serviceProfileContent);
		} catch (Exception e) {
			log.error(
					"An error occured while registering the profile within the Software Gateway\n RegisterProfileClient returned: "
							+ storeResult, e);
			throw new Exception("Service profile registration failed.", e);
		}

		SoftwareGatewayRegistrationResult result;
		try {
			result = parseSoftwareGatewayResult(storeResult);
		} catch (Exception e) {
			log.error("An error occured while parsing the Software Gateway result", e);
			throw new Exception("Unable to parse Software Gateway response.");
		}

		if (result.getStatus() == SoftwareGatewayRegistrationResult.RegistrationStatus.ERROR) {
			log.error("Service Profile registration failed. Software Gateway report:\n" + result.getReport());
			throw new Exception("Service Profile registration failed.");
		}
		log.debug("Software Profile registration succeeded");
	}

	private SoftwareGatewayRegistrationResult parseSoftwareGatewayResult(String result) {
		if (result.toLowerCase().contains(("<status>warn</status>"))
				|| result.toLowerCase().contains(("<status>success</status>")))
			return new SoftwareGatewayRegistrationResult(SoftwareGatewayRegistrationResult.RegistrationStatus.OK,
					result);
		return new SoftwareGatewayRegistrationResult(SoftwareGatewayRegistrationResult.RegistrationStatus.ERROR, result);
	}

}
