package org.gcube.dataanalysis.ecoengine.interfaces;

import java.util.Map;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public interface DynamicTransducer {

	
		public Map<String,Transducerer>  getTransducers(AlgorithmConfiguration config);
}
