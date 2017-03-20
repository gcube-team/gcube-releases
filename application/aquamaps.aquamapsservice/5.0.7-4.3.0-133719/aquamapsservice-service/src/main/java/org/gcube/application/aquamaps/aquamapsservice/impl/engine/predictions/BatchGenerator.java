package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory.BatchPoolType;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.DBDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.MaxMinGenerator;
import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.EnvelopeGenerator;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables.INTERPOLATIONFUNCTIONS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BatchGenerator implements BatchGeneratorI {

	final static Logger logger= LoggerFactory.getLogger(BatchGenerator.class);

	private EngineConfiguration e = new EngineConfiguration();

	private static final int NUM_OF_THREADS=2;

	private DistributionGenerator dg =null;
	private EnvelopeGenerator eg=null;
	private Integer internalId;
	private InterpolateTables interpolator=null;


	private BatchPoolType type;


	public BatchGenerator(String path,DBDescriptor credentials) {
		setConfiguration(path, credentials);
	}


	@Override
	public String generateHSPECTable(String hcaf, String hspen,String filteredHSPEN,
			AlgorithmType type,Boolean iscloud,String endpoint) throws Exception {

		return generateHSPEC(hcaf, hspen, filteredHSPEN,
				type.equals(AlgorithmType.NativeRange)||type.equals(AlgorithmType.NativeRange2050),
				type.equals(AlgorithmType.SuitableRange2050)||type.equals(AlgorithmType.NativeRange2050), 
				NUM_OF_THREADS,
				"", "", "", new HashMap<String, String>(), GenerationModel.AQUAMAPS,SourceManager.getToUseTableStore());
	}
	@Override
	public void setConfiguration(String path, DBDescriptor credentials) {
		logger.trace("***** SETTING BATCH GENERATOR CONFIGURATION (path : "+path+")");
		//path to the configuration directory
		e.setConfigPath(path);
		//remote db username (default defined in the configuration)

		e.setDatabaseUserName(credentials.getUser());
		logger.trace("user : "+credentials.getUser());
		//remote db password (default defined in the configuration)
		e.setDatabasePassword(credentials.getPassword());
		logger.trace("user : "+credentials.getPassword());
		//remote db URL (default defined in the configuration)
		String url= "jdbc:postgresql:"+credentials.getEntryPoint();
		e.setDatabaseURL(url);
		//number of threads to use in the calculation
		//		e.setNumberOfThreads(NUM_OF_THREADS);
		//create table if it doesn't exist
		e.setCreateTable(true);



		logger.trace("passed argument : user "+e.getDatabaseUserName());
		logger.trace("passed argument : password "+e.getDatabasePassword());
		logger.trace("passed argument : url "+e.getDatabaseURL());
		logger.trace("passed argument : threads num "+e.getNumberOfThreads());
	}

	public BatchGenerator(BatchPoolType type) {
		this.internalId=this.hashCode();
		this.type=type;
		logger.trace("Created batch "+type+"generator with ID "+internalId);
	}

	public BatchPoolType getType() {
		return type;
	}

	@Override
	public EnvironmentalExecutionReportItem getReport(boolean getResourceInfo) {
		//		logger.trace("Forming report, my ID is "+getReportId());
		//		logger.trace("DistributionGenerator = "+dg);
		EnvironmentalExecutionReportItem toReturn=null;
		if(dg!=null){
			toReturn= new EnvironmentalExecutionReportItem();
			toReturn.setPercent(dg.getStatus());
			if(getResourceInfo){
				toReturn.setResourceLoad(dg.getResourceLoad());
				toReturn.setResourcesMap(dg.getResources());
				toReturn.setElaboratedSpecies(dg.getSpeciesLoad());
			}
		}else if(eg!=null){
			toReturn= new EnvironmentalExecutionReportItem();
			toReturn.setPercent(eg.getStatus());
			if(getResourceInfo){
				toReturn.setResourceLoad(eg.getResourceLoad());
				toReturn.setResourcesMap(eg.getResources());
				toReturn.setElaboratedSpecies(eg.getSpeciesLoad());
			}
		}else if(interpolator!=null){
			toReturn= new EnvironmentalExecutionReportItem();
			toReturn.setPercent(new Double(interpolator.getStatus()));
			if(getResourceInfo){
				//Not available
				//				toReturn.setResourceLoad(interpolator.getResourceLoad());
				//				toReturn.setResourcesMap(interpolator.getResources());
				//				toReturn.setElaboratedSpecies(interpolator.getSpeciesLoad());
			}
		}


		return toReturn;
	}


	@Override
	public int getReportId() {
		return internalId;
	}


	@Override
	public void generateTable(final TableGenerationConfiguration configuration)
	throws Exception {
		final BatchGeneratorI instance=this;
		Thread t=new Thread (){ 
			public void run() {
				ArrayList<String> toReturn=new ArrayList<String>();
				try{
					switch(configuration.getLogic()){
					case HSPEC :

						toReturn.add(generateHSPEC(configuration.getSources().get(ResourceType.HCAF).get(0).getTableName(),
								configuration.getSources().get(ResourceType.HSPEN).get(0).getTableName(),
								configuration.getMaxMinHspenTable(),
								configuration.getAlgorithm().equals(AlgorithmType.NativeRange)||configuration.getAlgorithm().equals(AlgorithmType.NativeRange2050),
								configuration.getAlgorithm().equals(AlgorithmType.SuitableRange2050)||configuration.getAlgorithm().equals(AlgorithmType.NativeRange2050),
								configuration.getPartitionsNumber(),
								configuration.getBackendUrl(),
								configuration.getAuthor(),
								configuration.getExecutionEnvironment(),
								configuration.getConfiguration(),
								configuration.getSubmissionBackend().equalsIgnoreCase(ServiceContext.getContext().getName())?GenerationModel.AQUAMAPS:GenerationModel.REMOTE_AQUAMAPS,
										SourceManager.getToUseTableStore())); 
						break;
					case HSPEN : toReturn.add(generateHSPEN(configuration.getSources().get(ResourceType.HCAF).get(0).getTableName(),
							configuration.getSources().get(ResourceType.HSPEN).get(0).getTableName(),
							configuration.getSources().get(ResourceType.OCCURRENCECELLS).get(0).getTableName(),
							configuration.getPartitionsNumber(),
							configuration.getBackendUrl(),
							configuration.getAuthor(),
							configuration.getExecutionEnvironment(),
							configuration.getConfiguration(),
							EnvelopeModel.AQUAMAPS,
							SourceManager.getToUseTableStore()));
					break;

					case HCAF :
						int firstHcaf=0;
						int secondHcaf=0;
						int firstHcafTime=0;
						int secondHcafTime=0;
						int numInterpolations=0;
						for(Field f:configuration.getAdditionalParameters()){
							if(f.name().equals(SourceGenerationRequest.FIRST_HCAF_ID)) firstHcaf=f.getValueAsInteger();
							else if(f.name().equals(SourceGenerationRequest.SECOND_HCAF_ID)) secondHcaf=f.getValueAsInteger();
							else if(f.name().equals(SourceGenerationRequest.FIRST_HCAF_TIME)) firstHcafTime=f.getValueAsInteger();
							else if(f.name().equals(SourceGenerationRequest.SECOND_HCAF_TIME)) secondHcafTime=f.getValueAsInteger();
							else if(f.name().equals(SourceGenerationRequest.NUM_INTERPOLATIONS)) numInterpolations=f.getValueAsInteger();
						}
						if(firstHcaf==0) throw new Exception("Unable to select first HCAF");
						if(secondHcaf==0) throw new Exception("Unable to select second HCAF");
						if(firstHcafTime==0) throw new Exception("Unable to detect first HCAF time");
						if(secondHcafTime==0) throw new Exception("Unable to detect second HCAF time");
						if(numInterpolations==0) throw new Exception("Unable to detect num Interpolations");
						Resource first=null;
						Resource second=null;
						for(Resource hcaf:configuration.getSources().get(ResourceType.HCAF)){
							if(hcaf.getSearchId()==firstHcaf) first=hcaf;
							else if(hcaf.getSearchId()==secondHcaf) second=hcaf;
						}
						if(first==null)throw new Exception("First hcaf not found, passed id : "+firstHcaf);
						if(second==null)throw new Exception("Second hcaf not found, passed id : "+secondHcaf);


						toReturn.addAll(generateHCAF(
								first.getTableName(),second.getTableName(),
								numInterpolations,INTERPOLATIONFUNCTIONS.valueOf(configuration.getAlgorithm()+""),
								firstHcafTime,secondHcafTime,SourceManager.getToUseTableStore()));
						break;
					}
					Collections.sort(toReturn);
					configuration.registerGeneratedSourcesCallback(toReturn);
				}catch(Exception e){
					logger.error("Unexpected error, request was "+configuration);
					configuration.notifyError(e);
				}finally{configuration.release(instance);}		
			}
		};
		t.start();
	}


	private String generateHSPEC(String hcafTable, String hspenTable,String maxMinHspen,boolean isNative,boolean is2050,int threadNum,
			String calculatorUrl,String calculationUser,String executioneEnvironment,HashMap<String,String> calculationConfig,GenerationModel model,String tableStore)throws Exception{

		String toGenerate=ServiceUtils.generateId("hspec", "");

		logger.debug("Current generator instance is "+this.toString());
		logger.debug("Using Engine "+e.toString());
		logger.trace("generating hspec : "+toGenerate);

		logger.trace("hspen : "+hspenTable);
		logger.trace("MAX MIN LAT To use : "+maxMinHspen);
		logger.trace("hcaf : "+hcafTable);
		logger.trace("native : "+isNative);
		logger.trace("2050 : "+is2050);
		logger.trace("thread N : "+threadNum);
		logger.trace("url : "+calculatorUrl);
		logger.trace("calculation user : "+calculationUser);
		logger.trace("model : "+model);
		logger.trace("environment : "+executioneEnvironment);
		logger.trace("config values : "+calculationConfig.size());



		try{
			//hspen reference table
			e.setHspenTable(hspenTable);
			//hcaf reference table
			e.setHcafTable(hcafTable);
			//output table - created if the CreateTable flag is true
			e.setDistributionTable(toGenerate);
			//native generation flag set to false - default value
			e.setNativeGeneration(isNative);
			//2050 generation flag set to false - default value
			e.setType2050(is2050);


			e.setMaxminLatTable(maxMinHspen);

			e.setGenerator(model);
			e.setRemoteCalculator(calculatorUrl);
			e.setServiceUserName(calculationUser);

			e.setRemoteEnvironment(executioneEnvironment);
			e.setNumberOfThreads(threadNum);
			e.setGeneralProperties(calculationConfig);
			e.setGenerator(model);
			e.setTableStore(tableStore);

			dg= new DistributionGenerator(e);
			logger.debug("Distribution Generator inited, gonna execute generation.. ");
			//calculation
			dg.generateHSPEC();

			return toGenerate;
		}catch(Exception e){
			logger.warn("Execution failed, exception was "+e.getMessage());
			cleanDirtyTables(toGenerate);
			throw e;
		}
	}

	private String generateHSPEN(String hcafTable, String hspenTable,String occurrenceCellsTable, int threadNum,
			String calculatorUrl,String calculationUser,String executioneEnvironment,HashMap<String,String> calculationConfig,EnvelopeModel model,String tableStore)throws Exception{


		String toGenerate=ServiceUtils.generateId("hspen", "");


		logger.debug("Current generator instance is "+this.toString());
		logger.debug("Using Engine "+e.toString());
		logger.trace("generating hspen : "+toGenerate);

		logger.trace("hspen : "+hspenTable);
		logger.trace("hcaf : "+hcafTable);

		logger.trace("thread N : "+threadNum);
		logger.trace("url : "+calculatorUrl);
		logger.trace("calculation user : "+calculationUser);
		logger.trace("model : "+model);
		logger.trace("environment : "+executioneEnvironment);
		logger.trace("config values : "+calculationConfig.size());



		try{
			//hspen reference table
			e.setOriginHspenTable(hspenTable);

			e.setHspenTable(toGenerate);
			//hcaf reference table
			e.setHcafTable(hcafTable);

			e.setOccurrenceCellsTable(occurrenceCellsTable);
			e.setEnvelopeGenerator(model);


			e.setRemoteCalculator(calculatorUrl);
			e.setServiceUserName(calculationUser);

			e.setRemoteEnvironment(executioneEnvironment);
			e.setNumberOfThreads(threadNum);
			e.setGeneralProperties(calculationConfig);

			e.setTableStore(tableStore);


			eg=new EnvelopeGenerator(e);

			eg.reGenerateEnvelopes();

			logger.trace("Generating Max Min table..");

			MaxMinGenerator maxmin = new MaxMinGenerator(e);
			maxmin.populatemaxminlat(toGenerate);

			return toGenerate;
		}catch(Exception e){
			cleanDirtyTables(toGenerate);
			throw e;
		}
	}

	private List<String> generateHCAF(String startingHCAF,String endHCAF,int numIntervals,INTERPOLATIONFUNCTIONS function,int startingTime,int endTime,String tableStore)throws Exception{
		logger.debug("Current generator instance is "+this.toString());
		logger.debug("Using Engine "+e.toString());
		ArrayList<String> toReturn=new ArrayList<String>();
		interpolator=new InterpolateTables(e.getConfigPath(), ServiceContext.getContext().getFolderPath(FOLDERS.TABLES), e.getDatabaseURL(), e.getDatabaseUserName(), e.getDatabasePassword());

		interpolator.interpolate(startingHCAF, endHCAF, numIntervals, function,startingTime,endTime);
		toReturn.addAll(Arrays.asList(interpolator.getInterpolatedTables()));

		//Removing first and last because are passed source tables
		toReturn.remove(0);
		toReturn.remove(toReturn.size()-1);

		return toReturn;
	}


	private void cleanDirtyTables(String toDelete){
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();			
			session.dropTable(toDelete);
		}catch(Exception e){
			logger.error("Unexpected Exception while trying to delete table "+toDelete,e);
		}finally{
			if(session!=null) try{
				session.close();
			}catch(Exception e){
				logger.error("Unable to close connection ",e);
			}
		}
	}
}
