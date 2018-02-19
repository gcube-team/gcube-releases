package org.gcube.usecases.ws.thredds;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

public class TestCommons {

	public static void setScope() {
		TokenSetter.set("/gcube/devsec");
	}
	
	
	public static WorkspaceFolder getTestFolder() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException {
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		
		String folderName="WS-Tests";
		WorkspaceFolder folder=null;
		try{
			folder=ws.getRoot().createFolder(folderName, "test purposes");
		}catch(ClassCastException e) {
			folder=(WorkspaceFolder) ws.getItemByPath("/Workspace/"+folderName);
		}
		return folder;
	}
	
}
