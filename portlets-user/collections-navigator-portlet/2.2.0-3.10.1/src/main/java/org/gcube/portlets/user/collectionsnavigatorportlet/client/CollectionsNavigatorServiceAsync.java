package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfo;
import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfoModel;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CollectionsNavigatorServiceAsync {
	
	public void changeCollectionStatus(List<String> collections, boolean addCollection, AsyncCallback<Boolean> callback);
	
	public void setCollectionOpenStatus(String collectionID, boolean openStatus, AsyncCallback<Void> callback);
	
	public void isCollectionSelected(String collectionID, AsyncCallback<Boolean> callback);
	
	public void refreshInformation(AsyncCallback<Void> callback);
	
	public void searchForCollections(String keyword, AsyncCallback<CollectionInfo[]> callback);

	public void isAllCollectionsBoxSelected(AsyncCallback<Boolean> callback);

	public void getAvailableCollections(AsyncCallback<HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>>> callback);
}
