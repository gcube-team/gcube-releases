package org.gcube.portlets.widgets.wstaskexecutor.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wstaskexecutor.server.WsTaskExecutorWidgetServiceImpl;
import org.gcube.portlets.widgets.wstaskexecutor.server.util.RuntimeResourceReader;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScope;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScopeType;

/**
 * The Class CheckDMScopes.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Sep 13, 2019
 */
public class CheckDMScopes {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		List<GcubeScope> listOfScopes = new ArrayList<GcubeScope>();
		//WsTaskExecutorWidgetServiceImpl sImpl = new WsTaskExecutorWidgetServiceImpl();
		listOfScopes.add(new GcubeScope("devVRE", "/gcube/devsec/devVRE", GcubeScopeType.VRE));
		listOfScopes.add(new GcubeScope("NextNext", "/gcube/devNext/NextNext", GcubeScopeType.VRE));
		
		for (GcubeScope gcubeScope : listOfScopes) {
			boolean exists = RuntimeResourceReader.serviceEndpointExists(gcubeScope.getScopeName(), "DataMiner", "DataAnalysis");
			System.out.println("Is the DataMiner deployed in the scope: "+gcubeScope.getScopeName()+"? "+exists);
		}
		
		
		WsTaskExecutorWidgetServiceImpl wsImpl = new WsTaskExecutorWidgetServiceImpl();
		List<GcubeScope> listScopes = wsImpl.getListOfScopesForLoggedUser();
		
		for (GcubeScope gcubeScope : listScopes) {
			System.out.println(gcubeScope.getScopeTitle());
		}
		System.out.println("\n\n");
		for(int i=0; i<listScopes.size(); i++) {
			System.out.println(listScopes.get(i).getScopeTitle());
		}
	}

}
