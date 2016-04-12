package org.gcube.portlets.user.searchportlet.client.interfaces;

import java.util.ArrayList;
import java.util.LinkedList;

import org.gcube.portlets.user.searchportlet.shared.BrowsableFieldBean;
import org.gcube.portlets.user.searchportlet.shared.PreviousResultsInfo;
import org.gcube.portlets.user.searchportlet.shared.SavedBasketQueriesInfo;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;
import org.gcube.portlets.user.searchportlet.shared.SearchableFieldBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchServiceAsync
{
	public void addSearchField(SearchableFieldBean sField, AsyncCallback<Void> callback);
	
	public void addSearchFieldOnPreviousQuery(SearchableFieldBean sField, AsyncCallback<Void> callback);
	
	public void createPreviousQuery(int indexOfQueryGroupToClone, AsyncCallback<SearchableFieldBean[]> callback);
	
	void getBrowsableFields(AsyncCallback<ArrayList<BrowsableFieldBean>> callback);
	
	public void getQueryFromBasket(String id, AsyncCallback<SavedBasketQueriesInfo> callback);
	
	public void getResultsNumberPerPage(AsyncCallback<Integer> callback);

	public void getNumberOfSelectedCollections(AsyncCallback<Integer> callback);
	
	public void getPreviousQueries(AsyncCallback<LinkedList<PreviousResultsInfo>> callback);
	
	public void getSelectedLanguage(AsyncCallback<String> callback);
	
	void getSearchFields(AsyncCallback<ArrayList<SearchableFieldBean>> callback);
	
	public void getSearchStatus(AsyncCallback<SearchAvailabilityType> callback);
	
	public void getSelectedConditionType(AsyncCallback<String> callback);
	
	public void getSelectedConditionTypeOnPreviousSearch(AsyncCallback<String> callback);
	
	void getSelectedFields(AsyncCallback<ArrayList<SearchableFieldBean>> callback);

	public void setSelectedLanguage(String language, AsyncCallback<Boolean> callback);
	
	public void getSelectedFieldsOnPreviousSearch(AsyncCallback<ArrayList<SearchableFieldBean>> callback);
	
	public void getSelectedTab(AsyncCallback<Integer> callback);
	
	public void getSimpleSearchTerm(AsyncCallback<String> callback);
	
	void getSortableFields(AsyncCallback<ArrayList<SearchableFieldBean>> callback);
	
	public void removeSearchField(int searchFieldNo, AsyncCallback<Void> callback);
	
	public void removeSearchFieldOnPreviousQuery(int searchFieldNo, AsyncCallback<Void> callback);
	
	public void resetFields(boolean isPrevious, AsyncCallback<Void> callback);
	
	public void setSelectedTab(Integer x, AsyncCallback<Void> callback);
	
	public void storeConditionType(String newValue, AsyncCallback<Void> callback);
	
	public void storeConditionTypeOnPreviousSearch(String newValue, AsyncCallback<Void> callback);
	
	public void submitAdvancedQuery(ArrayList<SearchableFieldBean> criteria, String sortby, String order, boolean searchPerCollection, boolean isSemanticEnriched, AsyncCallback<Void> callback);
	
	public void submitBrowseQuery(BrowsableFieldBean browseBy, String sortOrder, String typeOfBrowse, int resultNumPerPage,
			AsyncCallback<Boolean> callback);
	
	public void submitBrowseQueryOnPreviousResult(int index, AsyncCallback<Void> callback);

	public void submitQueryOnPreviousResult(ArrayList<SearchableFieldBean> criteria, String sortby, String sortOrder, String showRank,
			AsyncCallback<Void> callback);
	
	public void submitSimpleQuery(String fts,  boolean setRanking, boolean isSemanticSearch, boolean searchPeρΨολλεψτιον, AsyncCallback<Void> callback);
	
	public void submitGenericQuery(String query, String type, AsyncCallback<Void> callback);
	
	public void updateCriterionName(int internalNo, String newID, String newName, AsyncCallback<Void> callback);
	
	public void updateCriterionNameOnPreviousQuery(int internalNo, String newID, String newName, AsyncCallback<Void> callback);

	public void updateCriterionValue(int internalNo, String newValue, AsyncCallback<Void> callback);
	
	public void updateCriterionValueOnPreviousQuery(int internalNo, String newName, AsyncCallback<Void> callback);
	
	public void getAvailableLanguages(AsyncCallback<ArrayList<String>> callback);
	
	public void stackTraceAsString(Throwable caught, AsyncCallback<String> callback);

	public void sendEmailWithErrorToSupport(Throwable caught, AsyncCallback<Void> callback);

	public void isAdvancedOpen(AsyncCallback<Boolean> callback);

	public void setAdvancedPanelStatus(boolean isOpened, AsyncCallback<Void> callback);

	public void setSelectedRadioBtn(boolean isBasketSelected, AsyncCallback<Void> callback);

	public void getSelectedRadioBtn(AsyncCallback<Boolean> getSelectedRadioCallback);

	public void setSimpleSearchTerm(String term, AsyncCallback<Void> callback);

	public void isSemanticAvailableForCurrentScope(AsyncCallback<Boolean> callback);

	void isSemanticSelected(AsyncCallback<Boolean> callback);

	void isRankSelected(AsyncCallback<Boolean> callback);
}
