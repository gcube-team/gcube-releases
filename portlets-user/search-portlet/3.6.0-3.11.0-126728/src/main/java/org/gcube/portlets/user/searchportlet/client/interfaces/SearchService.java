package org.gcube.portlets.user.searchportlet.client.interfaces;

import java.util.ArrayList;
import java.util.LinkedList;

import org.gcube.portlets.user.searchportlet.client.exceptions.SearchSubmissionException;
import org.gcube.portlets.user.searchportlet.shared.BrowsableFieldBean;
import org.gcube.portlets.user.searchportlet.shared.PreviousResultsInfo;
import org.gcube.portlets.user.searchportlet.shared.SavedBasketQueriesInfo;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;
import org.gcube.portlets.user.searchportlet.shared.SearchableFieldBean;



import com.google.gwt.user.client.rpc.RemoteService;

public interface SearchService extends RemoteService
{

	public void addSearchField(SearchableFieldBean sField);
	
	public void addSearchFieldOnPreviousQuery(SearchableFieldBean sField);
	
	public SearchableFieldBean[] createPreviousQuery(int indexOfQueryGroupToClone);
	
	public ArrayList<BrowsableFieldBean> getBrowsableFields();
	
	public Integer getResultsNumberPerPage();
	
	public Integer getNumberOfSelectedCollections();

	public LinkedList<PreviousResultsInfo> getPreviousQueries();
	
	public SavedBasketQueriesInfo getQueryFromBasket(String id);
	
	public String getSelectedLanguage();
	
	public Boolean setSelectedLanguage(String language);
	
	public ArrayList<SearchableFieldBean> getSearchFields();
	
	public void setSelectedRadioBtn(boolean isBasketSelected);
	
	public SearchAvailabilityType getSearchStatus();
	
	public String getSelectedConditionType();
	
	public String getSelectedConditionTypeOnPreviousSearch();
	
	public ArrayList<SearchableFieldBean> getSelectedFields();

	public ArrayList<SearchableFieldBean> getSelectedFieldsOnPreviousSearch();
	
	public Integer getSelectedTab();
	
	public Boolean isSemanticSelected();
	
	public Boolean isRankSelected();
	
	public String getSimpleSearchTerm();
	
	public ArrayList<SearchableFieldBean> getSortableFields();
	
	public void removeSearchField(int searchFieldNo);
	
	public void removeSearchFieldOnPreviousQuery(int searchFieldNo);
	
	public void resetFields(boolean isPrevious);
	
	public void setSelectedTab(Integer x);
	
	public Boolean getSelectedRadioBtn();
	
	public void setSimpleSearchTerm(String term);
	
	public void storeConditionType(String newValue); 
	
	public void storeConditionTypeOnPreviousSearch(String newValue);
	
	public void submitAdvancedQuery(ArrayList<SearchableFieldBean> criteria, String sortby, String order, boolean searchPerCollection, boolean isSemanticEnriched) throws SearchSubmissionException;

	public Boolean submitBrowseQuery(BrowsableFieldBean browseBy, String sortOrder, String typeOfBrowse, int resultNumPerPage) throws SearchSubmissionException;
	
	public void submitBrowseQueryOnPreviousResult(int index) throws SearchSubmissionException;
	
	public void submitQueryOnPreviousResult(ArrayList<SearchableFieldBean> criteria, String sortby, String sortOrder, String showRank) throws SearchSubmissionException;

	public void submitSimpleQuery(String fts, boolean setRanking, boolean isSemanticSearch, boolean searchPerCollection) throws SearchSubmissionException;
	
	public void submitGenericQuery(String query, String type) throws SearchSubmissionException;
	
	public void updateCriterionName(int internalNo, String newID, String newName);
	
	public void updateCriterionNameOnPreviousQuery(int internalNo, String newID, String newName);
	
	public void updateCriterionValue(int internalNo, String newValue);
	
	public void updateCriterionValueOnPreviousQuery(int internalNo, String newValue);
	
	public ArrayList<String> getAvailableLanguages();
	
	public String stackTraceAsString(Throwable caught);
	
	public void sendEmailWithErrorToSupport(Throwable caught);
	
	public boolean isAdvancedOpen();
	
	public void setAdvancedPanelStatus(boolean isOpened);
		
	public Boolean isSemanticAvailableForCurrentScope();
}
