package org.gcube.portlets.user.workspace.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;

@ServerEndpoint(value = "/downloadfolder", subprotocols = { "downloadfolder" })
public class DownloadFolderWebSocketServer {


	protected static Logger _log = Logger.getLogger(DownloadFolderWebSocketServer.class);

	private static Set<Session> peers = Collections.synchronizedSet( new HashSet<Session>() );

	@OnOpen
	public void onOpen( final Session session )	{
		_log.debug( "onOpen(" + session.getId() + ")" );
		peers.add( session );
	}

	@OnClose
	public void onClose( final Session session ) {
		_log.debug( "onClose(" + session.getId() + ")" );
		peers.remove( session );
	}

	@OnMessage
	public void onMessage( final String message, final Session session ) {
		_log.info( "onMessage(" + message + "," + session.getId() + ")" );
		for ( final Session peer : peers )
		{
			if ( peer.getId().equals( session.getId() ) ) {
				if (message.startsWith(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_DO_ZIP)) {
					String itemId = message.split(":")[1];
					if(itemId==null || itemId.isEmpty()){
						 peer.getAsyncRemote().sendText(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_NOT_FOUND);
					} else {
						peer.getAsyncRemote().sendText(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ZIPPING);
						String username = message.split(":")[2];
						String absolutePath = zipFolder(itemId, peer, username);
						peer.getAsyncRemote().sendText(absolutePath);
					}
				}
			}
			
		}
	}
	private final static String DEFAULT_ROLE = "OrganizationMember";
	private static void setAuthorizationToken(String username) throws Exception {
	
		String scope = "/"+PortalContext.getConfiguration().getInfrastructureName();
		ScopeProvider.instance.set(scope);
		_log.debug("calling service token on scope " + scope);
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		String token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);
		_log.debug("received token: "+token);
		_log.info("Security token set in session for: "+username + " on " + scope);
	}
	
	private String zipFolder(String itemId, Session peer, String username) {

		_log.info("FOLDER DOWNLOAD REQUEST itemId="+itemId + " user=" + username);
		Workspace wa = null;
		try {
			setAuthorizationToken(username);
			wa = HomeLibrary.getUserWorkspace(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (wa == null) {
			peer.getAsyncRemote().sendText(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_SESSION_EXPIRED);
			return null;
		}

		WorkspaceItem item;
		try {

			item = wa.getItem(itemId);
			try{
				//ACCOUNTING
				item.markAsRead(true);
			} catch (InternalErrorException e) {
				_log.error("Requested item "+itemId+" has thrown an internal error exception",e);
			}

		} catch (ItemNotFoundException e) {
			_log.error("Requested item "+itemId+" not found",e);
			 peer.getAsyncRemote().sendText(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_NOT_FOUND);
			 return null;
		}

		switch (item.getType()) {
			case SHARED_FOLDER:
			case FOLDER:{
				try {
					File tmpZip = ZipUtil.zipFolder((WorkspaceFolder) item);
					
					String tmpSys = System.getProperty("java.io.tmpdir");
					String randomDirName = UUID.randomUUID().toString();
					String toReturn = randomDirName + File.separator + item.getName();
					_log.debug("Copying in " + tmpSys + File.separator + toReturn);
					String path = tmpSys + File.separator + randomDirName;
					File thePath = new File (path);
					thePath.mkdirs();
					File toCopy = new File(tmpSys + File.separator + toReturn);
					if (!toCopy.exists())
						toCopy.createNewFile();
					copyFileUsingFileChannels(tmpZip, toCopy);				
					_log.info("Zipped folder in="+toCopy.getAbsolutePath() + ", returning="+toReturn);
					tmpZip.deleteOnExit();
					return toReturn;
				} catch (Exception e) {
					_log.error("Error during folder compression "+itemId,e);
					 peer.getAsyncRemote().sendText(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_DURING_COMPRESSION);
				}
			}
			default:
				_log.error("Error during folder compression "+itemId);
				 peer.getAsyncRemote().sendText(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_NOT_A_FOLDER);
				return null;
		}
	}
	@SuppressWarnings("resource")
	private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}


	

}