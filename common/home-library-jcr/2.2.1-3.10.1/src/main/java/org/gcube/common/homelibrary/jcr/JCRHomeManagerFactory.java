package org.gcube.common.homelibrary.jcr;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.manager.HomeLibraryManager;
import org.gcube.common.homelibrary.home.workspace.accessmanager.AccessManager;
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRAccessManager;
import org.gcube.common.homelibrary.jcr.workspace.privilegemanager.JCRPrivilegeManager;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRHomeManagerFactory implements HomeManagerFactory {
	
	private static Logger logger = LoggerFactory.getLogger(JCRHomeManagerFactory.class);
	private static HomeManager homeManager;
	private static UserManager userManager;
	private static AccessManager accessManager;
	private static PrivilegeManager privilegeManager;
	private static File persistenceFolder;
	
	@Override
	public void initialize(String pathPersistenceFolder)
			throws InternalErrorException {
			
//		persistenceFolder = new File(pathPersistenceFolder);
//		
//		if(!persistenceFolder.exists()) {
//			persistenceFolder.mkdirs();
//		}
		
		logger.debug("Initialize content manager");
		try {
			JCRRepository.initialize();
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		
		JCRExternalResourcePluginManager.initialize();

		homeManager = new JCRHomeManager(this);
	

	}

	public File getPersistenceFolder() {
		return persistenceFolder;
	}
	
	@Override
	public List<String> listScopes() throws InternalErrorException {
		//TODO e.g.
		List<String> list = new LinkedList<String>();
//		list.add("/gcube/devsec");
//		list.add("/gcube/devNext");
//		list.add("/gcube/devNext/NextNext");
//		list.add("/gcube/devsec/devVRE");
		return list;
	}
	@Override
	public boolean exists(String scope) throws InternalErrorException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HomeLibraryManager getHomeLibraryManager()
			throws InternalErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listUserScopes(String portalLogin)
			throws InternalErrorException {
		
		try {
			Home home = homeManager.getHome(portalLogin);
			return home.listScopes();
		} catch (HomeNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (UserNotFoundException e) {
			throw new InternalErrorException(e);
		}
		
	}

	@Override
	public List<String> listInfrastructureScopes()
			throws InternalErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listInfrastructureScopeUsers(String scope)
			throws InternalErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() throws InternalErrorException {
		

	}

	@Override
	public HomeManager getHomeManager()
			throws InternalErrorException {
		logger.debug("getHomeManager");
		return homeManager;
	}

	@Override
	public void removeHomeManager()
			throws InternalErrorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HomeManager getHomeManager(String scope)
			throws InternalErrorException {
		return getHomeManager();
	}

	@Override
	public UserManager getUserManager() throws InternalErrorException {
		if (userManager==null)
			userManager = new JCRUserManager();
		return userManager;
	}

	@Override
	public AccessManager getAccessManager() throws InternalErrorException {
		if (accessManager==null)
			accessManager = new JCRAccessManager();
		return accessManager;
	}

	@Override
	public PrivilegeManager getPrivilegeManager() throws InternalErrorException {
		if (privilegeManager==null)
			privilegeManager = new JCRPrivilegeManager();
		return privilegeManager;
	}

}
