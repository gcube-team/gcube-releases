package org.gcube.application.aquamaps.ecomodelling.generators.examples.hspec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.ecomodelling.generators.connectors.BoundingBoxInformation;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.DistributionGeneratorInterface;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hcaf;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspec;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Coordinates;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope;

public class FineProcessing {

	/**
	 * This example shows how to interrogate some specific functionalities of the generative models included in the "Ecological Modelling Library" of gCube.
	 * the library can be interrogated by setting up objects representing hspen and hspec information
	 * 
	 * functionalities currently include:
	 * calculation of the presence probability for a single species in a single square (EXAMPLE 1)
	 * calculation of the bounding box information for a single species in a single square (EXAMPLE 2)
	 * calculation of the HSPEC information for a single species along several squares (EXAMPLE 3)  
	 * calculation of the HSPEC information for a group of species along several squares (EXAMPLE 4)
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		DistributionGeneratorInterface generator = new DistributionGeneratorInterface(GenerationModel.AQUAMAPS, "./cfg/");
		
		//manual setup of the hspen object
		Hspen hspen = new Hspen();
		//set species ID 
		hspen.setSpeciesID("Fis-124204");
		//build and set an Envelop object for temperature
		Envelope temperature = new Envelope("16.97","23.08","28.67","32.87");
		hspen.setTemperature(temperature);
		//other needed Envelopes
		Envelope depth = new Envelope("0","126","434","1000");
		hspen.setDepth(depth);
		Envelope iceConcentration = new Envelope("-1","0","0","0");
		hspen.setIceConcentration(iceConcentration);
		Envelope landDistance = new Envelope("4","18","202","324");
		hspen.setLandDistance(landDistance);
		Envelope primaryproduction = new Envelope("37","477","1399","1860");
		hspen.setPrimaryProduction(primaryproduction);
		Envelope salinity = new Envelope("29.26","31.55","35.31","38.54");
		hspen.setSalinity(salinity);
		//build up a Coordinates object containing data from hspen along with maximum and minimum center latitude reported in occurrence cells for this species
		Coordinates coordinates = new Coordinates("28","10","-115","-78","32.25","6.25");
		hspen.setCoordinates(coordinates);
		//set remaining parameters		
		hspen.setLayer("s");
		hspen.setMeanDepth(null);
		hspen.setPelagic(true);
		hspen.setLandDistanceYN(false);
		hspen.setFaoAreas("77");
		
		//manual setup the hcaf object
		Hcaf hcaf = new Hcaf();
		//csquare code and other parameters coming from hcaf_d and hcaf_s
		hcaf.setCsquareCode("1000:100:1");
		hcaf.setCenterlat("0.25");
		hcaf.setCenterlong("0.25");
		hcaf.setDepthmax("5014");
		hcaf.setDepthmean("4896");
		hcaf.setDepthmin("4760");
		hcaf.setFaoaream("34");
		hcaf.setIceconann("0");
		hcaf.setLanddist("594");
		hcaf.setOceanarea("3091.036");
		hcaf.setPrimprodmean("450");
		hcaf.setSalinitybmean("34.826");
		hcaf.setSalinitymean("34.76");
		hcaf.setSbtanmean("1.89");
		hcaf.setSstanmean("27.27");
	
		//EXAMPLE 1:
		System.out.println("EXAMPLE 1:");
		//calculate probability in the square for the selected species
		double probability = generator.computeProbability(hcaf, hspen);
		System.out.println("Point probability: "+ probability);

		//EXAMPLE 2:
		System.out.println("EXAMPLE 2:");
		//calculate bounding box information
		boolean generationFor2050 = false;
		BoundingBoxInformation box = generator.getBoudingBox(hcaf, hspen,generationFor2050);
		System.out.println("In Bounding Box flag:"+box.isInBoundingBox()+" InFAOArea flag:"+box.isInFaoArea());
		
		//EXAMPLE 3:
		System.out.println("EXAMPLE 3:");
		//prepare the hcaflist to be analized
		ArrayList<Hcaf> hcaflist = new ArrayList<Hcaf>();
		hcaflist.add(hcaf);
		hcaflist.add(hcaf);
		
		//calculate the hspec for the species along the squares
		List<Hspec> hspecList = generator.compute(hcaflist, hspen);
		//report results
		for (Hspec hspec:hspecList){
			System.out.println("CALCULATED HSPEC:"+hspec.getSpeciesID()+" , "+hspec.getCsquarecode()+" , "+hspec.getProbability()+" , "+hspec.getBoundingBox().isInBoundingBox()+" , "+hspec.getBoundingBox().isInFaoArea());
		}
		
		//EXAMPLE 4:
		System.out.println("EXAMPLE 4:");
		//prepare an array of species
		ArrayList<Hspen> hspenlist = new ArrayList<Hspen>();
		hspenlist.add(hspen);
		
		//calculate the hspecs along the squares for all the species - calculation is performed sequencially
		Map<String,List<Hspec>> hspecMap = generator.compute(hcaflist, hspenlist);
		//report results
		for (String speciesID:hspecMap.keySet()){
			List<Hspec> hspecsublist = hspecMap.get(speciesID);
			System.out.println("SPECIES: "+speciesID);
			for (Hspec hspecelement:hspecsublist){
				System.out.println("CALCULATED HSPEC:"+hspecelement.getSpeciesID()+" , "+hspecelement.getCsquarecode()+" , "+hspecelement.getProbability()+" , "+hspecelement.getBoundingBox().isInBoundingBox()+" , "+hspecelement.getBoundingBox().isInFaoArea());
			}
		}
		
	}
	
	
}
