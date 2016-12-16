package org.gcube.accounting.aggregator.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gcube.accounting.aggregator.configuration.Constant;
import org.gcube.accounting.aggregator.madeaggregation.Aggregation;
import org.gcube.accounting.aggregator.madeaggregation.AggregationType;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;


/**
 * @author Alessandro Pieve (ISTI - CNR)
 *
 */
public class WorkSpaceManagement {
	public static Logger logger = LoggerFactory.getLogger(Aggregation.class);

	/**
	 * Save a backup file compressed into workspace 
	 * @param bucket
	 * @param startKeyString
	 * @param endKeyString
	 * @param aggType
	 * @return
	 * @throws Exception
	 */
	protected static boolean onSaveBackupFile(Bucket accountingBucket,String bucket,String scope,String startKeyString,String endKeyString,
			AggregationType aggType) throws Exception{

		String nameFile="complete.json";
		String nameFileZip="complete.zip";
		String namePathFile=Constant.PATH_DIR_BACKUP+"/"+nameFile;
		String namePathFileZip=Constant.PATH_DIR_BACKUP+"/"+nameFileZip;
		String subFolderName="";
		if (scope==null)
			subFolderName=endKeyString.replace(",","-")+"_"+startKeyString.replace(",","-");
		else
			subFolderName=scope.replace("/", "")+"_"+endKeyString.replace(",","-")+"_"+startKeyString.replace(",","-");
		try {

			WorkspaceFolder wsRootDir=init(Constant.user);
			//bucket folder for backup
			WorkspaceFolder folderBucketName=createFolder(Constant.user, wsRootDir.getId(),	bucket, "Backup Folder");
			//type folder for backup
			WorkspaceFolder folderTypeName=createFolder(Constant.user, folderBucketName.getId(),	aggType.name(), "Backup Folder");		
			//type folder for backup
			WorkspaceFolder folderStartTimeName=createFolder(Constant.user, folderTypeName.getId(),	subFolderName, "Backup Folder");		
			DesignID designid=DesignID.valueOf(bucket);		
			String designDocId=designid.getNameDesign();
			String viewName="";
			if (scope!=null)
				viewName=designid.getNameViewScope();
			else
				viewName=designid.getNameView();
			JsonArray startKey = Utility.generateKey(scope,startKeyString);
			JsonArray endKey = Utility.generateKey(scope,endKeyString);
			ViewQuery query = ViewQuery.from(designDocId, viewName);
			query.startKey(startKey);
			query.endKey(endKey);
			query.reduce(false);
			query.inclusiveEnd(false);
			ViewResult viewResult;
			try {
				viewResult = accountingBucket.query(query);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
				throw e;
			}

			//manage error
			BufferedWriter filebackup =null;
			File logFile = new File(namePathFile);
			logFile.delete();
						
			Thread.sleep(500);
			filebackup = new BufferedWriter(new FileWriter(logFile));	
			for (ViewRow row : viewResult){
				if (row.document()!=null){
					if (!row.document().content().toString().isEmpty()){
						filebackup.write(row.document().content().toString());
						filebackup.newLine();
					}
				}
			}
			filebackup.close();			
			//create a zip file
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(namePathFileZip);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze= new ZipEntry(nameFile);
			
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(namePathFile);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			
			in.close();
			zos.closeEntry();
			zos.close();
			
			InputStream fileZipStream = new FileInputStream(namePathFileZip);
			WorkSpaceManagement.saveItemOnWorkspace(Constant.user,fileZipStream,"complete.zip", "Description",  folderStartTimeName.getId());
			logger.trace("Save a backup file into workspace; bucket{},scope:{}, startkey:{},endkey:{}, aggregation type:{}",bucket,scope,startKeyString,endKeyString ,aggType.toString());
			logFile.delete();
			File logFileZip = new File(namePathFileZip);
			logFileZip.delete();
			return true;
		}
		catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			logger.error(e.getMessage());
			logger.error("onSaveBackupFile excepiton:{}",e);						
			throw e;
		}
	}

	/**
	 * Init 
	 * Return a workspace folder 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	protected static WorkspaceFolder init(String user) throws Exception{
		// TODO Auto-generated constructor stub
		try {
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
			HomeManager manager = factory.getHomeManager();
			User userWS = manager.createUser(user);
			Home home = manager.getHome(userWS);
			Workspace ws = home.getWorkspace();
			WorkspaceFolder root = ws.getRoot();
			return root;
		} catch (Exception e){
			logger.error("init excepiton:{}",e);						
			throw e;
		}

	}

	/**
	 * Create Folder into workspace
	 * @param user
	 * @param parentId folder parent
	 * @param folderName
	 * @param folderDescription
	 * @return
	 * @throws Exception 
	 */
	protected static WorkspaceFolder createFolder(String user, String parentId,	String folderName, String folderDescription) throws Exception
	{
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);
			WorkspaceFolder projectTargetFolder;
			if (!ws.exists(folderName, parentId))
				projectTargetFolder = ws.createFolder(folderName, folderDescription, parentId);
			else
				projectTargetFolder = (WorkspaceFolder) ws.find(folderName, parentId);
			return projectTargetFolder;
		} catch (Exception e){
			logger.error("createFolder:{}",e);						
			throw e;
		}
	}

	/**
	 * Save a Item on workspace
	 * @param user of workspace
	 * @param inputStream 
	 * @param name
	 * @param description
	 * @param folderId
	 * @throws Exception
	 */
	protected static void saveItemOnWorkspace(String user, InputStream inputStream,String name, String description,String folderId) throws Exception
	{
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);			
			WorkspaceItem workSpaceItem = ws.getItem(folderId);			
			if (!workSpaceItem.isFolder()) {
				throw new Exception(
						"Destination is not a folder!");
			}			
			WorkspaceItem projectItem = ws.find(name, folderId);			
			logger.trace("Save Item on WorkSpace Folder:{}, name:{},description:{}, folderID:{}",projectItem,name,description,folderId);
			if (projectItem == null) {
				ws.createExternalFile(name, description, null, inputStream, folderId);
				
			}				
			else{
				ws.remove(name, folderId);
				Thread.sleep(2000);
				ws.createExternalFile(name, description, null, inputStream, folderId);
				//ws.updateItem(projectItem.getId(), inputStream);
			}
			return;
		} catch (Exception e) {			
			logger.error("saveItemOnWorkspace:{}",e);						
			throw e;
		}
	}
}
