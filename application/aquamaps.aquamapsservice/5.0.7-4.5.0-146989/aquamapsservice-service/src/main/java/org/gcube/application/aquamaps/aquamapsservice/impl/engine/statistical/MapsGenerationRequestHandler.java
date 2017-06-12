package org.gcube.application.aquamaps.aquamapsservice.impl.engine.statistical;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MapsGenerationRequestHandler extends Thread {

	final static Logger logger= LoggerFactory.getLogger(MapsGenerationRequestHandler.class);
	
	String referenceId;
	String scopeName;
	 
	
	public MapsGenerationRequestHandler() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void run() {
		try{
			SourceGenerationRequest request=SourceGenerationRequestsManager.getById(referenceId);
			try{
				logger.trace("Starting execution for request ID "+request.getId());
				logger.debug("Request is "+request.toXML());
				SourceGenerationRequestsManager.setStartTime(request.getId());
				SourceGenerationRequestsManager.setPhasePercent(0d, request.getId());
				
//				//Generate subsets of sources selection for multiple generations
//				ArrayList<ArrayList<Resource>> sourcesSubsets=getComplexGenerationSubSets(request);
//				if(sourcesSubsets.size()==0) throw new ConfigurationException("No valid sources subset for generation request "+request.getId());
//				//checks for generation sets
//				//for each set check existing/ generating source
//					//if found bind to request
//					//else insert source reference, submit generation and link both to request
//				
//				
//				
//				
//				SMComputationConfig config=new SMComputationConfig();
//				
//				switch(comp.getLogic()){
//				case HCAF : config.setAlgorithm(comp.getAlgorithm());
//				break;
//				case HSPEC : config.setAlgorithm(comp.getAlgorithm());
//				break;
//				case HSPEN : config.setAlgorithm(comp.getAlgorithm());
//				break;
//				default : throw new ConfigurationException("Unable to set algorithm "+comp.getLogic());
//				}
//				
//				List<SMInputEntry> entries=new ArrayList<SMInputEntry>();
//				for(Entry<String,String> param:comp.getEnvironmentConfiguration().entrySet()){
//					entries.add(new SMInputEntry(param.getKey(), param.getValue()));
//				}
//				config.setParameters(new SMEntries(entries.toArray(new SMInputEntry[entries.size()])));
				
			}catch(Exception e){
				SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.error, request.getId());
			}
		}catch(Exception e){
			logger.error("Unable to execute, reference id was "+referenceId,e);
		}
	}
	
	
	
}
