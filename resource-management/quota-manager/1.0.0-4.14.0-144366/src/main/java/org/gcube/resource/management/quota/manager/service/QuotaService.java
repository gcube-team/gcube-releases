package org.gcube.resource.management.quota.manager.service;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
/**
 * StringListConverter
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
@ApplicationPath("/gcube/service/")
public class QuotaService extends ResourceConfig  {
	public QuotaService(){
		packages("org.gcube.resource.management.quota.manager.service");
	}
}