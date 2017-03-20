package org.gcube.spatial.data.geonetwork.iso;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.gcube.spatial.data.geonetwork.utils.StringValidator;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;

public class GcubeISOMetadata {

	private EnvironmentConfiguration config;
	
	private String user=null;
	
	
	//Identification
	private String title=null;
	private Date creationDate=null;
	private PresentationForm presentationForm=null;
	
	private String abstractField=null;
	
	private String purpose=null;
	
	private ArrayList<String> credits=new ArrayList<String>();

	private HashMap<Thesaurus,HashSet<String>> descriptiveKeywords=new HashMap<Thesaurus, HashSet<String>>();
	
	private ArrayList<TopicCategory> topicCategories=new ArrayList<TopicCategory>();
	
	private DefaultExtent extent=(DefaultExtent) DefaultExtent.WORLD;
	
	
	
	//Spatial Representation
	
	private GeometricObjectType geometricObjectType=GeometricObjectType.SURFACE;
	
	private int geometryCount=0;
	
	private TopologyLevel topologyLevel=TopologyLevel.GEOMETRY_ONLY;
	
	private double resolution=0.5d;
	
	
	private ArrayList<String> graphicOverviewsURI=new ArrayList<String>(); 
	
	
	
	
	public GcubeISOMetadata() throws Exception {
		config=EnvironmentConfiguration.getConfiguration();
		credits.add(config.getProjectCitation());
		addKeyword(config.getProjectName(), config.getThesauri().get("General"));
	}
	

