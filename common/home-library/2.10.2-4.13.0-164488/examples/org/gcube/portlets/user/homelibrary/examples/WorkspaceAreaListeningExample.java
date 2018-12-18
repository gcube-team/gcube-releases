/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.portlets.user.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.home.workspace.Workspace;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceArea;
import org.gcube.portlets.user.homelibrary.home.workspace.basket.Basket;
import org.gcube.portlets.user.homelibrary.home.workspace.events.WorkspaceAreaEvent;
import org.gcube.portlets.user.homelibrary.home.workspace.events.WorkspaceAreaListener;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.WorkspaceNotFoundException;


/**
 * This example show how to listen workspace events.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceAreaListeningExample {

	/**
	 * @param args not used.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 * @throws InsufficientPrivilegesException if an error occurs.
	 * @throws ItemAlreadyExistException if an error occurs.
	 */
	public static void main(String[] args) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException {
		WorkspaceArea workspaceArea = ExamplesUtil.createWorkspaceArea();

		WorkspaceAreaListener listener = new WorkspaceAreaListener(){

			public void workspaceEvent(WorkspaceAreaEvent event) {
				//	System.out.println("This is a workspace area event: "+event);
			}
		};
		
		workspaceArea.addWorkspaceAreaListener(listener);
		
		Workspace workspace = workspaceArea.getRoot().createWorkspace("TestWorkspace", "This is a test workspace");
		
		Basket basket = workspace.createBasket("TestBasket", "This is a test basket");
		
		basket.createExternalUrlItem("My url", "My prefered url", "http://localhost/");
		
	}

}
