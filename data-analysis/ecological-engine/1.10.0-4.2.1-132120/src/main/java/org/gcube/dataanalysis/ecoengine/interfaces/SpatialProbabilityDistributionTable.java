package org.gcube.dataanalysis.ecoengine.interfaces;

import java.util.Queue;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.hibernate.SessionFactory;

//implements a SpatialProbabilityDistribution where data are taken from a Database
public interface SpatialProbabilityDistributionTable extends GenericAlgorithm{
		
		//define the properties of this algorithm
		public ALG_PROPS[] getProperties();
		
		//initialization of the distribution model
		public void init(AlgorithmConfiguration config,SessionFactory dbHibConnection);

		//get the way principal info will be queried
		public String getMainInfoQuery();
		
		//get the way geographical information will be taken
		public String getGeographicalInfoQuery();
		
		//get the structure of the table which will contain the prob distribution
		public String getDistributionTableStatement();
		
		//calculate a single step of probability
		public float calcProb(Object mainInfo,Object area);
		
		//get additonal metadata list to be put in the final table
		public String getAdditionalMetaInformation();	
		
		//get the additional content to be put in the final table according to the Metadata
		public String getAdditionalInformation(Object mainInfo,Object area);
		
		//preprocessing before calculating a single probability value
		public void singleStepPreprocess(Object mainInfo,Object area);

		//postprocessing after calculating a single probability value		
		public void singleStepPostprocess(Object mainInfo,Object allAreasInformation);
		
		//preprocessing after the whole calculation
		public void postProcess();

		//get the internal processing status for the single step calculation
		public float getInternalStatus();
		
		//get a unique identifier for the object representing the main information , e.g. speciesID representing the first element to be put in the species probability insert
		public String getMainInfoID(Object mainInfo);
			
		//get a unique identifier for the geographical information: e.g. csquarecode representing the second element to be put in the species probability insert
		public String getGeographicalID(Object geoInfo);
		
		//apply a filter to a single table row representing a probability point 
		public String filterProbabiltyRow(String probabiltyRow);
		
		//apply a bulk filter when a synchronous write is enabled
		public Queue<String> filterProbabilitySet(Queue<String> probabiltyRows);
		
		//indicate if the write of the probability rows will be during the overall computation for a single mainInformation object or after the whole processing
		public boolean isSynchronousProbabilityWrite();
}
