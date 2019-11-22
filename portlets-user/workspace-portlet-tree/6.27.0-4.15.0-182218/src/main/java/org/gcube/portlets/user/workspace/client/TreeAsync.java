package org.gcube.portlets.user.workspace.client;

import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService;
import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceServiceAsync;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TreeAsync implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side
	 */
	private final GWTWorkspaceServiceAsync rpcWorkspaceService = (GWTWorkspaceServiceAsync) GWT.create(GWTWorkspaceService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		Registry.register(ConstantsExplorer.RPC_WORKSPACE_SERVICE, rpcWorkspaceService);
		
		//UNCOMMENT FOLLOWING TO USE ONLY TREE
//		AppControllerExplorer appController = new AppControllerExplorer();
//		appController.go(RootPanel.get("treePanelWs"), true);
		
	}
}
