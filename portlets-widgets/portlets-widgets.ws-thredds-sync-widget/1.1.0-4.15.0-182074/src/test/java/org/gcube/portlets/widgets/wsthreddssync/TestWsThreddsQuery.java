package org.gcube.portlets.widgets.wsthreddssync;

import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.Properties;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;

public class TestWsThreddsQuery {

	final static String folderId = "61ea6d91-4b09-43ec-91c4-b2fdb9b8c538";
	static String scope = "/gcube/devsec";
	static String username = "francesco.mangiacrapa";
	// NextNeext Francesco's Token
	static String token = "TOKEN";
	public static final String WS_SYNCH_SYNCH_STATUS = "WS-SYNCH.SYNCH-STATUS";

	public static void main(String[] args) {

		isItemSynched();
	
	}
	
	public static void isItemSynched(){
		
		try {
			ScopeProvider.instance.set(scope);
			
			StorageHubWrapper shWrapper = new StorageHubWrapper(scope, token, false, false, true);
			Workspace workspace = shWrapper.getWorkspace();
			Map<String, Object> metadata = workspace.getMetadata(folderId);

			if (metadata == null || metadata.isEmpty()) {
				throw new ItemNotSynched("No properties to read");
			}

			String wsSyncStatus = (String) metadata.get(WS_SYNCH_SYNCH_STATUS);
			System.out.println("Current: " + WS_SYNCH_SYNCH_STATUS + " has value: " + wsSyncStatus);

			//System.out.println("isSynched: " + isSynched);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
