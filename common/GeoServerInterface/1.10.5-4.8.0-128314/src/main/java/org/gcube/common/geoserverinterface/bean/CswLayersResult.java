/**
 * 
 */
package org.gcube.common.geoserverinterface.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ceras
 *
 */
public class CswLayersResult {
	int resultLayersCount; // total layers found
	List<LayerCsw> layers = new ArrayList<LayerCsw>();
	
	
	/**
	 * 
	 */
	public CswLayersResult(int resultLayersCount, List<LayerCsw> layers) {
		this.layers = layers;
		this.resultLayersCount = resultLayersCount;
	}
	
	/**
	 * 
	 */
	public CswLayersResult() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the layers
	 */
	public List<LayerCsw> getLayers() {
		return layers;
	}
	
	/**
	 * @param layers the layers to set
	 */
	public void setLayers(List<LayerCsw> layers) {
		this.layers = layers;
	}
	
	
	public void addLayer(LayerCsw layer) {
		this.layers.add(layer);
	}
	
	/**
	 * @param resultLayersCount the totalLayers found to set
	 */
	public void setResultLayersCount(int resultLayersCount) {
		this.resultLayersCount = resultLayersCount;
	}
	
	/**
	 * @return the totalLayers found
	 */
	public int getResultLayersCount() {
		return resultLayersCount;
	}
}
