/**
 * 
 */
package org.gcube.common.homelibrary.examples;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.WorkspaceTreeVisitor;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeManagerListAllUsersContent {

	/**
	 * @param args not used.
	 * @throws InternalErrorException if an error occurs.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 */
	public static void main(String[] args) throws  InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException {
		
		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
		HomeManager homeManager = factory.getHomeManager();		
	
		listUsersContent(homeManager);

	}
	
	protected static void listUsersContent(HomeManager homeManager) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		for (User user:homeManager.getUsers()) listUserContent(homeManager, user);
	}
	
	protected static void listUserContent(HomeManager homeManager, User user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		Home home = homeManager.getHome(user);
		Workspace wa = home.getWorkspace();
		
		WorkspaceTreeVisitor visitor = new WorkspaceTreeVisitor();
		visitor.visitVerbose(wa.getRoot());
	}

}
