/**
 *
 */
package org.gcube.portlets.user.workspace.server.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.portlets.user.workspace.server.reader.ApplicationProfileReader;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.shared.TransferOnThreddsReport;
import org.gcube.usecases.ws.thredds.FolderConfiguration;
import org.gcube.usecases.ws.thredds.PublishFolders;
import org.gcube.usecases.ws.thredds.TokenSetter;
import org.gcube.usecases.ws.thredds.engine.TransferRequestServer;
import org.gcube.usecases.ws.thredds.engine.TransferRequestServer.Report;


/**
 * The Class PublishOnThredds.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 27, 2017
 */
public class PublishOnThredds {

	private HashSet<FolderConfiguration> configs;
	private Logger logger = Logger.getLogger(ApplicationProfileReader.class);
	private String username;
	private HttpSession httpSession;
	private String wsScopeUserToken;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");


	/**
	 * Instantiates a new publish on thredds.
	 *
	 * @param wsScopeUserToken the ws scope user token
	 * @param username the username
	 * @param httpSession the http session
	 */
	public PublishOnThredds(String wsScopeUserToken, String username, HttpSession httpSession) {
		this.wsScopeUserToken = wsScopeUserToken;
		this.configs = new HashSet<FolderConfiguration>();
		this.username = username;
		this.httpSession = httpSession;
	}


	/**
	 * Publish folder.
	 *
	 * @param folderId the folder id
	 * @param metadataFolderID the metadata folder id
	 * @param publishingTargetScopeUserToken the publishing user token
	 * @param catalogueName the catalogue name
	 * @return the transfer on thredds report
	 */
	public TransferOnThreddsReport publishFolder(final String folderId, final String metadataFolderID, final String publishingTargetScopeUserToken, final String catalogueName){

		final String transferId = UUID.randomUUID().toString();
		final TransferOnThreddsReport tr = new TransferOnThreddsReport(transferId, folderId, false, null);

		new Thread(){
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {

				try{

					WsUtil.setTransferPublishingOnThredds(httpSession, tr);
					//System.out.println("Setting ws user token : "+wsScopeUserToken);

					TokenSetter.setToken(wsScopeUserToken);
					FolderConfiguration folderConfig=new FolderConfiguration(publishingTargetScopeUserToken,folderId,catalogueName);
					folderConfig.setProvidedMetadata(false);

					if(metadataFolderID!=null){
						folderConfig.setProvidedMetadata(true);
						folderConfig.setMetadataFolderId(metadataFolderID);
					}
					configs.add(folderConfig);
					TransferRequestServer server=new TransferRequestServer();
					for(FolderConfiguration entry:configs){

						try{
							Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(username).getWorkspace();
	//						FolderReport report=new FolderReport(entry);
							logger.debug("Managing {} "+entry);
							WorkspaceFolder folder = (WorkspaceFolder) ws.getItem(entry.getFolderId());
							PublishFolders.handleFolder(ws,entry,server,folder);


						}catch(WorkspaceException | HomeNotFoundException | InternalErrorException | UserNotFoundException e){
							logger.error("WORKSPACE EXC ", e);
							setStatusOnTransferId(transferId, true, "Sorry, an error has occurred during getting workspace for user: "+e.getMessage(), false);
						}catch(Exception e){
							logger.error("UNEXPECTED EXC ", e);
							setStatusOnTransferId(transferId, true, "Sorry, an unexpected error has occurred during getting workspace for user", false);
						}
					}

					logger.info("Waiting for service.. ");
					server.waitCompletion();

					Report report = server.getReport();
					File reportFile = report.toFile(folderConfig);
					if(reportFile!=null){
						Workspace workspace;
						try {
							workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(username).getWorkspace();
							InputStream fileData = new FileInputStream(reportFile);
							Timestamp timestamp = new Timestamp(System.currentTimeMillis());
							workspace.createExternalFile("Transferring on Thredds report file "+sdf.format(timestamp), "", "text/plain", fileData, folderId);
							setStatusOnTransferId(transferId, false, "Sorry, an unexpected error has occurred during getting workspace for user",true);

						}
						catch (WorkspaceFolderNotFoundException
										| InternalErrorException
										| HomeNotFoundException
										| UserNotFoundException e) {
							logger.error("Error on getting workspace for thredds transferring to folder: "+folderId, e);
							setStatusOnTransferId(transferId, true, "Sorry, an error has occurred during getting workspace for user: "+e.getMessage(),false);
						}
						catch (FileNotFoundException | InsufficientPrivilegesException | ItemAlreadyExistException | WrongDestinationException e) {
							// TODO Auto-generated catch block
							logger.error("Error on writing report for thredds transferring to folder: "+folderId, e);
							setStatusOnTransferId(transferId, true, "Sorry, an error has occurred during report creation to transfer resulting: "+e.getMessage(),false);
						}
					}
				}catch(Exception e){
					logger.error("Unexpected error has occurred when performing the tranferring to Thredds, folderID is: "+folderId, e);
					e.printStackTrace();
					setStatusOnTransferId(transferId, true, "Sorry, an unexpected error has occurred when performing the tranferring on Thredds. Refresh and try again later",false);
				}
			}

		}.start();

		return tr;

	}


	/**
	 * Gets the status of transfer id.
	 *
	 * @param httpSession the http session
	 * @param transferId the transfer id
	 * @return the status of transfer id
	 */
	public static TransferOnThreddsReport getStatusOfTransferId(HttpSession httpSession, String transferId){
		return WsUtil.geTransferPublishingOnThreddsForId(httpSession, transferId);
	}


	/**
	 * Sets the error on transfer id.
	 *
	 * @param transferId the new error on transfer id
	 * @param error the error
	 * @param reportMessage the report message
	 * @param reportCreated the report created
	 */
	public void setStatusOnTransferId(String transferId, Boolean error, String reportMessage, Boolean reportCreated){
		TransferOnThreddsReport tr = WsUtil.geTransferPublishingOnThreddsForId(httpSession, transferId);

		if(tr!=null){
			tr.setOnError(error);
			tr.setReportMessage(reportMessage);
			tr.setReportCreatedOnWorkspace(reportCreated);
			WsUtil.setTransferPublishingOnThredds(httpSession, tr);
		}


	}
}
