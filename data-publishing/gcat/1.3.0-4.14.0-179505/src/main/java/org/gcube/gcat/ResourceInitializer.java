package org.gcube.gcat;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.MediaType;

import org.gcube.gcat.rest.Group;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@ApplicationPath("/")
public class ResourceInitializer extends ResourceConfig {
	
	public static final String APPLICATION_JSON_CHARSET_UTF_8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	
	public ResourceInitializer() {
		packages(Group.class.getPackage().toString());
	}
	
}
