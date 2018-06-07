package org.gcube.datacatalogue.catalogue.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.Delegator;


@Path(Constants.LICENSES)
/**
 * Licenses service endpoint.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class License {

	@GET
	@Path(Constants.LIST_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.license_list
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);
		return Delegator.delegateGet(caller, context, Constants.LICENSES_SHOW, uriInfo, isApplication);

	}

}
