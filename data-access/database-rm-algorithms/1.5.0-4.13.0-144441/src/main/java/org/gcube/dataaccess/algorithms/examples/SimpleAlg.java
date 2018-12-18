package org.gcube.dataaccess.algorithms.examples;

import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;


public class SimpleAlg extends
StandardLocalExternalAlgorithm{

	
	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initialization");
	}

	@Override
	public String getDescription() {
		return "An algorithm for testing";
	}

	@Override
	protected void process() throws Exception {
				
		AnalysisLogger.getLogger().debug("in process");
			
	}

	@Override
	protected void setInputParameters() {
		addStringInput("Name","name","");
		
		addStringInput("Surname","surname","Liccardo");
		
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");
		
		// closes database's connection
		
	}
	
	@Override
	public StatisticalType getOutput() {
		
		
		AnalysisLogger.getLogger().debug("retrieving results");
		
		String name= getInputParameter("Name");
		
		String surname= getInputParameter("Surname");

		
		
		
		List<StatisticalType> list = getInputParameters();

		System.out.println("size: " + list.size());

		for (int i = 0; i < list.size(); i++) {

			System.out.println(list.get(i).getName()+" "+list.get(i).getDefaultValue());
			
		}
		
		
		
		PrimitiveType n = new PrimitiveType( 
		String.class.getName(), 
		getInputParameter("Name") , 
		PrimitiveTypes.STRING, 
		"Name", 
		"name");
		
		PrimitiveType s = new PrimitiveType( 
		String.class.getName(), 
		getInputParameter("Surname") , 
		PrimitiveTypes.STRING, 
		"Surname", 
		"surname");	
		
			
		
		
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		
		map.put("Name", n);
		
		map.put("Surname", s);
		
		
		AnalysisLogger.getLogger().debug("name: " + name);
		
		AnalysisLogger.getLogger().debug("surname: " + surname);
		
		return null;
	}	


}
