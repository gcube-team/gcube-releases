package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.CommunicationFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.DeleteFieldFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.FieldsRetrievalFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.SearchableFieldInfoMissingException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.StoreFieldFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.CollectionFieldsBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;

/**
 * The client side stub for the RPC service.
 */
public interface SearchManagerService extends RemoteService {
	
	public List<FieldInfoBean> getFieldsInfo(boolean loadDetails) throws FieldsRetrievalFailureException;
	
	public FieldInfoBean createField(FieldInfoBean field, boolean isUpdated) throws SearchableFieldInfoMissingException, StoreFieldFailureException, CommunicationFailureException;
	
	public void deleteFieldInfo(String fieldID) throws DeleteFieldFailureException;
	
	public ArrayList<String> getIndexLocatorList(String fieldID, String collectionID, String type) throws CommunicationFailureException;
	
	public Set<String> getIndexCapabilities(String indexLocator) throws CommunicationFailureException;
	
	public void sendEmailWithErrorToSupport(Throwable caught);
	
	public Boolean getBridgingStatusFromSession();
	
	public ArrayList<CollectionFieldsBean> getCollectionAndFieldsInfo();
	
	public ArrayList<String> getSemanticAnnotations();
	
	public void saveAnnotations(String fieldID, ArrayList<String> annotationsToBeAdded, ArrayList<String> annotationsToBeRemoved);
	
	public ArrayList<String> getFieldAnnotations(String fieldID);
	
	public ArrayList<String> getGroups();
	
	public void addKeywordToPresentationGroup(String group, String keyword);
	
	public HashMap<String, ArrayList<String>> getGroupsAndKeywords();
	
	public void removeKeywordsFromPresentationGroup(String group, ArrayList<String> keywords);
	
	public Boolean resetRegistry();
}
