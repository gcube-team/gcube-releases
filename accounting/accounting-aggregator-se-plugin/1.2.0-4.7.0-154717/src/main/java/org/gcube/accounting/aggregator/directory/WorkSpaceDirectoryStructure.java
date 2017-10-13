package org.gcube.accounting.aggregator.directory;

import org.gcube.accounting.aggregator.workspace.WorkSpaceManagement;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class WorkSpaceDirectoryStructure extends DirectoryStructure<String>{
	
	private static final String BACKUP_FOLDER_DESCRIPTION = "Accouting Aggregator Plugin Backup Folder"; 
	
	@Override
	protected String getRoot() throws Exception {
		return WorkSpaceManagement.getHome();
	}

	@Override
	protected String createDirectory(String parent, String name) throws Exception {
		return WorkSpaceManagement.createFolder(parent, name, BACKUP_FOLDER_DESCRIPTION);
	}

	
}
