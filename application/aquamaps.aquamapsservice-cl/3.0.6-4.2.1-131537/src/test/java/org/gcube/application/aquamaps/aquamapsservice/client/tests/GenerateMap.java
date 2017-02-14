package org.gcube.application.aquamaps.aquamapsservice.client.tests;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.rmi.RemoteException;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.common.scope.api.ScopeProvider;

public class GenerateMap {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException, Exception {
		ScopeProvider.instance.set(TestCommon.SCOPE);		
		
		DataManagement dm=dataManagement().build();	
		Resource hspec=null;
		for(Field f:dm.getDefaultSources())
			if(f.name().equalsIgnoreCase("HSPEC")) hspec=dm.loadResource(f.getValueAsInteger());
		
		if(hspec==null) throw new Exception("No Default HSPEC");
		System.out.println("Found default "+hspec);
		
		
		

	}

}
