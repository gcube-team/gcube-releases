package org.gcube.usecases.ws.thredds;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class GetWSSynchSpace {

	public static void main(String[] args) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException {
		TokenSetter.set("/gcube/preprod/preVRE");
		SyncEngine engine=SyncEngine.get();

		String folderId=TestCommons.getWSIdByPath("/Workspace/Thredds main catalog");
		
		System.out.println(folderId);
		
		
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		WorkspaceFolder folder=(WorkspaceFolder) ws.getItem(folderId);
		
		SynchFolderConfiguration config=new SynchFolderConfiguration();
		System.out.println("Total size : "+computeLength(folder, config));
	}

	
	private static final long computeLength(WorkspaceItem item,SynchFolderConfiguration config) throws InternalErrorException {
		long toReturn=0l;
		if(item.isFolder()) {
			for(WorkspaceItem child:((WorkspaceFolder)item).getChildren()) {
				if(item.isFolder()||config.matchesFilter(item.getName()))
						toReturn=toReturn+computeLength(child, config);
			}
		}else toReturn=toReturn+((FolderItem)item).getLength();
		return toReturn;
	}
	
	
}
