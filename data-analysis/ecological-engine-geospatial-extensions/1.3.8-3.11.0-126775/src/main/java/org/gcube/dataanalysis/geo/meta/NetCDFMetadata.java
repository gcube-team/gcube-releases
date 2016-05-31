package org.gcube.dataanalysis.geo.meta;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultContact;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.constraint.DefaultLegalConstraints;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.distribution.DefaultFormat;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.metadata.iso.identification.DefaultResolution;
import org.geotoolkit.metadata.iso.identification.DefaultUsage;
import org.geotoolkit.metadata.iso.lineage.DefaultLineage;
import org.geotoolkit.metadata.iso.lineage.DefaultNominalResolution;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessStep;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessing;
import org.geotoolkit.metadata.iso.lineage.DefaultSource;
import org.geotoolkit.metadata.iso.maintenance.DefaultMaintenanceInformation;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.quality.DefaultScope;
import org.geotoolkit.metadata.iso.spatial.DefaultGeometricObjects;
import org.geotoolkit.metadata.iso.spatial.DefaultVectorSpatialRepresentation;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.xml.XML;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.metadata.maintenance.MaintenanceFrequency;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.metadata.spatial.TopologyLevel;
import org.opengis.util.InternationalString;

public class NetCDFMetadata {

	private String geonetworkUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork/";
	private String geonetworkUser = "admin";
	private String geonetworkPwd = "admin";
	private String threddsCatalogUrl = "http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml";
	private String title = "temperature 04091217ruc.nc";
	private String layerName = "T";
	private String usageField = "Environmental enrichment";
	private String processdescription = "Maps publication";
	private String usageLimitations = "Not for commercial scopes";
	private Date sourceGenerationDate = new Date(System.currentTimeMillis());
	private String sourceFileName = "04091217_ruc.nc";
	private String contactInfo = "support@d4science.research-infrastructures.eu";
	private String abstractField = "T: temperature (degK) from 04091217ruc.nc resident on the THREDDS instance " + threddsCatalogUrl;
	private String purpose = "Maps publication";
	private String author = "D4Science";
	private double res = 0.5d;
	private double xLL = -180;
	private double xRU = 180;
	private double yLL = -85.5;
	private double yRU = 85.5;
	private String layerUrl = "http://thredds.research-infrastructures.eu:8080/thredds/dodsC/public/netcdf/04091217_ruc.nc";
	private HashSet<String> customTopics;
	private Date startDate;
	private Date endDate;
	
	public void setCustomTopics(String... topics){
		customTopics  = new HashSet<String>();
		for (String topic:topics)
			customTopics.add(topic);
	}
	
	public HashSet<String> getCustomTopics(){
		return customTopics;
	}
	
	public void setStartDate(Date date){
		startDate=date;
	}
	
