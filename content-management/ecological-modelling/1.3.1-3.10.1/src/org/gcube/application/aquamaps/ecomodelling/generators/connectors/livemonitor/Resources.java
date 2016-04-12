package org.gcube.application.aquamaps.ecomodelling.generators.connectors.livemonitor;

import java.util.ArrayList;
import java.util.List;

public class Resources {
	
	public List<SingleResource> list;
	
	public Resources(){
		list = new ArrayList<SingleResource>();
	}
	
	public void addResource(String resID, double value){
		
		list.add(new SingleResource(resID, value));
		
	}
	
}
