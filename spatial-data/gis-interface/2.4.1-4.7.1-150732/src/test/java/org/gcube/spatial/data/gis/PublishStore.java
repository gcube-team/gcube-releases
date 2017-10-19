package org.gcube.spatial.data.gis;

import java.net.MalformedURLException;

import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;

import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;

public class PublishStore {

	public static void main (String[] args) throws Exception{
		TokenSetter.set("/gcube/devsec");
		
		String workspace="My another workspace";
				
		GISInterface gis= GISInterface.get();
		printWorkspaces(gis);
		
		GISInterface.get().createWorkspace(workspace);
		//Second should skip where existing
		GISInterface.get().createWorkspace(workspace);
		
		printWorkspaces(gis);
		
		
		GSPostGISDatastoreEncoder datastore=new GSPostGISDatastoreEncoder("My datastore");
//		datastore.set
		// Utils parameters to simplify caller's life will be provided
		
		
//		gis.createDataStore(workspace, datastore);
	}
	
	
	public static void printWorkspaces(GISInterface gis) throws MalformedURLException{
		for(AbstractGeoServerDescriptor gs: gis.getCurrentCacheElements(false)){
			System.out.println(gs.getWorkspaces());
			
		}
	}
}
