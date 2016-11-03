package gr.cite.geoanalytics.execution;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ModelSpecification {
	private Map<String, ModelLayer> inputLayers = null;
	private ModelLayer outputLayer = null;
	

	public Map<String, ModelLayer> getInputLayers() {
		return inputLayers;
	}
	
	public void setInputLayers(Map<String, ModelLayer> inputLayers) {
		this.inputLayers = inputLayers;
	}
	
	public ModelLayer getOutputLayer() {
		return outputLayer;
	}
	public void setOutputLayer(ModelLayer outputLayer) {
		this.outputLayer = outputLayer;
	}
	
	
	
	
}
