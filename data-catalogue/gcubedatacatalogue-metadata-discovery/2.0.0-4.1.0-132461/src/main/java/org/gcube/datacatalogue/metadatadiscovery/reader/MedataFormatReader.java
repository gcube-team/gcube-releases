package org.gcube.datacatalogue.metadatadiscovery.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * The Class MedataFormatReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 26, 2013
 */
public class MedataFormatReader {

	private static Logger logger = LoggerFactory.getLogger(MedataFormatReader.class);
	private ScopeBean scope;
	private MetadataFormat metadataFormat;
	private String resourceID;


	/**
	 * Instantiates a new medata format reader.
	 *
	 * @param scope the scope
	 * @param resourceID the resource id
	 * @throws Exception the exception
	 */
	public MedataFormatReader(ScopeBean scope, String resourceID) throws Exception {
		this.scope = scope;
		this.resourceID = resourceID;
		this.metadataFormat = getMedataFormatByID(resourceID);
	}


	/**
	 * Gets the medata format by id.
	 *
	 * @param resourceID the resource id
	 * @return the medata format by id
	 * @throws Exception the exception
	 */
	private MetadataFormat getMedataFormatByID(String resourceID) throws Exception {
		logger.trace("Get MedataFormat with resourceID: "+resourceID);

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: "+scopeString);

		MetadataFormat mf = new MetadataFormat();

		try {

//			ScopeProvider.instance.set(scopeString);
//			logger.info("scope provider set instance: "+scopeString);

			logger.info("Using scope from ScopeProvider: "+scopeString);

			String queryString = getQuesryStringForGenericResourceById(resourceID);
			logger.trace("queryString: " +queryString);
			Query q = new QueryBox(queryString);

			DiscoveryClient<String> client = client();
		 	List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0)
				throw new ApplicationProfileNotFoundException("Your resourceID "+resourceID+" is not registered in the infrastructure, the scope is "+scopeString);
			else {
				String theResource = null;
				try{
					theResource = appProfile.get(0);
//					logger.trace("Resource with resourceID "+resourceID+" matched "+theResource);
					logger.trace("Resource with resourceID "+resourceID+" found ");
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(theResource))).getDocumentElement();
					XPathHelper helper = new XPathHelper(node);
					List<MetadataField> fields = getMetadataFieldsFromResource(helper);
					mf.setMetadataFields(fields);
				}catch(Exception e){
					logger.error("Error while parsing Resource "+theResource+" from the infrastructure, the scope is "+scopeString,e);
				}
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch resourceID "+resourceID+" from the infrastructure, the scope is "+scopeString,e);
			return mf;
		} finally{
//			ScopeProvider.instance.reset();
		}

		return mf;
	}

	/**
	 * Gets the metadata fields from resource.
	 *
	 * @param helper the helper
	 * @return the metadata fields from resource
	 * @throws Exception the exception
	 */
	private List<MetadataField> getMetadataFieldsFromResource(XPathHelper helper) throws Exception{

		try {

			List<String> metadatafields = helper.evaluate("/Resource/Profile/Body/metadataformat/metadatafield");

			if(metadatafields==null || metadatafields.isEmpty())
				throw new Exception("Metadata fields not found");

			JAXBContext jaxbContext = JAXBContext.newInstance(MetadataField.class);
		    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

//		    for (String string : metadatafields) {
//		    	logger.debug("mf "+string);
//			}

		    List<MetadataField> mfs = new ArrayList<MetadataField>();
		    logger.debug("MetadataFields are: "+metadatafields.size());

		    for (String mfield : metadatafields) {
		    	try{
		    	InputStream stream = new ByteArrayInputStream(mfield.getBytes());
		    	MetadataField mtdf = (MetadataField) jaxbUnmarshaller.unmarshal(stream);
		    	logger.debug("Unmarshalled: "+mtdf);
				mfs.add(mtdf);
		    	}catch(Exception e){
		    		logger.error("An error occurred during unmarshall performing on "+mfield+", skipping this metadatafield");
		    	}
			}

			return mfs;

		} catch (Exception e) {
			logger.error("An error occurred in getListDefaultLayerFromNode ", e);
			return null;
		}

	}

	/**
	 * Gets the quesry string for generic resource by id.
	 *
	 * @param resourceId the resource id
	 * @return the quesry string for generic resource by id
	 */
	public synchronized String getQuesryStringForGenericResourceById(String resourceId){

		return String.format("declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; " +
						"for $resource in collection('/db/Profiles')//Document/Data/ic:Profile/Resource " +
						"where ($resource/ID/text() eq '%s') return $resource", resourceId);
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
	 * Gets the metadata format.
	 *
	 * @return the metadataFormat
	 */
	public MetadataFormat getMetadataFormat() {

		return metadataFormat;
	}


	/**
	 * Gets the resource id.
	 *
	 * @return the resourceID
	 */
	public String getResourceID() {

		return resourceID;
	}
}
