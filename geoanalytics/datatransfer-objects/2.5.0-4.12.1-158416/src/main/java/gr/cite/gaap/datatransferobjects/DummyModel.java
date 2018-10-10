package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyModel {

	private static Logger logger = LoggerFactory.getLogger(DummyModel.class);
	private String layer1;
	private String newLayerName;

	
	
	public DummyModel() {
		super();
		logger.trace("Initialized default contructor for DummyModel");
	}
	public String getNewLayerName() {
		return newLayerName;
	}
	public void setNewLayerName(String newLayerName) {
		this.newLayerName = newLayerName;
	}
	public String getLayer1() {
		return layer1;
	}
	public void setLayer1(String layer1) {
		this.layer1 = layer1;
	}
}
