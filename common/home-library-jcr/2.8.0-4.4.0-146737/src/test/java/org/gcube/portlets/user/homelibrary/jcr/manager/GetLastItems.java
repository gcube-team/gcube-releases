package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;

public class GetLastItems {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
//		ScopeProvider.instance.set("/gcube");
					ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("massimiliano.assante").getWorkspace();



		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject");
 
		List<WorkspaceItem> list = folder.getLastItems(0);
		System.out.println();
//		for (WorkspaceItem item: list){
//			try{
//			System.out.println(item.getPublicLink(false));
//			}catch (Exception e){ 
//				e.printStackTrace();
//			}
//		}
		
//		List<WorkspaceItem> list = folder.getChildren();
//		int i=0;
//		for (WorkspaceItem item: list){
//			
//
//			i++;
//			try{
//				System.out.println(i+") " + item.getPublicLink(false));
//				
//
//			}catch (Exception e){ 
//				System.out.println("**** ERROR " + i+") " +item.getPath());
////				List<AccountingEntry> accounting = item.getAccounting();
////				for(AccountingEntry entry: accounting){
////					System.out.println(entry.getEntryType() + " - date: " + entry.getDate().getTime());
////				} 
//			}
//		}
			
	}
	
//	WP4_Reservoir_characterization/Task4.2_Areal_seismic_network/Piggy back experiment/map raster/D306070.TIF
//	WP4_Reservoir_characterization/Task4.1_VSP_acquisition/Piggy back experiment/map raster

}
