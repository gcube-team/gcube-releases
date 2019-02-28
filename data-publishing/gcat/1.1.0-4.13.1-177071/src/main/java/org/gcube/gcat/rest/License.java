package org.gcube.gcat.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.persistence.ckan.CKANLicense;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(License.LICENSES)
public class License extends REST<CKANLicense> implements org.gcube.gcat.api.interfaces.License {

	public License() {
		super(LICENSES, null, CKANLicense.class);
	}
	
	@GET
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String list() {
		return super.list(-1, -1);
	}
	
}
