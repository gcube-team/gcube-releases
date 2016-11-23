/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import java.io.File;

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
import org.gcube.portlets.user.homelibrary.util.Util;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ExamplesUtil {
	
	/**
	 * The persistence folder for home library.
	 */
	protected static String EXAMPLES_PERSISTENCE_ROOT = System.getProperty("java.io.tmpdir")+File.separator+"home_library_examples";
	
	/**
	 * Create a workspace area in a temporary folder.
	 * @return the workspace area.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 */
	public static WorkspaceArea createWorkspaceArea() throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
		return createWorkspaceArea("/test/testscope");
	}
	
	/**
	 * Create a workspace area in a temporary folder.
	 * @param scope the workspace area scope.
	 * @return the workspace area.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 */
	public static WorkspaceArea createWorkspaceArea(String scope) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
		return createWorkspaceArea("/test/testscope", "user.test");
	}
	
	/**
	 * Create a workspace area in a temporary folder.
	 * @param scope the workspace area scope.
	 * @param user the workspace area user.
	 * @return the workspace area.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 */
	public static WorkspaceArea createWorkspaceArea(String scope, String user) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
			
		//Home manager factory instance
		HomeManagerFactory factory = getHomeManagerFactory();
		
		return createWorkspaceArea(factory, scope, user);
	}
	
	/**
	 * @param scope the user scope.
	 * @param user the home user.
	 * @return the created home.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 */
	public static Home createHome(String scope, String user) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
			
		//Home manager factory instance
		HomeManagerFactory factory = getHomeManagerFactory();
		
		return createHome(factory, scope, user);
	}
	
	/**
	 * @param factory the home manager factory.
	 * @param scope the user scope.
	 * @param user the home user.
	 * @return the user workspace area.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 */
	public static WorkspaceArea createWorkspaceArea(HomeManagerFactory factory, String scope, String user) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
		
		//we get the user home.
		Home home = createHome(factory, scope, user);
		
		//we get the user workspace area
		return home.getWorkspaceArea();
	}
	
	/**
	 * @param factory the home manager factory.
	 * @param scope the user scope.
	 * @param user the home user.
	 * @return the user home.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 */
	public static Home createHome(HomeManagerFactory factory, String scope, String user) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException
	{
		
		//we require the Home manager for a given scope
		HomeManager manager = factory.getHomeManager(GCUBEScope.getScope(scope));
		
		//we create a test user
		User testUser = manager.getUser(user);
		
		//we get the user home.
		Home home = manager.getHome(testUser);
		
		return home;
	}
	
	
	/**
	 * @param persistenceRoot the home manager persistence root.
	 * @return the home manager.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static HomeManagerFactory getHomeManagerFactory(String persistenceRoot) throws InternalErrorException
	{
		return HomeLibrary.getHomeManagerFactory(persistenceRoot);
	}
	
	/**
	 * Create an empty Home manager.
	 * @return the Home manager.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static HomeManagerFactory getHomeManagerFactory() throws InternalErrorException
	{
		//the directory where the home library save all persistence files
		File homeLibraryExampleDir = new File(EXAMPLES_PERSISTENCE_ROOT);
		
		//if a dir with that name already exits this is cleaned
		if (homeLibraryExampleDir.exists()) Util.cleanDir(homeLibraryExampleDir);
		
		homeLibraryExampleDir.mkdir();
		
		return getHomeManagerFactory(EXAMPLES_PERSISTENCE_ROOT);
	}

}
