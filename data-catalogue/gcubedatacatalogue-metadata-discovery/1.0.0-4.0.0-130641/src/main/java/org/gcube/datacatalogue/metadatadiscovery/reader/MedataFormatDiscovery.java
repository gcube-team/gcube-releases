package org.gcube.datacatalogue.metadatadiscovery.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * The Class MedataFormatDiscovery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public class MedataFormatDiscovery {

	private static Logger logger = LoggerFactory.getLogger(MedataFormatDiscovery.class);
	protected static final String DATA_CATALOGUE_METADATA_SECONDARY_TYPE = "DataCatalogueMetadata";
	private String secondaryType;
	private ScopeBean scope;
	private List<MetadataType> metadataTypes;

	/**
	 * Instantiates a new medata format reader.
	 *
	 * @param scope - the scope to be searched
	 * @throws Exception the exception
	 */
	public MedataFormatDiscovery(ScopeBean scope) throws Exception {
		this.scope = scope;
		this.secondaryType = DATA_CATALOGUE_METADATA_SECONDARY_TYPE;
		this.metadataTypes = readProfileFromInfrastrucure();
	}

	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure.
	 *
	 * @return the applicationProfile profile
	 * @throws Exception the exception
	 */
	private List<MetadataType> readProfileFromInfrastrucure() throws Exception {
		logger.trace("read secondary type: "+secondaryType);

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: "+scopeString);

		List<MetadataType> list = new ArrayList<MetadataType>();

		try {

//			ScopeProvider.instance.set(scopeString);
//			logger.info("scope provider set instance: "+scopeString);

//			ScopeProvider.instance.set(scopeString);
			logger.info("Using scope from ScopeProvider: "+scopeString);

			String queryString = getGcubeGenericQueryString(secondaryType);
			logger.trace("queryString: " +queryString);
			Query q = new QueryBox(queryString);

			DiscoveryClient<String> client = client();
		 	List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0)
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure, the scope is "+scopeString);
			else {

				for (String elem : appProfile) {
					try{
						DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
						XPathHelper helper = new XPathHelper(node);
						MetadataType meta = getMetadataTypeFromResource(helper);
						list.add(meta);
					}catch(Exception e){
						logger.error("Error while trying to fetch applicationProfile "+secondaryType+" from the infrastructure, ",e);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile "+secondaryType+" from the infrastructure, "+e);
			return list;
		} finally{
//			ScopeProvider.instance.reset();
		}

		return list;

	}

	/**
	 * Gets the metadata type from resource.
	 *
	 * @param helper the helper
	 * @return the metadata type from resource
	 * @throws ApplicationProfileNotFoundException the application profile not found exception
	 */
	private MetadataType getMetadataTypeFromResource(XPathHelper helper) throws ApplicationProfileNotFoundException{


		try {

			MetadataType metaType = new MetadataType();

			List<String> id = helper.evaluate("/Resource/ID/text()");

			if(id==null || id.isEmpty())
				throw new ApplicationProfileNotFoundException("Resource ID not found for "+helper.toString());
			else{
				metaType.setId(id.get(0));
			}

			List<String> name = helper.evaluate("/Resource/Profile/Name/text()");

			if(name==null || name.isEmpty())
				throw new ApplicationProfileNotFoundException("Resource Name not found for "+helper.toString());
			else
				metaType.setName(name.get(0));

			List<String> description = helper.evaluate("/Resource/Profile/Description/text()");

			if(description==null || description.isEmpty())
				throw new ApplicationProfileNotFoundException("Resource Name not found for "+helper.toString());
			else
				metaType.setDescription(description.get(0));

			return metaType;

		} catch (Exception e) {
			logger.error("An error occurred in getListDefaultLayerFromNode ", e);
			return null;
		}

	}

	/**
	 * Gets the gcube generic query string.
	 *
	 * @param secondaryType the secondary type
	 * @return the gcube generic query string
	 */
	public synchronized String getGcubeGenericQueryString(String secondaryType){

		return "for $profile in collection('/db/Profiles/GenericResource')//Resource" +
				" where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"'" +
				" return $profile";
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
	public List<MetadataType> getMetadataTypes() {

		return metadataTypes;
	}
}
