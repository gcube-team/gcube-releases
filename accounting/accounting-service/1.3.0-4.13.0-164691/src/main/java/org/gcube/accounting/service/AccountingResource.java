package org.gcube.accounting.service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.MediaType;

import org.gcube.accounting.service.resources.RecordManagement;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 * @author Luca Frosini (ISTI - CNR)
 */
@ApplicationPath("/")
public class AccountingResource extends ResourceConfig {
	
	public static final String APPLICATION_JSON_CHARSET_UTF_8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	
	public AccountingResource() {
		packages(RecordManagement.class.getPackage().toString());
	}
	
}
