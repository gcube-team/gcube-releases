package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.exceptions.ApplicationProfileNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Look up from the IS other information that the widget should lookup
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GenericResourceReaderExtras {

	private Set<String> lookedUpExtrasKeys = new HashSet<String>();

	private static final String GENERIC_RESOURCE_NAME = "GRSFManageEntries";
	private static final String GENERIC_RESOURCE_SECONDARY_TYPE = "ApplicationProfile";

	private static final Logger logger = LoggerFactory.getLogger(GenericResourceReaderExtras.class);

	// generic resource properties (a list of information)
	public GenericResourceReaderExtras(){

		String scope = ScopeProvider.instance.get();
		logger.debug("Trying to fetch applicationProfile profile from the infrastructure for " + GENERIC_RESOURCE_NAME + " scope: " +  scope);

		try {
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq '"+ GENERIC_RESOURCE_SECONDARY_TYPE + "' and  $profile/Profile/Name/string() " +
					" eq '" + GENERIC_RESOURCE_NAME + "'" +
					"return $profile");

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");
			else {

				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				List<String> currValue = null;
				currValue = helper.evaluate("/Resource/Profile/Body/text()");

				if (currValue != null && currValue.size() > 0) {
					String body = currValue.get(0);
					String[] splittedSet = body.split(",");
					if(splittedSet != null && splittedSet.length > 0)
						for (String entry : splittedSet) {
							String trimmed = entry.trim();
							if(trimmed.isEmpty())
								continue;
							lookedUpExtrasKeys.add(trimmed);
						}
				} 
			}
			
			logger.info("Extras entries are " + lookedUpExtrasKeys);

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
		} 

	}

	public Set<String> getLookedUpExtrasKeys() {
		return lookedUpExtrasKeys;
	}

	@Override
	public String toString() {
		return "GenericResourceReaderExtras [lookedUpExtrasKeys="
				+ lookedUpExtrasKeys + "]";
	}

}
