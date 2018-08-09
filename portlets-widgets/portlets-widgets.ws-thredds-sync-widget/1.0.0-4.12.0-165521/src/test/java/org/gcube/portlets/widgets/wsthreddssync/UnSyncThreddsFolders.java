/**
 *
 */
package org.gcube.portlets.widgets.wsthreddssync;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.widgets.wsthreddssync.server.SyncronizeWithThredds;
import org.gcube.usecases.ws.thredds.Constants;



/**
 * The Class UnSyncThreddsFolders.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 13, 2018
 */
public class UnSyncThreddsFolders {

	public static String DEFAULT_SCOPE = "/gcube"; //DEV
	public static String TEST_USER = "francesco.mangiacrapa";
	public static String TEST_USER_TOKEN = "0e2c7963-8d3e-4ea6-a56d-ffda530dd0fa-98187548"; //token user: Francesco Mangiacrapa - /gcube (root)

	private static List<String> lstUnSynchedItem = new ArrayList<String>();
	private static List<String> lstUnSynchedFailedItem = new ArrayList<String>();
	private static List<String> lstErrorItem = new ArrayList<String>();
	private static long totalAttempts = 0;


	public static SyncronizeWithThredds syncService = new SyncronizeWithThredds();


	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{

		System.out.println("Get Workspace scope: "+DEFAULT_SCOPE + " username: "+TEST_USER);
		ScopeProvider.instance.set(DEFAULT_SCOPE);
		Workspace workspace = HomeLibrary.getUserWorkspace(TEST_USER);
		return workspace;
	}


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 */
	public static void main(String[] args) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException {

		Workspace ws = getWorkspace();
		unsycFirstLevel(ws, ws.getRoot().getId(), false);

		System.out.println("UnSync completed");

		System.out.println("\nUnsync attempted: "+totalAttempts);

		System.out.println("\nTotal failed unsync: "+lstUnSynchedFailedItem.size());

		for (String string : args) {
			System.out.println("Failed unsync: "+string);
		}

		System.out.println("\nUnsynched "+lstUnSynchedItem.size() +" item/s");
		for (String string : lstUnSynchedItem) {
			System.out.println("Unsynched id: "+string);
		}

		System.out.println("\nErros on "+lstErrorItem.size() +" item/s");
		for (String string : lstErrorItem) {
			System.out.println("Error on id: "+string);
		}
	}



	/**
	 * Unsyc first level.
	 *
	 * @param ws the ws
	 * @param itemId the item id
	 * @param depthUnsync the depth unsync
	 */
	public static void unsycFirstLevel(Workspace ws, String itemId, boolean depthUnsync){

		WorkspaceItem item;
		try {

			item = ws.getItem(itemId);

			if(item.isFolder()){

				List<? extends WorkspaceItem> children = item.getChildren();
				for (WorkspaceItem workspaceItem : children) {
					if(depthUnsync)
						unsycFirstLevel(ws, workspaceItem.getId(), depthUnsync);

					unsynFolder(workspaceItem);
				}
			}

		}catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lstErrorItem.add(itemId);

		}catch (InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lstErrorItem.add(itemId);
		}

	}

	/**
	 * Unsyn folder.
	 *
	 * @param workspaceItem the workspace item
	 */
	public static void unsynFolder(WorkspaceItem workspaceItem) {

		if(workspaceItem==null)
			return;

		try{
			boolean synched = syncService.isItemSynched(workspaceItem.getId(), DEFAULT_SCOPE, TEST_USER);
			System.out.println("Is the workspace item: "+workspaceItem.getId() + " synched? "+synched +", Is folder? "+workspaceItem.isFolder());

			if(synched && workspaceItem.isFolder()){
				//Boolean unsynched = syncService.doUnSync(workspaceItem.getId(), false, DEFAULT_SCOPE, TEST_USER_TOKEN);
				ScopeProvider.instance.set(DEFAULT_SCOPE);
				SecurityTokenProvider.instance.set(TEST_USER_TOKEN);
				cleanItem(workspaceItem);
				totalAttempts++;
//				if(unsynched)
//					lstUnSynchedItem.add(workspaceItem.getId());
//				else
//					lstUnSynchedFailedItem.add(workspaceItem.getId());
			}

		}catch(Exception e){
			e.printStackTrace();
			try {
				lstErrorItem.add(workspaceItem.getId());
			}
			catch (InternalErrorException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	static void cleanItem(WorkspaceItem item) throws InternalErrorException {
        Properties props=item.getProperties();
        if(props.hasProperty(Constants.WorkspaceProperties.TBS)) {
            if(item.isFolder()) {
                props.addProperties(Constants.cleanedFolderPropertiesMap);
                for(WorkspaceItem child : ((WorkspaceFolder)item).getChildren())
                    cleanItem(child);
            }else props.addProperties(Constants.cleanedItemPropertiesMap);
        }
    }


}
