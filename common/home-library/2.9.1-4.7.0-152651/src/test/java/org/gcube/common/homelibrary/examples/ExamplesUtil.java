/**
 * 
 */
package org.gcube.common.homelibrary.examples;

import java.io.File;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.Util;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ExamplesUtil {
	
	/**
	 * The persistence folder for home library.
	 */
	public static final String EXAMPLES_PERSISTENCE_ROOT = System.getProperty("java.io.tmpdir")+File.separator+"home_library_examples";
	
	/**
	 * Create a workspace in a temporary folder.
	 * @return the workspace.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 */
	public static Workspace createWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		return createWorkspace("/test/testscope");
	}
	
	/**
	 * Create a workspace in a temporary folder.
	 * @param scope the workspace scope.
	 * @return the workspace.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 */
	public static Workspace createWorkspace(String scope) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		return createWorkspace(scope, "user.test");
	}
	
	/**
	 * Create a workspace in a temporary folder.
	 * @param scope the workspace scope.
	 * @param user the workspace user.
	 * @return the workspace.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 */
	public static Workspace createWorkspace(String scope, String user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
			
		//Home manager factory instance
		HomeManagerFactory factory = getHomeManagerFactory();
		
		return createWorkspace(factory, user);
	}
	
	/**
	 * @param scope the user scope.
	 * @param user the home user.
	 * @return the created home.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 */
	public static Home createHome(String user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
			
		//Home manager factory instance
		HomeManagerFactory factory = getHomeManagerFactory();
		
		return createHome(factory, user);
	}
	
	/**
	 * @param factory the home manager factory.
	 * @param scope the user scope.
	 * @param user the home user.
	 * @return the user workspace.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 */
	public static Workspace createWorkspace(HomeManagerFactory factory, String user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		
		//we get the user home.
		Home home = createHome(factory, user);
		
		//we get the user workspace
		return home.getWorkspace();
	}
	
	/**ersistenceRoot
	 * @param factory the home manager factory.
	 * @param scope the user scope.
	 * @param user the home user.
	 * @return the user home.
	 * @throws MalformedScopeExpressionException if a wrong scope is given.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 */
	public static Home createHome(HomeManagerFactory factory, String user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		
		//we require the Home manager for a given scope
		HomeManager manager = factory.getHomeManager();
		
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
	 * Create an empty Home manager cleaning the tmp dir.
	 * @return the Home manager.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static HomeManagerFactory getHomeManagerFactory() throws InternalErrorException
	{
		return getHomeManagerFactory(false);
	}
	
	/**
	 * Create an empty Home manager.
	 * @return the Home manager.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static HomeManagerFactory getHomeManagerFactory(boolean clean) throws InternalErrorException
	{
		//the directory where the home library save all persistence files
		File homeLibraryExampleDir = new File(EXAMPLES_PERSISTENCE_ROOT);
		
		//if a dir with that name already exits this is cleaned
		if (clean && homeLibraryExampleDir.exists()) Util.cleanDir(homeLibraryExampleDir);
		
		homeLibraryExampleDir.mkdir();
		
		return getHomeManagerFactory(EXAMPLES_PERSISTENCE_ROOT);
	}

}
