package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.DynamicTransducer;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;

public class DynamicTransducerersFactory {

	
	public DynamicTransducerersFactory (){
		
	}
	
	
	public static Transducerer getTransducerer(AlgorithmConfiguration config) throws Exception {
		String agent = config.getAgent();
		Map<String,Transducerer> subTransducerers = getAllSubTransducerers(config);
		AnalysisLogger.getLogger().debug("DynamicTransducerFactory: Getting the following Agent:"+agent+" From the list of N Transducers: "+subTransducerers.size());
		Transducerer trans = subTransducerers.get(agent);
		return trans;	
	}

	public static List<String> getTransducerersNames(AlgorithmConfiguration config) throws Exception {
		
		Map<String,Transducerer> subTransducerers = getAllSubTransducerers(config);
		List<String> names = new ArrayList<String>();
		for (String key:subTransducerers.keySet()){
			names.add(key);
		}
		return names;
	}
	
	public static List<String> getAllDynamicTransducerers(AlgorithmConfiguration config) throws Exception {
		List<String> trans = ProcessorsFactory.getClasses(config.getConfigPath() + AlgorithmConfiguration.dynamicTransducerersFile);
		/*
		if (trans!=null && trans.size()>0)
			AnalysisLogger.getLogger().debug("Dynamic Transducerers Factory: Found "+trans.size()+" external connectors");
		else
			AnalysisLogger.getLogger().debug("Dynamic Transducerers Factory: No external connectors found!");
			*/
		return trans;
	}

	public static Map<String,Transducerer> getAllSubTransducerers(AlgorithmConfiguration config) throws Exception {
		List<String> dynatransducers = getAllDynamicTransducerers(config);
		Map<String,Transducerer> transducerList = new LinkedHashMap<String,Transducerer>(); 
		for (String dynatransducer:dynatransducers){
			Object algclass = Class.forName(dynatransducer, true, config.getAlgorithmClassLoader()).newInstance();
			DynamicTransducer g = (DynamicTransducer) algclass;
			Map<String,Transducerer> subtrans = g.getTransducers(config);
			if (subtrans!=null){
				for (String stransK:subtrans.keySet()){
					Transducerer t = subtrans.get(stransK);
					t.setConfiguration(config);
					t.init();
					transducerList.put(stransK,t);
				}
			}
		}
		return transducerList;
	}
	
	public static List<StatisticalType> getTransducerParameters(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception {
		List<StatisticalType> inputs = ProcessorsFactory.getParameters(configPath + AlgorithmConfiguration.transducererFile, algorithmName, config.getAlgorithmClassLoader());
		return inputs;
	}

	public static String getDescription(AlgorithmConfiguration config, String algorithmName) throws Exception{
		Map<String,Transducerer> subTransducerers = getAllSubTransducerers(config);
		Transducerer tr = subTransducerers.get(algorithmName);
		if (tr != null){
			tr.init();
			return tr.getDescription();
		}
		else 
			return "";
	}
	
	
}
