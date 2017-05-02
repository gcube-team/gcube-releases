package gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.DefaultConfiguration;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.geonetwork.model.faults.GeoNetworkException;
import org.gcube.spatial.data.geonetwork.model.faults.InvalidInsertConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.geotools.metadata.iso.extent.ExtentImpl;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.identification.TopicCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.access.InvocationFailureException;

import com.google.common.base.Strings;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoNetworkBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaData;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaDataForm;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaDataFormXML;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.BooleanPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.CharacterStringPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.CodeListValueType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.DatePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.DecimalPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.DistancePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.DistanceType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gco.IntegerPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.AbstractMDIdentificationType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIAddressPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIAddressType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CICitationPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CICitationType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIContactPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIContactType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIDatePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIDateType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIDateTypeCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIOnlineResourcePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIOnlineResourceType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIPresentationFormCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIResponsiblePartyPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIResponsiblePartyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.CIRoleCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.EXExtentPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.EXExtentType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.EXGeographicBoundingBoxPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.EXGeographicBoundingBoxType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.LanguageCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDBrowseGraphicPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDBrowseGraphicType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDCharacterSetCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDGeometricObjectTypeCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDGeometricObjectsPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDGeometricObjectsType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDIdentificationPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDKeywordTypeCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDKeywordsPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDKeywordsType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDLegalConstraintsPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDLegalConstraintsType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDMaintenanceFrequencyCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDMaintenanceInformationPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDMaintenanceInformationType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDMetadataType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDResolutionPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDResolutionType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDRestrictionCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDScopeCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDTopicCategoryCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDTopicCategoryCodeType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDTopologyLevelCodePropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDVectorSpatialRepresentationPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.MDVectorSpatialRepresentationType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.geographic.gmd.URLPropertyType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.wrappers.Geonetwork;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.wrappers.GeonetworkPublisher;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;

public class GSManagerGeoNetworkBridge implements GeoNetworkBridge{
	private static Logger logger = LoggerFactory.getLogger(GSManagerGeoNetworkBridge.class);

	private static DefaultConfiguration defaultConf = null;
	private static GeonetworkPublisher publisher = null;

	@Override	
	public void publishGeonetwork(String tenantName,MetaDataForm meta) throws GeoNetworkBridgeException
	{
		logger.debug("Entering publish in geonetwork in scope: "+tenantName);
		ScopeProvider.instance.set(tenantName);
		
		try {
			logger.debug("Getting publisher with default configuration...");
			defaultConf = new DefaultConfiguration();
			publisher = Geonetwork.get(defaultConf);
			logger.debug("Got publisher with default configuration");
		} catch (MissingConfigurationException 
				| EncryptionException 
				| MissingServiceEndpointException 
				| GNLibException 
				| GNServerException 
				| AuthorizationException e) {
			
			logger.error("Error while initializing configuration and publisher", e);
			throw new GeoNetworkBridgeException(
					"Error while initializing configuration and publisher", e);
		}
		
		//Cannot publish without logging in
		try {
			logger.debug("Logging in with default LoginLevel...");
			publisher.login(LoginLevel.DEFAULT);
			logger.debug("Logged in with default LoginLevel");
		} catch (MissingConfigurationException | AuthorizationException
				| MissingServiceEndpointException e) {
			logger.error("Error while trying to log in", e);
			throw new GeoNetworkBridgeException(
					"Error while trying to log in", e);
		}
		
		//Get publish configuration
		GNInsertConfiguration config;
		try {
			logger.debug("Getting current user configuration...");
			config = publisher.getCurrentUserConfiguration("datasets", "_none_");
			logger.debug("Got current user configuration");
		} catch (GeoNetworkException e) {
			logger.error("Error while getting current configuration", e);
			throw new GeoNetworkBridgeException(
					"Error while getting current configuration", e);
		}
		
		//Publish Metadata object
		File file = null;
		
		try {
			logger.debug("Transforming metadata to xml");
			MetaData m = translate(meta,tenantName);
			file=mYmeta2File(inverseTranslate(m));
			logger.debug("Transformed metadata to xml");
		} catch (InvocationFailureException | IOException | JAXBException | DatatypeConfigurationException e) {
			logger.error("Error while transforming from GcubeISOMetadata to Metadata", e);
			throw new GeoNetworkBridgeException(
					"Error while transforming from GcubeISOMetadata to Metadata", e);
		}

		long publishedObjectId;
		try {
			logger.debug("Publishing metadata in geonetwork...");
			publishedObjectId = publisher.insertMetadata(config, file);
			logger.debug("Published in geonetwork with id : "+publishedObjectId);
		} catch (MissingConfigurationException | GNLibException
				| GNServerException | MissingServiceEndpointException
				| InvalidInsertConfigurationException | AuthorizationException e) {
			logger.error("Error while inserting to Geonetwork", e);
			throw new GeoNetworkBridgeException(
					"Error while inserting to Geonetwork", e);
		}
	}
	
