package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.CollectionFieldsBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchManagerServiceAsync {

	public void getFieldsInfo(boolean loadDetails, AsyncCallback<List<FieldInfoBean>> callback);

	public void createField(FieldInfoBean field, boolean isUpdated, AsyncCallback<FieldInfoBean> callback);

	public void deleteFieldInfo(String fieldID, AsyncCallback<Void> callback);

	public void getIndexLocatorList(String fieldID, String collectionID, String type, AsyncCallback<ArrayList<String>> callback);

	public void getIndexCapabilities(String indexLocator, AsyncCallback<Set<String>> callback);

	public void sendEmailWithErrorToSupport(Throwable caught, AsyncCallback<Void> callback);

	public void getBridgingStatusFromSession(AsyncCallback<Boolean> callback);

	public void getCollectionAndFieldsInfo(AsyncCallback<ArrayList<CollectionFieldsBean>> callback);

	public void getSemanticAnnotations(AsyncCallback<ArrayList<String>> callback);

	public void saveAnnotations(String fieldID, ArrayList<String> annotationsToBeAdded, ArrayList<String> annotationsToBeRemoved, AsyncCallback<Void> callback);
	
	public void getFieldAnnotations(String fieldID, AsyncCallback<ArrayList<String>> callback);

	public void getGroups(AsyncCallback<ArrayList<String>> callback);

	public void addKeywordToPresentationGroup(String group, String keyword, AsyncCallback<Void> callback);

	void getGroupsAndKeywords(
			AsyncCallback<HashMap<String, ArrayList<String>>> callback);

	void removeKeywordsFromPresentationGroup(String group,
			ArrayList<String> keywords, AsyncCallback<Void> callback);
	
	void resetRegistry(AsyncCallback<Boolean> callback);

}
