package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.ApplicationProfileNotFoundException;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.CKANPublisherServicesImpl;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Discover in a given context if there is a Generic Resource containing the list of tags to be used within the widget.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DiscoverTagsList {

	private static final Log logger = LogFactoryUtil.getLog(CKANPublisherServicesImpl.class);

	private final static String APPLICATION_PROFILE_NAME = "Tags";
	private final static String QUERY = "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
			"where $profile/Profile/SecondaryType/string() eq 'DataCatalogueMetadataTags' and  $profile/Profile/Name/string() " +
			" eq '" + APPLICATION_PROFILE_NAME + "'" +
			"return $profile";

	/**
	 * Discover the list of tags vocabulary if needed
	 * @throws Exception 
	 */
	public static List<String> discoverTagsList(String context){

		if(context == null || context.isEmpty())
			throw new IllegalArgumentException("Context cannot be empty or null!");

		String currentContext = ScopeProvider.instance.get();
		try{
			ScopeProvider.instance.set(context);
			Query q = new QueryBox(QUERY);
			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);
			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("No DataCatalogueMetadataTags is registered in the infrastructure in context " + context);
			else{
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				List<String> tagsVocabulary = helper.evaluate("/Resource/Profile/Body/tags/tag/text()");
				logger.debug("Retrieved tags " + tagsVocabulary);
				return tagsVocabulary;
			}
		}catch(Exception e){
			logger.warn("Failed to retrieve the list of tags vocabulary " + e.getMessage());
		}finally{
			ScopeProvider.instance.set(currentContext);
		}
		
		return null;
	}

}