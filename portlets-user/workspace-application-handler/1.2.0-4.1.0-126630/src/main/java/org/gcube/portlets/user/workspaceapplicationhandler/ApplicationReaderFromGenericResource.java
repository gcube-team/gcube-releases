package org.gcube.portlets.user.workspaceapplicationhandler;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.workspaceapplicationhandler.exception.GetUrlFromApplicationProfileException;
import org.gcube.portlets.user.workspaceapplicationhandler.reader.GcubeApplicationReader;
import org.gcube.portlets.user.workspaceapplicationhandler.util.GenericResourcePropertyReader;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ApplicationReaderFromGenericResource {
	
	protected Logger logger =  Logger.getLogger(ApplicationReaderFromGenericResource.class);

	public ApplicationReaderFromGenericResource(){
		
	}

	/**
	 * 
	 * @param oid - the oid of the workspace item that must be opened
	 * @param session - the asl session
	 * @param httpSession - the current httpsession
	 * @return
	 * @throws GetUrlFromApplicationProfileException
	 */
	public String getURLFromApplicationProfile(String oid, ASLSession session, HttpSession httpSession) throws GetUrlFromApplicationProfileException {

		String url = "";

		try {
			logger.info("get URL for item id: " + oid);

			if(session==null){
				logger.warn("ASLSession is null, return empty url for application profile");
				return "";
			}
			// GET WORKSPACE
			Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());

			// GET ITEM FROM WORKSPACE
			WorkspaceItem item = workspace.getItem(oid);

			// ITEM IS A WorkspaceItemType.FOLDER_ITEM?
			if (item.getType().equals(WorkspaceItemType.FOLDER_ITEM)) {

				ScopeBean scope = new ScopeBean(session.getScope());
				logger.info("item id has scope: " + scope.toString());
				FolderItem folderItem = (FolderItem) item;

				//Read property file
				GenericResourcePropertyReader gRead = new GenericResourcePropertyReader();

				GcubeApplicationReader app = new GcubeApplicationReader(folderItem.getFolderItemType().toString(), scope, gRead.getGenericResource(), gRead.getAppId());

				//Rewrite scope into ASL session
				ASLSession newSession = SessionManager.getInstance().getASLSession(httpSession.getId(), session.getUsername());
				String newScope = app.getGcubeApplication().getAppProfile().getScope();
				newSession.setScope(newScope);
				
				logger.info("Setting new scope into ScopeProvider: " + newScope);
				ScopeProvider.instance.set(newScope);
				
//				System.out.println("Rewrite scope into ASL session with new scope: " + newSession.getScopeName());
				logger.info("Rewriting scope into ASL session with new scope: " + newSession.getScopeName());
				setValueInSession(newSession, app.getGcubeApplication().getKeyOID(), oid);

				url = app.getGcubeApplication().getAppProfile().getUrl();
			} else
				throw new GetUrlFromApplicationProfileException(
						"The workspace item with oid: " + oid
								+ " is not a WorkspaceItemType.FOLDER_ITEM");

		} catch (Exception e) {
			logger.error("getURLFromApplicationProfile", e);
			throw new GetUrlFromApplicationProfileException(
					"Sorry, an error occurred in getURLFromApplicationProfile, try again");
		}

		return url;
	}

	/**
	 * 
	 * @param session
	 * @param name
	 * @param value
	 * @throws Exception
	 */
	private void setValueInSession(ASLSession session, String name, String value) throws Exception {

		try {
			session.setAttribute(name, value);
			logger.info("in scope "+session.getScopeName()+ " set attribute name: " + name+ ", value: " + value);
		} catch (Exception e) {
			logger.error("setValueInSession", e);
			throw new Exception(e.getMessage());
		}
	}
	/*
	public static void main(String[] args) throws Exception
	{
		String scopeString = "/gcube";
		
//		scopeString = "/gcube/devNext/NextNext";
		String type = "REPORT_TEMPLATE";
		
		ScopeBean scope = new ScopeBean(scopeString);

		GenericResourcePropertyReader gRead = new GenericResourcePropertyReader();

		GcubeApplicationReader app = new GcubeApplicationReader(type, scope, gRead.getGenericResource(), gRead.getAppId());
		
		System.out.println(app.getGcubeApplication());
		
		System.out.println(app.getGcubeApplication().getAppProfile());

	}*/

}
