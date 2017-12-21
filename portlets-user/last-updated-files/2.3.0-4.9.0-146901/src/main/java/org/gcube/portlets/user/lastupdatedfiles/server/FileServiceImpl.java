package org.gcube.portlets.user.lastupdatedfiles.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.lastupdatedfiles.client.FileService;
import org.gcube.portlets.user.lastupdatedfiles.shared.FileItemsWrapper;
import org.gcube.portlets.user.lastupdatedfiles.shared.ImageType;
import org.gcube.portlets.user.lastupdatedfiles.shared.LufFileItem;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class FileServiceImpl extends RemoteServiceServlet implements FileService {
	private static Logger _log = LoggerFactory.getLogger(FileServiceImpl.class);
	/**
	 * the number of top recent items to retrieve
	 */
	private final static int ITEMS_NO_TO_RETRIEVE = 6;
	public static final String TEST_USER = "test.user";
	public static final String RECENT_FILES_CACHE_NAME = "LUF_CACHE";
	public static final String LAST_RETRIEVED_TIME = "LAST_RETRIEVED_TIME";


	@Override
	public void setRead(String workspaceItemId) {
		PortalContext pContext = PortalContext.getConfiguration();
		String username = pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
		String currScope = pContext.getCurrentScope(getThreadLocalRequest());
		ScopeProvider.instance.set(currScope);
		Workspace ws = null;
		try {
			ws = HomeLibrary.getUserWorkspace(username);
			ws.getItem(workspaceItemId).markAsRead(true);
			_log.debug(username + ": item read marked as true with id: " + workspaceItemId);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public String getWorkspaceFolderURL() {
		PortalContext pContext = PortalContext.getConfiguration();
		String currScope = pContext.getCurrentScope(getThreadLocalRequest());
		String username = pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
		Workspace ws = null;
		String siteLandingPagePath = pContext.getSiteLandingPagePath(getThreadLocalRequest());
		String toReturn = siteLandingPagePath;
		ScopeProvider.instance.set(currScope);
		
		try {
			ws = HomeLibrary.getUserWorkspace(username);
			WorkspaceSharedFolder sharedFolder = ws.getVREFolderByScope(currScope);
			toReturn = new String(new StringBuffer(siteLandingPagePath)
					.append(GCubePortalConstants.USER_WORKSPACE_FRIENDLY_URL)
					.append("?itemid=")
					.append(sharedFolder.getId()));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * 
	 */
	@Override
	public FileItemsWrapper getLastUpdateFiles() {
		FileItemsWrapper toReturn = null;
		PortalContext pContext = PortalContext.getConfiguration();
		String currScope = pContext.getCurrentScope(getThreadLocalRequest());
		String username = pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
		ScopeProvider.instance.set(currScope);
		toReturn = getFilesFromCache();
		if (toReturn != null) 
			return toReturn;

		ArrayList<LufFileItem> fileItems = new  ArrayList<LufFileItem>();
		Workspace ws = null;
		try {
			ws = HomeLibrary.getUserWorkspace(username);


			_log.info("Trying to get Group folder for scope="+currScope);
			boolean isInfra = isInfrastructureScope();
			String folderName = "";
			String folderId = "";
			List<WorkspaceItem> items = null;
			if (isInfrastructureScope()) {
				WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/");
				folderId = folder.getId();
				items = folder.getLastItems(ITEMS_NO_TO_RETRIEVE);
			} else {
				WorkspaceSharedFolder sharedFolder = ws.getVREFolderByScope(currScope);
				folderName = sharedFolder.getDisplayName();
				folderId = sharedFolder.getId();
				items = sharedFolder.getLastItems(ITEMS_NO_TO_RETRIEVE);

			}

			for (WorkspaceItem item : items) {
				String[] splits =  item.getName().split("\\.");
				String extension = "";
				if (splits.length > 0) {
					extension = splits[splits.length-1];
				}
				fileItems.add(new LufFileItem(item.getId(), item.getName(), item.getOwner().getPortalLogin(), "", item.getLastModificationTime().getTime(), getIconImageType(extension), item.getPublicLink(false)));
			}

			String siteLandingPagePath = PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest());
			toReturn = new FileItemsWrapper(folderName, folderId, fileItems, isInfra, siteLandingPagePath);
			storeFilesInCache(toReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private FileItemsWrapper getFilesFromCache() {
		HttpSession session = getThreadLocalRequest().getSession();

		Long now = new Date().getTime();
		Long previous = (Long) session.getAttribute(LAST_RETRIEVED_TIME);
		if (previous == null)
			return null;

		if (now > (previous + 120000) ) {
			_log.debug("more than 120 secs have passed");
			return null;
		}

		_log.debug("less than 120 secs, getting from cache");
		if (session.getAttribute(RECENT_FILES_CACHE_NAME) == null) { //see if you have to initialise
			HashMap<String, FileItemsWrapper> toStore = new HashMap<>();
			session.setAttribute(RECENT_FILES_CACHE_NAME, toStore);
			return null;
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, FileItemsWrapper> map = (HashMap<String, FileItemsWrapper>) session.getAttribute(RECENT_FILES_CACHE_NAME);
			String currentScope = PortalContext.getConfiguration().getCurrentScope(getThreadLocalRequest());
			if (map.containsKey(currentScope)) {
				return map.get(currentScope);
			}			
		}
		return null;
	}

	private void storeFilesInCache(FileItemsWrapper wrapper) {
		HttpSession session = getThreadLocalRequest().getSession();
		String currentScope = PortalContext.getConfiguration().getCurrentScope(getThreadLocalRequest());
		if (session.getAttribute(RECENT_FILES_CACHE_NAME) == null) { //see if you have to initialise
			HashMap<String, FileItemsWrapper> toStore = new HashMap<>();
			session.setAttribute(RECENT_FILES_CACHE_NAME, toStore);
		}
		@SuppressWarnings("unchecked")
		HashMap<String, FileItemsWrapper> map = (HashMap<String, FileItemsWrapper>) session.getAttribute(RECENT_FILES_CACHE_NAME);
		map.put(currentScope, wrapper);
		session.setAttribute(RECENT_FILES_CACHE_NAME, map);
		session.setAttribute(LAST_RETRIEVED_TIME, new Date().getTime());
	}

	private ImageType getIconImageType(String extension) {
		if (extension == null || extension.compareTo("") == 0)
			return ImageType.NONE;

		switch (extension) {
		case "doc":
		case "docx":
		case "rtf":
			return ImageType.DOC;
		case "xls":
		case "xlsx":
			return ImageType.XLS;
		case "ppt":
		case "pptx":
			return ImageType.PPT;
		case "pdf":
			return ImageType.PDF;
		case "jpg":
		case "jpeg":
		case "gif":
		case "bmp":
		case "png":
		case "tif":
		case "tiff":
			return ImageType.IMAGE;
		case "avi":
		case "mp4":
		case "mpeg":
			return ImageType.MOVIE;
		case "html":
		case "htm":
		case "jsp":
			return ImageType.HTML;
		case "rar":
			return ImageType.RAR;
		case "zip":
		case "tar":
		case "tar.gz":
			return ImageType.ZIP;
		default:
			return ImageType.NONE;
		}
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		long currGroupId = PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
		try {
			return new LiferayGroupManager().isRootVO(currGroupId);
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return false;
	}


}
