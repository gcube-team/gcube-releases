/**
 * 
 */
package org.gcube.common.homelibrary.home;

import java.io.File;

import org.gcube.common.homelibrary.home.data.ApplicationsArea;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.config.HomeLibraryConfiguration;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeLibrary {

	/**
	 * Home attribute name in ASLSession.
	 */
	public static final String HOME_ATTRIBUTE_NAME = "USER_HOME";

//	protected static final Logger staticLogger;
//	
//	static{
//		staticLogger = Logger.getLogger("HomeManageFactory");
//		staticLogger.setLevel(Level.ALL);
//	}

	protected static Logger staticLogger = LoggerFactory.getLogger("HomeManageFactory");
	
	protected static HomeManagerFactory instance;

	/**
	 * Returns the HomeManagerFactory implementation to use.
	 * @return an instance of HomeManagerFactory.
	 * @throws InternalErrorException if an error occurs retrieving the HomeManagerFactory implementation.
	 */
	protected static HomeManagerFactory getHomeManagerFactoryImplementation() throws InternalErrorException
	{
		try {
			Class<?> homeManagerFactoryClass = HomeLibraryConfiguration.getInstance().getHomeManagerFactoryClass();
			return (HomeManagerFactory) homeManagerFactoryClass.newInstance();
		} catch (Exception e)
		{
			staticLogger.error("An error occured retrieving the HomeManagerFactory implementation.", e);
			throw new InternalErrorException("An error occured retrieving the HomeManagerFactory implementation.", e);
		}
	}
	
	/**
	 * Check if the persistence folder exists otherwise create it.
	 * @param persistenceFolder the persistence folder to check.
	 * @throws InternalErrorException if the persistence folder check fails.
	 */
	protected static void checkPersistenceFolder(String persistenceFolder) throws InternalErrorException
	{
		staticLogger.trace("persistenceFolder = "+persistenceFolder);

		File persistenceDir = new File(persistenceFolder);
		if (!persistenceDir.exists()) {
			staticLogger.trace("The persistence folder don't exists, creating it");
			boolean created = persistenceDir.mkdirs();
			if (!created){
				staticLogger.error("FATAL: the home library can't create his persistence folder!!! "+persistenceFolder);
				System.err.println("FATAL: the home library can't create his persistence folder!!! "+persistenceFolder);
				throw new InternalErrorException("FATAL: the home library can't create his persistence folder!!! "+persistenceFolder);
			}
				
		} else staticLogger.trace("The persistence folder exists");
		

	}

	


	protected synchronized static HomeManagerFactory createHomeManagerFactoryInstance(String persistenceRoot) throws InternalErrorException
	{
		
		checkPersistenceFolder(persistenceRoot);				
		HomeManagerFactory homeManagerFactory = getHomeManagerFactoryImplementation();

		homeManagerFactory.initialize(persistenceRoot);
		return homeManagerFactory;
	}

	/**
	 * Return an instance of HomeManager factory for the given path.
	 * @param persistenceRoot the persistence root.
	 * @return the HomeManagerFactory.
	 * @throws InternalErrorException if an error occurs.
	 */
	public synchronized static HomeManagerFactory getHomeManagerFactory(String persistenceRoot) throws InternalErrorException
	{
		staticLogger.info("getInstance persistenceRoot: "+persistenceRoot);

		if (instance ==null) {
			instance = createHomeManagerFactoryInstance(persistenceRoot);
		}
		return instance;
	}

	/**
	 * Return the HomeManagerFactory.
	 * @return the HomeManagerFactory.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static HomeManagerFactory getHomeManagerFactory() throws InternalErrorException
	{
		staticLogger.info("getHomeManagerFactory");

		if (instance!=null) return instance;

		String persistenceRoot = HomeLibraryConfiguration.getInstance().getPersistenceFolder();

		return getHomeManagerFactory(persistenceRoot);
	}

	/**
	 * Return the Workspace for the user in session.
	 * @param session the asl session.
	 * @return the user Workspace.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if the user home is not found.
	 * @throws WorkspaceFolderNotFoundException if the user workspace is not found.
	 */
	public static Workspace getUserWorkspace(String portalLogin) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{

		if (portalLogin == null) {
			staticLogger.error("user parameter null");
			throw new IllegalArgumentException("user can't be null.");
		}

		Home home = getUserHome(portalLogin);

		return home.getWorkspace();

	}




	/**
	 * Return the DataArea for the user in session.
	 * @param session the ASL session.
	 * @return the user DataArea.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if the user home is not found.
	 */
	public static ApplicationsArea getUserDataArea(String portalLogin) throws InternalErrorException, HomeNotFoundException
	{

		if (portalLogin == null) {
			staticLogger.error("user parameter null");
			throw new IllegalArgumentException("user can't be null.");
		}

		Home home = getUserHome(portalLogin);

		return home.getDataArea();
	}

	/**
	 * Return the Home for the user in session.
	 * @param session the asl session.
	 * @return the user Home.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if the user home is not found.
	 */
	public static Home getUserHome(String portalLogin) throws InternalErrorException, HomeNotFoundException
	{


		//TODO: Homes caching

		String username = portalLogin;
		String scope = ScopeProvider.instance.get();

		if (username == null){
			staticLogger.error("The username is null");
			throw new IllegalArgumentException("The username  is null.");
		}

		if (scope == null){
			staticLogger.error("The scope in session is null");
			throw new IllegalArgumentException("The scope is null.");
		}


		staticLogger.info("loading home. (Username: "+username+", scope: "+scope+")");

		//				NotificationsManager notificationManager = new ApplicationNotificationsManager(session);


		HomeManagerFactory homeManagerFactory = getHomeManagerFactory();

		HomeManager homeManager = homeManagerFactory.getHomeManager();
		
		User user = homeManager.getUser(username);

		Home home = homeManager.getHome(user);

		return home;
	}

	

}
