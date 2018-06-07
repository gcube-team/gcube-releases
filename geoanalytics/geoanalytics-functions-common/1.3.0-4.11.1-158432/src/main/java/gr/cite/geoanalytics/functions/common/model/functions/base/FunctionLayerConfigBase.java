package gr.cite.geoanalytics.functions.common.model.functions.base;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import gr.cite.geoanalytics.functions.common.model.functions.LayerConfig;

//import gr.cite.geoanalytics.functions.common.model.exceptions.ImmutablesChangedException;

/**
 * Edit this class only if you are sure about what your're doing
 */
public class FunctionLayerConfigBase implements FunctionLayerConfigBaseI {

	private final int initializationSetSize;

	private Set<LayerConfig> layerConfigs = new HashSet<LayerConfig>();

	
	protected FunctionLayerConfigBase(Set<LayerConfig> layerConfigs) {
		initializationSetSize = layerConfigs.size();
		this.layerConfigs = layerConfigs;
	}
	
	
	public Set<LayerConfig> getLayerConfigs() {
		return layerConfigs;
	}


	public LayerConfig getLayerConfigByObjectID(String objID){
		return layerConfigs.stream().filter(lc -> objID.equals(lc.getObjectID())).findFirst().get();
	}

	public boolean isValidForSubmission(){
		if(layerConfigs.size() != initializationSetSize) return false;
		Iterator<LayerConfig> iter = layerConfigs.iterator();
		while(iter.hasNext()){
			LayerConfig lc = iter.next();
			if(lc.getLayerID()==null || lc.getLayerID().isEmpty()) return false;
			if(lc.getCaptionForUser()==null || lc.getCaptionForUser().isEmpty()) return false;
		}
		return true;
	}
	
	
}
