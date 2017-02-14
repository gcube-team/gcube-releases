package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.DBDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HSPECFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analyzer {

	final static Logger logger= LoggerFactory.getLogger(Analyzer.class);
	
	
	
	
	private BioClimateAnalysis bioClimate=null;
	
	private Integer internalId;
	
	
	public Analyzer(int i) {
		internalId=i;
		logger.trace("Created batch analyzer with ID "+internalId);
	}
	public EnvironmentalExecutionReportItem getReport(boolean getResources) {
		EnvironmentalExecutionReportItem toReturn=new EnvironmentalExecutionReportItem();
		float status=bioClimate.getStatus();
		logger.debug("bioclimate status for analyzer id "+internalId+" = "+status);
		toReturn.setPercent(new Double(status));
		return toReturn;
	}
	
	

	public void setConfiguration(String path, DBDescriptor credentials) throws Exception {
		logger.trace("***** SETTING ANALYZER GENERATOR CONFIGURATION (path : "+path+")");
		
		
		String user=credentials.getUser();
		String password=credentials.getPassword();
		String url="jdbc:postgresql:"+credentials.getEntryPoint();
		
		logger.trace("passed argument : user "+user);
		logger.trace("passed argument : password "+password);
		logger.trace("passed argument : url "+url);
		
		bioClimate=new BioClimateAnalysis(path, 
				ServiceContext.getContext().getFolderPath(FOLDERS.ANALYSIS),
				url, user, password, false);
		
	}
	
	public void produceImages(final AnalysisRequest toPerform) throws Exception{
		final Analyzer instance=this; 
		Thread t=new Thread(){
			@Override
			public void run() {
				AnalysisResponseDescriptor toReturn=new AnalysisResponseDescriptor(toPerform.getToPerformAnalysis());
				logger.debug("Analyzer inner thread, request is "+toPerform);
				try{
					String groupLabel=getLabel(toPerform.getToPerformAnalysis());
				
					switch(toPerform.getToPerformAnalysis()){
						case HCAF : bioClimate.hcafEvolutionAnalysis(toPerform.getTables(ResourceType.HCAF),toPerform.getLabels(ResourceType.HCAF));
									break;
						case HSPEC : bioClimate.hspecEvolutionAnalysis(toPerform.getTables(ResourceType.HSPEC),toPerform.getLabels(ResourceType.HSPEC),
								HSPECFields.probability+"", HCAF_SFields.csquarecode+"",toPerform.getHspecThreshold());
										break;
						case MIXED : bioClimate.globalEvolutionAnalysis(toPerform.getTables(ResourceType.HCAF), toPerform.getTables(ResourceType.HSPEC), 
								toPerform.getLabels(ResourceType.HCAF), toPerform.getLabels(ResourceType.HSPEC), HSPECFields.probability+"", HCAF_SFields.csquarecode+"",toPerform.getHspecThreshold());
										break;
						case GEOGRAPHIC_HCAF : bioClimate.geographicEvolutionAnalysis(toPerform.getTables(ResourceType.HCAF),toPerform.getLabels(ResourceType.HCAF));
										break;
						case GEOGRAPHIC_HSPEC : bioClimate.speciesGeographicEvolutionAnalysis(toPerform.getTables(ResourceType.HSPEC),toPerform.getLabels(ResourceType.HSPEC),toPerform.getHspecThreshold());
												break;
						case HSPEN : bioClimate.speciesEvolutionAnalysis(toPerform.getTables(ResourceType.HSPEN), toPerform.getLabels(ResourceType.HSPEN), BioClimateAnalysis.salinityMinFeature, BioClimateAnalysis.salinityDefaultRange);
						break;									
					}
					ArrayList<ImageDescriptor> generated=new ArrayList<ImageDescriptor>();
					for(Entry<String,Image> entry:bioClimate.getProducedImages().entrySet()){
						logger.debug("Adding image "+entry.getKey());
						generated.add(new ImageDescriptor(entry.getKey(),entry.getValue()));					
					}
					
				toReturn.getCategorizedImages().put(groupLabel, generated);
				}catch(Exception e){
				toReturn.getResults().put(toPerform.getToPerformAnalysis(), false);
				toReturn.getMessages().put(toPerform.getToPerformAnalysis(), "CAUSE : "+e.getMessage());
				}finally{
					toPerform.notify(toReturn, instance);
				}
			}
		};
		t.start();
	}
	
	
	private static final String getLabel(AnalysisType type){
		switch(type){
			case GEOGRAPHIC_HCAF	: return "Environment_Analysis_By_Area";
			case GEOGRAPHIC_HSPEC 	: return "Prediction_Analysis_By_Area";
			case HCAF 				: return "Overall_Environment_Analysis";
			case HSPEC				: return "Overall_Prediction_Analysis";
			case HSPEN 				: return "Envelope_Analysis";
			default : throw new IllegalArgumentException();
		}
		
	}
	
	
	public Integer getReportId() {
		return internalId;
	}
}
