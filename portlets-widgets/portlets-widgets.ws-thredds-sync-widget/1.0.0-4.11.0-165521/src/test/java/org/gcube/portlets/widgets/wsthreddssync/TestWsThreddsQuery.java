package org.gcube.portlets.widgets.wsthreddssync;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;

public class TestWsThreddsQuery {

	final static String folderId = "61ea6d91-4b09-43ec-91c4-b2fdb9b8c538";
	static String scope = "/gcube/devsec";
	static String username = "francesco.mangiacrapa";
	// NextNeext Francesco's Token
	static String token = "89257623-0570-4fbe-a15b-458bb84f4902-98187548";
	public static final String WS_SYNCH_SYNCH_STATUS = "WS-SYNCH.SYNCH-STATUS";

	public static void main(String[] args) {

		isItemSynched();
	
	}
	
	public static void isItemSynched(){
		
		try {
			ScopeProvider.instance.set(scope);

			Workspace workspace = HomeLibrary.getUserWorkspace(username);
			WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(folderId);
			Properties properties = folder.getProperties();

			if (properties == null || properties.getProperties() == null || properties.getProperties().size() == 0) {
				throw new ItemNotSynched("No properties to read");
			}

			String wsSyncStatus = properties.getProperties().get(WS_SYNCH_SYNCH_STATUS);
			System.out.println("Current: " + WS_SYNCH_SYNCH_STATUS + " has value: " + wsSyncStatus);

			//System.out.println("isSynched: " + isSynched);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
