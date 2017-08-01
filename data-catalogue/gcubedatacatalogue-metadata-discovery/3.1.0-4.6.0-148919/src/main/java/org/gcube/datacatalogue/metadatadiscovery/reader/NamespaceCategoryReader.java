/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

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

	/**
	 *
	 */
	public static final String NAMESPACES_CATALOGUE_CATEGORIES_PROPERTIES = "NamespacesCatalogueCategories.properties";
	private static Logger logger = LoggerFactory.getLogger(NamespaceCategoryReader.class);
	private ScopeBean scope;
	private String resourceID;

	private NamespaceCategories namespaces;

	/**
	 * Instantiates a new medata format reader.
	 *
	 * @param scope the scope
	 * @param resourceID the resource id
	 * @throws Exception the exception
	 */
	public NamespaceCategoryReader(ScopeBean scope, String resourceID) throws Exception {
		this.scope = scope;
		this.resourceID = resourceID;
		if(resourceID == null || resourceID.isEmpty())
			throw new Exception("Please, specify a valid ResourceID in the property file: "+NAMESPACES_CATALOGUE_CATEGORIES_PROPERTIES);

		readNamespaceCategoryByResourceID();
	}

	/**
	 * Gets the metadata format by id.
	 *
	 * @return the metadata format by id
	 * @throws Exception the exception
	 */
	private void readNamespaceCategoryByResourceID() throws Exception {
		logger.trace("Getting Namespace Category for resourceID: "+resourceID);

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: "+scopeString);

		namespaces = new NamespaceCategories();

		try {

			logger.info("Using scope from ScopeProvider: "+scopeString);

			String queryString = QueryForResourceUtil.queryForGenericResourceById(resourceID);
			logger.trace("queryString: " +queryString);
			Query q = new QueryBox(queryString);

			DiscoveryClient<String> client = client();
		 	List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0)
				throw new MetadataProfileNotFoundException("Your resourceID "+resourceID+" is not registered in the infrastructure, the scope is "+scopeString);
			else {
				String theResource = null;
				try{
					theResource = appProfile.get(0);
//					logger.trace("Resource with resourceID "+resourceID+" matched "+theResource);
					logger.debug("Resource (Namespaces Catalogue Categories) within resourceID "+resourceID+" found");
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(theResource))).getDocumentElement();
					XPathHelper helper = new XPathHelper(node);
//					List<MetadataField> fields = getMetadataFieldsFromResource(helper);
//					mf.setMetadataFields(fields);
					logger.debug("Unmarshalling it..");
					readNamespaceCategoryFromResource(helper);
				}catch(Exception e){
					logger.error("Error while parsing Resource "+theResource+" from the infrastructure, the scope is "+scopeString,e);
				}
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch resourceID "+resourceID+" from the infrastructure, the scope is "+scopeString,e);
			throw new Exception("Error while trying to fetch resourceID "+resourceID+" from the infrastructure, the scope is "+scopeString, e);
		} finally{
//			ScopeProvider.instance.reset();
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
	 * Namespaces catalogue categories resource id.
	 *
	 * @return the string
	 * @throws PropertyFileNotFoundException the property file not found exception
	 */
	public static String NamespacesCatalogueCategoriesResourceID() throws PropertyFileNotFoundException {

		Properties prop = new Properties();

		try {

			InputStream in = NamespaceCategoryReader.class.getResourceAsStream(NAMESPACES_CATALOGUE_CATEGORIES_PROPERTIES);
			// load a properties file
			prop.load(in);
			return  prop.getProperty("RESOURCE_ID");

		} catch (IOException e) {
			logger.error("An error occurred on read property file "+NAMESPACES_CATALOGUE_CATEGORIES_PROPERTIES, e);
			throw new PropertyFileNotFoundException("An error occurred on read property file "+NAMESPACES_CATALOGUE_CATEGORIES_PROPERTIES+": "+e);
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
		builder.append(", resourceID=");
		builder.append(resourceID);
		builder.append(", namespaces=");
		builder.append(namespaces);
		builder.append("]");
		return builder.toString();
	}

}