	private MetaData translate(MetaDataForm metadata, String tenantName) throws GeoNetworkBridgeException{
		
		MetaData meta;
		try {
			meta = new MetaData(tenantName);
		} catch (Exception e) {
			logger.error("Error while initializing Metadata", e);
			throw new GeoNetworkBridgeException(
					"Error while initializing Metadata", e);
		}
		
		meta.setMetadataAbstract(metadata.getAbstractField());
		meta.setMetadataPurpose(metadata.getPurpose());
		meta.setCredits(metadata.getCredits());
		meta.setDate(metadata.getDate());
		meta.setDistributorIndividualName(metadata.getDistributor().getIndividualName());
		meta.setDistributorOrganisationName(metadata.getDistributor().getOrganisationName());
		meta.setDistributorSite(metadata.getDistributor().getSite());
		meta.setExtent(metadata.getExtent());
		meta.setGeometricObjectType(metadata.getGeometricObjectType());
		meta.setGraphicOverview(metadata.getGraphicOverview());
		meta.setKeywords(metadata.getKeywords());
		meta.setLanguage(metadata.getLanguage());
		meta.setPresentationForm(metadata.getPresentationForm());
		meta.setProviderIndividualName(metadata.getProvider().getIndividualName());
		meta.setProviderOrganisationName(metadata.getProvider().getOrganisationName());
		meta.setProviderSite(metadata.getProvider().getSite());
		meta.setResolution(metadata.getResolution());
		meta.setTitle(metadata.getTitle());
		meta.setTopicCategory(metadata.getTopicCategory());
		meta.setTopologyLevel(metadata.getTopologyLevel());
		meta.setUser(metadata.getUser());
		meta.setUserLimitation(metadata.getUserLimitation());
		meta.setProjectName(metadata.getProjectName());
		meta.setGeometryCount(metadata.getGeometrycount());
		meta.setLanguage(metadata.getLanguage());
		
		return meta;
	}
	
