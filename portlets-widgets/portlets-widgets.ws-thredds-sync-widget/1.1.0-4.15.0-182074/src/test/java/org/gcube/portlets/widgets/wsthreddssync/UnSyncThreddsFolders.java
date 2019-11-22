/**
 *
 */
package org.gcube.portlets.widgets.wsthreddssync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WorkspaceFolderNotFoundException;
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
	
	static String scope = "/gcube/devsec";
	static String token = "TOKEN";


	public static SyncronizeWithThredds syncService = new SyncronizeWithThredds();


	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace() throws Exception{
		
		ScopeProvider.instance.set(scope);
		StorageHubWrapper shWrapper = new StorageHubWrapper(scope, token, false, false, true);
		return shWrapper.getWorkspace();
	}


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 */
	public static void main(String[] args) throws Exception {

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
	 * @throws Exception 
	 */
	public static void unsycFirstLevel(Workspace ws, String itemId, boolean depthUnsync) throws Exception{

		WorkspaceItem item;
		try {

			item = ws.getItem(itemId);

			if(item.isFolder()){

				List<? extends WorkspaceItem> children = ws.getChildren(item.getId());
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
				Workspace ws = getWorkspace();
				cleanItem(workspaceItem, ws);
				totalAttempts++;
//				if(unsynched)
//					lstUnSynchedItem.add(workspaceItem.getId());
//				else
//					lstUnSynchedFailedItem.add(workspaceItem.getId());
			}

		}catch(Exception e){
			e.printStackTrace();
			lstErrorItem.add(workspaceItem.getId());
		}

	}

	static void cleanItem(WorkspaceItem item, Workspace ws) throws Exception {
	
//        Map<String, Object> props = ws.getMetadata(item.getId());
//        if(props.containsKey(Constants.WorkspaceProperties.TBS)) {
//            if(item.isFolder()) {
//                //props.addProperties(Constants.cleanedFolderPropertiesMap);
//                ws.updateMetadata(item.getId(), Constants.cleanedFolderPropertiesMap);
//                List<? extends WorkspaceItem> children = ws.getChildren(item.getId());
//                for(WorkspaceItem child : children)
//                    cleanItem(child, ws);
//            }else {
//            	ws.updateMetadata(item.getId(), Constants.cleanedFolderPropertiesMap);
//            }
//        }
    }


}
