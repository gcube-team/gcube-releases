package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope;

import java.util.List;

import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableScopeAvailable implements ScopeAvailable {
	
	List<String> availableScopeInfras;
	
	public ConfigurableScopeAvailable(List<String> availableScopeInfras) {
		this.availableScopeInfras = availableScopeInfras;
	}

	private static final Logger log = LoggerFactory.getLogger(ConfigurableScopeAvailable.class);

	@Override
	public boolean isAvailableForScope(String scope) {
		String infrastructure = ScopeUtil.getInfrastructure(scope);
		log.trace("Evaluting availability for scope infrastructure: " + infrastructure);
		if (availableScopeInfras.contains(infrastructure))
			return true;
		log.warn("Current scope infrastructure is unmanaged: " + infrastructure);
		return false;
	}
	
	

}