	private MetaDataFormXML inverseTranslate(MetaData metadata) throws GeoNetworkBridgeException{
		
		MetaDataFormXML meta;
		try {
			meta = new MetaDataFormXML();
		} catch (Exception e) {
			logger.error("Error while initializing Metadata", e);
			throw new GeoNetworkBridgeException(
					"Error while initializing Metadata", e);
		}
		
		meta.setAbstractField(metadata.getMetadataAbstract());
		meta.setPurpose(metadata.getMetadataPurpose());
		meta.setCredits(metadata.getCredits());
		meta.setDate(metadata.getDate());
		meta.setDistributorIndividualName(metadata.getDistributorIndividualName());
		meta.setDistributorOrganisationName(metadata.getDistributorOrganisationName());
		meta.setDistributorSite(metadata.getDistributorSite());
		meta.setExtent(metadata.getExtent());
		meta.setGeometricObjectType(metadata.getGeometricObjectType());
		meta.setGraphicOverview(metadata.getGraphicOverview());
		HashMap<Thesaurus, HashSet<String>> keywordsThesaurus = metadata.getKeywords();
		for (Map.Entry<Thesaurus, HashSet<String>> entry : keywordsThesaurus.entrySet()) {
			List<String> keywords = new ArrayList<String>(entry.getValue());
			meta.setKeywords(keywords);
		}

		switch (metadata.getLanguage().toString())
	    {
	      case "en":
	    	  meta.setLanguage(MetaDataFormXML.Language.ENGLISH);
	    	  break;
	      case "fr":
	    	  meta.setLanguage(MetaDataFormXML.Language.FRENCH);
	    	  break;
	      case "de":
	    	  meta.setLanguage(MetaDataFormXML.Language.GERMAN);
	    	  break;
	      case "it":
	    	  meta.setLanguage(MetaDataFormXML.Language.ITALIAN);
	    	  break;
	      case "ja":  
	    	  meta.setLanguage(MetaDataFormXML.Language.JAPANESE);
	    	  break;
	      case "ko":    
	    	  meta.setLanguage(MetaDataFormXML.Language.KOREAN);
	    	  break;
	      case "zh":
	    	  meta.setLanguage(MetaDataFormXML.Language.CHINESE);
	    	  break;
	      default:        
	    	  meta.setLanguage(MetaDataFormXML.Language.ENGLISH);
	    }
		
		meta.setPresentationForm(metadata.getPresentationForm());
		meta.setProviderIndividualName(metadata.getProviderIndividualName());
		meta.setProviderOrganisationName(metadata.getProviderOrganisationName());
		meta.setProviderSite(metadata.getProviderSite());
		meta.setResolution(metadata.getResolution());
		meta.setTitle(metadata.getTitle());
		meta.setTopicCategory(metadata.getTopicCategory());
		meta.setTopologyLevel(metadata.getTopologyLevel());
		meta.setUser(metadata.getUser());
		meta.setUserLimitation(metadata.getUserLimitation());
		meta.setProjectName(metadata.getProjectName());
		meta.setGeometrycount(metadata.getGeometryCount());
		meta.setFileIdentifier(UUID.randomUUID().toString());
		
		return meta;
		
	}
	
private static File mYmeta2File(MetaDataFormXML meta) throws IOException, JAXBException, DatatypeConfigurationException, GeoNetworkBridgeException{
		
		
		MDMetadataType metaType;
		
		metaType = new MDMetadataType();
		
		//characterSet
    	MDCharacterSetCodePropertyType charSetCodePropertyType = new MDCharacterSetCodePropertyType();
    	CodeListValueType codeListValType = new CodeListValueType();
    	codeListValType.setValue("UTF-8");
    	codeListValType.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_CharacterSetCode");
    	codeListValType.setCodeListValue("utf8");
    	charSetCodePropertyType.setMDCharacterSetCode(codeListValType);
    	
    	
    	//FileIdentifier
    	CharacterStringPropertyType charStringPropertyType = new CharacterStringPropertyType();
    	charStringPropertyType.setCharacterString(meta.getFileIdentifier().toString());
    	
    	
    	//Language
    	CodeListValueType languageValue = new CodeListValueType();
    	languageValue.setValue(meta.getLanguage().toString());
    	languageValue.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/ML_gmxCodelists.xml#LanguageCode");
    	languageValue.setCodeListValue("deu");
    	LanguageCodePropertyType languageCodePropertytype = new LanguageCodePropertyType();
    	languageCodePropertytype.setLanguageCode(languageValue);
    	
		
    	//hierarchyLevel
    	CodeListValueType hierarchyValue = new CodeListValueType();
    	hierarchyValue.setValue("Dataset");
    	hierarchyValue.setCodeSpace("deu");
    	hierarchyValue.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_ScopeCode");
    	hierarchyValue.setCodeListValue("dataset");
    	MDScopeCodePropertyType mdHierarchy = new MDScopeCodePropertyType();
    	mdHierarchy.setMDScopeCode(hierarchyValue);
    	
    	//contact
    	List<CIResponsiblePartyPropertyType> listContacts = new ArrayList<CIResponsiblePartyPropertyType>();
    	
    	////author
    	CharacterStringPropertyType authorName = new CharacterStringPropertyType();
    	authorName.setCharacterString(meta.getUser());
    	CharacterStringPropertyType authorOrganisationName = new CharacterStringPropertyType();
    	authorOrganisationName.setCharacterString(meta.getProjectName());
    	CIResponsiblePartyType author = new CIResponsiblePartyType();
    	CIRoleCodePropertyType authorRole = new CIRoleCodePropertyType();
    	CodeListValueType authorRoleValue = new CodeListValueType();
    	authorRoleValue.setValue("Author");
    	authorRoleValue.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_RoleCode");
    	authorRoleValue.setCodeListValue("author");
    	authorRole.setCIRoleCode(authorRoleValue);
    	author.setIndividualName(authorName);
    	author.setOrganisationName(authorOrganisationName);
    	author.setRole(authorRole);
    	CIResponsiblePartyPropertyType responsiblePartyAuthor = new CIResponsiblePartyPropertyType();
    	responsiblePartyAuthor.setCIResponsibleParty(author);
    	
    	////distributor
    	CharacterStringPropertyType distributorName = new CharacterStringPropertyType();
    	distributorName.setCharacterString(meta.getDistributor().getIndividualName());
    	CharacterStringPropertyType distributorOrganisationName = new CharacterStringPropertyType();
    	distributorOrganisationName.setCharacterString(meta.getDistributor().getOrganisationName());
    	CIResponsiblePartyType distributor = new CIResponsiblePartyType();
    	CIRoleCodePropertyType distributorRole = new CIRoleCodePropertyType();
    	CIContactPropertyType distributorContactInfo = new CIContactPropertyType();
    	CIContactType distributorContactInfoContactType = new CIContactType();
    	CIOnlineResourcePropertyType distributorContactInfoContactTypeOnlineResource = new CIOnlineResourcePropertyType();
    	CIOnlineResourceType distributorContactInfoContactTypeOnlineResourceValue = new CIOnlineResourceType();
    	URLPropertyType distributorContactInfoContactTypeOnlineResourceValueLinkage = new URLPropertyType();
    	CIAddressPropertyType distributorContactInfoContactTypeAddress = new CIAddressPropertyType();
    	distributorContactInfoContactTypeAddress.setCIAddress(new CIAddressType());
    	CodeListValueType distributorRoleValue = new CodeListValueType();
    	distributorRoleValue.setValue(meta.getDistributor().getRole().toString());
    	distributorRoleValue.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_RoleCode");
    	distributorRoleValue.setCodeListValue("distributor");
    	distributorRole.setCIRoleCode(distributorRoleValue);
    	distributorContactInfoContactTypeOnlineResourceValueLinkage.setURL(meta.getDistributor().getSite());
    	distributorContactInfoContactTypeOnlineResourceValue.setLinkage(distributorContactInfoContactTypeOnlineResourceValueLinkage);
    	distributorContactInfoContactTypeOnlineResource.setCIOnlineResource(distributorContactInfoContactTypeOnlineResourceValue);
    	distributorContactInfoContactType.setAddress(distributorContactInfoContactTypeAddress);
    	distributorContactInfoContactType.setOnlineResource(distributorContactInfoContactTypeOnlineResource);
    	distributorContactInfo.setCIContact(distributorContactInfoContactType);
    	distributor.setIndividualName(distributorName);
    	distributor.setOrganisationName(distributorOrganisationName);
    	distributor.setRole(distributorRole);
    	distributor.setContactInfo(distributorContactInfo);
    	CIResponsiblePartyPropertyType responsiblePartyDistributor = new CIResponsiblePartyPropertyType();
    	responsiblePartyDistributor.setCIResponsibleParty(distributor);
    	
    	////provider
    	CharacterStringPropertyType providerName = new CharacterStringPropertyType();
    	providerName.setCharacterString(meta.getProvider().getIndividualName());
    	CharacterStringPropertyType providerOrganisationName = new CharacterStringPropertyType();
    	providerOrganisationName.setCharacterString(meta.getProvider().getOrganisationName());
    	CIResponsiblePartyType provider = new CIResponsiblePartyType();
    	CIRoleCodePropertyType providerRole = new CIRoleCodePropertyType();
    	CIContactPropertyType providerContactInfo = new CIContactPropertyType();
    	CIContactType providerContactInfoContactType = new CIContactType();
    	CIOnlineResourcePropertyType providerContactInfoContactTypeOnlineResource = new CIOnlineResourcePropertyType();
    	CIOnlineResourceType providerContactInfoContactTypeOnlineResourceValue = new CIOnlineResourceType();
    	URLPropertyType providerContactInfoContactTypeOnlineResourceValueLinkage = new URLPropertyType();
    	CIAddressPropertyType providerContactInfoContactTypeAddress = new CIAddressPropertyType();
    	providerContactInfoContactTypeAddress.setCIAddress(new CIAddressType());
    	CodeListValueType providerRoleValue = new CodeListValueType();
    	providerRoleValue.setValue(meta.getProvider().getRole().toString());
    	providerRoleValue.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_RoleCode");
    	providerRoleValue.setCodeListValue("resourceProvider");
    	providerRole.setCIRoleCode(providerRoleValue);
    	providerContactInfoContactTypeOnlineResourceValueLinkage.setURL(meta.getProvider().getSite());
    	providerContactInfoContactTypeOnlineResourceValue.setLinkage(providerContactInfoContactTypeOnlineResourceValueLinkage);
    	providerContactInfoContactTypeOnlineResource.setCIOnlineResource(providerContactInfoContactTypeOnlineResourceValue);
    	providerContactInfoContactType.setAddress(providerContactInfoContactTypeAddress);
    	providerContactInfoContactType.setOnlineResource(providerContactInfoContactTypeOnlineResource);
    	providerContactInfo.setCIContact(providerContactInfoContactType);
    	provider.setIndividualName(providerName);
    	provider.setOrganisationName(providerOrganisationName);
    	provider.setRole(providerRole);
    	provider.setContactInfo(providerContactInfo);
    	CIResponsiblePartyPropertyType responsiblePartyProvider = new CIResponsiblePartyPropertyType();
    	responsiblePartyProvider.setCIResponsibleParty(provider);
    	
    	listContacts.add(responsiblePartyAuthor);
    	listContacts.add(responsiblePartyDistributor);
    	listContacts.add(responsiblePartyProvider);

    	//datestamp
    	GregorianCalendar c = new GregorianCalendar();
    	c.setTime(meta.getDate());
    	XMLGregorianCalendar dateGregorian;
    	DatePropertyType dateProperty = new DatePropertyType();
		try {
			dateGregorian = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	    	dateProperty.setDateTime(dateGregorian);
	    	
		} catch (DatatypeConfigurationException e) {
			logger.error("Error while initializing Gregorian Calendar", e);
			throw new GeoNetworkBridgeException(
					"Error while initializing Gregorian Calendar", e);
		}
    	
		//spatialRepresantationInfo
		CodeListValueType spatialRepresentationTypeTopologyLevelValue = new CodeListValueType();
		spatialRepresentationTypeTopologyLevelValue.setValue(meta.getTopologyLevel().toString().replace("TopologyLevel", "").replace('[', ' ').replace(']', ' ').trim());
		spatialRepresentationTypeTopologyLevelValue.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_TopologyLevelCode");
		spatialRepresentationTypeTopologyLevelValue.setCodeListValue(meta.getTopologyLevel().toString().replace("TopologyLevel", "").replace('[', ' ').replace(']', ' ').trim().toLowerCase());
		MDTopologyLevelCodePropertyType spatialRepresentationTypeTopologyLevel = new MDTopologyLevelCodePropertyType();
		spatialRepresentationTypeTopologyLevel.setMDTopologyLevelCode(spatialRepresentationTypeTopologyLevelValue);
		MDVectorSpatialRepresentationType spatialRepresentationType = new MDVectorSpatialRepresentationType();
		List<MDGeometricObjectsPropertyType> geometricObjects = new ArrayList<MDGeometricObjectsPropertyType>();
		MDGeometricObjectsPropertyType geometricObjectsPropertyType = new MDGeometricObjectsPropertyType();
		MDGeometricObjectsType geometricObjectsType = new MDGeometricObjectsType();
		IntegerPropertyType geometryCount = new IntegerPropertyType();
		geometryCount.setInteger(BigInteger.valueOf(meta.getGeometrycount()));
		CodeListValueType codeListValueType = new CodeListValueType();
		codeListValueType.setValue(meta.getGeometricObjectType().toString().replace("GeometricObjectType", "").replace('[', ' ').replace(']', ' ').trim());
		codeListValueType.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_GeometricObjectTypeCode");
		codeListValueType.setCodeListValue(meta.getGeometricObjectType().toString().replace("GeometricObjectType", "").replace('[', ' ').replace(']', ' ').trim().toLowerCase());
		MDGeometricObjectTypeCodePropertyType geometricObjectTypeCode = new MDGeometricObjectTypeCodePropertyType();
		geometricObjectTypeCode.setMDGeometricObjectTypeCode(codeListValueType);
		
		geometricObjectsType.setGeometricObjectType(geometricObjectTypeCode);
		geometricObjectsType.setGeometricObjectCount(geometryCount);
		geometricObjectsPropertyType.setMDGeometricObjects(geometricObjectsType);
		
		geometricObjects.add(geometricObjectsPropertyType);
		
		spatialRepresentationType.setTopologyLevel(spatialRepresentationTypeTopologyLevel);
		spatialRepresentationType.setGeometricObjects(geometricObjects);
		MDVectorSpatialRepresentationPropertyType spatialRepresentation = new MDVectorSpatialRepresentationPropertyType();
		spatialRepresentation.setMDVectorSpatialRepresentation(spatialRepresentationType);
		
		//identificationInfo
		////citation
		
		//////date
		DatePropertyType date = new DatePropertyType();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(meta.getDate());
    	XMLGregorianCalendar dateGregorianXml;
	
		dateGregorianXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    	date.setDate(dateGregorianXml.toString());
	    
    	CodeListValueType dateTypeCode = new CodeListValueType();
    	dateTypeCode.setValue("Creation");
    	dateTypeCode.setCodeSpace("deu");
    	dateTypeCode.setCodeListValue("creation");
    	dateTypeCode.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_DateTypeCode");
		CIDateTypeCodePropertyType dateTypeCodeProperty = new CIDateTypeCodePropertyType();
		dateTypeCodeProperty.setCIDateTypeCode(dateTypeCode);
		
		CIDateType dateType = new CIDateType();
		dateType.setDate(date);
		dateType.setDateType(dateTypeCodeProperty);
		CIDatePropertyType datePropertyType = new CIDatePropertyType();
		datePropertyType.setCIDate(dateType);
		
		//////title
		CharacterStringPropertyType title = new CharacterStringPropertyType();
		title.setCharacterString(meta.getTitle());
		
		//////presentationForm
		CodeListValueType presentationForm = new CodeListValueType();
		presentationForm.setValue("Map Digital");
		presentationForm.setCodeListValue("mapDigital");
		presentationForm.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_PresentationFormCode");
		CIPresentationFormCodePropertyType presentationFromCodeProperty = new CIPresentationFormCodePropertyType();
		presentationFromCodeProperty.setCIPresentationFormCode(presentationForm);
		
		CICitationType citationType = new CICitationType();
		citationType.setTitle(title);
		citationType.setDate(datePropertyType);
		citationType.setPresentationForm(presentationFromCodeProperty);
		
		CICitationPropertyType citationPropertyType = new CICitationPropertyType();
		citationPropertyType.setCICitation(citationType);
		
		////abstract
		CharacterStringPropertyType _abstract = new CharacterStringPropertyType();
		_abstract.setCharacterString(meta.getAbstractField());
		
		////purpose
		CharacterStringPropertyType purpose = new CharacterStringPropertyType();
		purpose.setCharacterString(meta.getPurpose());
		
		////credits
		List<CharacterStringPropertyType> credits = new ArrayList<CharacterStringPropertyType>();
		for(String credit : meta.getCredits()) {
			CharacterStringPropertyType creditProperty = new CharacterStringPropertyType();
			creditProperty.setCharacterString(credit);
			credits.add(creditProperty);
		}
		
		////resourceMaintenance
		CodeListValueType maintenanceFrequencyCode = new CodeListValueType();
		maintenanceFrequencyCode.setValue("As needed");
		maintenanceFrequencyCode.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_MaintenanceFrequencyCode");
		maintenanceFrequencyCode.setCodeListValue("asNeeded");
		MDMaintenanceFrequencyCodePropertyType maintenanceFrequency = new MDMaintenanceFrequencyCodePropertyType();
		maintenanceFrequency.setMDMaintenanceFrequencyCode(maintenanceFrequencyCode);
		MDMaintenanceInformationType maintenanceType = new MDMaintenanceInformationType();
		maintenanceType.setMaintenanceAndUpdateFrequency(maintenanceFrequency);
		MDMaintenanceInformationPropertyType maintenancePropertyType = new MDMaintenanceInformationPropertyType();
		maintenancePropertyType.setMDMaintenanceInformation(maintenanceType);
		
		////graphicOverview
		CharacterStringPropertyType graphicOverview = new CharacterStringPropertyType();
		graphicOverview.setCharacterString(meta.getGraphicOverview());
		MDBrowseGraphicType graphicOverviewType = new MDBrowseGraphicType();
		graphicOverviewType.setFileName(graphicOverview);
		MDBrowseGraphicPropertyType graphicOverviewPropertyType = new MDBrowseGraphicPropertyType();
		graphicOverviewPropertyType.setMDBrowseGraphic(graphicOverviewType);
		
		////descriptiveKeywords
		//////keywords
		MDKeywordsType keywordsType = new MDKeywordsType();
		List<CharacterStringPropertyType> keywords = new ArrayList<CharacterStringPropertyType>();
		for(String keyword : meta.getKeywords()) {
			CharacterStringPropertyType keywordProperty = new CharacterStringPropertyType();
			keywordProperty.setCharacterString(keyword);
			
			keywords.add(keywordProperty);
		}
		
		//////type
		CodeListValueType keywordTypeCode = new CodeListValueType();
		keywordTypeCode.setValue("Theme");
		keywordTypeCode.setCodeListValue("theme");
		keywordTypeCode.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_KeywordTypeCode");
		MDKeywordTypeCodePropertyType keywordTypeCodeProperty = new MDKeywordTypeCodePropertyType();
		keywordTypeCodeProperty.setMDKeywordTypeCode(keywordTypeCode);
		
		//////thesaurus
		////////title
		CharacterStringPropertyType thesaurusTypeTitle = new CharacterStringPropertyType();
		thesaurusTypeTitle.setCharacterString("General");
		
		////////date
		DatePropertyType thesaurusDatePropertyType = new DatePropertyType();
		thesaurusDatePropertyType.setDate("2013-07-04T15:09:55.783+03:00");
		
		CodeListValueType thesaurusDatetypeCode = new CodeListValueType();
		thesaurusDatetypeCode.setValue("Creation");
		thesaurusDatetypeCode.setCodeSpace("deu");
		thesaurusDatetypeCode.setCodeListValue("creation");
		thesaurusDatetypeCode.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_DateTypeCode");
		CIDateTypeCodePropertyType thesaurusDatetype = new CIDateTypeCodePropertyType();
		thesaurusDatetype.setCIDateTypeCode(thesaurusDatetypeCode);
		
		CIDateType thesaurusDateType = new CIDateType();
		thesaurusDateType.setDate(thesaurusDatePropertyType);
		thesaurusDateType.setDateType(thesaurusDatetype);
		CIDatePropertyType thesaurusDate = new CIDatePropertyType();
		thesaurusDate.setCIDate(thesaurusDateType);
		
		CICitationType thesaurusType = new CICitationType();
		thesaurusType.setTitle(thesaurusTypeTitle);
		thesaurusType.setDate(thesaurusDate);
		CICitationPropertyType thesaurusPropertyType = new CICitationPropertyType();
		thesaurusPropertyType.setCICitation(thesaurusType);
		
		keywordsType.setKeyword(keywords);
		keywordsType.setType(keywordTypeCodeProperty);
		keywordsType.setThesaurusName(thesaurusPropertyType);
		MDKeywordsPropertyType keywordsPropertyType = new MDKeywordsPropertyType();
		keywordsPropertyType.setMDKeywords(keywordsType);
		
		////spatialResolution
		DistanceType distanceType = new DistanceType();
		distanceType.setValue(String.valueOf(meta.getResolution()));
		distanceType.setUom("http://schemas.opengis.net/iso/19139/20070417/resources/uom/gmxUom.xml#xpointer(//*[@gml:id='m'])");
		DistancePropertyType distance = new DistancePropertyType();
		distance.setDistance(distanceType);
		MDResolutionType resolutionType = new MDResolutionType();
		resolutionType.setDistance(distance);
		MDResolutionPropertyType resolutionPropertyType = new MDResolutionPropertyType();
		resolutionPropertyType.setMDResolution(resolutionType);
		
		////language
		CodeListValueType languageCode = new CodeListValueType();
		languageCode.setValue("English");
		languageCode.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/ML_gmxCodelists.xml#LanguageCode");
		languageCode.setCodeListValue("eng");
		LanguageCodePropertyType languangePropertyType = new LanguageCodePropertyType();
		languangePropertyType.setLanguageCode(languageCode);
		
		////topicCategory
		List<MDTopicCategoryCodePropertyType> listTopicCategory = new ArrayList<MDTopicCategoryCodePropertyType>();
		for(TopicCategory category : meta.getTopicCategory()) {
			MDTopicCategoryCodePropertyType topicCategoryPropertyType = new MDTopicCategoryCodePropertyType();
			topicCategoryPropertyType.setMDTopicCategoryCode(MDTopicCategoryCodeType.fromValue(category.toString().replace("TopicCategory", "").replace('[', ' ').replace(']', ' ').trim().toLowerCase()));
			
			listTopicCategory.add(topicCategoryPropertyType);
		}
		
		////extent
	    ////extentDescription
		CharacterStringPropertyType extentDescription = new CharacterStringPropertyType();
		extentDescription.setCharacterString(meta.getExtentDescription());
		
		////geographicExtent
		BooleanPropertyType extentTypeCode = new BooleanPropertyType();
		extentTypeCode.setBoolean(Boolean.TRUE);
		GeographicBoundingBox extentgeographicBoundingBox= ExtentImpl.getGeographicBoundingBox(meta.getExtent());
		
		DecimalPropertyType eastBounding = new DecimalPropertyType();
		eastBounding.setDecimal(extentgeographicBoundingBox.getEastBoundLongitude());
		DecimalPropertyType westBounding = new DecimalPropertyType();
		westBounding.setDecimal(extentgeographicBoundingBox.getWestBoundLongitude());
		DecimalPropertyType northBounding = new DecimalPropertyType();
		northBounding.setDecimal(extentgeographicBoundingBox.getNorthBoundLatitude());
		DecimalPropertyType southBounding = new DecimalPropertyType();
		southBounding.setDecimal(extentgeographicBoundingBox.getSouthBoundLatitude());
		
		EXGeographicBoundingBoxType geographicBoundingBoxType = new EXGeographicBoundingBoxType();
		geographicBoundingBoxType.setExtentTypeCode(extentTypeCode);
		geographicBoundingBoxType.setEastBoundLongitude(eastBounding);
		geographicBoundingBoxType.setWestBoundLongitude(westBounding);
		geographicBoundingBoxType.setNorthBoundLatitude(northBounding);
		geographicBoundingBoxType.setSouthBoundLatitude(southBounding);
		EXGeographicBoundingBoxPropertyType geographicBoundingBox = new EXGeographicBoundingBoxPropertyType();
		geographicBoundingBox.setEXGeographicBoundingBox(geographicBoundingBoxType);
		
		EXExtentType extentType = new EXExtentType();
		extentType.setDescription(extentDescription);
		extentType.setGeographicElement(geographicBoundingBox);
		EXExtentPropertyType extentPropertyType = new EXExtentPropertyType();
		extentPropertyType.setEXExtent(extentType);
		
		AbstractMDIdentificationType identificationType = new AbstractMDIdentificationType();
		identificationType.setCitation(citationPropertyType);
		identificationType.setAbstract(_abstract);
		identificationType.setPurpose(purpose);
		identificationType.setCredit(credits);
		identificationType.setGraphicOverview(graphicOverviewPropertyType);
		identificationType.setResourceMaintenance(maintenancePropertyType);
		identificationType.setDescriptiveKeywords(keywordsPropertyType);
		identificationType.setSpatialResolution(resolutionPropertyType);
		identificationType.setLanguage(languangePropertyType);
		identificationType.setTopicCategory(listTopicCategory);
		identificationType.setExtent(extentPropertyType);
		MDIdentificationPropertyType identificationPropertyType = new MDIdentificationPropertyType();
		identificationPropertyType.setAbstractMDIdentification(identificationType);
		
		//metadataConstraints
		////useContraints - accessConstraints
		CodeListValueType constraintsPropertyTypeCode = new CodeListValueType();
		constraintsPropertyTypeCode.setValue("Licence");
		constraintsPropertyTypeCode.setCodeListValue("licence");
		constraintsPropertyTypeCode.setCodeList("http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_RestrictionCode");
		
		MDRestrictionCodePropertyType constraintsRestrictionPropertyType = new MDRestrictionCodePropertyType();
		constraintsRestrictionPropertyType.setMDRestrictionCode(constraintsPropertyTypeCode);
		List<MDRestrictionCodePropertyType> listcontraints = new ArrayList<MDRestrictionCodePropertyType>();
		listcontraints.add(constraintsRestrictionPropertyType);
		
		////useLimitation
		List<CharacterStringPropertyType> useLimitationList = new ArrayList<CharacterStringPropertyType>();
		CharacterStringPropertyType useLimitationPropertyType = new CharacterStringPropertyType();
		useLimitationPropertyType.setCharacterString(meta.getUserLimitation());
		useLimitationList.add(useLimitationPropertyType);
		
		MDLegalConstraintsType constraintsType = new MDLegalConstraintsType();
		constraintsType.setUseConstraints(listcontraints);
		constraintsType.setAccessConstraints(listcontraints);
		constraintsType.setUseLimitation(useLimitationList);
		MDLegalConstraintsPropertyType constraintsPropertyType = new MDLegalConstraintsPropertyType();
		constraintsPropertyType.setMDLegalConstraints(constraintsType);
		
		//Fill meta
		metaType.setCharacterSet(charSetCodePropertyType);
    	metaType.setFileIdentifier(charStringPropertyType);
    	metaType.setLanguage(languageCodePropertytype);
    	metaType.setHierarchyLevel(mdHierarchy);
		metaType.setContact(listContacts);
    	metaType.setDateStamp(dateProperty);
		metaType.setSpatialRepresentationInfo(spatialRepresentation);
		metaType.setIdentificationInfo(identificationPropertyType);
		metaType.setMetadataConstraints(constraintsPropertyType);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(MDMetadataType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	
		//--------------------------------------------------------------------------------------------
		File temp=File.createTempFile("meta", ".xml");
		FileWriter writer=new FileWriter(temp);

		jaxbMarshaller.marshal(metaType, writer);
		jaxbMarshaller.marshal(metaType, System.out);
		
		writer.close();
		
		replaceAttributes(temp);
		
		return temp;
	}
	
	public static void replaceAttributes(File temp) throws IOException {
		
		RandomAccessFile raInputFile = new RandomAccessFile(temp, "rw");
		RandomAccessFile raInputFile2 = new RandomAccessFile(temp, "rw");
		raInputFile.readLine();
		raInputFile2.readLine();
		String line = raInputFile2.readLine();
		long filePointer = raInputFile.getFilePointer();
		raInputFile.seek(filePointer);
		
		String property = "<gmd:MD_Metadata xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">";
		raInputFile.writeBytes(property);
		
		
		raInputFile.seek(filePointer + property.length());
		raInputFile.writeBytes(Strings.repeat(" ", line.length()-property.length()));
		
		
		raInputFile.close();
		raInputFile2.close();
		
	}
	
}
