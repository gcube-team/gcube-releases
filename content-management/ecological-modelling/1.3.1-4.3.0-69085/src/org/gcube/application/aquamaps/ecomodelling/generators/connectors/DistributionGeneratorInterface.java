package org.gcube.application.aquamaps.ecomodelling.generators.connectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;

public class DistributionGeneratorInterface {

	double status;
	GenerationModel model;
	String configPath;
	boolean overallComputation;
	
	/**
	 * initializes the calculator
	 * @param model
	 * @param configPath
	 */
	public DistributionGeneratorInterface(GenerationModel model, String configPath){
		status = 0;
		this.model = model;
		this.configPath = configPath;
		overallComputation = false;
	}
	
	/**
	 * gets the status in percentage of the current calculation
	 * @return
	 */
	public double getStatus(){
		return status;
	}
	
	/**
	 * gets the bounding box information according to the model
	 * @param hcaf
	 * @param hspen
	 * @param gen2050
	 * @return
	 * @throws Exception
	 */
	public BoundingBoxInformation getBoudingBox(Hcaf hcaf,Hspen hspen, boolean gen2050) throws Exception{
		BoundingBoxInformation bbi = new BoundingBoxInformation();
		EngineConfiguration e = new EngineConfiguration();
		e.setGenerator(model);
		e.setUseDB(false);
		e.setType2050(gen2050);
		e.setConfigPath(configPath);
		
		DistributionGenerator generator = new DistributionGenerator(e);
		
		Object [] hspenArray = hspen.toObjectArray();
		Object [] latitudeExtent = hspen.latitudeExtent();
		Object [] hcafArray = hcaf.toObjectArray();
		
		generator.getBoundingBoxInformation(hspenArray,latitudeExtent);
		
		HashMap<String,Integer> boundingInfo = generator.calculateBoundingBox(hcafArray);
		Integer Inbox = boundingInfo.get("$InBox");
		Integer InFAO = boundingInfo.get("$InFAO");
		bbi.setInBoundingBox((Inbox==1)?true:false);
		bbi.setInFaoArea((InFAO==1)?true:false);
		
		return bbi;
	}
	
	/**
	 * computes the punctual probability according to the model
	 * @param hcaf
	 * @param hspen
	 * @return
	 * @throws Exception
	 */
	public double computeProbability(Hcaf hcaf,Hspen hspen)throws Exception {
		
		double probability = Double.MIN_VALUE;
		EngineConfiguration e = new EngineConfiguration();
		e.setGenerator(model);
		e.setUseDB(false);
		e.setConfigPath(configPath);
		DistributionGenerator generator = new DistributionGenerator(e);
		Object [] hspenArray = hspen.toObjectArray();
		Object [] hcafArray = hcaf.toObjectArray();
		probability = generator.calculateModelProbability(hspenArray, hcafArray);
		
		return probability;
	}
	
	/**
	 * computes the list of Hspec for the couples
	 * (species,hcaf1), (species,hcaf2), ... 
	 * @param hcaflist
	 * @param hspen
	 * @return
	 * @throws Exception
	 */
	public List<Hspec> compute(List<Hcaf> hcaflist,Hspen hspen)throws Exception {
		
		if (!overallComputation)
			status = 0;
		
		double probability = Double.MIN_VALUE;
		EngineConfiguration e = new EngineConfiguration();
		e.setGenerator(model);
		e.setUseDB(false);
		e.setConfigPath(configPath);
		DistributionGenerator generator = new DistributionGenerator(e);
		
		Object [] hspenArray = hspen.toObjectArray();
		Object [] latitudeExtent = hspen.latitudeExtent();
		generator.getBoundingBoxInformation(hspenArray,latitudeExtent);
		ArrayList<Hspec> hspecList = new ArrayList<Hspec>();
		int size = hcaflist.size();
		int counter = 0;
		
		for (Hcaf hcaf:hcaflist){
		
			Object [] hcafArray = hcaf.toObjectArray();
			
			probability = generator.calculateModelProbability(hspenArray, hcafArray);
			
			BoundingBoxInformation bbi = new BoundingBoxInformation();  
			HashMap<String,Integer> boundingInfo = generator.calculateBoundingBox(hcafArray);
			Integer Inbox = boundingInfo.get("$InBox");
			Integer InFAO = boundingInfo.get("$InFAO");
			bbi.setInBoundingBox((Inbox==1)?true:false);
			bbi.setInFaoArea((InFAO==1)?true:false);
			Hspec hspec = new Hspec();
			hspec.setBoundingBox(bbi);hspec.setProbability(probability);
			hspec.setCsquareCode(hcaf.getCsquarecode());hspec.setSpeciesID(hspen.getSpeciesID());
			hspec.setFaoaream(hcaf.getFaoaream());
			hspec.setEezall(hcaf.getEezall());
			hspec.setLme(hcaf.getLme());
			hspecList.add(hspec);
			counter ++;
			if (!overallComputation)
				status=(double)((int)(((double)counter*100f/(double)size)*100f))/100f;
		}
		
		if (!overallComputation)
			status = 100;
		
		return hspecList;
	}
	
	/**
	 * sequentially computes the overall hspec for the species along the cells
	 * the output map uses speciesID as key. To each key, a list of resulting Hspec is associated
	 * @param hcaflist
	 * @param hspenlist
	 * @return
	 * @throws Exception
	 */
	public HashMap<String,List<Hspec>> compute(List<Hcaf> hcaflist,List<Hspen> hspenlist)throws Exception {
		
		overallComputation = true;
		HashMap<String,List<Hspec>> HspecMap = new HashMap<String, List<Hspec>>();
		status = 0;
		int size = hspenlist.size();
		int counter = 0;
		for (Hspen hspen:hspenlist){
			List<Hspec> hspeclist = compute(hcaflist,hspen);
			HspecMap.put(hspen.getSpeciesID(), hspeclist);
			counter++;
			status = (double)((int)(((double)counter*100f/(double)size)*100f))/100f;
		}
		status = 100;
		overallComputation = false;
		return HspecMap;
	}
	
}
