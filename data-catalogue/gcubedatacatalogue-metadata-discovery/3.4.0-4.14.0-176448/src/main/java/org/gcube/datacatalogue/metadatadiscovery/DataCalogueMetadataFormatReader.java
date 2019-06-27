package org.gcube.datacatalogue.metadatadiscovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.gcube.datacatalogue.metadatadiscovery.reader.MetadataFormatDiscovery;
import org.gcube.datacatalogue.metadatadiscovery.reader.MetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.reader.NamespaceCategoryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class DataCalogueMetadataReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public class DataCalogueMetadataFormatReader implements DataCatalogueMetadataDiscovery {
	
	private static String SCHEMA_FILENAME = "Gdcmetadataprofilev3.xsd";
	
	private MetadataFormatDiscovery medataFormatDiscovery;
	private ScopeBean scope;
	private Map<String,MetadataFormat> hashMetadataFormats = null;
	private List<NamespaceCategory> namespaceCategories = null;

	private String profileSchema = null;
	
	private static Logger logger = LoggerFactory.getLogger(DataCalogueMetadataFormatReader.class);
	
	/**
	 * Instantiates a new data calogue metadata format reader.
	 *
	 * @throws Exception the exception
	 */
	public DataCalogueMetadataFormatReader() throws Exception {
		
		String scopeString = ScopeProvider.instance.get();
		logger.debug("Read scope " + scopeString + " from ScopeProvider");
		
		if(scopeString == null || scopeString.isEmpty())
			throw new Exception("Please set a valid scope into ScopeProvider");
		
		scope = new ScopeBean(scopeString);
		readNamespaces();
		readMetadaFormats();
	}
	
	/**
	 * Read metada formats.
	 *
	 * @throws Exception the exception
	 */
	private void readMetadaFormats() throws Exception {
		
		medataFormatDiscovery = new MetadataFormatDiscovery(scope);
		logger.info("MedataFormatDiscovery has retrieved: " + medataFormatDiscovery.getMetadataProfiles().size()
				+ " metadata type/s");
		logger.debug("filling cache for MedataFormat");
		hashMetadataFormats = new HashMap<String,MetadataFormat>(medataFormatDiscovery.getMetadataProfiles().size());
		for(MetadataProfile mT : medataFormatDiscovery.getMetadataProfiles()) {
			if(mT == null)
				continue;
			
			MetadataFormatReader reader = new MetadataFormatReader(scope, mT.getId());
			hashMetadataFormats.put(mT.getId(), reader.getMetadataFormat());
			logger.debug("MetadataType id: " + mT.getId() + " cached as: " + reader.getMetadataFormat());
		}
	}
	
	/**
	 * Read namespaces.
	 */
	private void readNamespaces() {
		
		try {
			if(namespaceCategories == null || namespaceCategories.isEmpty()) {
				
				if(namespaceCategories == null)
					namespaceCategories = new ArrayList<NamespaceCategory>();
				
				NamespaceCategoryReader rd = new NamespaceCategoryReader(scope);
				namespaceCategories.addAll(rd.getNamespaces().getNamespaceCategories());
			}
		} catch(Exception e) {
			logger.debug("An error occurred during read namespaces for categories: ", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.datacatalogue.metadatadiscovery.DataCatalogueMetadataDiscovery#getMetadataFormatForMetadataType(org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType)
	 */
	@Override
	public MetadataFormat getMetadataFormatForMetadataProfile(MetadataProfile profile) throws Exception {
		
		if(profile == null)
			throw new Exception("Input " + MetadataProfile.class.getSimpleName() + " is null");
		
		MetadataFormat format = hashMetadataFormats.get(profile.getId());
		if(format != null)
			return format;
		
		MetadataFormatReader reader = new MetadataFormatReader(scope, profile.getId());
		return reader.getMetadataFormat();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.datacatalogue.metadatadiscovery.DataCatalogueMetadataDiscovery#getListOfMetadataTypes()
	 */
	@Override
	public List<MetadataProfile> getListOfMetadataProfiles() throws Exception {
		
		if(medataFormatDiscovery == null)
			readMetadaFormats();
		
		return medataFormatDiscovery.getMetadataProfiles();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.datacatalogue.metadatadiscovery.DataCatalogueMetadataDiscovery#getListOfNamespaceCategories()
	 */
	@Override
	public List<NamespaceCategory> getListOfNamespaceCategories() throws Exception {
		
		if(namespaceCategories == null)
			readNamespaces();
		
		return namespaceCategories;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.datacatalogue.metadatadiscovery.DataCatalogueMetadataDiscovery#resetMetadataProfile()
	 */
	@Override
	public void resetMetadataProfile() {
		
		medataFormatDiscovery = null;
		hashMetadataFormats = null;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.datacatalogue.metadatadiscovery.DataCatalogueMetadataDiscovery#resetNamespaceCategories()
	 */
	@Override
	public void resetNamespaceCategories() {
		
		namespaceCategories = null;
	}
	
	@Override
	public String getProfileSchema() {
		if(profileSchema == null) {
			InputStream inputStream = getProfileSchemaInputStream();
			profileSchema = new BufferedReader(new InputStreamReader(inputStream)).lines()
					.collect(Collectors.joining("\n"));
		}
		return profileSchema;
		
	}
	
	static InputStream getProfileSchemaInputStream() {
		return DataCalogueMetadataFormatReader.class.getResourceAsStream(SCHEMA_FILENAME);
	}
	
	static URL getProfileSchemaURL() {
		return DataCalogueMetadataFormatReader.class.getResource(SCHEMA_FILENAME);
	}
	
	static void validateAgainstXSD(Source xml, URL xsd) throws SAXException, IOException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory.newSchema(xsd);
		Validator validator = schema.newValidator();
		validator.validate(xml);
	}
	
	@Override
	public void validateProfile(String xmlProfile) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse( new InputSource(new StringReader(xmlProfile))); 
		DOMSource source = new DOMSource(doc);
		validateAgainstXSD(source, getProfileSchemaURL());
	}
	
	public static void validateProfile(InputStream xml) throws Exception {
		validateAgainstXSD(new StreamSource(xml), getProfileSchemaURL());
	}
	
}
