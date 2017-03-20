package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapefileImportProperties {
	private static Logger logger = LoggerFactory.getLogger(TsvImportProperties.class);

	private String newLayerName;
	
	public ShapefileImportProperties() {
		super();
		logger.trace("Initialized default contructor for TsvImportProperties");
	}

	public String getNewLayerName() {
		return newLayerName;
	}

	public void setNewLayerName(String newLayerName) {
		this.newLayerName = newLayerName;
	}

	
	@Override
	public String toString(){
		return 	"\n" +
				"New Layer = " + newLayerName +  "\n";
	}
}
