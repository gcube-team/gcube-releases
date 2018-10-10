/**
 * 
 */
package org.gcube.common.homelibrary.util;

import java.util.List;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

/**
 * @author fedy2
 * 
 */
public class HomeLibraryVisitor extends IndentedVisitor {

	protected boolean visitHomes = false;
	
	/**
	 * 
	 */
	public HomeLibraryVisitor() {
	}
	
	/**
	 * @param visitHomes if visit the homes.
	 */
	public HomeLibraryVisitor(boolean visitHomes) {
		this.visitHomes = visitHomes;
	}

	/**
	 * Visit the specified home library.
	 * @param factory the home manager factory.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws HomeNotFoundException if an home is not found.
	 * @throws WorkspaceFolderNotFoundException if a workspace is not found.
	 */
	public void visitHomeLibrary(HomeManagerFactory factory) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException {
		reset();
		List<String> scopes = factory.listScopes();
		for (String scope : scopes) {
		
			println(scope);
			HomeManager homeManager = factory.getHomeManager();
			visitHomeManager(homeManager);
		}
	}

	protected void visitHomeManager(HomeManager homeManager) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException {
		indent();
		List<User> users = homeManager.getUsers();

		for (User user : users) {
		
			println(user.getPortalLogin());
			if (visitHomes) {
				Home home = homeManager.getHome(user);
				visitUser(home);
			}
		}
		outdent();

	}

	protected void visitUser(Home home) throws InternalErrorException, WorkspaceFolderNotFoundException {
		indent();
		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor(indentationLevel, indentationChar, os);
		wtv.visitItem(home.getWorkspace().getRoot());
		outdent();
	}
/*	
	public void visitUser(Home home, GCUBEScope scope) throws InternalErrorException, WorkspaceFolderNotFoundException {
		indent();
		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor(indentationLevel, indentationChar, os, logger);
		wtv.visitItem(home.getWorkspace().getRoot(scope));
		outdent();
	}
*/
	
}
