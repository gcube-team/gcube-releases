package org.gcube.common.homelibrary.jcr.repository;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.jcr.workspace.util.Utils;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class JCRRepository {


	public static final String PATH_SEPARATOR 				= "/";
	public static final String ROOT_WEBDAV					= "/repository/default/";

	//root level
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";
	private static final String GCUBE_FOLDER 				= "GCube";

	//home level
	private static final String DOWNLOADS					= "Downloads";
	private static final String SMART_FOLDER 				= "Folders";
	private static final String IN_BOX_FOLDER 				= "InBox";
	private static final String OUT_BOX_FOLDER				= "OutBox";

	//	private static final String SCOPES						= "hl:scopes";

	private static final String nameResource 				= "HomeLibraryRepository";


	private static Repository repository;
	private String portalLogin;

	public static String user;
	public static String pass;
	public static String url;
	private static String webdavUrl;
	public static String serviceName;

	//HL release version
	public static String HLversion;
	private static String version;
	private static String minorVersion;
	private static String revisionVersion;


	private static Logger logger = LoggerFactory.getLogger(JCRRepository.class);


	private static synchronized void initializeRepository() throws InternalErrorException {

		if(repository != null)
			return;


		String callerScope = ScopeProvider.instance.get();

		try {

			if (callerScope==null) throw new IllegalArgumentException("scope is null");

			String rootScope = Utils.getRootScope(callerScope);

			logger.debug("scope for repository creation is "+rootScope+" caller scope is "+callerScope);

			ScopeProvider.instance.set(rootScope);


			SimpleQuery query = queryFor(ServiceEndpoint.class);

			query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> resources = client.submit(query);

			if(resources.size() != 0) {	   
				try {
					ServiceEndpoint resource = resources.get(0);

					for (AccessPoint ap:resource.profile().accessPoints()) {

						if (ap.name().equals("JCR")) {

							url = ap.address();
							user = ap.username();						
							pass = StringEncrypter.getEncrypter().decrypt(ap.password());

							Iterator<org.gcube.common.resources.gcore.ServiceEndpoint.Property> properties = ap.properties().iterator();
							while(properties.hasNext()) {
								org.gcube.common.resources.gcore.ServiceEndpoint.Property p = properties.next();		
								if (p.name().equals("version")){							
									version = p.value();
								} else if (p.name().equals("minorVersion")){							
									minorVersion = p.value();
								} else if (p.name().equals("revisionVersion")){							
									revisionVersion = p.value();
								}
							}	
							HLversion =  version + "." + minorVersion + "." + revisionVersion;
							//							System.out.println("HL VERSION: " + version + "." + minorVersion + "." + revisionVersion);					
						}
						else if (ap.name().equals("WebDav")) {								
							webdavUrl = ap.address();				
						}else if (ap.name().equals("ServiceName")) {								
							serviceName = ap.address();				
						}
					}
				} catch (Throwable e) {
					logger.error("error decrypting resource",e);
				}
			}


			if (user==null || pass==null) throw new InternalErrorException("cannot discover password and username in scope "+callerScope);

			repository = new URLRemoteRepository(url + "/rmi");
			//			repository = JcrUtils.getRepository(url + "/server");

			logger.debug("user is "+user+" password is null?"+(pass==null)+" and repository is null?"+(repository==null));

		} catch (Exception e) {
			throw new InternalErrorException(e);
		}finally{
			if (callerScope!=null)
				ScopeProvider.instance.set(callerScope);
		}

	}

	/**
	 * Get jackrabbit credentials
	 * @return a string with credentials
	 */
	public static String getCredentials() {
		String credentials = "adminId=" + JCRRepository.user + "&adminPassword=" + JCRRepository.pass;
		return credentials;
	}


	/**
	 * Get users home names
	 * @return a list of users home names
	 * @throws RepositoryException
	 */
	public static List<String> getHomeNames() throws RepositoryException{
		List<String> homes = new ArrayList<String>();
		JCRServlets session = null;
		try {
			session = new JCRServlets();
			ItemDelegate home = session.getItemByPath(PATH_SEPARATOR+HOME_FOLDER);
			List<ItemDelegate> children = session.getChildrenById(home.getId(), false);

			for (ItemDelegate child: children){ 
				if (child.getPrimaryType().equals(PrimaryNodeType.NT_HOME))
					homes.add(child.getName());
			}
		} catch (ItemNotFoundException e) {
			throw new RepositoryException(e.getMessage());
		}finally{
			session.releaseSession();
		}
		return homes;
	}


	/**
	 * Get Home Folder "/Home/"
	 * @return
	 * @throws RepositoryException
	 */
	public static ItemDelegate getHome() throws RepositoryException{
		ItemDelegate home = null;
		JCRServlets session = null;
		try {
			session = new JCRServlets();
			home = session.getItemByPath(PATH_SEPARATOR+HOME_FOLDER);
		} catch (ItemNotFoundException e) {
			throw new RepositoryException(e.getMessage());
		}finally{
			session.releaseSession();
		}
		return home;
	}

	//	private static void addUserToJCRUserManager(String userId, String userHome) {
	//
	//		GetMethod getMethod = null;
	//		try {
	//
	//			HttpClient httpClient = new HttpClient();            
	//			//			 System.out.println(url);
	//			getMethod =  new GetMethod(url + "/PortalUserManager?userId=" + userId + "&userHome=" +userHome);
	//			httpClient.executeMethod(getMethod);
	//
	//			logger.debug("User set with status code " + getMethod.getResponseBodyAsString());
	//
	//		} catch (Exception e) {
	//			logger.error("User set with error ", e);
	//		} finally {
	//			if(getMethod != null)
	//				getMethod.releaseConnection();
	//		}
	//
	//	}



	public static Session getSession(String user) throws InternalErrorException  {
		//		System.out.println("GET SESSION BY USER");
		initializeRepository();
		synchronized (repository) {
			try {
				logger.debug("session of " + user);
				Session session = repository.login( 
						new SimpleCredentials(user, JCRUserManager.getSecurePassword(user).toCharArray()));
				return session;
			} catch (Exception e) {
				throw new InternalErrorException(e);
			}
		}
	}

	public static Session getSession() throws InternalErrorException  {
		//		System.out.println("GET SESSION");
		initializeRepository();
		synchronized (repository) {
			try {
				Session session = repository.login( 
						new SimpleCredentials(user, pass.toCharArray()));
				return session;
			} catch (Exception e) {
				throw new InternalErrorException(e);
			}
		}
	}

	public synchronized static void initialize() throws InternalErrorException {
		logger.debug("Initialize repository");
		initializeRepository();

	}

	public JCRRepository(final User user) throws InternalErrorException {

		portalLogin = user.getPortalLogin();
		logger.info("getHome " + portalLogin);
		try {
			init();
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}

	}



	//	public static void removeUser(User user) throws Exception {
	//		getServlets().removeItem(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + user.getPortalLogin());
	//	}


	/**
	 * Create folder in /Home/xxx/
	 * @param user
	 * @throws Exception 
	 */
	public void init() throws Exception{

		//		System.out.println("INIT.................");
		JCRUserManager um = new JCRUserManager();
		String userVersion = getUserVersion(portalLogin, um);
		//		System.out.println(userVersion   + " <--- " );

		logger.info(portalLogin + " - USER VERSION: " + userVersion + " - HL VERSION: " + HLversion);

		if(!HLversion.equals(userVersion)){

			JCRServlets servlets = null;
			try {
				servlets = new JCRServlets(portalLogin);

				ItemDelegate home = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER);

				DelegateManager homeManager = new DelegateManager(home, portalLogin);

				//create user folder es. Home/test.test

				ItemDelegate userHome = null;
				try {
					userHome = homeManager.addNode(portalLogin, PrimaryNodeType.NT_HOME);	
					//					System.out.println("userHome " + userHome.toString());
					try{
						homeManager.save(userHome);
					}catch (Exception e) {
						logger.error("Impossible to create " + portalLogin);
						//						throw new RepositoryException(e.getMessage());
					}
					um.createUser(portalLogin, version+"");

				} catch (InternalErrorException e) {
					userHome = homeManager.getNode(portalLogin);
				}

				//create folders es. Home/test.test/Folders
				DelegateManager userHomeManager = new DelegateManager(userHome, portalLogin);

				try{
					ItemDelegate smartFolder = userHomeManager.addNode(SMART_FOLDER, PrimaryNodeType.NT_FOLDER);	
					userHomeManager.save(smartFolder);
				}catch (Exception e) {
					logger.error("Impossible to create " + SMART_FOLDER);
					//						throw new RepositoryException(e.getMessage());
				}
				try{
					ItemDelegate inBoxDelegate = userHomeManager.addNode(IN_BOX_FOLDER, PrimaryNodeType.NT_ROOT_ITEM_SENT);	
					userHomeManager.save(inBoxDelegate);
				}catch (Exception e) {
					logger.error("Impossible to create " + IN_BOX_FOLDER);
					//						throw new RepositoryException(e.getMessage());
				}
				try{
					ItemDelegate outBoxDelegate = userHomeManager.addNode(OUT_BOX_FOLDER, PrimaryNodeType.NT_ROOT_ITEM_SENT);	
					userHomeManager.save(outBoxDelegate);
				}catch (Exception e) {
					logger.error("Impossible to create " + OUT_BOX_FOLDER);
					//						throw new RepositoryException(e.getMessage());
				}
				try{
					ItemDelegate downloadFolder = userHomeManager.addNode(DOWNLOADS, PrimaryNodeType.NT_ROOT_FOLDER_BULK_CREATOR);	
					userHomeManager.save(downloadFolder);
				}catch (Exception e) {
					logger.error("Impossible to create " + DOWNLOADS);
					//						throw new RepositoryException(e.getMessage());
				}


			}catch (RepositoryException e) {
				throw new RepositoryException(e.getMessage());
			} finally {
				servlets.releaseSession();
			}

		}else
			logger.info("skip init in JCRRepository");
	}



	//	public boolean exist(Node parent, String childName) throws RepositoryException {
	//		try {
	//			parent.getNode(childName);
	//		} catch (PathNotFoundException e) {
	//			logger.info(childName + " does not exist");
	//			return false;
	//		} 
	//		return true;
	//	}


	public List<String> listScopes() throws RepositoryException, InternalErrorException {

		List<String> list = new LinkedList<String>();
		//
		//		Session session = getSession();
		//		try {
		//			Node userHome = session.getNode(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + portalLogin);
		//			Property scopes = userHome.getProperty(SCOPES);
		//
		//			for (Value value  : scopes.getValues()) {
		//				list.add(value.getString());
		//			}
		return list;
		//
		//		} catch (PathNotFoundException e) {
		//			return new LinkedList<String>();
		//		} finally {
		//			if (session != null)
		//				session.logout();
		//		}
	}


	/**
	 * Get gCube root
	 * @return gCube root
	 * @throws RepositoryException
	 * @throws InternalErrorException 
	 */
	public static ItemDelegate getGCubeRoot() throws RepositoryException, InternalErrorException {

		logger.info("getGCubeRoot");

		JCRServlets servlet = null;
		ItemDelegate gcubeRoot = null;
		try{
			servlet = new JCRServlets();
			try{
				gcubeRoot = servlet.getItemByPath(PATH_SEPARATOR + GCUBE_FOLDER);
			}catch (ItemNotFoundException e) {
				gcubeRoot = getRootNode().addNode(GCUBE_FOLDER, PrimaryNodeType.NT_FOLDER);
				servlet.saveItem(gcubeRoot);
			}
		} catch (Exception e) {
			logger.error("Error retrieving shared root");
		} finally {
			servlet.releaseSession();
		}
		return gcubeRoot;
	}

	/**
	 * get SharedRoot
	 * @return SharedRoot
	 * @throws RepositoryException
	 * @throws InternalErrorException 
	 */
	public static ItemDelegate getSharedRoot() throws RepositoryException, InternalErrorException {
		logger.info("getSharedRoot");
		JCRServlets servlet = null;
		ItemDelegate sharedNode = null;
		try {
			servlet = new JCRServlets();
			try{
				sharedNode = servlet.getItemByPath(PATH_SEPARATOR + SHARED_FOLDER);
			}catch (Exception e) {
				sharedNode = getRootNode().addNode(SHARED_FOLDER, PrimaryNodeType.NT_FOLDER);
				servlet.saveItem(sharedNode);
			}

		} catch (RepositoryException e) {
			logger.error("Error retrieving shared root");
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlet.releaseSession();
		}
		return sharedNode;
	}


	/**
	 * Get root node
	 * @return root node
	 * @throws RepositoryException 
	 */
	private static DelegateManager getRootNode() throws RepositoryException {

		JCRServlets servlet = null;
		ItemDelegate root = null;
		DelegateManager wrap = null;
		try{
			servlet = new JCRServlets();
			root = servlet.getItemByPath(PATH_SEPARATOR);
			wrap = new DelegateManager(root, "");
		}catch (Exception e) {
			logger.error("Error retrieving Root Node " + e);
		}finally{
			servlet.releaseSession();
		}
		return wrap;
	}


	/**
	 * get User Home
	 * @param session
	 * @return
	 * @throws RepositoryException
	 * @throws ItemNotFoundException 
	 */
	public ItemDelegate getUserHome() throws RepositoryException, ItemNotFoundException {		
		logger.info("getUserHome: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + portalLogin);
		ItemDelegate userHomeDelegate = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(portalLogin);
			userHomeDelegate = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + portalLogin);
		}catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			servlets.releaseSession();
		}
		return userHomeDelegate;
	}


	/**
	 * Get Smart Folders root
	 * @param session
	 * @return Smart Folders root
	 * @throws RepositoryException
	 */
	public ItemDelegate getRootSmartFolders() throws RepositoryException{
		ItemDelegate smartFolders = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(portalLogin);
			logger.info("getRootSmartFolders: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
					+ portalLogin + PATH_SEPARATOR + SMART_FOLDER);

			if (smartFolders==null){
				//create applicationFolder
				try {		
					smartFolders = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
							+ portalLogin + PATH_SEPARATOR + SMART_FOLDER);
				} catch (ItemNotFoundException e) {	
					smartFolders = getUserHome(portalLogin).addNode(SMART_FOLDER, PrimaryNodeType.NT_FOLDER);
					servlets.saveItem(smartFolders);
				}
			}
		}catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		} finally {
			servlets.releaseSession();
		}

		return smartFolders;
	}



	private DelegateManager getUserHome(String user) throws InternalErrorException, RepositoryException {
		DelegateManager wrap = null;
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(portalLogin);
			ItemDelegate item = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
					+ portalLogin);
			wrap = new DelegateManager(item, portalLogin);

		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		}finally{
			servlets.releaseSession();
		}
		return wrap;

	}



	/**
	 * get InBoxFolder
	 * @param session
	 * @return InBoxFolder
	 * @throws RepositoryException
	 */
	public ItemDelegate getOwnInBoxFolder() throws javax.jcr.RepositoryException {
		ItemDelegate inBoxNode = null;

		JCRServlets servlets = null;
		try {

			servlets = new JCRServlets(portalLogin);
			//create applicationFolder
			try {				
				inBoxNode = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
						+ portalLogin + PATH_SEPARATOR + IN_BOX_FOLDER);	
			} catch (ItemNotFoundException e) {
				inBoxNode = getUserHome(portalLogin).addNode(IN_BOX_FOLDER, PrimaryNodeType.NT_ROOT_ITEM_SENT);
				servlets.saveItem(inBoxNode);
			}

		}catch (Exception e) {
			throw new javax.jcr.RepositoryException(e);
		}finally{
			servlets.releaseSession();
		}

		return inBoxNode;

	}

	/**
	 * get OutBoxFolder
	 * @param session
	 * @return OutBoxFolder
	 * @throws RepositoryException
	 */
	public ItemDelegate getOutBoxFolder() throws javax.jcr.RepositoryException {

		ItemDelegate outBoxNode = null;
		JCRServlets servlets = null;
		logger.info("getOutBoxFolder: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
				+ portalLogin + PATH_SEPARATOR + OUT_BOX_FOLDER);
		try {

			servlets = new JCRServlets(portalLogin);
			//create applicationFolder
			try {				
				outBoxNode = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
						+ portalLogin + PATH_SEPARATOR + OUT_BOX_FOLDER);
			} catch (ItemNotFoundException e) {
				outBoxNode = getUserHome(portalLogin).addNode(OUT_BOX_FOLDER, PrimaryNodeType.NT_ROOT_ITEM_SENT);
				servlets.saveItem(outBoxNode);
			}

		}catch (Exception e) {
			throw new javax.jcr.RepositoryException(e);
		}finally{
			servlets.releaseSession();
		}
		return outBoxNode;
	}
	/**
	 * Get Download folder
	 * @param session
	 * @return Download folder
	 * @throws RepositoryException
	 */
	public Node getRootFolderBulkCreators(Session session) throws javax.jcr.RepositoryException {

		Node downloads = null;

		logger.info("Get Download Folder: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
				+ portalLogin + PATH_SEPARATOR + DOWNLOADS);
		if (downloads==null){
			try {
				//create applicationFolder
				try {				
					downloads = session.getNode(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
							+ portalLogin + PATH_SEPARATOR + DOWNLOADS);	
				} catch (PathNotFoundException e) {
					downloads = session.getNode(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
							+ portalLogin).addNode(DOWNLOADS, PrimaryNodeType.NT_ROOT_FOLDER_BULK_CREATOR);
					session.save();
				}
			}catch (Exception e) {
				throw new javax.jcr.RepositoryException(e);
			}
		}
		return downloads;
	}



	/**
	 * get InBoxFolder
	 * @param session
	 * @param user
	 * @return InBoxFolder
	 * @throws RepositoryException
	 * @throws InternalErrorException
	 */
	public ItemDelegate getInBoxFolder(String user) throws javax.jcr.RepositoryException,
	InternalErrorException  {
		JCRServlets servlets = null;
		ItemDelegate inBoxFolder = null;
		try {
			servlets = new JCRServlets(portalLogin);
			inBoxFolder = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
					+ user + PATH_SEPARATOR + IN_BOX_FOLDER);	
		}catch (Exception e) {
			throw new javax.jcr.RepositoryException(e);
		}finally{
			servlets.releaseSession();
		}
		return inBoxFolder;
	}

	public String getUserHomeUrl(String portalLogin) {
		return url + ROOT_WEBDAV + HOME_FOLDER + PATH_SEPARATOR + portalLogin;     
	}

	public String getWebDavUrl(String portalLogin) {
		return webdavUrl + PATH_SEPARATOR + portalLogin;      
	}



	public static String getUserVersion(String portalLogin, JCRUserManager um) throws InternalErrorException {
		if (um==null)
			um = new JCRUserManager();
		String userVersion = um.getVersionByUser(portalLogin);
		return userVersion;
	}


	//	public JCRServlets getServlets() {
	//		if (servletManager==null)
	//			servletManager = new JCRServlets();
	//
	//		return servletManager;
	//	}




}
