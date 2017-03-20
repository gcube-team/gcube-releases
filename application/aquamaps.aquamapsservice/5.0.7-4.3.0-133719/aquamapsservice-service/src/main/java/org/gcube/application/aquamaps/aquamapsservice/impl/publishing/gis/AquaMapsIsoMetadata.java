package org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis;

import java.net.URISyntaxException;
import java.util.Date;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.MissingInformationException;
import org.gcube.spatial.data.geonetwork.utils.StringValidator;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.lineage.DefaultLineage;
import org.geotoolkit.metadata.iso.lineage.DefaultNominalResolution;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessStep;
import org.geotoolkit.metadata.iso.lineage.DefaultSource;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.quality.DefaultScope;
import org.geotoolkit.util.DefaultInternationalString;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;

public class AquaMapsIsoMetadata extends GcubeISOMetadata {

	private static final String aquamapsAlgorithmCitation="Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, "+
			"S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: "+
			"Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008.";
	
	
	private ObjectType type=null;
	private AlgorithmType algorithm=null;
	private String sourceTitle=null;
	private Date sourceGenerationTime=null;
	private String sourceTableName=null;
	
	
	public AquaMapsIsoMetadata() throws Exception {
		super();
		setPresentationForm(PresentationForm.MAP_DIGITAL);
		addTopicCategory(TopicCategory.BIOTA);
		setGeometricObjectType(GeometricObjectType.SURFACE);
		setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);
		setResolution(0.5d);
		addCredits(aquamapsAlgorithmCitation);
	}

	@Override
	public String getAbstractField() {
		return "This "+getTitle()+" "+(getType().equals(ObjectType.SpeciesDistribution)?"Species Distribution":"Biodiversity")+" Map has been generated with the AquaMaps methodology " +
				"by exploiting the technology and the computational resources provided by iMarine. "+
			"In particular, this map has been produced using the "+sourceTitle+" dataset, generated using AquaMaps "+getAlgorithm()+" algorithm.";
	}
	
	@Override
	public String getPurpose() {
		return "The aim of this "+(getType().equals(ObjectType.SpeciesDistribution)?"Species Distribution":"Biodiversity")+" map is to provide its users with a model-based map displaying prediction of species distributions based on occurrence records.";
	}
	
	@Override
	protected void checkConstraints() throws MissingInformationException {	
		super.checkConstraints();
		if(getType()==null) throw new MissingInformationException("Field type is mandatory");
		if(getAlgorithm()==null)throw new MissingInformationException("Field algorithm is mandatory");		
		if(!StringValidator.isValidateString(getSourceTitle())) throw new MissingInformationException("Field sourceTitle is mandatory");
		if(!StringValidator.isValidateString(getSourceTableName())) throw new MissingInformationException("Field sourceTableName is mandatory");
		if(getSourceGenerationTime()==null)throw new MissingInformationException("Field sourceGenerationTime is mandatory");
	}
	
	@Override
	public Metadata getMetadata() throws URISyntaxException,
			MissingInformationException {
		DefaultMetadata meta=(DefaultMetadata) super.getMetadata();
		meta.getDataQualityInfo().add(getDataQuality(this));
		return meta;
	}
	
	
	/**
	 * @return the type
	 */
	public ObjectType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ObjectType type) {
		this.type = type;
	}

	/**
	 * @return the algorithm
	 */
	public AlgorithmType getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(AlgorithmType algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the sourceTitle
	 */
	public String getSourceTitle() {
		return sourceTitle;
	}

	/**
	 * @param sourceTitle the sourceTitle to set
	 */
	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}

	/**
	 * @return the sourceGenerationTime
	 */
	public Date getSourceGenerationTime() {
		return sourceGenerationTime;
	}

	/**
	 * @param sourceGenerationTime the sourceGenerationTime to set
	 */
	public void setSourceGenerationTime(Date sourceGenerationTime) {
		this.sourceGenerationTime = sourceGenerationTime;
	}

	/**
	 * @return the sourceTableName
	 */
	public String getSourceTableName() {
		return sourceTableName;
	}

	/**
	 * @param sourceTableName the sourceTableName to set
	 */
	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	private static DefaultDataQuality getDataQuality(AquaMapsIsoMetadata params){
		DefaultDataQuality processQuality=new DefaultDataQuality();
		DefaultLineage processLineage=new DefaultLineage();


		DefaultSource source=new DefaultSource();
		DefaultNominalResolution sourceResolution=new DefaultNominalResolution();
		sourceResolution.setGroundResolution(0.5d);
		sourceResolution.setScanningResolution(0.5d);
		source.getSourceExtents().add(DefaultExtent.WORLD);
		//FIXME Breaks Validation
//		source.setResolution(sourceResolution);

		
		
		
		DefaultCitation sourceCitation=new DefaultCitation();
		sourceCitation.setTitle(new DefaultInternationalString(params.getSourceTitle()));
		sourceCitation.getDates().add(new DefaultCitationDate(params.getSourceGenerationTime(), DateType.CREATION));
		sourceCitation.getIdentifiers().add(new DefaultIdentifier(params.getSourceTableName()));		
		source.setSourceCitation(sourceCitation);




		DefaultProcessStep processStep=new DefaultProcessStep();
		processStep.setDescription(new DefaultInternationalString("AquaMaps Ecological Niche Modelling"));
//		DefaultProcessing processing=new DefaultProcessing();				
//		DefaultAlgorithm algorithm=new DefaultAlgorithm();
//		algorithm.setDescription(new DefaultInternationalString(AquaMapsObjectMetadataParameter.algorithmDescription));
//		StringBuilder customList=new StringBuilder();
//		for(SpeciesCustomization custom:params.getCustomizations())
//			customList.append(custom.toXML());
//		processing.setRunTimeParameters(new DefaultInternationalString(customList.toString()));
//		processing.getAlgorithms().add(algorithm);
		
		// FIXME Brakes Validation 
//		processStep.setProcessingInformation(processing);

		processLineage.setStatement(new DefaultInternationalString(aquamapsAlgorithmCitation));		




		processLineage.getProcessSteps().add(processStep);
		processLineage.getSources().add(source);
		processQuality.setLineage(processLineage);			
		processQuality.setScope(new DefaultScope(ScopeCode.DATASET));
		
		return processQuality;
	}
}
