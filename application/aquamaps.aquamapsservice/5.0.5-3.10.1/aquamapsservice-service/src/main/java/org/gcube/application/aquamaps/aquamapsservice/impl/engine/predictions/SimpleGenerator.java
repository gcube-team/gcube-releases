package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.utils.ModelTranslation;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Cell;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_DFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HSPECFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.BoundingBoxInformation;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.DistributionGeneratorInterface;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeGeneratorInterface;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeName;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hcaf;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePoint;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePointSets;

public class SimpleGenerator implements SimpleGeneratorI {

	private DistributionGeneratorInterface distributionGenerator;
	private EnvelopeGeneratorInterface envelopeGenerator;
	
	public SimpleGenerator(String path) {
		distributionGenerator= new DistributionGeneratorInterface(GenerationModel.AQUAMAPS, path);
		envelopeGenerator= new EnvelopeGeneratorInterface(EnvelopeModel.AQUAMAPS, path);
	}
	
	
	
	@Override
	public List<Field> getProbability(Species species, Cell cell, Boolean useBoundingBox, Boolean useFao)
			throws Exception {
		
		Hcaf hcaf=ModelTranslation.cell2Hcaf(cell);
		Hspen hspen=ModelTranslation.species2HSPEN(species);
		BoundingBoxInformation bb=distributionGenerator.getBoudingBox(hcaf, hspen, false);
		List<Field> row=null;
		if((useBoundingBox==bb.isInBoundingBox())&&(useFao==bb.isInFaoArea())){
			row=new ArrayList<Field>();
			row.add(new Field(SpeciesOccursumFields.speciesid+"",species.getId(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.csquarecode+"",cell.getCode(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.faoaream+"",cell.getFieldbyName(HCAF_SFields.faoaream+"").value(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.eezall+"",cell.getFieldbyName(HCAF_SFields.eezall+"").value(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.lme+"",cell.getFieldbyName(HCAF_SFields.lme+"").value(),FieldType.STRING));
			row.add(new Field(HSPECFields.boundboxyn+"",bb.isInBoundingBox()+"",FieldType.BOOLEAN));
			row.add(new Field(HSPECFields.faoareayn+"",bb.isInFaoArea()+"",FieldType.BOOLEAN));
			row.add(new Field(HSPECFields.probability+"",distributionGenerator.computeProbability(hcaf, hspen)+"",FieldType.DOUBLE));
		}
		return row;
	}

	@Override
	public List<Field> isAreaConstraints(BoundingBox bb, List<Area> areas)
			throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("NOT YET IMPLEMENTED");
	}

	
	@Override
	public List<Field> getEnvelope(Species species, Set<Cell> cells)
			throws Exception {
		OccurrencePointSets ocs = new OccurrencePointSets();
		List<OccurrencePoint> tempFeatures = new ArrayList<OccurrencePoint>();
		List<OccurrencePoint> salFeatures = new ArrayList<OccurrencePoint>();
		List<OccurrencePoint> primProdFeatures = new ArrayList<OccurrencePoint>();
		List<OccurrencePoint> landDistFeatures = new ArrayList<OccurrencePoint>();
		List<OccurrencePoint> seaIceFeatures = new ArrayList<OccurrencePoint>();
		String defaultDoubleValue=ServiceContext.getContext().getProperty(PropertiesConstants.DOUBLE_DEFAULT_VALUE);
		for(Cell c:cells){
			tempFeatures.add(new OccurrencePoint(species.getId(), c.getCode(), c.getFieldbyName(HCAF_DFields.sstanmean+"").getValueAsDouble(defaultDoubleValue)));
			salFeatures.add(new OccurrencePoint(species.getId(), c.getCode(), c.getFieldbyName(HCAF_DFields.salinitymean+"").getValueAsDouble(defaultDoubleValue)));
			primProdFeatures.add(new OccurrencePoint(species.getId(), c.getCode(), c.getFieldbyName(HCAF_DFields.primprodmean+"").getValueAsDouble(defaultDoubleValue)));
			landDistFeatures.add(new OccurrencePoint(species.getId(), c.getCode(), c.getFieldbyName(HCAF_SFields.landdist+"").getValueAsDouble(defaultDoubleValue)));
			seaIceFeatures.add(new OccurrencePoint(species.getId(), c.getCode(), c.getFieldbyName(HCAF_DFields.iceconann+"").getValueAsDouble(defaultDoubleValue)));
		}
		
		ocs.addOccurrencePointList(EnvelopeName.TEMPERATURE+"",tempFeatures);
		ocs.addOccurrencePointList(EnvelopeName.SALINITY+"",salFeatures);
		ocs.addOccurrencePointList(EnvelopeName.PRIMARY_PRODUCTION+"",primProdFeatures);
		ocs.addOccurrencePointList(EnvelopeName.LAND_DISTANCE+"",landDistFeatures);
		ocs.addOccurrencePointList(EnvelopeName.ICE_CONCENTRATION+"",seaIceFeatures);
		
		Hspen hspen=ModelTranslation.species2HSPEN(species);
		Hspen envelope=envelopeGenerator.reCalculateEnvelope(hspen, ocs);
		return ModelTranslation.Hspen2Fields(envelope);
	}
	
}
