package org.gcube.dataanalysis.ecoengine.interfaces;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;

public interface Model {
	
	public ALG_PROPS[] getProperties();
	
	public String getName();
	
	//gets the description of the model
	public String getDescription();
	
	//set the input parameters for this generator
	public List<StatisticalType> getInputParameters();
	
	public float getVersion();

	public void setVersion(float version);
	
	public void init(AlgorithmConfiguration Input, Model previousModel);
	
	public String getResourceLoad();
	
	public String getResources();
	
	public float getStatus();
	
	public void postprocess(AlgorithmConfiguration Input, Model previousModel);
	
	public void train(AlgorithmConfiguration Input, Model previousModel);

	public void stop();
	
	public StatisticalType getOutput();
}
