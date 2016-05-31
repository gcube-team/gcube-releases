package org.gcube.portlets.user.searchportlet.client.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.gcube.portlets.user.searchportlet.client.exceptions.CollectionRetrievalException;
import org.gcube.portlets.user.searchportlet.client.exceptions.NoCollectionsAvailableException;
import org.gcube.portlets.user.searchportlet.client.exceptions.SearchSubmissionException;
import org.gcube.portlets.user.searchportlet.shared.CollectionBean;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;
import org.gcube.portlets.user.searchportlet.shared.SearchTypeBean;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SearchService extends RemoteService
{
	
	public HashMap<CollectionBean, ArrayList<CollectionBean>> getAvailableCollections() throws CollectionRetrievalException;
	
	public void submitGenericQuery(String term, SearchTypeBean type,HashSet<String> selectedCollections) throws SearchSubmissionException, NoCollectionsAvailableException;
	
	public Integer getNumberOfSelectedCollections();

	public SearchAvailabilityType getSearchStatus();
	
	public String getSimpleSearchTerm();
	
	public void setSimpleSearchTerm(String term);
	
	public void setSelectedCollectionsToSession(HashSet<String> selectedCollections);
	
	public HashSet<String> getSelectedCollectionsFromSession();
	
	public String stackTraceAsString(Throwable caught);
	
	public void sendEmailWithErrorToSupport(Throwable caught);
	
	public SearchTypeBean getSelectedSearchType();
	
	public String getLogoURL();
	
	public Boolean areExternalCollectionsAvailable();
	
	public Boolean areNativeCollectionsAvailable();
}
