package org.gcube.application.aquamaps.aquamapsservice.client.tests;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;
import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Maps;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.common.scope.api.ScopeProvider;

public class MapTests {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException, Exception {
		ScopeProvider.instance.set(TestCommon.SCOPE);
//		Maps maps=maps().withTimeout(5, TimeUnit.MINUTES).build();
//		
////		System.out.println(maps.loadObject(604699).toXML());
//		
//		DataManagement dmService=dataManagement().build();
//		
//		int hspenID=0;
//		for(Field f:dmService.getDefaultSources()){
//			if(f.name().equals(ResourceType.HSPEN+"")) {
//				hspenID=f.getValueAsInteger();				
//				break;
//			}
//		}
		
//		System.out.println(maps.getCSVSpecies(hspenID, null, null).getAbsolutePath());
		
		System.out.println(dataManagement().withTimeout(2, TimeUnit.MINUTES).build().exportTableAsCSV("speciesoccursum",null,null,null,ExportOperation.TRANSFER));
		
	}

	
	
	
}
