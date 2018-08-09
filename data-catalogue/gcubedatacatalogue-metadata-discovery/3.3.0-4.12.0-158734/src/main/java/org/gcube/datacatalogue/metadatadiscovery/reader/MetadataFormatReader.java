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
 * The Class MetadataFormatReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 19, 2017
 */
public class MetadataFormatReader {

	private static Logger logger = LoggerFactory.getLogger(MetadataFormatReader.class);
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
	public MetadataFormatReader(ScopeBean scope, String resourceID) throws Exception {
		this.scope = scope;
		this.resourceID = resourceID;
		this.metadataFormat = getMetadataFormatByResourceID(resourceID);
	}



	/**
	 * Gets the metadata format by id.
	 *
	 * @param resourceID the resource id
	 * @return the metadata format by id
	 * @throws Exception the exception
	 */
	private MetadataFormat getMetadataFormatByResourceID(String resourceID) throws Exception {
		logger.trace("Getting MedataFormat for resourceID: "+resourceID);

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: "+scopeString);

		MetadataFormat mf = new MetadataFormat();

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
					logger.debug("Resource (Metadata Format) with resourceID "+resourceID+" found");
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(theResource))).getDocumentElement();
					XPathHelper helper = new XPathHelper(node);
//					List<MetadataField> fields = getMetadataFieldsFromResource(helper);
//					mf.setMetadataFields(fields);
					logger.debug("Unmarshalling it..");
					mf = getMetadataFormatFromResource(helper);
					String metadataSource = getMetadataFormatSourceFromResource(helper);
					mf.setMetadataSource(metadataSource);
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
	 * Gets the metadata format source from resource.
	 *
	 * @param helper the helper
	 * @return the metadata format source from resource
	 * @throws Exception the exception
	 */
	private String getMetadataFormatSourceFromResource(XPathHelper helper) throws Exception{

		try {

			List<String> metadataFormatSource = helper.evaluate("/Resource/Profile/Body/metadataformat");

			if(metadataFormatSource==null || metadataFormatSource.isEmpty())
				throw new Exception("metadataformat not found in the body, consider to add <metadataformat>");

			return metadataFormatSource.get(0);

		}catch(Exception e){
			logger.error("An error occurred in getMetadataFormatSourceFromResource ", e);
			return null;
		}
	}



	/**
	 * Gets the metadata format from resource.
	 *
	 * @param helper the helper
	 * @return the metadata format from resource
	 * @throws Exception the exception
	 */
	private MetadataFormat getMetadataFormatFromResource(XPathHelper helper) throws Exception{

		try {

			List<String> metadataFormatSource = helper.evaluate("/Resource/Profile/Body/metadataformat");

			JAXBContext jaxbContext = JAXBContext.newInstance(MetadataFormat.class);
		    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		    if(metadataFormatSource==null || metadataFormatSource.size()==0)
		    	throw new Exception("Resource does not contain <metadataformat> in the body");

		    InputStream stream = new ByteArrayInputStream(metadataFormatSource.get(0).getBytes());
		    MetadataFormat mtdf = (MetadataFormat) jaxbUnmarshaller.unmarshal(stream);
		    logger.debug("returning metadata format with medata type: "+mtdf.getType());

		    return mtdf;

		}catch(Exception e){
			logger.error("An error occurred in getMetadataFormatSourceFromResource ", e);
			return null;
		}
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
			logger.error("An error occurred in getMetadataFieldsFromResource ", e);
			return null;
		}

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
