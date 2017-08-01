package org.gcube.dataanalysis.ecoengine.modeling;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.interfaces.Modeler;

public class SimpleModeler implements Modeler{
	protected  Model innermodel;
	protected AlgorithmConfiguration Input;
	
	@Override
	public void model(Model previousModel) {
		innermodel.init(Input, previousModel);
		innermodel.train(Input, previousModel);
		innermodel.postprocess(Input, previousModel);
	}

	@Override
	public void compute() throws Exception{
		innermodel.init(Input, null);
		innermodel.train(Input, null);
		innermodel.postprocess(Input, null);
	}
	
	@Override
	public String getResourceLoad() {
		return innermodel.getResourceLoad();
	}

	@Override
	public String getResources() {
		return innermodel.getResources();
	}

	@Override
	public Model getModel() {
		return innermodel;
	}

	@Override
	public void setmodel(Model model) {
		innermodel = model;
	}

	@Override
	public float getStatus() {
		return innermodel.getStatus();
	}

	@Override
	public void shutdown() {
		innermodel.stop();
	}

	@Override
	public ALG_PROPS[] getSupportedModels() {
		ALG_PROPS[] props = {ALG_PROPS.SPECIES_ENVELOPES,ALG_PROPS.SPECIES_MODEL};
		return props;
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		return new ArrayList<StatisticalType>();
//		return innermodel.getInputParameters();
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	public StatisticalType getOutput() {
		return innermodel.getOutput();
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		Input = config;
	}

	@Override
	public String getDescription() {
		return "A Generic Modeler invoking training";
	}

}
