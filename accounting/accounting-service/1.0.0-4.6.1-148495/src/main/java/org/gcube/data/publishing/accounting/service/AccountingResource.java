package org.gcube.data.publishing.accounting.service;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
/**
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
@ApplicationPath("/gcube/service/")
public class AccountingResource extends ResourceConfig{
	public AccountingResource(){
		packages("org.gcube.data.publishing.accounting.service.resources");
	}

}