	protected void checkConstraints()throws MissingInformationException{
		if(!StringValidator.isValidateString(getUser())) throw new MissingInformationException("Field user is mandatory");
		if(!StringValidator.isValidateString(getTitle())) throw new MissingInformationException("Field title is mandatory");		
		if(getCreationDate()==null)throw new MissingInformationException("Field creationDate is mandatory");
		if(getPresentationForm()==null) throw new MissingInformationException("Field presentationForm is mandatory");

		if(!StringValidator.isValidateString(getAbstractField())) throw new MissingInformationException("Field abstractField is mandatory");
		if(!StringValidator.isValidateString(getPurpose())) throw new MissingInformationException("Field purpose is mandatory");
		
		if(getTopicCategories().size()==0) throw new MissingInformationException("At least one topic category is required");
		if(getExtent()==null) throw new MissingInformationException("Field Extent is mandatory");

		if(getGeometricObjectType()==null)throw new MissingInformationException("Field geometricObjectType is mandatory");
		if(getTopologyLevel()==null)throw new MissingInformationException("Field topology level is mandatory");	
		if(getCredits().size()==0) throw new MissingInformationException("At least one credits is needed");
		if(getDescriptiveKeywords().isEmpty())throw new MissingInformationException("Missing Descriptive keywords");
	}
	
	
	public Metadata getMetadata() throws URISyntaxException, MissingInformationException{
		checkConstraints();		
		
		return ISOMetadataFactory.generateMeta(this);
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the presentationForm
	 */
	public PresentationForm getPresentationForm() {
		return presentationForm;
	}

	/**
	 * @param presentationForm the presentationForm to set
	 */
	public void setPresentationForm(PresentationForm presentationForm) {
		this.presentationForm = presentationForm;
	}

	/**
	 * @return the abstractField
	 */
	public String getAbstractField() {
		return abstractField;
	}

	/**
	 * @param abstractField the abstractField to set
	 */
	public void setAbstractField(String abstractField) {
		this.abstractField = abstractField;
	}

	/**
	 * @return the extent
	 */
	public DefaultExtent getExtent() {
		return extent;
	}

	/**
	 * @param extent the extent to set [Default is WORLD]
	 */
	public void setExtent(DefaultExtent extent) {
		this.extent = extent;
	}

	/**
	 * @return the geometricObjectType 
	 */
	public GeometricObjectType getGeometricObjectType() {
		return geometricObjectType;
	}

	/**
	 * @param geometricObjectType the geometricObjectType to set [Default is SURFACE]
	 */
	public void setGeometricObjectType(GeometricObjectType geometricObjectType) {
		this.geometricObjectType = geometricObjectType;
	}

	/**
	 * @return the geometryCount
	 */
	public int getGeometryCount() {
		return geometryCount;
	}

	/**
	 * @param geometryCount the geometryCount to set [Default is 0]
	 */
	public void setGeometryCount(int geometryCount) {
		this.geometryCount = geometryCount;
	}

	/**
	 * @return the config
	 */
	public EnvironmentConfiguration getConfig() {
		return config;
	}

	/**
	 * @return the credits
	 */
	public ArrayList<String> getCredits() {
		return (ArrayList<String>) credits.clone();
	}

	/**
	 * Adds credits to the metadata
	 * 
	 * @param toAddCredits
	 */
	public void addCredits(String toAddCredits){
		credits.add(toAddCredits);
	}
	
	/**
	 * @return the descriptiveKeywords
	 */
	public HashMap<Thesaurus, HashSet<String>> getDescriptiveKeywords() {
		return (HashMap<Thesaurus, HashSet<String>>) descriptiveKeywords.clone();
	}

	/**
	 * Adds descriptive keywords to the metadata
	 * 
	 * @param keyword
	 * @param thesaurus
	 */
	public synchronized void addKeyword(String keyword,Thesaurus thesaurus){
		if(!descriptiveKeywords.containsKey(thesaurus)) descriptiveKeywords.put(thesaurus, new HashSet<String>());
		descriptiveKeywords.get(thesaurus).add(keyword);
	}
	
	/**
	 * @return the topicCategories
	 */
	public ArrayList<TopicCategory> getTopicCategories() {
		return (ArrayList<TopicCategory>) topicCategories.clone();
	}

	
	/**
	 * Adds a Topic Category to the metadata
	 * 
	 * @param toAdd
	 */
	public void addTopicCategory(TopicCategory toAdd){
		topicCategories.add(toAdd);
	}
	
	
	/**
	 * @return the graphicOverviewsURI
	 */
	public ArrayList<String> getGraphicOverviewsURI() {
		return (ArrayList<String>) graphicOverviewsURI.clone();
	}

	/**
	 * Adds a graphic overview uri to the metadata
	 * 
	 * @param uri
	 */
	public void addGraphicOverview(String uri){
		graphicOverviewsURI.add(uri);
	}

	
	
	/**
	 * @return the purpose
	 */
	public String getPurpose() {
		return purpose;
	}

	/**
	 * @param purpose the purpose to set
	 */
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GcubeISOMetadata [config=");
		builder.append(config);
		builder.append(", user=");
		builder.append(user);
		builder.append(", title=");
		builder.append(title);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", presentationForm=");
		builder.append(presentationForm);
		builder.append(", abstractField=");
		builder.append(abstractField);
		builder.append(", credits=");
		builder.append(credits);
		builder.append(", descriptiveKeywords=");
		builder.append(descriptiveKeywords);
		builder.append(", topicCategories=");
		builder.append(topicCategories);
		builder.append(", extent=");
		builder.append(extent);
		builder.append(", geometricObjectType=");
		builder.append(geometricObjectType);
		builder.append(", geometryCount=");
		builder.append(geometryCount);
		builder.append(", graphicOverviewsURI=");
		builder.append(graphicOverviewsURI);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 
	 * @return the current Topology Level
	 */
	public TopologyLevel getTopologyLevel() {
		return topologyLevel;
	}

	/**
	 * 
	 * @param topologyLevel the Topology level to set [Default is GEOMETRY_ONLY] 
	 */
	public void setTopologyLevel(TopologyLevel topologyLevel) {
		this.topologyLevel = topologyLevel;
	}

	/**
	 * 
	 * @return the current layer resolution
	 */
	public double getResolution() {
		return resolution;
	}

	/**
	 * 
	 * 
	 * @param resoulution  The resolution to be set [Default is 0.5]
	 */
	public void setResolution(double resoulution) {
		this.resolution = resoulution;
	}
	
	
	
	
}
