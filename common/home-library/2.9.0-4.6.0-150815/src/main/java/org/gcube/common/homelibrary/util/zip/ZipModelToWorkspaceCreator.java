/**
 * 
 */
package org.gcube.common.homelibrary.util.zip;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFile;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFolder;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This utility class read the zip model and create the corresponding element on the specified WorkspaceFolder.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipModelToWorkspaceCreator {

	protected Logger logger = LoggerFactory.getLogger(ZipModelToWorkspaceCreator.class);
	boolean replace;
	boolean hardreplace;
	WorkspaceFolder rootUnzip = null;

	public ZipModelToWorkspaceCreator() {
		this.replace = false;
		this.hardreplace = false;
	}


	public ZipModelToWorkspaceCreator(boolean replace, boolean hardreplace) {
		this.replace = replace;
		this.hardreplace = hardreplace;
	}

	/**
	 * @param root the folder to zip.createItem
	 * @param items the resulting zip folders.
	 */
	public WorkspaceFolder create(WorkspaceFolder root, List<ZipItem> items)
	{	
		for (ZipItem item:items) {
			try {
				createWorkspaceItem(root, item);
			}catch(Exception e)
			{
				logger.error("Error creating item "+item,e);
			}
		}
		return rootUnzip;
	}

	protected void createWorkspaceItem(WorkspaceFolder parentFolder, ZipItem item) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException, IOException
	{
		switch (item.getType()) {
		case FOLDER: createWorkspace(parentFolder, (ZipFolder)item); break;
		case FILE: createItem(parentFolder, (ZipFile) item); break;
		}
	}

	protected void createWorkspace(WorkspaceFolder parentFolder, ZipFolder zipFolder) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException
	{
		WorkspaceFolder folder = null;

		if(replace){
			try{
				folder = (WorkspaceFolder) parentFolder.find(zipFolder.getName());

				if (hardreplace & folder!= null & rootUnzip==null){
					logger.trace("Remove folder " + folder.getPath());
					folder.remove();
					this.replace = false;
					this.hardreplace = false;
					folder = null;
				}

				if (folder!=null)
					logger.trace("Folder  "+zipFolder.getName() + " already in " + parentFolder.getPath() + ". Skip creation.");
			}catch(Exception e){
				throw new InternalErrorException(e);
			}		
		}
		if (folder==null){
			String name  = parentFolder.getUniqueName(zipFolder.getName(), false);
			String description = (zipFolder.getComment()!=null)?zipFolder.getComment():"";
			folder = parentFolder.createFolder(name, description);
		}

		if (rootUnzip==null)
			rootUnzip = folder;

		for (ZipItem workspaceFolder:zipFolder.getChildren()) {
			try{
				createWorkspaceItem(folder, workspaceFolder);
			}catch(Exception e)
			{
				logger.error("Error creating item "+workspaceFolder,e);
			}
		}
	}

	protected void createItem(WorkspaceFolder folder, ZipFile zipFile) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, IOException
	{		
		logger.trace("Creating item "+zipFile.getName());
		FolderItem item =  null;
		String zipItemName = zipFile.getName();	

		if(replace){
			try {				
				item = (FolderItem) folder.find(zipItemName);
				if (item!=null){
					WorkspaceUtil.overwrite(item, new FileInputStream(zipFile.getContentFile()));
					logger.trace("Item replaced "+zipItemName);
				}
			} catch (ItemNotFoundException e) {
				throw new InternalErrorException(e);
			}
		}
		if (item==null){
			String name = folder.getUniqueName(zipItemName,false);	
			String description = (zipFile.getComment()!=null)?zipFile.getComment():"";
			//			String mimeType = MimeTypeUtil.getMimeType(zipFile.getName(), zipFile.getContentFile());
			item = WorkspaceUtil.createExternalFile(folder, name, description, new FileInputStream(zipFile.getContentFile()));
			logger.trace("Item created "+item);
		}

		zipFile.getContentFile().delete();
		logger.trace("Tmp file deleted");
	}

}
