package org.gcube.datacatalogue.metadatadiscovery.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * The Class MedataFormatDiscovery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public class MetadataFormatDiscovery {

	private static Logger logger = LoggerFactory.getLogger(MetadataFormatDiscovery.class);
	protected static final String DATA_CATALOGUE_METADATA_SECONDARY_TYPE = "DataCatalogueMetadata";
	private String secondaryType;
	private ScopeBean scope;
	private List<MetadataProfile> metadataProfiles;

	/**
	 * Instantiates a new medata format reader.
	 *
	 * @param scope - the scope to be searched
	 * @throws Exception the exception
	 */
	public MetadataFormatDiscovery(ScopeBean scope) throws Exception {
		this.scope = scope;
		this.secondaryType = DATA_CATALOGUE_METADATA_SECONDARY_TYPE;
		this.metadataProfiles = readMetadataProfilesFromInfrastrucure();
	}

	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure.
	 *
	 * @return the applicationProfile profile
	 * @throws Exception the exception
	 */
	private List<MetadataProfile> readMetadataProfilesFromInfrastrucure() throws Exception {
		logger.trace("read secondary type: "+secondaryType);

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: "+scopeString);

		List<MetadataProfile> list = new ArrayList<MetadataProfile>();

		try {

//			ScopeProvider.instance.set(scopeString);
//			logger.info("scope provider set instance: "+scopeString);

//			ScopeProvider.instance.set(scopeString);
			logger.info("Using scope from ScopeProvider: "+scopeString);

			String queryString = QueryForResourceUtil.getGcubeGenericQueryStringForSecondaryType(secondaryType);
			logger.trace("queryString: " +queryString);
			Query q = new QueryBox(queryString);

			DiscoveryClient<String> client = client();
		 	List<String> metaProfile = client.submit(q);

			if (metaProfile == null || metaProfile.size() == 0)
				throw new MetadataProfileNotFoundException("Resource/s with secondaryType: "+secondaryType+" is/are not registered in the scope: "+scopeString);
			else {

				for (String elem : metaProfile) {
					try{
						DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document doc = docBuilder.parse(new InputSource(new StringReader(elem)));
						Node node = doc.getDocumentElement();
						XPathHelper helper = new XPathHelper(node);
						MetadataProfile meta = getMetadataProfileFromResource(doc, helper);
						list.add(meta);
					}catch(Exception e){
						logger.error("Error while trying to fetch resource with secondary type "+secondaryType+" from the infrastructure, scope: "+scopeString,e);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile "+secondaryType+" from the infrastructure, "+e);
			return list;
		}

		return list;

	}

	/**
	 * Gets the metadata type from resource.
	 *
	 * @param helper the helper
	 * @return the metadata type from resource
	 * @throws MetadataProfileNotFoundException the application profile not found exception
	 */
	private MetadataProfile getMetadataProfileFromResource(Document doc, XPathHelper helper) throws MetadataProfileNotFoundException{

		try {

			MetadataProfile metadataProfile = new MetadataProfile();
			List<String> id = helper.evaluate("/Resource/ID/text()");

			if(id==null || id.isEmpty())
				throw new MetadataProfileNotFoundException("Resource ID not found for "+helper.toString());
			else{
				metadataProfile.setId(id.get(0));
			}

			List<String> name = helper.evaluate("/Resource/Profile/Name/text()");

			if(name==null || name.isEmpty())
				throw new MetadataProfileNotFoundException("Resource Name not found for Resource Id: "+metadataProfile.getId());
			else
				metadataProfile.setName(name.get(0));

			logger.info("Building Profile for Resource Name: "+metadataProfile.getName());

			List<String> description = helper.evaluate("/Resource/Profile/Description/text()");

			if(description==null || description.isEmpty())
				logger.info("Description not found for Resource ID: "+metadataProfile.getId()+", continuing.. ");
			else
				metadataProfile.setDescription(description.get(0));

			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression expr = xpath.compile("/Resource/Profile/Body/metadataformat/@"+MetadataFormat.LOCAL_NAME_METADATA_TYPE);
			String metadataType = (String) expr.evaluate(doc, XPathConstants.STRING);

			if(metadataType==null || metadataType.isEmpty())
				throw new MetadataProfileNotFoundException("Required attribute '"+MetadataFormat.LOCAL_NAME_METADATA_TYPE+"' not found in the element '"+MetadataFormat.LOCAL_NAME_METADATA_FORMAT+"' for Metadata Profile (within Resource) Id: "+metadataProfile.getId());
			else
				metadataProfile.setMetadataType(metadataType);

			return metadataProfile;

		} catch (Exception e) {
			logger.error("An error occurred in getMetadataProfileFromResource ", e);
			return null;
		}

	}

	/**
	 * Gets the secondary type.
	 *
	 * @return the secondary type
	 */
	public String getSecondaryType() {
		return secondaryType;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public ScopeBean getScope() {
		return scope;
	}

	/**
	 * Gets the metadata types.
	 *
	 * @return the metadataTypes
	 */
	public List<MetadataProfile> getMetadataProfiles() {

		return metadataProfiles;
	}
}
