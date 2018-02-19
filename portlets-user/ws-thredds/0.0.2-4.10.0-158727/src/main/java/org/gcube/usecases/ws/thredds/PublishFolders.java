package org.gcube.usecases.ws.thredds;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRemoval;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.usecases.ws.thredds.engine.PublishRequest;
import org.gcube.usecases.ws.thredds.engine.PublishRequest.Mode;
import org.gcube.usecases.ws.thredds.engine.PublishRequest.PublishItem;
import org.gcube.usecases.ws.thredds.engine.TransferRequestServer;
import org.gcube.usecases.ws.thredds.engine.TransferRequestServer.Report;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublishFolders {

	private static String WS_USER="fabio.sinibaldi";


	public static void main(String[] args) {
		TokenSetter.set("/gcube");
		HashSet<FolderConfiguration> configs=new HashSet<>();


				String folderId="be451663-4d4f-4e23-a2c8-060cf15d83a7"; // NETCDF DATASETS
		//		String metadataFolderID="2de04273-ca79-4478-a593-354c5a12f942"; //metadata files


//		String folderId="a711a8d7-5e93-498f-a29c-b888d7c2e48f"; TICKET

		String publishingUserToken="5741e3e4-dbde-46fa-828d-88da609e0517-98187548"; //fabio @NextNext


		FolderConfiguration folderConfig=new FolderConfiguration(publishingUserToken,folderId,"ICCAT_BFT_TEST_rewrite");
		//		folderConfig.setProvidedMetadata(true);
		//		folderConfig.setMetadataFolderId(metadataFolderID);




		configs.add(folderConfig);







		TransferRequestServer server=new TransferRequestServer();
		for(FolderConfiguration entry:configs){			
			try{
				Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(WS_USER).getWorkspace();				
				//				FolderReport report=new FolderReport(entry);
				log.info("Managing {} ",entry);
				WorkspaceFolder folder = (WorkspaceFolder) ws.getItem(entry.getFolderId());
				handleFolder(ws,entry,server,folder);



			}catch(WorkspaceException e){
				System.err.println("WORKSPACE EXC ");
				e.printStackTrace(System.err);
			}catch(HomeNotFoundException e){
				System.err.println("WORKSPACE EXC ");
				e.printStackTrace(System.err);
			}catch(InternalErrorException e){
				System.err.println("WORKSPACE EXC ");
				e.printStackTrace(System.err);
			}catch(UserNotFoundException e){
				System.err.println("WORKSPACE EXC ");
				e.printStackTrace(System.err);
			}catch(Exception e){
				System.err.println("UNEXPECTED EXC");
				e.printStackTrace(System.err);
			}
		}
		System.out.println("Waiting for service.. ");
		server.waitCompletion();

		Report report=server.getReport();

		File reportFile =report.toFile(folderConfig);
		System.out.println("Report at "+reportFile.getAbsolutePath());


	}

	/**
	 * For *.nc | *.ncml 
	 * 	if relatedMetadataFolder contains <filename>.xml use meta
	 * 	else ask DT to generate it
	 * 
	 * 
	 * @param wsFolder
	 * @param config
	 * @param server
	 * @return
	 * @throws WorkspaceException
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException 
	 * @throws HomeNotFoundException
	 * @throws UserNotFoundException
	 */

	public static final void handleFolder(Workspace ws, FolderConfiguration config,TransferRequestServer server,  WorkspaceFolder folder) throws InternalErrorException, ItemNotFoundException{
		
		log.info("Handling folder {} ",folder.getPath());
		
		cleanUpForFileRemoval(ws, folder, config.getPublishingUserToken(),config.getCatalogName());
		
		log.info("Folder {} cleaned up. Going to publish phase..",folder.getPath());
		publishFolder(ws,config,server,folder);		
	}
	
	
	public static final void publishFolder(Workspace ws, FolderConfiguration config,TransferRequestServer server,  WorkspaceFolder folder) throws InternalErrorException, ItemNotFoundException{

		
		
		
		//Access folder
		List<WorkspaceItem> folderItems=folder.getChildren();

		if(config.isIncludeSubfolders()) {
			log.info("Going through subfolders first.....");
			for(WorkspaceItem item:folderItems) {
				try {
					if(item.isFolder()) {						
						FolderConfiguration subConfig=new FolderConfiguration(config);
						subConfig.setCatalogName(config.getCatalogName()+"/"+item.getName());
						publishFolder(ws,subConfig,server,(WorkspaceFolder) item);						
					}
				}catch(Exception e) {
					log.warn("Unable to handle folder {} .",item,e);
				}
			}
		}



		log.debug("Checking for ncml files .... ");
		for(WorkspaceItem item : folderItems) {
			try {
				if(!item.isFolder()) {
					String prefix=item.getName().substring(item.getName().lastIndexOf("."), item.getName().length());
					if(prefix.equals(".ncml")) {
						PublishRequest req=new PublishRequest(new PublishItem(item),Mode.NCML, config.getCatalogName(), config.getPublishingUserToken());				
						if(config.isProvidedMetadata()) {
							String toLookForName=item.getName().substring(0, item.getName().lastIndexOf(prefix))+".xml";
							File meta=getMetadataForDataset(ws, toLookForName, config.getMetadataFolderId());
							if (meta!=null) req.setMetadata(meta);					
						}
						// TODO NB Check for queue		
						server.put(req);			
					}
				}
			}catch(Exception e) {
				log.warn("Unabel to check item {} ",item,e);
			}
		}

		log.debug("Checking nc files.. ");

		for(WorkspaceItem item:folder.getChildren()){
			try {
				if(!item.isFolder()) {
					String prefix=item.getName().substring(item.getName().lastIndexOf("."), item.getName().length());
					if(prefix.equals(".nc")){
						// NC
						PublishRequest req=new PublishRequest(new PublishItem(item),Mode.NC, config.getCatalogName(), config.getPublishingUserToken());
						if(config.isProvidedMetadata()) {
							String toLookForName=item.getName().substring(0, item.getName().lastIndexOf(prefix))+".xml";
							File meta=getMetadataForDataset(ws, toLookForName, config.getMetadataFolderId());
							if (meta!=null) req.setMetadata(meta);					
						}
						server.put(req);					
					}
				}
			}catch(Exception e) {
				log.warn("Unable to check item {} ",item,e);
			}
		}
		folder.getProperties().addProperties(Collections.singletonMap(Commons.Constants.LAST_UPDATE_TIME, System.currentTimeMillis()+""));
		log.debug("Creating requests... ");
	}

	private static final File getMetadataForDataset(Workspace userWorkspace, String toLookForName, String metadataFolderId) throws WrongItemTypeException, InternalErrorException {
		try{
			WorkspaceItem found=userWorkspace.find(toLookForName,metadataFolderId);
			if(found==null) throw new ItemNotFoundException("Found item was null");			
			return NetUtils.toFile(((ExternalFile)found).getData());
		}catch(ItemNotFoundException e) {
			return null;
		}
	}

	// Remotely deletes folders which has history operations : RENAMING, REMOVAL
	private static final void cleanUpForFileRemoval(Workspace ws, WorkspaceFolder folder,String targetToken, String remoteFolderPath) throws InternalErrorException {
		List<AccountingEntry> history=folder.getAccounting();
		long lastUpdateTimeMillis=getLastUpdateTime(folder);
		Date lastUpdate=new Date(lastUpdateTimeMillis);
		log.info("Checking history for {} (last update time {}) ",folder.getPath(),Commons.DATE_FORMAT.format(lastUpdate));
		
		
		//look into history
		boolean toDeleteCurrentFolder=false;
		for(AccountingEntry entry: history) {
			Date eventTime=entry.getDate().getTime();
			
			switch(entry.getEntryType()) {
			case REMOVAL:
				
			case RENAMING:{
				log.debug("Found Accounting Entry [type : {}, date {}] ",entry.getEntryType(),Commons.DATE_FORMAT.format(eventTime));
				if(eventTime.after(lastUpdate)) {
					log.info("Found Accounting Entry [type : {}, date {}]. Removing remote folder. ",entry.getEntryType(),Commons.DATE_FORMAT.format(eventTime));
					toDeleteCurrentFolder=true;					
				}				
			}				
			}
			if(toDeleteCurrentFolder) break;
		}
		
		
		//Delete Folder or scan children
		if(toDeleteCurrentFolder) {
			log.info("Deleting current folder {} from remote location {} ",folder.getPath(),remoteFolderPath);
			try{
				Commons.cleanupFolder(remoteFolderPath,targetToken);
			}catch(Throwable t) {
				log.warn("Unable To cleanup folder {} . Remote Folder might not exists. If this is first publishing ignor this.",remoteFolderPath,t);
				if(lastUpdateTimeMillis!=0) // do no rethrow in case of first publish 
					throw t;
				
			}
		}else {
			log.info("Folder is not to be cleaned up. Checking children..");
			for(WorkspaceItem item:folder.getChildren())
				if(item.isFolder())cleanUpForFileRemoval(ws, (WorkspaceFolder) item,targetToken, remoteFolderPath+"/"+item.getName());
		}
	}

	private static long getLastUpdateTime(WorkspaceItem item) throws InternalErrorException {
		try{
			return Long.parseLong(item.getProperties().getPropertyValue(Commons.Constants.LAST_UPDATE_TIME));
		}catch(Throwable e) {
			log.debug("Unable to get last update time for {}. Considering 0..",item.getPath());
			return 0l;
		}
	}
	
	
	
	
}
