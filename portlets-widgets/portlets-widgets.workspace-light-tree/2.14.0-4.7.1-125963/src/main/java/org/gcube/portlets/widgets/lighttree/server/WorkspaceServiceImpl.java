/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.server;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.portlets.widgets.lighttree.client.FilterCriteria;
import org.gcube.portlets.widgets.lighttree.client.Item;
import org.gcube.portlets.widgets.lighttree.client.ItemType;
import org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreeServiceException;
import org.gcube.portlets.widgets.lighttree.client.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class WorkspaceServiceImpl extends RemoteServiceServlet implements WorkspaceService{
	
	public static final Logger _log = LoggerFactory.getLogger(WorkspaceServiceImpl.class);
	public static final String USERNAME_ATTRIBUTE = "username";

	
	private ASLSession getASLSession(HttpSession httpSession)	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		_log.error("WORKSPACE PORTLET SessionID= " + sessionID);
		
		//TODO we check for the older attribute name
		if (user == null) user = (String) httpSession.getAttribute("user");

		if (user == null) {

			_log.error("WORKSPACE PORTLET STARTING IN TEST MODE - NO USER FOUND");

			//for test only
			user = "test.user";
			user = "gianpaolo.coro";
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope("/gcube/devsec/devVRE");

			return session;
		} else _log.trace("user found in session "+user);
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}


	protected Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException	{
		ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
		
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
		return workspace;
	}

	/**
	 * {@inheritDoc}
	 */
	public Item getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceLightTreeServiceException {
		_log.trace("getRoot showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+ filterCriteria);
	
		try {

			Workspace workspace = getWorkspace();

			_log.trace("Start getRoot...");

			WorkspaceFolder specials = workspace.getMySpecialFolders();
			
			WorkspaceItem root = workspace.getRoot();

			_log.trace("GetRoot  - Replyiing root");

			long startTime = System.currentTimeMillis();
			_log.trace("start time - " + startTime);

			Item rootItem = ItemBuilder.getItem(null, root, showableTypes, filterCriteria, 2);
	
			Item specialFolders = ItemBuilder.getItem(null, specials, showableTypes, filterCriteria, 2);
			specialFolders.setShared(true);
			rootItem.addChild(specialFolders);		

			if(rootItem==null)
				_log.trace("############ rootItem null");

			_log.trace("Only showable types:");
			//printName("", rootItem);

			if (purgeEmpyFolders) rootItem = ItemBuilder.purgeEmptyFolders(rootItem);

			_log.trace("Returning:");

			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.trace("end time - " + time);

			//printName("", rootItem);
			
			Collections.sort(rootItem.getChildren(), new ItemComparator());

			return rootItem;

		} catch (Exception e) {
			_log.error("Error during root retrieving", e);
			throw new WorkspaceLightTreeServiceException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Item getFolder(String folderId, List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceLightTreeServiceException {
		_log.trace("getFolder folderId: "+folderId+" showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem folder = workspace.getItem(folderId);

			_log.trace("GetFolder - Replyiing folder");

			long startTime = System.currentTimeMillis();
			_log.trace("start time - " + startTime);


			Item folderItem = ItemBuilder.getItem(null, folder, showableTypes, filterCriteria, 2);

			_log.trace("Only showable types:");
			//printName("", folderItem);

			if (purgeEmpyFolders) folderItem = ItemBuilder.purgeEmptyFolders(folderItem);

			_log.trace("Returning:");

			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.trace("end time - " + time);

			//printName("", folderItem);
			
			Collections.sort(folderItem.getChildren(), new ItemComparator());

			return folderItem;

		} catch (Exception e) {
			_log.error("Error during folder retrieving", e);
			throw new WorkspaceLightTreeServiceException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean checkName(String name) throws WorkspaceLightTreeServiceException {
		_log.trace("checkName name: "+name);
		try {
			ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
			Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
			return workspace.isValidName(name);
		} catch (Exception e) {
			_log.error("Error during folder retrieving", e);
			throw new WorkspaceLightTreeServiceException(e.getMessage());
		}
	}

	/*protected void printName(String indentation, Item item)
	{
		if(item!=null){
			_log.trace(indentation+item.getName());
			for (Item child:item.getChildren()) printName(indentation+"\t", child);
		}
	}*/

}
