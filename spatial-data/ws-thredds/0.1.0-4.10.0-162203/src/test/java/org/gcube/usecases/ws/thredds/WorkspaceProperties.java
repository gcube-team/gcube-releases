package org.gcube.usecases.ws.thredds;

import java.util.Collections;
import java.util.Map.Entry;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

public class WorkspaceProperties {

	public static void main(String[] args) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, ItemNotFoundException {
		
		TestCommons.setScope();
		
		WorkspaceFolder folder=TestCommons.getTestFolder();
		
		printProperties(folder.getProperties());
		
		System.out.println("Has property : "+folder.getProperties().hasProperty(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS));
		printProperties(folder.getProperties());
		
		
		System.out.println("Setting property.. ");
		folder.getProperties().addProperties(Collections.singletonMap(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS, "true"));
		
		System.out.println("Has property : "+folder.getProperties().hasProperty(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS));
		printProperties(folder.getProperties());
		
		
		System.out.println("Removing (setting it null) ");
		folder.getProperties().addProperties(Collections.singletonMap(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS, null));
		
		System.out.println("Has property : "+folder.getProperties().hasProperty(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS));
		printProperties(folder.getProperties());
		
//		String folderId=
//		
//		WorkspaceFolderManager manager=new WorkspaceFolderManager(folderId);
//		
//		manager.configure(new SynchFolderConfiguration("myRemoteFolder","thredds","*.nc,*.ncml,*.asc"));
//		
//		manager.dismiss(false);
		
	}

	public static void printProperties(Properties prop) throws InternalErrorException {
		System.out.print("Properties : ");
		for(Entry<String,String> entry:prop.getProperties().entrySet()) {
			if(entry.getValue()==null) System.out.print(entry.getKey()+" is null;");
			else System.out.print(entry.getKey()+" = "+entry.getValue()+";");
		}
	}
	
}
