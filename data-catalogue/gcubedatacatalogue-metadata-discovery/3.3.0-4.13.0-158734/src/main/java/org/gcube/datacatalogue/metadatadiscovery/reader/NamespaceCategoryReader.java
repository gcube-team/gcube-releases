/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategories;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * The Class NamespaceCategoryReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 26, 2017
 */
public class NamespaceCategoryReader {

	private static Logger logger = LoggerFactory.getLogger(NamespaceCategoryReader.class);

	private static final String GENERIC_RESOURCE_NAMESPACES_NAME = "Namespaces Catalogue Categories";
	private static final String GENERIC_RESOURCE_NAMESPACES_SECONDARY_TYPE = "DataCatalogueNamespace";

	private ScopeBean scope;

	private NamespaceCategories namespaces;

	/**
	 * Instantiates a new medata format reader.
	 *
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public NamespaceCategoryReader(ScopeBean scope) throws Exception {
		this.scope = scope;
		readNamespaceCategory();
	}

	/**
	 * Gets the metadata format by id.
	 *
	 * @return the metadata format by id
	 * @throws Exception the exception
	 */
	private void readNamespaceCategory() throws Exception {
		logger.trace("Getting Namespace Category");

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: "+scopeString);

		namespaces = new NamespaceCategories();

		try {

			logger.info("Using scope from ScopeProvider: "+scopeString);

			String queryString = QueryForResourceUtil.getGcubeGenericQueryStringForSecondaryTypeAndName(GENERIC_RESOURCE_NAMESPACES_NAME, GENERIC_RESOURCE_NAMESPACES_SECONDARY_TYPE);
			logger.trace("queryString: " +queryString);
			Query q = new QueryBox(queryString);

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0)
				throw new MetadataProfileNotFoundException("Your resource is not registered in the infrastructure, the scope is "+scopeString);
			else {
				String theResource = null;
				try{
					theResource = appProfile.get(0);
					logger.debug("Resource (Namespaces Catalogue Categories) found");
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(theResource))).getDocumentElement();
					XPathHelper helper = new XPathHelper(node);
					logger.debug("Unmarshalling it..");
					readNamespaceCategoryFromResource(helper);
				}catch(Exception e){
					logger.error("Error while parsing Resource "+theResource+" from the infrastructure, the scope is "+scopeString,e);
				}
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch resource with name " + GENERIC_RESOURCE_NAMESPACES_NAME + " and secondary type " + GENERIC_RESOURCE_NAMESPACES_SECONDARY_TYPE + 
					" from the infrastructure, the scope is "+scopeString, e);
			throw new Exception("Error while trying to fetch resource with name " + GENERIC_RESOURCE_NAMESPACES_NAME + " and secondary type " + GENERIC_RESOURCE_NAMESPACES_SECONDARY_TYPE + 
					" from the infrastructure, the scope is "+scopeString, e);
		} finally{
		}
	}


	/**
	 * Read namespace category from resource.
	 *
	 * @param helper the helper
	 * @throws Exception the exception
	 */
	private void readNamespaceCategoryFromResource(XPathHelper helper) throws Exception{

		try {

			List<String> namespaceSources = helper.evaluate("/Resource/Profile/Body/namespaces");

			JAXBContext jaxbContext = JAXBContext.newInstance(NamespaceCategories.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			if(namespaceSources==null || namespaceSources.size()==0){
				throw new Exception("Resource does not contain <namespaces> <namespace>...</namespace> </namespaces> in the body");
			}

			InputStream stream = new ByteArrayInputStream(namespaceSources.get(0).getBytes());
			namespaces = (NamespaceCategories) jaxbUnmarshaller.unmarshal(stream);

		}catch(Exception e){
			String error = "An error occurred in readNamespaceCategoryFromResource " + e.getMessage();
			logger.error("An error occurred in readNamespaceCategoryFromResource ", e);
			throw new Exception(error);
		}
	}

	/**
	 * Gets the namespaces.
	 *
	 * @return the namespaces
	 */
	public NamespaceCategories getNamespaces() {

		return namespaces;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("NamespaceCategoryReader [scope=");
		builder.append(scope);
		builder.append(", namespaces=");
		builder.append(namespaces);
		builder.append("]");
		return builder.toString();
	}

}
