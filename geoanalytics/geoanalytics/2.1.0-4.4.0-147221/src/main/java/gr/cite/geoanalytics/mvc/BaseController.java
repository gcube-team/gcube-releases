/**
 * 
 */
package gr.cite.geoanalytics.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import gr.cite.geoanalytics.security.SecurityContextAccessor;

/**
 * @author vfloros
 *
 */
@Controller
public abstract class BaseController {

	@Autowired private SecurityContextAccessor securityContextAccessor;
	
//	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	public SecurityContextAccessor getSecurityContextAccessor() {
		return securityContextAccessor;
	}

	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}
	
}
