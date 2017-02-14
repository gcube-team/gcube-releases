/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.portlets.user.homelibrary.home.Home;
import org.gcube.portlets.user.homelibrary.home.HomeLibrary;
import org.gcube.portlets.user.homelibrary.home.HomeManager;
import org.gcube.portlets.user.homelibrary.home.HomeManagerFactory;
import org.gcube.portlets.user.homelibrary.home.User;
import org.gcube.portlets.user.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceArea;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.WorkspaceNotFoundException;
import org.gcube.portlets.user.homelibrary.util.WorkspaceTreeVisitor;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeManagerListAllUsersContent {

	/**
	 * @param args not used.
	 * @throws InternalErrorException if an error occurs.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 */
	public static void main(String[] args) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException {
		
		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
		HomeManager homeManager = factory.getHomeManager(GCUBEScope.getScope("/gcube/devsec"));		
	
		listUsersContent(homeManager);

	}
	
	protected static void listUsersContent(HomeManager homeManager) throws InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
		for (User user:homeManager.getUsers()) listUserContent(homeManager, user);
	}
	
	protected static void listUserContent(HomeManager homeManager, User user) throws InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
		Home home = homeManager.getHome(user);
		WorkspaceArea wa = home.getWorkspaceArea();
		
		WorkspaceTreeVisitor visitor = new WorkspaceTreeVisitor();
		visitor.visitVerbose(wa.getRoot());
	}

}
