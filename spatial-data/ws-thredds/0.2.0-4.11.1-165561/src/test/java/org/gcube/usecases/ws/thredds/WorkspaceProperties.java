package org.gcube.usecases.ws.thredds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class WorkspaceProperties {

	public static void main(String[] args) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, ItemNotFoundException, MalformedURLException, IOException, WorkspaceInteractionException, InternalException {
		
		TestCommons.setScope();
		WorkspaceFolder folder=TestCommons.getTestFolder();
		
		
		SyncEngine.get().check(folder.getId(), true);
		scanForPrint(folder);
		
//		SyncEngine.get().shutDown();
//		for(Workspace)
//		printProperties(folder.getProperties());
//		
//		System.out.println("Has property : "+folder.getProperties().hasProperty(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS));
//		printProperties(folder.getProperties());
//		
//		
//		System.out.println("Setting property.. ");
//		folder.getProperties().addProperties(Collections.singletonMap(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS, "true"));
//		
//		System.out.println("Has property : "+folder.getProperties().hasProperty(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS));
//		printProperties(folder.getProperties());
//		
//		
//		System.out.println("Removing (setting it null) ");
//		folder.getProperties().addProperties(Collections.singletonMap(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS, null));
//		
//		System.out.println("Has property : "+folder.getProperties().hasProperty(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS));
//		printProperties(folder.getProperties());
//		
//		String folderId=
//		
//		WorkspaceFolderManager manager=new WorkspaceFolderManager(folderId);
//		
//		manager.configure(new SynchFolderConfiguration("myRemoteFolder","thredds","*.nc,*.ncml,*.asc"));
//		
//		manager.dismiss(false);
		
	}

	public static void scanForPrint(WorkspaceFolder folder) throws InternalErrorException {
		System.out.println("Folder "+folder.getPath());
		printProperties(folder.getProperties());
		SynchFolderConfiguration config=new SynchFolderConfiguration("", "", "", "","");
		for(WorkspaceItem item:folder.getChildren())
			if(!item.isFolder()&&config.matchesFilter(item.getName())) {
				System.out.println("ITEM "+item.getPath());
				printProperties(item.getProperties());
			}
		for(WorkspaceItem item:folder.getChildren())
			if(item.isFolder())scanForPrint((WorkspaceFolder) item);
	}
	
	
	public static void printProperties(Properties prop) throws InternalErrorException {
		Map<String,String> map=prop.getProperties();
		System.out.print("Properties : ..");
		for(Entry<String,String> entry:map.entrySet()) {
//			if(entry.getKey().equals(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS)||
//					entry.getKey().equals(Constants.WorkspaceProperties.LAST_UPDATE_STATUS)||
//					entry.getKey().equals(Constants.WorkspaceProperties.LAST_UPDATE_TIME)) {
			if(true) {
				if(entry.getValue()==null) System.out.print(entry.getKey()+" is null;");
				else System.out.print(entry.getKey()+" = "+entry.getValue()+";");
			}
		}
		System.out.println();
	}
	
}