	public void setEndDate(Date date){
		endDate=date;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public Date getEndDate(){
		return endDate;
	}
	
	public String getGeonetworkUrl() {
		return geonetworkUrl;
	}

	public void setGeonetworkUrl(String geonetworkUrl) {
		this.geonetworkUrl = geonetworkUrl;
	}

	public String getGeonetworkUser() {
		return geonetworkUser;
	}

	public void setGeonetworkUser(String geonetworkUser) {
		this.geonetworkUser = geonetworkUser;
	}

	public String getGeonetworkPwd() {
		return geonetworkPwd;
	}

	public void setGeonetworkPwd(String geonetworkPwd) {
		this.geonetworkPwd = geonetworkPwd;
	}

	public String getThreddsCatalogUrl() {
		return threddsCatalogUrl;
	}

	public void setThreddsCatalogUrl(String threddsCatalogUrl) {
		this.threddsCatalogUrl = threddsCatalogUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getUsageField() {
		return usageField;
	}

	public void setUsageField(String usageField) {
		this.usageField = usageField;
	}

	public String getProcessdescription() {
		return processdescription;
	}

	public void setProcessdescription(String processdescription) {
		this.processdescription = processdescription;
	}

	public String getUsageLimitations() {
		return usageLimitations;
	}

	public void setUsageLimitations(String usageLimitations) {
		this.usageLimitations = usageLimitations;
	}

	public Date getSourceGenerationDate() {
		return sourceGenerationDate;
	}

	public void setSourceGenerationDate(Date sourceGenerationDate) {
		this.sourceGenerationDate = sourceGenerationDate;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceTableName) {
		this.sourceFileName = sourceTableName;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getAbstractField() {
		return abstractField;
	}

	public void setAbstractField(String abstractField) {
		this.abstractField = abstractField;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getResolution() {
		return res;
	}

	public void setResolution(double res) {
		this.res = res;
	}

	public double getXLeftLow() {
		return xLL;
	}

	public void setXLeftLow(double xLL) {
		this.xLL = xLL;
	}

	public double getXRightUpper() {
		return xRU;
	}

	public void setXRightUpper(double xRU) {
		this.xRU = xRU;
	}

	public double getYLeftLow() {
		return yLL;
	}

	public void setYLeftLow(double yLL) {
		this.yLL = yLL;
	}

	public double getYRightUpper() {
		return yRU;
	}

	public void setYRightUpper(double yRU) {
		this.yRU = yRU;
	}

	public String getLayerUrl() {
		return layerUrl;
	}

	public void setLayerUrl(String layerUrl) {
		this.layerUrl = layerUrl;
	}

	static File meta2File(Metadata meta) throws IOException, JAXBException {
		File temp = File.createTempFile("meta", ".xml");
		FileWriter writer = new FileWriter(temp);
		writer.write(XML.marshal(meta));
		writer.close();
		return temp;
	}

	public void insertMetaData() throws Exception{
		insertMetaData("3", "datasets", "_none_", true);
	}
	
	public void insertMetaData(String group, String category, String stylesheet, boolean  validate) throws Exception {

		// layer uri: wms, wfs wcs
		List<String> layerUris = new ArrayList<String>();
		layerUris.add(OGCFormatter.getWmsNetCDFUrl(layerUrl, layerName, OGCFormatter.buildBoundingBox(xLL, yLL, xRU, yRU)));
		layerUris.add(layerUrl);
		layerUris.add(OGCFormatter.getWcsNetCDFUrl(layerUrl, layerName, OGCFormatter.buildBoundingBox(xLL, yLL, xRU, yRU)));
		layerUris.add(threddsCatalogUrl);

		// layer keywords
		HashMap<KeywordType, HashSet<String>> descriptiveKeyWords = new HashMap<KeywordType, HashSet<String>>();
		HashSet<String> keySet = new HashSet<String>();
		keySet.add("THREDDS");
		keySet.add("D4Science");
		keySet.add("NetCDF");
		if (customTopics!=null)
			keySet.addAll(customTopics);
		
		descriptiveKeyWords.put(KeywordType.THEME, keySet);
		
		if (startDate!=null){
			HashSet<String> temporalkeySet = new HashSet<String>();
			temporalkeySet.add(startDate.toString());
			if (!endDate.equals(startDate))
				temporalkeySet.add(endDate.toString());
			descriptiveKeyWords.put(KeywordType.TEMPORAL, temporalkeySet);	
		}
		
		
		// author:
		DefaultResponsibleParty party = new DefaultResponsibleParty();
		party.setIndividualName(author);
		DefaultContact contact = new DefaultContact();
		contact.setContactInstructions(new DefaultInternationalString(contactInfo));
		party.setContactInfo(contact);
		party.setRole(Role.ORIGINATOR);

		// citation:
		DefaultCitation citation = new DefaultCitation();
		citation.setTitle(new DefaultInternationalString(title));
		ArrayList<DefaultCitationDate> citDates = new ArrayList<DefaultCitationDate>();
		citDates.add(new DefaultCitationDate(sourceGenerationDate, DateType.CREATION));
		citDates.add(new DefaultCitationDate(sourceGenerationDate, DateType.PUBLICATION));
		citDates.add(new DefaultCitationDate(sourceGenerationDate, DateType.REVISION));
		citation.setDates(citDates);
		ArrayList<InternationalString> citAltTitle = new ArrayList<InternationalString>();
		citAltTitle.add(new DefaultInternationalString(title));
		citation.setAlternateTitles(citAltTitle);
		citation.setEditionDate(sourceGenerationDate);
		citation.getPresentationForms().add(PresentationForm.MAP_DIGITAL);
		ArrayList<DefaultKeywords> keywordslist = new ArrayList<DefaultKeywords>();
		for (Entry<KeywordType, HashSet<String>> entry : descriptiveKeyWords.entrySet()) {
			DefaultKeywords keywords = new DefaultKeywords();
			for (String key : entry.getValue()) 
				keywords.getKeywords().add(new DefaultInternationalString(key));

			keywords.setType(entry.getKey());
			DefaultCitation thesaurus = new DefaultCitation();
			thesaurus.setTitle(new DefaultInternationalString("General"));
			thesaurus.setDates(citDates);
			keywords.setThesaurusName(thesaurus);
			keywordslist.add(keywords);
		}

		// usage:
		DefaultUsage usage = new DefaultUsage();
		usage.setSpecificUsage(new DefaultInternationalString(usageField));
		usage.setUsageDate(sourceGenerationDate);
		usage.setUserDeterminedLimitations(new DefaultInternationalString(usageLimitations));
		usage.setUserContactInfo(new ArrayList<ResponsibleParty>(Arrays.asList(party)));
		ArrayList<DefaultUsage> usages = new ArrayList<DefaultUsage>(Arrays.asList(usage));
		//build categories by guessing on the filename
		List<TopicCategory> categories = guessTopicCategory(sourceFileName);
		AnalysisLogger.getLogger().debug("Guessed Topics: "+categories);
		// Spatial Rapresentation Info
		DefaultGeometricObjects geoObjs = new DefaultGeometricObjects();
		geoObjs.setGeometricObjectType(GeometricObjectType.COMPLEX);
		DefaultVectorSpatialRepresentation spatial = new DefaultVectorSpatialRepresentation();
		spatial.setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);
		spatial.getGeometricObjects().add(geoObjs);
		
		// Extent:
		DefaultExtent extent = new DefaultExtent();
		extent.setGeographicElements(Collections.singleton(new DefaultGeographicBoundingBox(xLL, xRU, yLL, yRU)));
		extent.setDescription(new DefaultInternationalString("Bounding box"));
		
		/*Only with Geotoolkit 4.x
		DefaultTemporalExtent stext = new DefaultTemporalExtent(startDate,endDate);
		stext.setStartTime(startDate);
		stext.setEndTime(endDate);
		extent.setTemporalElements(Arrays.asList(stext));
		*/
		extent.freeze();
		
		//resolution
		DefaultNominalResolution resolution = new DefaultNominalResolution();
		resolution.setGroundResolution(res);
		resolution.setScanningResolution(res);
		DefaultResolution dres = new DefaultResolution();
		dres.setDistance(res);
		
		// layers access:
		DefaultDistribution distribution = new DefaultDistribution();
		DefaultDigitalTransferOptions transferOptions = new DefaultDigitalTransferOptions();
		for (String uri : layerUris)
			transferOptions.getOnLines().add(new DefaultOnlineResource(new URI(uri)));
		distribution.getTransferOptions().add(transferOptions);
		DefaultFormat format1 = new DefaultFormat();
		format1.setName(new DefaultInternationalString("WMS"));
		format1.setVersion(new DefaultInternationalString("1.1.0"));
		DefaultFormat format2 = new DefaultFormat();
		format2.setName(new DefaultInternationalString("OPeNDAP"));
		format2.setVersion(new DefaultInternationalString("2.0.0"));
		DefaultFormat format3 = new DefaultFormat();
		format3.setName(new DefaultInternationalString("WCS"));
		format3.setVersion(new DefaultInternationalString("1.0.0"));

		distribution.setDistributionFormats(new ArrayList<DefaultFormat>(Arrays.asList(format1, format2, format3)));

		// legal constraints
		DefaultLegalConstraints constraints = new DefaultLegalConstraints();
		constraints.getUseLimitations().add(new DefaultInternationalString("Licensed"));
		constraints.getAccessConstraints().add(Restriction.LICENSE);
		constraints.getUseConstraints().add(Restriction.LICENSE);

		// quality declaration:
		DefaultDataQuality processQuality = new DefaultDataQuality();

		//citation
		DefaultCitation sourceCitation = new DefaultCitation();
		sourceCitation.setTitle(new DefaultInternationalString(title));
		sourceCitation.getDates().add(new DefaultCitationDate(sourceGenerationDate, DateType.CREATION));
		sourceCitation.getIdentifiers().add(new DefaultIdentifier(sourceFileName));
		
		//source
		DefaultSource source = new DefaultSource();
		source.setResolution(resolution);
		source.setDescription(new DefaultInternationalString(title));
		source.setSourceCitation(sourceCitation);

		// provenance
		DefaultProcessStep preprocessStep = new DefaultProcessStep();
		DefaultProcessStep processStep = new DefaultProcessStep(preprocessStep);
		DefaultProcessing processing = new DefaultProcessing();
		processing.setSoftwareReferences(new ArrayList<DefaultCitation>(Arrays.asList(sourceCitation)));
		processStep.setDescription(new DefaultInternationalString(processdescription));
		DefaultLineage processLineage = new DefaultLineage();
		processLineage.setProcessSteps(new ArrayList<ProcessStep>(Arrays.asList(processStep)));
		processQuality.setLineage(processLineage);
		processQuality.setScope(new DefaultScope(ScopeCode.DATASET));

		// fulfill identification
		DefaultDataIdentification ident = new DefaultDataIdentification();
		ident.setCitation(citation);
		ident.setAbstract(new DefaultInternationalString(abstractField));
		ident.setPurpose(new DefaultInternationalString(purpose));
		ident.getResourceMaintenances().add(new DefaultMaintenanceInformation(MaintenanceFrequency.AS_NEEDED));
		ident.setDescriptiveKeywords(keywordslist);
		ident.setTopicCategories(categories);
		ident.setResourceSpecificUsages(usages);
		ident.setExtents(new ArrayList<DefaultExtent>(Arrays.asList(extent)));
		ident.setSpatialRepresentationTypes(new ArrayList<SpatialRepresentationType>(Arrays.asList(SpatialRepresentationType.GRID)));
		ident.setSpatialResolutions(new ArrayList<DefaultResolution>(Arrays.asList(dres)));
		ident.setLanguages(new ArrayList<Locale>(Arrays.asList(Locale.ENGLISH)));
		
		// Metadata Obj:
		DefaultMetadata meta = new DefaultMetadata(party, sourceGenerationDate, ident);
		meta.getSpatialRepresentationInfo().add(spatial);
		meta.setDistributionInfo(distribution);
		meta.getMetadataConstraints().add(constraints);
		meta.getDataQualityInfo().add(processQuality);
		meta.setLanguage(Locale.ENGLISH);
//		System.out.println(meta);
		GNClient client = new GNClient(geonetworkUrl);
		client.login(geonetworkUser, geonetworkPwd);
		File tmetafile = meta2File(meta);
		client.insertMetadata(new GNInsertConfiguration(group, category, stylesheet,validate), tmetafile);
		tmetafile.delete();
	
	}

	public static List<TopicCategory> guessTopicCategory(String refString){
		String searcher = refString.toLowerCase();
		List<TopicCategory> categories = new ArrayList<TopicCategory>();
		for (TopicCategory topic:TopicCategory.values()){
			if (searcher.contains("_"+topic.name().toLowerCase()+"_")){
				categories.add(topic);
			}
		}
		return categories;
	}
}
