package org.gcube.dataanalysis.ecoengine.utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;

public class IOHelper {

	protected AlgorithmConfiguration config;
	
	public LinkedHashMap<String, String> outputParameters = new LinkedHashMap<String, String>();
	
	
	public static void addEnumerateInput(List<StatisticalType> inputs,Object itemsList, String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(Enum.class.getName(), itemsList, PrimitiveTypes.ENUMERATED, name, description, defaultvalue));
	}

	public static void addRemoteDatabaseInput(List<StatisticalType> inputs, String runtimeResourceName, String urlParameterName, String userParameterName, String passwordParameterName, String driverParameterName, String dialectParameterName) {
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASERRNAME,runtimeResourceName,"RR name"));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEURL,urlParameterName,""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEUSERNAME,userParameterName,""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEPASSWORD,passwordParameterName,""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEDRIVER,driverParameterName,""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEDIALECT,dialectParameterName,""));
	}
	
	public static void addStringInput(List<StatisticalType> inputs,String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, name, description, defaultvalue));
	}

	public static void addIntegerInput(List<StatisticalType> inputs,String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, name,description,defaultvalue));
	}
	
	public static void addDoubleInput(List<StatisticalType> inputs,String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, name,description,defaultvalue));
	}
	
	public static void addFileInput(List<StatisticalType> inputs,String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, name,description, defaultvalue));
	}
	
	public static void addRandomStringInput(List<StatisticalType> inputs,String name,String description, String defaultPrefix) {
		inputs.add(new ServiceType(ServiceParameters.RANDOMSTRING, name,description,defaultPrefix));
	}
	
	public static void addOutputString(LinkedHashMap<String, String> outputParameters, String name, String value) {
		outputParameters.put(name, value);
	}
	
	
	
	public static String getInputParameter(AlgorithmConfiguration config, String paramName) {
		return config.getParam(paramName);
	}

}
