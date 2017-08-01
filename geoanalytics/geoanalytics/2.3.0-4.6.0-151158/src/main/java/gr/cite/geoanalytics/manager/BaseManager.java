package gr.cite.geoanalytics.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gr.cite.geoanalytics.security.SecurityContextAccessor;

public class BaseManager {
	@Autowired private SecurityContextAccessor securityContextAccessor;

	public BaseManager() {
		super();
		log.debug(BaseManager.class.getName() + " Initialized");
	}

	public BaseManager(SecurityContextAccessor securityContextAccessor) {
		log.debug(BaseManager.class.getName() + " Initialized");
		this.securityContextAccessor = securityContextAccessor;
	}
	
	public SecurityContextAccessor getSecurityContextAccessor() {
		return securityContextAccessor;
	}

	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}

	static Logger log = LoggerFactory.getLogger(BaseManager.class);
}
