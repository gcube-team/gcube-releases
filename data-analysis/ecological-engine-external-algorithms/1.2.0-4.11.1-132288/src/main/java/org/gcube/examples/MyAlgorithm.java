package org.gcube.examples;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;

public class MyAlgorithm extends StandardLocalInfraAlgorithm{

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initialization");
		
	}

	@Override
	public String getDescription() {
		return "An algorithm for testing deployment";
	}

	@Override
	protected void process() throws Exception {
		String myname = getInputParameter("Name");
		FileWriter fw = new FileWriter(new File(myname));
		fw.write("hi there");
		fw.close();
	}

	@Override
	protected void setInputParameters() {
		addStringInput("Name","Your Name","Gianpaolo");
		
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");
		
	}
	
	@Override
	public StatisticalType getOutput() {
		PrimitiveType file = new PrimitiveType(
				File.class.getName(), 
				new File(getInputParameter("Name")), 
				PrimitiveTypes.FILE, 
				"MyNameFile", 
				"my output file");
		
		PrimitiveType name = new PrimitiveType(
				String.class.getName(), 
				getInputParameter("Name") , 
				PrimitiveTypes.STRING, 
				"MyName", 
				"My name");
		
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		
		map.put("MyFile", file);
		map.put("MyName", name);
		
		PrimitiveType output = new PrimitiveType(
				LinkedHashMap.class.getName(), 
				map, 
				PrimitiveTypes.MAP, 
				"ResultsMap", 
				"Results Map");
		
		return output;
	}

}
