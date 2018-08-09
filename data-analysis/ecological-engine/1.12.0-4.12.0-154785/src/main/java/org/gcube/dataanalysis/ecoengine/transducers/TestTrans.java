package org.gcube.dataanalysis.ecoengine.transducers;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;

public class TestTrans implements Transducerer{
	
	float status = 0;
	
	@Override
	public INFRASTRUCTURE getInfrastructure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatisticalType getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compute() throws Exception {
		// TODO Auto-generated method stub
		
	}
	ResourceFactory resourceManager;
	public String getResourceLoad() {
		if (resourceManager==null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}


	@Override
	public String getResources() {
		return ResourceFactory.getResources(100f);
	}
}
