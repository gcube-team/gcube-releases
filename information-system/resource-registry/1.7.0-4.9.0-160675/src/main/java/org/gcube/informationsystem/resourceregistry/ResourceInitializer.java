package org.gcube.informationsystem.resourceregistry;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.resourceregistry.rest.Access;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@ApplicationPath("/")
public class ResourceInitializer extends ResourceConfig {
	
	public static final String APPLICATION_JSON_CHARSET_UTF_8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	
	public ResourceInitializer() {
		packages(Access.class.getPackage().toString());
	}
	
}
