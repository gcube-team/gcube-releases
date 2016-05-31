package org.gcube.dataanalysis.ecoengine.interfaces;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;

public interface ComputationalAgent {
	
	//set the input parameters for this generator
	public List<StatisticalType> getInputParameters();	
	
	public String getResourceLoad();
	
	public String getResources();
	
	public float getStatus();
	
	//gets the weight of the generator: according to this the generator will be placed in the execution order
	public INFRASTRUCTURE getInfrastructure();
	
	// gets the content of the model: e.g. Table indications etc.
	public StatisticalType getOutput();
	
	public void init() throws Exception;

	public void setConfiguration(AlgorithmConfiguration config);

	public void shutdown();

	public String getDescription();
	
	public void compute() throws Exception;
	
	
}
