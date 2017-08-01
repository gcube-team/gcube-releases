package org.gcube.data.analysis.statisticalmanager.persistence.algorithms;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.exception.AlgorithmManagementException;
import org.gcube.data.analysis.statisticalmanager.experimentspace.AlgorithmCategory;
import org.gcube.data.analysis.statisticalmanager.experimentspace.FactoryComputationParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.SMAlgorithm;
import org.gcube.data.analysis.statisticalmanager.stubs.SMGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.SMParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ProcessorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.StatisticalServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmManager {


	private static Logger logger = LoggerFactory.getLogger(AlgorithmManager.class);

	private static String configPath=null;

	private static Map<AlgorithmCategory,AlgorithmCategoryDescriptor> staticallyLoadedAlgorithms=new LinkedHashMap<AlgorithmCategory,AlgorithmCategoryDescriptor>();
	private static Map<String,List<String>> foundAlgorithms=null;
	private static Map<String,List<String>> userPerspectiveMap;
	
	
	public static synchronized void initInstalledAlgorithms(String toSetConfigPath,boolean skipErrors) throws AlgorithmManagementException{
		try{
			if(configPath==null){
			// only if not configured yet
			configPath=toSetConfigPath;
			logger.trace("Initializing Algorithms, config path is "+configPath);

			AnalysisLogger.setLogger(configPath+ AlgorithmConfiguration.defaultLoggerFile);

			logger.trace("Loading installed algorithms, config path is "+configPath);
			AlgorithmConfiguration algoConfig=setUpBuildWPS(configPath);
			foundAlgorithms=ProcessorsFactory.getAllFeatures(algoConfig);
			for(Entry<String,List<String>> entry:foundAlgorithms.entrySet()){
				try{
					AlgorithmCategory category=AlgorithmCategory.valueOf(entry.getKey());			
					LinkedHashSet<AlgorithmDescriptor> descriptors= new LinkedHashSet<AlgorithmDescriptor>();					
					for(String algorithmName:entry.getValue()){
						try{
							descriptors.add(getAlgorithmDescriptor(category, algorithmName, algoConfig));
						}catch(Exception e){
							if(!skipErrors)throw new AlgorithmManagementException(e);
							logger.warn("Unable to load algorithm "+algorithmName,e);
						}
						logger.debug("Loaded "+descriptors.size()+" "+category+" algorithms");
					}
					if(!descriptors.isEmpty())staticallyLoadedAlgorithms.put(category,new AlgorithmCategoryDescriptor(category,descriptors));
				}catch(IllegalArgumentException e){
					//thrown by AlgorithmCategory.valueOf()
					logger.trace("Skipped invalid algorithm category "+entry.getKey());
				}
			}


			logger.trace("Loading user perspective");
			userPerspectiveMap=ProcessorsFactory.getAllFeaturesUser(algoConfig);
			for(Entry<String,List<String>> perspectiveEntry:userPerspectiveMap.entrySet()){
				for(String algorithmName: perspectiveEntry.getValue()){
					getAlgorithmByNameInMap(staticallyLoadedAlgorithms, algorithmName).getUserPerspectiveCategory().add(perspectiveEntry.getKey());
				}
			}
		}
		}catch(AlgorithmManagementException e){
			throw e;
		}catch(Exception e){
			throw new AlgorithmManagementException(e);
		}


		logger.trace("Loaded Algorithms : ");
		for(AlgorithmCategoryDescriptor categoryDesc:staticallyLoadedAlgorithms.values())
			logger.trace(categoryDesc.toString());		

	}

	public static String getConfigPath() {
		return configPath;
	}

	public static AlgorithmDescriptor getAlgorithmByName(String name) throws AlgorithmManagementException{
		return getAlgorithmByNameInMap(getInstalledAlgorithms(), name);
	}


	private static AlgorithmDescriptor getAlgorithmByNameInMap(Map<AlgorithmCategory,AlgorithmCategoryDescriptor> toLookInto,String algorithmName) throws AlgorithmManagementException{
		for(AlgorithmCategoryDescriptor category:toLookInto.values())
			if(category.containsAlgorithm(algorithmName))return category.getAlgorithmDescriptor(algorithmName);
		throw new AlgorithmManagementException("Unknown algorithm "+algorithmName);
	}

	private static Map<AlgorithmCategory,Set<String>> getInScope(){

		logger.debug("Getting Algorithms under scope "+ScopeUtils.getCurrentScope());

		LinkedHashMap<AlgorithmCategory,Set<String>> toReturn=new LinkedHashMap<AlgorithmCategory,Set<String>>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType eq '"+Configuration.getProperty(Configuration.GR_SECONDARY_TYPE)+"'");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		for(GenericResource genRes:client.submit(query)){
			try{
				AlgorithmCategory category=AlgorithmCategory.valueOf(genRes.profile().body().getElementsByTagName("category").item(0).getTextContent());
				if(!toReturn.containsKey(category)) toReturn.put(category, new LinkedHashSet<String>());
				toReturn.get(category).add(genRes.profile().name());
			}catch(Exception e){
				logger.warn("Unable to load algorithm from Generic Resource [name : {}, id {}]",genRes.profile().name(),genRes.id());
			}
		}
		return toReturn;
	}


	public static Map<AlgorithmCategory,AlgorithmCategoryDescriptor> getInstalledAlgorithms() throws AlgorithmManagementException{
		logger.debug("Loading dynamic algorithms, scope is "+ScopeUtils.getCurrentScope());
		AlgorithmConfiguration conf=setUpBuildWPS(configPath);
		LinkedHashMap<AlgorithmCategory,AlgorithmCategoryDescriptor> toReturn=new LinkedHashMap<>(staticallyLoadedAlgorithms);
		for(AlgorithmCategoryDescriptor categoryDescriptor:toReturn.values())
			for(Entry<String,AlgorithmDescriptor> entry : categoryDescriptor.getAlgorithms().entrySet())
				if(entry.getValue().isDinamycallyLoaded())
					try{entry.getValue().update(
							getAlgorithmDescriptor(categoryDescriptor.getCategory(), entry.getValue().getName(), conf));
					}catch(Exception e){
						String smExceptionMessage="Unable to load "+entry.getValue().getName();
						logger.warn(smExceptionMessage,e);
						if(!Boolean.parseBoolean(Configuration.getProperty(Configuration.SKIP_FAULTY_ALGORITHMS))) 
							throw new AlgorithmManagementException(smExceptionMessage, e);
					}

		return toReturn;
	}

	public static Map<AlgorithmCategory,Set<AlgorithmDescriptor>> publishMissingAlgorithms() throws AlgorithmManagementException {
		logger.debug("Publishing missing algorithms in scope "+ScopeUtils.getCurrentScope());
		Map<AlgorithmCategory,Set<String>> inScope=getInScope();
		LinkedHashMap<AlgorithmCategory,Set<AlgorithmDescriptor>> published=new LinkedHashMap<>();
		for(Entry<AlgorithmCategory,AlgorithmCategoryDescriptor> entry:getInstalledAlgorithms().entrySet()){
			logger.debug("Checking missing "+entry.getKey());
			Set<AlgorithmDescriptor> toPublishSet=new LinkedHashSet<>();
			Collection<AlgorithmDescriptor> algorithmsInCategory=entry.getValue().getAlgorithms().values();			
			if(inScope.containsKey(entry.getKey())){
				for(AlgorithmDescriptor desc:algorithmsInCategory)
					if(!inScope.get(entry.getKey()).contains(desc.getName()))toPublishSet.add(desc);			
			}else toPublishSet.addAll(algorithmsInCategory); // publish all category				

			for(AlgorithmDescriptor toPublish:toPublishSet) publish(toPublish);
			logger.debug("Published "+toPublishSet.size()+" "+entry.getKey());
			published.put(entry.getKey(), toPublishSet);
		}
		return published;
	}


	private static GenericResource publish(AlgorithmDescriptor toPublish){

		StringBuilder xmlBody=new StringBuilder();
		xmlBody.append("<category>");
		xmlBody.append(toPublish.getCategory().name());
		xmlBody.append("</category>");

		xmlBody.append("<inputs>");
		for (StatisticalType param : toPublish.getParameters().values()) {
			SMParameter smParameter = FactoryComputationParameter.createParameter(param);
			if(smParameter != null){
				xmlBody.append("<input>" + "<name>" + smParameter.getName() + "</name>"
						+ "<description>" + smParameter.getDescription()+ "</description>" 
						+ "<defaultValue>" + smParameter.getDefaultValue()+ "</defaultValue>" 
						+"<type>" + smParameter.getType().getName()+ "</type>"+
						"</input>");
			}
		}
		xmlBody.append("</inputs>");


		GenericResource toCreate=new GenericResource();
		toCreate.newProfile().description(toPublish.getDescription()).name(toPublish.getName()).type(Configuration.getProperty(Configuration.GR_SECONDARY_TYPE));


		toCreate.profile().newBody(xmlBody.toString());

		logger.trace("Publishing algorithm "+toPublish+" in scope "+ScopeUtils.getCurrentScope());
		RegistryPublisher rp=RegistryPublisherFactory.create();
		return rp.create(toCreate);
	}


	private static AlgorithmConfiguration setUpBuildWPS(String configPath) {
		AlgorithmConfiguration algoConfig = new AlgorithmConfiguration();

		algoConfig.setConfigPath(configPath);
		algoConfig.setPersistencePath(configPath);

		algoConfig.setGcubeScope(ScopeUtils.getCurrentScope());

		return algoConfig;
	}


	private static  AlgorithmDescriptor getAlgorithmDescriptor(AlgorithmCategory category,String algorithmName, AlgorithmConfiguration config) throws AlgorithmManagementException{
		try{
			logger.debug("Loading algorithm "+category+" "+algorithmName);
		List<StatisticalType> parameters=null;
		String description=null;
		StatisticalType output=null;		

		switch (category) {
		case DISTRIBUTIONS:
			parameters=GeneratorsFactory.getAlgorithmParameters(config.getConfigPath(),algorithmName);
			description=GeneratorsFactory.getDescription(config.getConfigPath(),algorithmName);
			output= GeneratorsFactory.getAlgorithmOutput(config.getConfigPath(),algorithmName);
			break;
		case EVALUATORS:
			parameters=EvaluatorsFactory.getEvaluatorParameters(config.getConfigPath(),algorithmName);
			description=EvaluatorsFactory.getDescription(config.getConfigPath(),algorithmName);
			output= EvaluatorsFactory.getEvaluatorOutput(config.getConfigPath(),algorithmName);
			break;
		case MODELS:
			parameters=ModelersFactory.getModelParameters(config.getConfigPath(),algorithmName);
			description=ModelersFactory.getDescription(config.getConfigPath(),algorithmName);
			output= ModelersFactory.getModelOutput(config.getConfigPath(),algorithmName);
			break;
		case TRANSDUCERS:
			description=TransducerersFactory.getDescription(config,algorithmName);
			try{
				parameters=TransducerersFactory.getTransducerParameters(config,algorithmName);
				output= TransducerersFactory.getTransducerOutput(config,algorithmName);
			}catch(Exception e){
				// will be set for dynamically load || output
			}
			break;
		case CLUSTERERS:
			parameters=ClusterersFactory.getClustererParameters(config.getConfigPath(),algorithmName);
			description=ClusterersFactory.getDescription(config.getConfigPath(),algorithmName);
			output= ClusterersFactory.getClustererOutput(config.getConfigPath(),algorithmName);
			break;
		}

		if(parameters==null) return new AlgorithmDescriptor(algorithmName,category); // dynamically load
		return new AlgorithmDescriptor(algorithmName, category, parameters, description,output); 
		}catch(Exception e){
			throw new AlgorithmManagementException("Unable to load algorithm "+algorithmName+", category is "+category, e);
		}

	}


	public static SMListGroupedAlgorithms asGroup(Map<AlgorithmCategory,AlgorithmCategoryDescriptor> toGroupAlgorithms){
		ArrayList<SMGroupedAlgorithms> features = new ArrayList<SMGroupedAlgorithms>();

		for(Entry<AlgorithmCategory,AlgorithmCategoryDescriptor> entry:toGroupAlgorithms.entrySet()){
			SMGroupedAlgorithms feature=new SMGroupedAlgorithms();
			ArrayList<SMAlgorithm> smAlgorithms=AlgorithmDescriptor.asList(entry.getValue().getAlgorithms().values());
			feature.setCategory(entry.getKey().name());			
			feature.setList(smAlgorithms.toArray(new SMAlgorithm[smAlgorithms.size()]));		
			features.add(feature);
		}

		return new SMListGroupedAlgorithms(	features.toArray(new SMGroupedAlgorithms[features.size()]));
	}


	public static SMListGroupedAlgorithms groupByUserPerspective(Map<AlgorithmCategory,AlgorithmCategoryDescriptor> toGroupAlgorithms){
		// group by user perspective		
		Map<String,List<AlgorithmDescriptor>> grouped=new LinkedHashMap<>();

		
		for(Entry<String,List<String>> entry:userPerspectiveMap.entrySet()){
			ArrayList<AlgorithmDescriptor> categoryList=new ArrayList<>();
			for(String toAdd:entry.getValue())
				try{
					categoryList.add(getAlgorithmByNameInMap(toGroupAlgorithms, toAdd));
				}catch(AlgorithmManagementException e){
					// algorithm might be not present in available ones
				}
			if(!categoryList.isEmpty())grouped.put(entry.getKey(), categoryList);
		}
		
		
		
//		for(AlgorithmCategoryDescriptor category:toGroupAlgorithms.values())
//			for(AlgorithmDescriptor algorithm:category.getAlgorithms().values()){
//				for(String userCategory : algorithm.getUserPerspectiveCategory()){
//					if(!grouped.containsKey(userCategory)) grouped.put(userCategory, new ArrayList<AlgorithmDescriptor>());
//					grouped.get(userCategory).add(algorithm);
//				}				
//			}

		// translate
		ArrayList<SMGroupedAlgorithms> features = new ArrayList<SMGroupedAlgorithms>();

		for(Entry<String,List<AlgorithmDescriptor>> groupedEntry:grouped.entrySet()){
			SMGroupedAlgorithms feature=new SMGroupedAlgorithms();
			ArrayList<SMAlgorithm> smAlgorithms=AlgorithmDescriptor.asList(groupedEntry.getValue());
			feature.setCategory(groupedEntry.getKey());			
			feature.setList(smAlgorithms.toArray(new SMAlgorithm[smAlgorithms.size()]));		
			features.add(feature);
		}

		return new SMListGroupedAlgorithms(	features.toArray(new SMGroupedAlgorithms[features.size()]));
	}



	/**
	 * Filters installed by scope presence and by passed parameters if any
	 * 
	 * @return
	 * @throws AlgorithmManagementException 
	 * @throws Exception 
	 */	
	public static Map<AlgorithmCategory,AlgorithmCategoryDescriptor> getAvailableAlgorithms(SMTypeParameter[] toFilterBy) throws AlgorithmManagementException{
		logger.debug("Getting available algorithms under scope "+ScopeUtils.getCurrentScope()+" filter by "+toFilterBy);
		Map<AlgorithmCategory, Set<String>> inScope=getInScope();
		Map<AlgorithmCategory,AlgorithmCategoryDescriptor> toReturn= new LinkedHashMap<>();

		for(Entry<AlgorithmCategory,AlgorithmCategoryDescriptor> entry:getInstalledAlgorithms().entrySet()){			
			if(inScope.containsKey(entry.getKey())){
				Set<String> inScopeAlgos=inScope.get(entry.getKey());
				LinkedHashSet<AlgorithmDescriptor> toSet=new LinkedHashSet<>();
				for(AlgorithmDescriptor installed:entry.getValue().getAlgorithms().values())
					if(inScopeAlgos.contains(installed.getName())){
						// Algorithm in scope
						try{
							if(toFilterBy==null || toFilterBy.length==0)	toSet.add(new AlgorithmDescriptor(installed));
							else{
								Collection<StatisticalType> algorithmParameters=installed.getParameters().values();

								// add To returned list if any of SMParameters is among the algorithm's ones
								for(SMTypeParameter param:toFilterBy)
									if(containParameter(param,algorithmParameters)){
										toSet.add(new AlgorithmDescriptor(installed));
										break;
									}

							}
						}catch(Throwable t){
							logger.warn("Invalid Algorithm "+installed,t);
						}
					}
				logger.debug("filtered "+toSet.size()+"/"+entry.getValue().getAlgorithms().size()+" "+entry.getKey()+" algorithm(s).");

				toReturn.put(entry.getKey(), new AlgorithmCategoryDescriptor(entry.getKey(), toSet));
			}else 
				logger.debug("No "+entry.getKey()+" found in current scope "+ScopeUtils.getCurrentScope());

		}

		return toReturn;		
	}


	private static boolean containParameter(SMTypeParameter typeParameter, Collection<StatisticalType> statisticalParameters) {
		try{
			if (typeParameter!=null)if(typeParameter.getName().equals(StatisticalServiceType.TABULAR)) {
				for(StatisticalType type : statisticalParameters) {
					if (type instanceof InputTable) {
						for (String template : typeParameter.getValues()) {						
							if (((InputTable)type).getTemplateNames().contains(TableTemplates.GENERIC) ||  
									((InputTable)type).getTemplateNames().contains(TableTemplates.valueOf(template)))
								return true;
						}
					}
				}
			}
			return false;
		}catch(Exception e){			
			logger.debug("Type Parameter is invalid not considering it among filters, "+(typeParameter!=null?"Name : "+typeParameter.getName()+" values "+typeParameter.getValues():" parameter null"));
			return true;
		}		
	}

}
