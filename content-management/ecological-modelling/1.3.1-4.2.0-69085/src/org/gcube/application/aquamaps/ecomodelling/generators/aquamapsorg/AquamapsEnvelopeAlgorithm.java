package org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.ecomodelling.generators.abstracts.AbstractEnvelopeAlgorithm;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.envelopes.SpEnv_landdist;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.envelopes.SpEnv_primprod;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.envelopes.SpEnv_salinity;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.envelopes.SpEnv_seaice;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.envelopes.SpEnv_temp;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeName;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.EnvelopeSet;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePoint;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePointSets;
import org.gcube.application.aquamaps.ecomodelling.generators.utils.DatabaseFactory;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

public class AquamapsEnvelopeAlgorithm extends AbstractEnvelopeAlgorithm{

	
	private static final String selectValues = "SELECT DISTINCT %OCCURRENCEPOINTS%.CsquareCode, %OCCURRENCEPOINTS%.SpeciesID, %HCAF%.%1$s FROM %OCCURRENCEPOINTS% INNER JOIN %HCAF% ON %OCCURRENCEPOINTS%.CsquareCode = %HCAF%.CsquareCode WHERE %OCCURRENCEPOINTS%.SpeciesID = '%2$s' AND %HCAF%.%1$s <> -9999 AND %HCAF%.%1$s is not null AND %HCAF%.OceanArea > 0 AND %OCCURRENCEPOINTS%.goodcell = '1' ORDER BY %HCAF%.%1$s";

	
	
	public AquamapsEnvelopeAlgorithm(){
		
	}
	
	//calculate envelopes on feature sets
	public EnvelopeSet calculateEnvelopes(String species, Object[] singleSpeciesValues, OccurrencePointSets occurrencePointsList){
		
		List<Object> tempvalues = new ArrayList<Object>();
		List<Object> salinityvalues = new ArrayList<Object>();
		List<Object> primprodvalues = new ArrayList<Object>();
		List<Object> icevalues = new ArrayList<Object>();
		List<Object> landdistvalues = new ArrayList<Object>();
		
		List<OccurrencePoint> list = occurrencePointsList.getOccurrenceMap().get(""+EnvelopeName.TEMPERATURE);
		for (OccurrencePoint op:list){
			tempvalues.add(op.toObjectArray());
		} 
		list = occurrencePointsList.getOccurrenceMap().get(""+EnvelopeName.SALINITY);
		for (OccurrencePoint op:list){
			salinityvalues.add(op.toObjectArray());
		}
		list = occurrencePointsList.getOccurrenceMap().get(""+EnvelopeName.PRIMARY_PRODUCTION);
		for (OccurrencePoint op:list){
			primprodvalues.add(op.toObjectArray());
		}
		list = occurrencePointsList.getOccurrenceMap().get(""+EnvelopeName.ICE_CONCENTRATION);
		for (OccurrencePoint op:list){
			icevalues.add(op.toObjectArray());
		}
		list = occurrencePointsList.getOccurrenceMap().get(""+EnvelopeName.LAND_DISTANCE);
		for (OccurrencePoint op:list){
			landdistvalues.add(op.toObjectArray());
		}
		//build up envelope set
		EnvelopeSet envSet = calcEnv(species,singleSpeciesValues,tempvalues,salinityvalues,primprodvalues,icevalues,landdistvalues);
		
		return envSet;
	}

