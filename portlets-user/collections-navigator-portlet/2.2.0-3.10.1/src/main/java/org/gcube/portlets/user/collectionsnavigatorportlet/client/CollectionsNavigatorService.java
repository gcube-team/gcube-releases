package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfo;
import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfoModel;
import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionRetrievalException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface CollectionsNavigatorService extends RemoteService{
	
	public Boolean changeCollectionStatus(List<String> collections, boolean addCollection) throws Exception;
	
	public void setCollectionOpenStatus(String collectionID, boolean openStatus);
	
	public Boolean isCollectionSelected(String collectionID);
	
	public void refreshInformation();
	
	public CollectionInfo[] searchForCollections(String keyword);
	
	public boolean isAllCollectionsBoxSelected();
	
	public HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>> getAvailableCollections() throws CollectionRetrievalException;
}
