package org.gcube.datacatalogue.ckanutillibrary.server.patch;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResponse;

/**
 * The Interface PatchedCkan.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 *         Jun 14, 2019
 */
public interface PatchedCkan {

	/**
	 * 
	 * This Class is private in {@link CkanClient} so I need to rewrite it
	 * 
	 * The Class OrganizationResponse.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * 
	 *         Jun 7, 2019
	 */
	static class OrganizationResponse extends CkanResponse {

		public CkanOrganization result;

	}

	/**
	 * 
	 * 
	 * This Class is private in {@link CkanClient} so I need to rewrite it
	 * 
	 * 
	 * The Class GroupResponse.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * 
	 *         Jun 7, 2019
	 */
	static class GroupResponse extends CkanResponse {

		public CkanGroup result;

		/**
		 * Instantiates a new group response.
		 */
		public GroupResponse() {

		}
	}

	/**
	 * 
	 * This method is private in {@link CkanClient} so I need to rewrite it
	 * 
	 * 
	 * 
	 * Performs HTTP GET on server. If {@link CkanResponse#isSuccess()} is false
	 * throws {@link CkanException}.
	 *
	 * @param              <T> the generic type
	 * @param responseType a descendant of CkanResponse
	 * @param path         something like /api/3/package_show
	 * @param params       list of key, value parameters. They must be not be url
	 *                     encoded. i.e. "id","laghi-monitorati-trento"
	 * @return the http
	 * @throws CkanException on error
	 */
	public <T extends CkanResponse> T getHttp(Class<T> responseType, String path, Object... params);

	/**
	 * 
	 * This method is private in {@link CkanClient} so I need to rewrite it
	 * 
	 * 
	 * Calculates a full url out of the provided params.
	 *
	 * @param path   something like /api/3/package_show
	 * @param params list of key, value parameters. They must be not be url encoded.
	 *               i.e. "id","laghi-monitorati-trento"
	 * @return the full url to be called.
	 * @throws JackanException if there is any error building the url
	 */
	public String calcFullUrl(String path, Object[] params);
	
	

	// WE NEED TO OVERRIDE THE FOLLOWING METHODS

	/**
	 * Gets the organization.
	 *
	 * @param idOrName the id or name
	 * @return the organization
	 */
	public CkanOrganization getOrganization(String idOrName);

	/**
	 * Gets the group.
	 *
	 * @param idOrName the id or name
	 * @return the group
	 */
	public CkanGroup getGroup(String idOrName);

}
