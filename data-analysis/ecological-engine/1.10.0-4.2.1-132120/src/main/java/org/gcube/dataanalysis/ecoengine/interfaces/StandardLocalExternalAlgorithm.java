package org.gcube.dataanalysis.ecoengine.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;

public abstract class StandardLocalExternalAlgorithm implements Transducerer {
	protected AlgorithmConfiguration config;
	protected float status = 0;
	protected List<StatisticalType> inputs = new ArrayList<StatisticalType>();
	protected ResourceFactory resourceManager;
	public LinkedHashMap<String, String> outputParameters = new LinkedHashMap<String, String>();

	protected void addEnumerateInput(Object itemsList, String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(Enum.class.getName(), itemsList, PrimitiveTypes.ENUMERATED, name, description, defaultvalue));
	}

	protected void addRemoteDatabaseInput(String runtimeResourceName, String urlParameterName, String userParameterName, String passwordParameterName, String driverParameterName, String dialectParameterName) {
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASERRNAME, runtimeResourceName, "RR name"));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEURL, urlParameterName, ""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEUSERNAME, userParameterName, ""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEPASSWORD, passwordParameterName, ""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEDRIVER, driverParameterName, ""));
		inputs.add(new DatabaseType(DatabaseParameters.REMOTEDATABASEDIALECT, dialectParameterName, ""));
	}

	protected void addStringInput(String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, name, description, defaultvalue));
	}

	protected void addIntegerInput(String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, name, description, defaultvalue));
	}

	protected void addBooleanInput(String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(Boolean.class.getName(), null, PrimitiveTypes.BOOLEAN, name, description, defaultvalue));
	}
	
	protected void addFileInput(String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, name, description, defaultvalue));
	}
	
	protected void addDoubleInput(String name, String description, String defaultvalue) {
		inputs.add(new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, name, description, defaultvalue));
	}

	protected void addOutputString(String name, String value) {
		outputParameters.put(name, value);
	}

	@Override
	public void compute() throws Exception {
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		status = 0;
		try {
			process();
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("StandardLocalExternalAlgorithm->Error in processing");
			throw e;
		} finally {
			status = 100;
		}
	}

	protected void log(Object message) {
		AnalysisLogger.getLogger().debug(message);
	}

	@Override
	public abstract void init() throws Exception;

	@Override
	public abstract String getDescription();

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		setInputParameters();
		return inputs;
	}

	protected String getInputParameter(String paramName) {
		return config.getParam(paramName);
	}

	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(), PrimitiveType.stringMap2StatisticalMap(outputParameters), PrimitiveTypes.MAP, "Species Match", "");
		return p;
	}

	public String getResourceLoad() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	@Override
	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

	@Override
	public float getStatus() {
		return status;
	}

	protected abstract void process() throws Exception;

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	protected abstract void setInputParameters();

	@Override
	public abstract void shutdown();

}
