package org.gcube.dataanalysis.ecoengine.interfaces;

import java.util.List;
import java.util.Map;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

//implements a SpatialProbabilityDistribution where data are taken from a Database
public interface SpatialProbabilityDistributionGeneric extends GenericAlgorithm{
			
		//initialization of the distribution model
		public void init(AlgorithmConfiguration config) throws Exception;
		
		public String getMainInfoType();
		
		public String getGeographicalInfoType();

		public List<Object> getMainInfoObjects();

		//get the way geographical information will be taken
		public List<Object> getGeographicalInfoObjects();
		
		//calculate a single step of probability
		public float calcProb(Object mainInfo,Object area);
		
		//preprocessing before calculating a single probability value
		public void singleStepPreprocess(Object mainInfo,Object area);

		//postprocessing after calculating a single probability value		
		public void singleStepPostprocess(Object mainInfo,Object allAreasInformation);
		
		//preprocessing after the whole calculation
		public void postProcess();

		//store the result of the probability distribution model: e.g. for the input species -> csquare , probability
		public void storeDistribution(Map<Object,Map<Object,Float>> distribution) throws Exception;
		
		//get the internal processing status for the single step calculation
		public float getInternalStatus();
		
		//get a unique identifier for the object representing the main information , e.g. speciesID representing the first element to be put in the species probability insert
		public String getMainInfoID(Object mainInfo);
			
		//get a unique identifier for the geographical information: e.g. csquarecode representing the second element to be put in the species probability insert
		public String getGeographicalID(Object geoInfo);
		
		
		
}
