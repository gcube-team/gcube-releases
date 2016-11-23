package org.gcube.application.aquamaps.ecomodelling.generators.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeGeneratorInterface;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeName;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePoint;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePointSets;

public class TestFineProcessing {

	public static void main(String[] args) throws Exception {
		
		//EXAMPLE 1:
		//Regeneration of a HSpen object from a previous one
		//the information will be taken from the database by the generation algorithm
		//the initialization is based on the Envelope Model to use and on the suggestion of the configuration path for the algorithm (log config., database defaults, etc.)
		EnvelopeGeneratorInterface eg = new EnvelopeGeneratorInterface(EnvelopeModel.AQUAMAPS,"./cfg/");
		
		//CONFIGURATION
		//setup a configuration for the service
		EngineConfiguration e = new EngineConfiguration();
		//database information - if the database connection is necessary
		e.setDatabaseUserName("utente");
		e.setDatabasePassword("d4science");
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		//..set other DB parameters if you want
		//set the occurrence cells table name
		e.setOccurrenceCellsTable("occurrencecells");
		//set the reference hcaf table
		e.setHcafTable("hcaf_d");
		
		//the following is an example of hspen object which can be passed for regeneration 
		Hspen hspenExample = new Hspen();
		hspenExample.setSpeciesID("Fis-116939");
		hspenExample.setLayer("s");
		hspenExample.setPrimaryProduction(new Envelope("224","227","1223","1687"));
		hspenExample.setSalinity(new Envelope("33.66","34.16","35.16","36.34"));
		hspenExample.setTemperature(new Envelope("22.02","25.43","28.44","32.64"));
		hspenExample.setIceConcentration(new Envelope("-1","0","0","0"));
		hspenExample.setLandDistance(new Envelope("14.5","16","27","111"));
		//regenerate an Hspen Object with updated values
		Hspen newHspen = eg.reCalculateEnvelope(e, hspenExample);
		//END OF EXAMPLE 1
		
		System.out.println("computation finished for Example - 1\n");
		
		//EXAMPLE 2:
		//Regeneration of a list of HSpen objects from a list of previous objects
		EnvelopeGeneratorInterface eg2 = new EnvelopeGeneratorInterface(EnvelopeModel.AQUAMAPS,"./cfg/");
		//another example hspen object is built
		Hspen hspenExample2 = new Hspen();
		hspenExample2.setSpeciesID("Fis-112490");
		hspenExample2.setLayer("s");
		hspenExample2.setPrimaryProduction(new Envelope("224","227","1223","1687"));
		hspenExample2.setSalinity(new Envelope("33.66","34.16","35.16","36.34"));
		hspenExample2.setTemperature(new Envelope("22.02","25.43","28.44","32.64"));
		hspenExample2.setIceConcentration(new Envelope("-1","0","0","0"));
		hspenExample2.setLandDistance(new Envelope("14.5","16","27","111"));
		//the list of hspen objects is built
		List<Hspen> hspenList = new ArrayList<Hspen>();
		hspenList.add(hspenExample);
		hspenList.add(hspenExample2);
		//as configuration, the one from Example 1 is used 
		//the database is used internally
		//a new list is generated
		List<Hspen> newHspen2 = eg2.reCalculateEnvelope(e, hspenList);
		//END OF EXAMPLE 2
		
		System.out.println("computation finished for Example - 2\n");
		
		
		//EXAMPLE 3:
		//Regeneration of an HSpen object from a previous one.
		//the occurrence point feature is passed from outside
		//database connection is not used as the features list is passed from outside
		EnvelopeGeneratorInterface eg3 = new EnvelopeGeneratorInterface(EnvelopeModel.AQUAMAPS,"./cfg/");
		//building of an example set of occurrence points
		//a set of features for a single species will be built
		OccurrencePointSets ocs = new OccurrencePointSets();
		String exampleSpecies = "Fis-116939";
		//building of the temperature features
		//note that the name can be taken form a preset enumeration object
		String occurrenceListTemperature = ""+EnvelopeName.TEMPERATURE;
		//an oredered list of squares and values is provided which must contain at least 20 elements
		List<OccurrencePoint> orderedTemperatureFeatures = new ArrayList<OccurrencePoint>();
		for (int i=0;i<21;i++){
			orderedTemperatureFeatures.add(new OccurrencePoint(exampleSpecies,"1000:100:"+i,27.27));
		}
		//the same is done for the remaining 4 features sets
		String occurrenceListSalinity = ""+EnvelopeName.SALINITY;
		List<OccurrencePoint> orderedSalinityFeatures = new ArrayList<OccurrencePoint>();
		for (int i=0;i<21;i++){
			orderedSalinityFeatures.add(new OccurrencePoint(exampleSpecies,"1000:100:"+i,34.826));
		}
		String occurrenceListPrimProd = ""+EnvelopeName.PRIMARY_PRODUCTION;
		List<OccurrencePoint> orderedPrimProdFeatures = new ArrayList<OccurrencePoint>();
		for (int i=0;i<21;i++){
			orderedPrimProdFeatures.add(new OccurrencePoint(exampleSpecies,"1000:100:"+i,450.00));
		}
		String occurrenceListLanddist = ""+EnvelopeName.LAND_DISTANCE;
		List<OccurrencePoint> orderedLanddistFeatures = new ArrayList<OccurrencePoint>();
		for (int i=0;i<21;i++){
			orderedLanddistFeatures.add(new OccurrencePoint(exampleSpecies,"1000:100:"+i,594.00));
		}
		String occurrenceListIceConcentration = ""+EnvelopeName.ICE_CONCENTRATION;
		List<OccurrencePoint> orderedIceConcentrationFeatures = new ArrayList<OccurrencePoint>();
		for (int i=0;i<21;i++){
			orderedIceConcentrationFeatures.add(new OccurrencePoint(exampleSpecies,"1000:100:"+i,27.27));
		}
		//
		
		//add the features to the Occurrence Lists container, along with their names
		ocs.addOccurrencePointList(occurrenceListTemperature,orderedTemperatureFeatures);
		ocs.addOccurrencePointList(occurrenceListSalinity,orderedSalinityFeatures);
		ocs.addOccurrencePointList(occurrenceListPrimProd,orderedPrimProdFeatures);
		ocs.addOccurrencePointList(occurrenceListLanddist,orderedLanddistFeatures);
		ocs.addOccurrencePointList(occurrenceListIceConcentration,orderedIceConcentrationFeatures);
		
		//recalculate the hspen by means of the previous hspen and the features lists
		Hspen newHspen3 = eg3.reCalculateEnvelope(hspenExample, ocs);
		//END OF EXAMPLE 3
		System.out.println("computation finished for Example - 3");
	}

}
