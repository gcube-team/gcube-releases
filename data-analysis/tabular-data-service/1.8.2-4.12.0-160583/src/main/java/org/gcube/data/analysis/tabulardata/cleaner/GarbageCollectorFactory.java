package org.gcube.data.analysis.tabulardata.cleaner;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;

@Singleton
public class GarbageCollectorFactory {

	private GarbageCollector collector;
	
	public GarbageCollector getGarbageCollector(){
		return collector;
	}
		
	@Inject
	public GarbageCollectorFactory(CubeManager cubeManager){
		this.collector = new GarbageCollector(cubeManager);
	}
	
}
