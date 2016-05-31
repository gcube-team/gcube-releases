package org.gcube.portlets.user.searchportlet.client.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.gcube.portlets.user.searchportlet.shared.CollectionBean;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;
import org.gcube.portlets.user.searchportlet.shared.SearchTypeBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchServiceAsync
{
	
	public void getNumberOfSelectedCollections(AsyncCallback<Integer> callback);
	
	public void getSearchStatus(AsyncCallback<SearchAvailabilityType> callback);
	
	public void getSimpleSearchTerm(AsyncCallback<String> callback);
	
	public void submitGenericQuery(String term, SearchTypeBean type, HashSet<String> selectedCollections, AsyncCallback<Void> callback);
		
	public void stackTraceAsString(Throwable caught, AsyncCallback<String> callback);

	public void sendEmailWithErrorToSupport(Throwable caught, AsyncCallback<Void> callback);

	public void setSimpleSearchTerm(String term, AsyncCallback<Void> callback);

	public void getAvailableCollections(
			AsyncCallback<HashMap<CollectionBean, ArrayList<CollectionBean>>> callback);

	public void setSelectedCollectionsToSession(HashSet<String> selectedCollections,
			AsyncCallback<Void> callback);

	public void getSelectedSearchType(AsyncCallback<SearchTypeBean> callback);

	void getSelectedCollectionsFromSession(AsyncCallback<HashSet<String>> callback);

	void getLogoURL(AsyncCallback<String> callback);

	void areExternalCollectionsAvailable(AsyncCallback<Boolean> callback);

	void areNativeCollectionsAvailable(AsyncCallback<Boolean> callback);
}
