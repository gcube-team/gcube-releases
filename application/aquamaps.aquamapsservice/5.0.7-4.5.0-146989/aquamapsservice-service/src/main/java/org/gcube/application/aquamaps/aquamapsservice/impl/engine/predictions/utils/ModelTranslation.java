package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.utils;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Cell;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_DFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hcaf;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Coordinates;

public class ModelTranslation {

	public static final String maxCLat="maxCLat";
	public static final String minCLat="minCLat";
	
	
	public static Hspen species2HSPEN(Species s){
		Hspen toReturn=new Hspen();
		toReturn.setSpeciesID(s.getId());
		Envelope speciesEnvelope=s.extractEnvelope();
		toReturn.setDepth(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.Depth)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.Depth)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.Depth)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.Depth)+""));
		toReturn.setTemperature(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.Temperature)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.Temperature)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.Temperature)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.Temperature)+""));
		toReturn.setIceConcentration(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.IceConcentration)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.IceConcentration)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.IceConcentration)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.IceConcentration)+""));
		toReturn.setLandDistance(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.LandDistance)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.LandDistance)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.LandDistance)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.LandDistance)+""));
		toReturn.setPrimaryProduction(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.PrimaryProduction)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.PrimaryProduction)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.PrimaryProduction)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.PrimaryProduction)+""));
		toReturn.setSalinity(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.Salinity)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.Salinity)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.Salinity)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.Salinity)+""));
		//TODO coordinates
		toReturn.setCoordinates( new Coordinates(
				s.getFieldbyName(HspenFields.nmostlat+"").value(),
				s.getFieldbyName(HspenFields.smostlat+"").value(),
				s.getFieldbyName(HspenFields.wmostlong+"").value(),
				s.getFieldbyName(HspenFields.emostlong+"").value(),
				s.getFieldbyName(maxCLat).value(), 
				s.getFieldbyName(minCLat).value()));
		
		toReturn.setLayer(s.getFieldbyName(HspenFields.layer+"").value());
		
		toReturn.setMeanDepth(s.getFieldbyName(HspenFields.meandepth+"").value());
		toReturn.setPelagic(speciesEnvelope.isPelagic());
		toReturn.setLandDistanceYN(s.getFieldbyName(HspenFields.landdistyn+"").getValueAsBoolean());
		toReturn.setFaoAreas(speciesEnvelope.getFaoAreas());
		return toReturn;
	}
	
	public static Hcaf cell2Hcaf(Cell c){
		Hcaf toReturn= new Hcaf();
		toReturn.setCsquareCode(c.getCode());
		toReturn.setCenterlat(c.getFieldbyName(HCAF_SFields.centerlat+"").value());
		toReturn.setCenterlong(c.getFieldbyName(HCAF_SFields.centerlong+"").value());
		toReturn.setDepthmax(c.getFieldbyName(HCAF_DFields.depthmax+"").value());
		toReturn.setDepthmean(c.getFieldbyName(HCAF_DFields.depthmean+"").value());
		toReturn.setDepthmin(c.getFieldbyName(HCAF_DFields.depthmin+"").value());
		toReturn.setFaoaream(c.getFieldbyName(HCAF_SFields.faoaream+"").value());
		toReturn.setIceconann(c.getFieldbyName(HCAF_DFields.iceconann+"").value());
		toReturn.setLanddist(c.getFieldbyName(HCAF_SFields.landdist+"").value());
		toReturn.setOceanarea(c.getFieldbyName(HCAF_SFields.oceanarea+"").value());
		toReturn.setPrimprodmean(c.getFieldbyName(HCAF_DFields.primprodmean+"").value());
		toReturn.setSalinitybmean(c.getFieldbyName(HCAF_DFields.salinitybmean+"").value());
		toReturn.setSalinitymean(c.getFieldbyName(HCAF_DFields.salinitymean+"").value());
		toReturn.setSbtanmean(c.getFieldbyName(HCAF_DFields.sbtanmean+"").value());
		toReturn.setSstanmean(c.getFieldbyName(HCAF_DFields.sstanmean+"").value());
		return toReturn;
	}
	
	public static ArrayList<Field> Hspen2Fields(Hspen hspen){
		ArrayList<Field> toReturn=new ArrayList<Field>();
		toReturn.add(new Field(HspenFields.depthmin+"",hspen.getDepth().getMin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.depthprefmin+"",hspen.getDepth().getPrefmin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.depthprefmax+"",hspen.getDepth().getPrefmax(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.depthmax+"",hspen.getDepth().getMax(),FieldType.DOUBLE));
		
		toReturn.add(new Field(HspenFields.iceconmin+"",hspen.getIceConcentration().getMin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.iceconprefmin+"",hspen.getIceConcentration().getPrefmin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.iceconprefmax+"",hspen.getIceConcentration().getPrefmax(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.iceconmax+"",hspen.getIceConcentration().getMax(),FieldType.DOUBLE));
		
		toReturn.add(new Field(HspenFields.landdistmin+"",hspen.getLandDistance().getMin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.landdistprefmin+"",hspen.getLandDistance().getPrefmin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.landdistprefmax+"",hspen.getLandDistance().getPrefmax(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.landdistmax+"",hspen.getLandDistance().getMax(),FieldType.DOUBLE));
		
		toReturn.add(new Field(HspenFields.tempmin+"",hspen.getTemperature().getMin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.tempprefmin+"",hspen.getTemperature().getPrefmin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.tempprefmax+"",hspen.getTemperature().getPrefmax(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.tempmax+"",hspen.getTemperature().getMax(),FieldType.DOUBLE));
		
		toReturn.add(new Field(HspenFields.primprodmin+"",hspen.getPrimaryProduction().getMin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.primprodmin+"",hspen.getPrimaryProduction().getPrefmin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.primprodprefmax+"",hspen.getPrimaryProduction().getPrefmax(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.primprodmax+"",hspen.getPrimaryProduction().getMax(),FieldType.DOUBLE));
		
		toReturn.add(new Field(HspenFields.salinitymin+"",hspen.getSalinity().getMin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.salinitymin+"",hspen.getSalinity().getPrefmin(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.salinityprefmax+"",hspen.getSalinity().getPrefmax(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.salinitymax+"",hspen.getSalinity().getMax(),FieldType.DOUBLE));
		
		
		toReturn.add(new Field(HspenFields.emostlong+"",hspen.getCoordinates().getEMostLong(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.wmostlong+"",hspen.getCoordinates().getWMostLong(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.nmostlat+"",hspen.getCoordinates().getNMostLat(),FieldType.DOUBLE));
		toReturn.add(new Field(HspenFields.smostlat+"",hspen.getCoordinates().getSMostLat(),FieldType.DOUBLE));
		
		toReturn.add(new Field(HspenFields.faoareas+"",hspen.getFaoAreas()+"",FieldType.STRING));

		toReturn.add(new Field(HspenFields.pelagic+"",hspen.isPelagic()+"",FieldType.BOOLEAN));
		
		toReturn.add(new Field(HspenFields.layer+"",hspen.getLayer()+"",FieldType.STRING));
		
		return toReturn;
	}
	
}