	//the core of the procedure
	public EnvelopeSet calcEnv(String species, Object[] singleSpeciesValues, List<Object> tempvalues,List<Object> salinityvalues,List<Object> primprodvalues,List<Object> icevalues,List<Object> landdistvalues){
		if (tempvalues.size()<10){
			AnalysisLogger.getLogger().warn("WARNING: NOT ENOUGH OCCURRENCES FOR SPECIES: "+species);
			AnalysisLogger.getLogger().warn("Leaving the hspen as is");
			return new EnvelopeSet();
		}	
		//take previousValues
		Double prevIceMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,2);
		Double prevIceMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,3);
		Double prevIcePMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,4);
		Double prevIcePMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,5);
		Double prevSalinityMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,6);
		Double prevSalinityMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,7);
		Double prevSalinityPMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,8);
		Double prevSalinityPMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,9);
		Double prevLanddistMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,10);
		Double prevLanddistMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,11);
		Double prevLanddistPMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,12);
		Double prevLanddistPMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,13);
		Double prevTempMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,14);
		Double prevTempMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,15);
		Double prevTempPMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,16);
		Double prevTempPMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,17);
		Double prevPrimProdMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,18);
		Double prevPrimProdMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,19);
		Double prevPrimProdPMin = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,20);
		Double prevPrimProdPMax = AquamapsEnvelopeAlgorithm.getNumber(singleSpeciesValues,21);
		//previous values taken
		String layer = AbstractEnvelopeAlgorithm.getElement(singleSpeciesValues,1);
		
		SpEnv_temp tempEnv = new SpEnv_temp();
		tempEnv.calcEnvelope(layer, tempvalues);
		
		SpEnv_salinity salinityEnv = new SpEnv_salinity();
		salinityEnv.calcEnvelope(salinityvalues,layer);
		
		SpEnv_primprod primprodEnv = new SpEnv_primprod();
		primprodEnv.calcEnvelope(primprodvalues);
		
		SpEnv_seaice seaiceEnv = new SpEnv_seaice();
		seaiceEnv.calcEnvelope(icevalues);
		
		SpEnv_landdist landdistEnv = new SpEnv_landdist();
		landdistEnv.calcEnvelope(landdistvalues);
		
		
		String addingElements = "";
		int countchunks = 0;
		if (!tempEnv.checkPrevious(prevTempMin,prevTempMax,prevTempPMin,prevTempPMax))
			{
			AnalysisLogger.getLogger().warn("DIFFERENCE ON SPECIES: "+species+" - "+prevTempMin+","+prevTempPMin+","+prevTempPMax+","+prevTempMax+" vs "+tempEnv.toString());
				addingElements+=tempEnv.toString();
				countchunks++;
			}
		if (!salinityEnv.checkPrevious(prevSalinityMin,prevSalinityMax,prevSalinityPMin,prevSalinityPMax))
			{
			AnalysisLogger.getLogger().warn("DIFFERENCE ON SPECIES: "+species+" - "+prevSalinityMin+","+prevSalinityPMin+","+prevSalinityPMax+","+prevSalinityMax+" vs "+salinityEnv.toString());
			if (countchunks>0)
				addingElements+=",";
			addingElements+=salinityEnv.toString();
			countchunks++;
			}
		if (!primprodEnv.checkPrevious(prevPrimProdMin,prevPrimProdMax,prevPrimProdPMin,prevPrimProdPMax))
			{
			AnalysisLogger.getLogger().warn("DIFFERENCE ON SPECIES: "+species+" - "+prevPrimProdMin+","+prevPrimProdPMin+","+prevPrimProdPMax+","+prevPrimProdMax+" vs "+primprodEnv.toString());
			if (countchunks>0)
				addingElements+=",";
			addingElements+=primprodEnv.toString();
			countchunks++;
			}
		if (!seaiceEnv.checkPrevious(prevIceMin,prevIceMax,prevIcePMin,prevIcePMax))
			{
			AnalysisLogger.getLogger().warn("DIFFERENCE ON SPECIES: "+species+" - "+prevIceMin+","+prevIcePMin+","+prevIcePMax+","+prevIceMax+" vs "+seaiceEnv.toString());
			if (countchunks>0)
				addingElements+=",";
			addingElements+=seaiceEnv.toString();
			countchunks++;
			}
		if (!landdistEnv.checkPrevious(prevLanddistMin,prevLanddistMax,prevLanddistPMin,prevLanddistPMax))
			{
			AnalysisLogger.getLogger().warn("DIFFERENCE ON SPECIES: "+species+" - "+prevLanddistMin+","+prevLanddistPMin+","+prevLanddistPMax+","+prevLanddistPMax+" vs "+landdistEnv.toString());
			if (countchunks>0)
				addingElements+=",";
			addingElements+=landdistEnv.toString();
			countchunks++;
			}
		
		//build up envelope set
		EnvelopeSet envSet = new EnvelopeSet();
		envSet.addEnvelope(tempEnv.toEnvelope(EnvelopeName.TEMPERATURE));
		envSet.addEnvelope(salinityEnv.toEnvelope(EnvelopeName.SALINITY));
		envSet.addEnvelope(primprodEnv.toEnvelope(EnvelopeName.PRIMARY_PRODUCTION));
		envSet.addEnvelope(seaiceEnv.toEnvelope(EnvelopeName.ICE_CONCENTRATION));
		envSet.addEnvelope(landdistEnv.toEnvelope(EnvelopeName.LAND_DISTANCE));
		
		envSet.setEnvelopeString(addingElements);
		
		return envSet;
		
	}
	
	
	public EnvelopeSet calculateEnvelopes(String species, SessionFactory vreConnection, String occurrencePointsTable, String HcafTable, Object[] singleSpeciesValues){
		
		String dynamicSelectValues = selectValues.replace("%OCCURRENCEPOINTS%", occurrencePointsTable).replace("%HCAF%", HcafTable);
		String layer = AbstractEnvelopeAlgorithm.getElement(singleSpeciesValues,1);
		
		String TemperatureField = "SSTAnMean";
		String SalinityField = "SalinityMean";
		String PrimProdField = "PrimProdMean";
		String IceField  = "IceConAnn";
		String LanddistField = "LandDist";
		
		if ((layer != null)&&(layer.equals("b"))){
			TemperatureField = "SBTAnMean";
			SalinityField = "SalinityBMean";
		}
		
		String TemperatureQuery = String.format(dynamicSelectValues,TemperatureField,species);
		String SalinityQuery = String.format(dynamicSelectValues,SalinityField,species);
		String PrimProdQuery = String.format(dynamicSelectValues,PrimProdField,species);
		String IceQuery = String.format(dynamicSelectValues,IceField,species);
		String LanddistQuery = String.format(dynamicSelectValues,LanddistField,species);
		
		List<Object> tempvalues = DatabaseFactory.executeSQLQuery(TemperatureQuery, vreConnection);
		List<Object> salinityvalues = DatabaseFactory.executeSQLQuery(SalinityQuery, vreConnection);
		List<Object> primprodvalues = DatabaseFactory.executeSQLQuery(PrimProdQuery, vreConnection);
		List<Object> icevalues = DatabaseFactory.executeSQLQuery(IceQuery, vreConnection);
		List<Object> landdistvalues = DatabaseFactory.executeSQLQuery(LanddistQuery, vreConnection);
		
		//build up envelope set
		EnvelopeSet envSet = calcEnv(species,singleSpeciesValues,tempvalues,salinityvalues,primprodvalues,icevalues,landdistvalues);
			
		return envSet;
		
	}
	
	@Override
	public Object[] hspen2ObjectArray(Hspen hspen) {
		
		//convert hspen to object array
		Object [] singleHspen = new Object[22];
		singleHspen[0] = hspen.getSpeciesID();singleHspen[1] = hspen.getLayer();
		
		singleHspen[2] = hspen.getIceConcentration().getMin();singleHspen[3] = hspen.getIceConcentration().getMax();
		singleHspen[4] = hspen.getIceConcentration().getPrefmin();singleHspen[5] = hspen.getIceConcentration().getPrefmax();
		
		singleHspen[6] = hspen.getSalinity().getMin();singleHspen[7] = hspen.getSalinity().getMax();
		singleHspen[8] = hspen.getSalinity().getPrefmin();singleHspen[9] = hspen.getSalinity().getPrefmax();
		
		singleHspen[10] = hspen.getLandDistance().getMin();singleHspen[11] = hspen.getLandDistance().getMax();
		singleHspen[12] = hspen.getLandDistance().getPrefmin();singleHspen[13] = hspen.getLandDistance().getPrefmax();
		
		singleHspen[14] = hspen.getTemperature().getMin();singleHspen[15] = hspen.getTemperature().getMax();
		singleHspen[16] = hspen.getTemperature().getPrefmin();singleHspen[17] = hspen.getTemperature().getPrefmax();
		
		singleHspen[18] = hspen.getPrimaryProduction().getMin();singleHspen[19] = hspen.getPrimaryProduction().getMax();
		singleHspen[20] = hspen.getPrimaryProduction().getPrefmin();singleHspen[21] = hspen.getPrimaryProduction().getPrefmax();
		
		return singleHspen;
	}
	
	
	
}
