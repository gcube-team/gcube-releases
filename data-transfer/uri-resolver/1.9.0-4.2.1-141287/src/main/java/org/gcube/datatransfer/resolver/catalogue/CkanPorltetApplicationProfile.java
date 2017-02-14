/**
 *
 */

package org.gcube.datatransfer.resolver.catalogue;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * The Class GetCkanPorltet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Dec 2, 2016
 */
public class CkanPorltetApplicationProfile {

	private static final Logger logger = LoggerFactory.getLogger(CkanPorltetApplicationProfile.class);
	private final static String APPLICATION_PROFILE_NAME = "CkanPortlet";

	/**
	 * Gets the portlet url from infrastrucure.
	 *
	 * @return the portlet url from infrastrucure
	 * @throws Exception
	 */
	public static String getPortletUrlFromInfrastrucure() throws Exception {

		String scope = ScopeProvider.instance.get();
		logger.debug("Trying to fetch applicationProfile profile from the infrastructure for " +
			APPLICATION_PROFILE_NAME + " scope: " + scope);
		try {
			Query q =
				new QueryBox(
					"for $profile in collection('/db/Profiles/GenericResource')//Resource " +
						"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Name/string() " +
						" eq '" +
						APPLICATION_PROFILE_NAME +
						"'" +
						"return $profile");
			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);
			if (appProfile == null || appProfile.size() == 0)
				throw new ApplicationProfileNotFoundException(
					"Your applicationProfile is not registered in the infrastructure");
			else {
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				List<String> currValue = null;
				currValue =
					helper.evaluate("/Resource/Profile/Body/url/text()");
				if (currValue != null && currValue.size() > 0) {
					logger.debug("CKAN Portlet url found is " + currValue.get(0));
					return currValue.get(0);
				}
			}
		}
		catch (Exception e) {
			throw new Exception("Error while trying to fetch applicationProfile profile for name "+APPLICATION_PROFILE_NAME+"from the infrastructure, using scope: "+scope);
		}

		return null;
	}
}
