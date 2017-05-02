package org.gcube.common.homelibrary.jcr.repository;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.Variable;
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
	private static final String CHAT_FOLDER					= "Chat";

	//home level
	private static final String DOWNLOADS					= "Downloads";
	private static final String SMART_FOLDER 				= "Folders";
	private static final String IN_BOX_FOLDER 				= "InBox";
	private static final String OUT_BOX_FOLDER				= "OutBox";

	private final static String SERVICENAME 				= "HomeLibraryWebapp";
	private final static String SERVICECLASS 				= "DataAccess";

	private final static String VERSION 					= "3";
	private final static String MINOR_VERSION 				= "1";
	private final static String REVISION_NUMBER 			= "1";
	public final static String HL_VERSION 					=  VERSION + "." + MINOR_VERSION + "." + REVISION_NUMBER;
	public final static String WEBDAV_URL					= "https://www.d4science.org/Home";

	private String portalLogin;

	public static Map<String, GCoreEndpoint.Profile.Endpoint> servlets;

	private static Logger logger = LoggerFactory.getLogger(JCRRepository.class);
	private static JCRUserManager um;

	public JCRRepository(final User user) throws InternalErrorException {

		this.portalLogin = user.getPortalLogin();
		logger.debug("getHome " + portalLogin);
		try {
			init();
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}

	private static synchronized void initializeRepository() throws InternalErrorException {


		String context = null;
		AuthorizationEntry entry = null;
		try {

			entry = authorizationService().get(SecurityTokenProvider.instance.get());
			logger.trace("Token for caller HL" + entry.toString());
			if ((context=entry.getContext()) == null)
				throw new IllegalArgumentException("context is null");

			logger.debug("scope for repository creation is {} ",context);
			ScopeProvider.instance.set(context);	

		} catch (Exception e1) {
			logger.debug("NO TOKEN");
			context = ScopeProvider.instance.get();
			//			throw new InternalErrorException("User not authorize to access Home Library");
		}

		try {

			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s' and $resource/Profile/ServiceClass/text() eq '%s' ", SERVICENAME, SERVICECLASS));

			DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

			List<GCoreEndpoint> resources = client.submit(query);
			if(resources.size() == 1) {	   

				try {
					GCoreEndpoint resource = resources.get(0);
					servlets = resource.profile().endpointMap();
//					System.out.println(servlets.toString());
				} catch (Throwable e) {
					logger.error("error decrypting resource",e);
				}
			}else if(resources.size() > 1){

				//				ScopeProvider.instance.set(context);

				SimpleQuery queryhn = queryFor(HostingNode.class);
				queryhn.addCondition(String.format("$resource/ID/text() eq '%s'", resources.get(0).profile().ghnId()));

				DiscoveryClient<HostingNode> clienthn = clientFor(HostingNode.class);

				List<HostingNode> hns = clienthn.submit(queryhn);
				boolean isPreProd = false;
				Iterator<Variable> iterator = hns.get(0).profile().description().environmentVariables().iterator();
				while (iterator.hasNext()){
					Variable res = iterator.next();

					//					System.out.println(res.toString());
					if (res.key().equals("environment") && res.value().equals("pre-poduction")){
						//												System.out.println(res.key() + " : " + res.value());
						isPreProd = true;
						break;
					}
				}

				GCoreEndpoint resource = null;
				try {
					if (isPreProd)
						resource = resources.get(1);
					else
						resource = resources.get(0);

					if (resource!=null)
						servlets = resource.profile().endpointMap();

				} catch (Throwable e) {
					logger.error("error decrypting resource",e);
				}
			}

		} catch (Exception e) {
			throw new InternalErrorException(e);
		}


	}

	/**
	 * Get users home names
	 * @return a list of users home names
	 * @throws RepositoryException
	 */
	public List<String> getHomeNames() throws RepositoryException{
		List<String> homes = new ArrayList<String>();
		JCRSession session = null;
		try {
			session = new JCRSession(portalLogin, false);
			ItemDelegate home = session.getItemByPath(PATH_SEPARATOR + HOME_FOLDER);
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
	public ItemDelegate getHome() throws RepositoryException{
		ItemDelegate home = null;
		JCRSession session = null;
		try {
			session = new JCRSession(portalLogin, false);
			home = session.getItemByPath(PATH_SEPARATOR+HOME_FOLDER);
		} catch (ItemNotFoundException e) {
			try {
				home = getRootNode().addNode(HOME_FOLDER, PrimaryNodeType.NT_FOLDER);
				session.saveItem(home);
			} catch (Exception e1) {
				throw new RepositoryException(e1.getMessage());
			}
		}finally{
			if (session!=null)
				session.releaseSession();
		}
		return home;
	}


	public synchronized static void initialize() throws InternalErrorException {
		logger.debug("Initialize repository");
		initializeRepository();

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

		String userVersion = getUserVersion(portalLogin);
		logger.debug(portalLogin + " - USER VERSION: " + userVersion + " - HL VERSION: " + HL_VERSION);
//		System.out.println(portalLogin + " - USER VERSION: " + userVersion + " - HL VERSION: " + HL_VERSION);
		if(!HL_VERSION.equals(userVersion)){

			JCRSession servlets = null;
			try {
				servlets = new JCRSession(portalLogin, false);

				ItemDelegate home = getHome();

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
						throw new RepositoryException(e.getMessage());
					}
					um.createUser(portalLogin, HL_VERSION);

				} catch (InternalErrorException e) {
//					System.out.println("****");
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
			logger.debug("skip init in JCRRepository");
	}



	//	public boolean exist(Node parent, String childName) throws RepositoryException {
	//		try {
	//			parent.getNode(childName);
	//		} catch (PathNotFoundException e) {
	//			logger.debug(childName + " does not exist");
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
	public ItemDelegate getGCubeRoot() throws RepositoryException, InternalErrorException {

		logger.debug("getGCubeRoot");

		JCRSession servlet = null;
		ItemDelegate gcubeRoot = null;
		try{
			servlet = new JCRSession(portalLogin, false);
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
	public ItemDelegate getSharedRoot() throws RepositoryException, InternalErrorException {
		logger.debug("getSharedRoot");
		JCRSession servlet = null;
		ItemDelegate sharedNode = null;
		try {
			servlet = new JCRSession(portalLogin, false);
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
	private DelegateManager getRootNode() throws RepositoryException {

		JCRSession servlet = null;
		ItemDelegate root = null;
		DelegateManager wrap = null;
		try{
			servlet = new JCRSession(portalLogin, false);
			root = servlet.getItemByPath(PATH_SEPARATOR);
			wrap = new DelegateManager(root, "");
		}catch (Exception e) {
			logger.error("Error retrieving Root Node "+ e.getMessage());
			//			throw new RepositoryException("Error retrieving Root Node "+ e.getMessage());
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
		logger.debug("getUserHome: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + portalLogin);
		ItemDelegate userHomeDelegate = null;
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(portalLogin, false);
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
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(portalLogin, true);
			logger.debug("getRootSmartFolders: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
					+ portalLogin + PATH_SEPARATOR + SMART_FOLDER);

			//create applicationFolder
			try {		
				smartFolders = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
						+ portalLogin + PATH_SEPARATOR + SMART_FOLDER);
			} catch (ItemNotFoundException e) {	
				smartFolders = getUserHome(portalLogin).addNode(SMART_FOLDER, PrimaryNodeType.NT_FOLDER);
				servlets.saveItem(smartFolders);
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
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(portalLogin, false);
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

		JCRSession servlets = null;
		try {

			servlets = new JCRSession(portalLogin, false);
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
		JCRSession servlets = null;
		logger.debug("getOutBoxFolder: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
				+ portalLogin + PATH_SEPARATOR + OUT_BOX_FOLDER);
		try {

			servlets = new JCRSession(portalLogin, false);
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

		logger.debug("Get Download Folder: " + PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
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
		JCRSession servlets = null;
		ItemDelegate inBoxFolder = null;
		try {
			servlets = new JCRSession(portalLogin, false);
			inBoxFolder = servlets.getItemByPath(PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR 
					+ user + PATH_SEPARATOR + IN_BOX_FOLDER);	
		}catch (Exception e) {
			throw new javax.jcr.RepositoryException(e);
		}finally{
			servlets.releaseSession();
		}
		return inBoxFolder;
	}

	//	public String getUserHomeUrl(String portalLogin) {
	//		return url + ROOT_WEBDAV + HOME_FOLDER + PATH_SEPARATOR + portalLogin;     
	//	}

	public String getWebDavUrl(String portalLogin) {
		return WEBDAV_URL + PATH_SEPARATOR + portalLogin;      
	}



	public static String getUserVersion(String portalLogin) throws InternalErrorException {	
		String userVersion = getUserManager().getVersionByUser(portalLogin);
		return userVersion;
	}


	/**
	 * Get Conversation root
	 * @return the conversation root
	 * @throws RepositoryException
	 * @throws InternalErrorException 
	 */
	public ItemDelegate getChatRoot() throws RepositoryException, InternalErrorException {
		logger.debug("Get Chat root");
		JCRSession servlet = null;
		ItemDelegate sharedNode = null;
		try {
			servlet = new JCRSession(portalLogin, false);
			try{
				sharedNode = servlet.getItemByPath(PATH_SEPARATOR + CHAT_FOLDER);
			}catch (Exception e) {
				sharedNode = getRootNode().addNode(CHAT_FOLDER, PrimaryNodeType.NT_FOLDER);
				servlet.saveItem(sharedNode);
			}

		} catch (RepositoryException e) {
			logger.error("Error retrieving chat root");
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} finally {
			servlet.releaseSession();
		}
		return sharedNode;
	}

	public static JCRUserManager getUserManager(){
		if (um==null)
			um = new JCRUserManager();		
		return um;
	}
}
