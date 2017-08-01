/**
 * 
 */
package gr.cite.geoanalytics.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import gr.cite.geoanalytics.manager.BaseManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

/**
 * @author vfloros
 *
 */
@Controller
public abstract class BaseController {
	@Autowired private SecurityContextAccessor securityContextAccessor;
	
	public BaseController() {
		super();
		logger.debug(BaseController.class.getName() + " Initialized");
	}

	public SecurityContextAccessor getSecurityContextAccessor() {
		return securityContextAccessor;
	}

	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}

	
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
}