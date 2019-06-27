package org.gcube.vremanagement.executor;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.MediaType;

import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.vremanagement.executor.api.rest.RestConstants;
import org.gcube.vremanagement.executor.rest.RestSmartExecutor;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@ApplicationPath(RestConstants.REST_PATH_PART)
// UnComment this when the SOAP API will be dismissed
@ManagedBy(SmartExecutorInitializator.class) 
public class ResourceInitializer extends ResourceConfig {
	
	public static final String APPLICATION_JSON_CHARSET_UTF_8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	
	public ResourceInitializer() {
		packages(RestSmartExecutor.class.getPackage().toString());
	}
	
}
