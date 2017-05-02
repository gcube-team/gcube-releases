package org.gcube.informationsystem.resourceregistry;

import javax.ws.rs.ApplicationPath;

import org.gcube.informationsystem.resourceregistry.rest.Access;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@ApplicationPath("/")
public class ResourceInitializer extends ResourceConfig  {

	public ResourceInitializer(){
		packages(Access.class.getPackage().toString());
	}

}
