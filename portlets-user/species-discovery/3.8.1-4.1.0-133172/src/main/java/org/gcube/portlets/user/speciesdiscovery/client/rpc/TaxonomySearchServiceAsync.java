package org.gcube.portlets.user.speciesdiscovery.client.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.model.ClassificationModel;
import org.gcube.portlets.user.speciesdiscovery.client.util.GridField;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSource;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;
import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrenceBatch;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchByQueryParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchFilters;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResult;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface TaxonomySearchServiceAsync {

	public void searchByScientificName(String searchTerm, SearchFilters searchFilters, AsyncCallback<Void> callback);
	
	public void searchByCommonName(String searchTerm, SearchFilters searchFilters, AsyncCallback<Void> callback);
	
	void getSearchStatus(boolean onlySelected, boolean isActiveFilterOnResult,
			AsyncCallback<SearchStatus> callback);

//	void getSearchStatus(boolean onlySelected, AsyncCallback<SearchStatus> callback);
	
	public void stopSearch(AsyncCallback<Void> callback);
	
	public void updateRowSelection(int rowId, boolean selection, AsyncCallback<Void> callback);

	public void retrieveOccurencesFromSelection(AsyncCallback<Integer> callback);
	
	void getOccurrencesBatch(int start, int count, AsyncCallback<OccurrenceBatch> callback);

	public void stopRetrievingOccurrences(AsyncCallback<Void> callback);

	public void generateMapFromSelectedOccurrencePoints(AsyncCallback<String> callback);

	//Added by Francesco M.
	
	void saveSelectedOccurrencePoints(String destinationFolderId, String fileName, SaveFileFormat fileFormat, OccurrencesSaveEnum typeCSV, AsyncCallback<Void> callback);

	void updateRowSelections(boolean selection,
			ResultFilter activeFiltersObject, AsyncCallback<Integer> callback);
	
	void loadDataSourceList(AsyncCallback<List<DataSourceModel>> callback);

	void getFilterCounterById(GridField field, AsyncCallback<HashMap<String, Integer>> callback);
	
	void getParentsList(Taxon taxon, AsyncCallback<List<Taxon>> callback);

	void getFilterCounterForClassification(String rank, AsyncCallback<HashMap<String, ClassificationModel>> callback);

	void loadListCommonNameByRowId(String resultRowId, AsyncCallback<ArrayList<CommonName>> asyncCallback);

	void countOfSelectedRow(AsyncCallback<Integer> callback);

	void searchByQuery(String query, AsyncCallback<SearchByQueryParameter> callback);

	void getSearchResultRows(int start, int limit,
			ResultFilter activeFiltersObject,
			boolean onlySelected,
			AsyncCallback<SearchResult<ResultRow>> callback);

	public void getSearchTaxonomyRow(int start, int limit,
			ResultFilter activeFiltersObject,
			boolean showOnlySelected,
			AsyncCallback<SearchResult<TaxonomyRow>> asyncCallback);

	void loadListChildrenByParentId(String parentId, AsyncCallback<ArrayList<LightTaxonomyRow>> asyncCallback);

	void saveSelectedTaxonomyPoints(String destinationFolderId, String fileName, SaveFileFormat fileFormat, AsyncCallback<Void> callback);

	void getCountOfOccurrencesBatch(AsyncCallback<OccurrencesStatus> callback);

	void getListTaxonomyJobs(AsyncCallback<List<JobTaxonomyModel>> callback);

//	void createTaxonomyJobByChildren(TaxonomyRow taxonomy, String dataSourceName,
//			AsyncCallback<JobTaxonomyModel> callback);

	void cancelTaxonomyJob(String jobIdentifier, AsyncCallback<Boolean> callback);

	void saveTaxonomyJob(String jobIdentifier, String destinationFolderId, String fileName, String scientificName, String dataSourceName, AsyncCallback<Boolean> callback);

	void retrieveTaxonomyByIds(List<String> ids, AsyncCallback<List<LightTaxonomyRow>> asyncCallback);

	void retrieveSynonymsByRefId(String refId, AsyncCallback<List<LightTaxonomyRow>> asyncCallback);

	void createOccurrencesJob(List<JobOccurrencesModel> listJobOccurrenceModel, SaveFileFormat saveFileFormat, OccurrencesSaveEnum csvType, boolean isByDataSource, int expectedOccurrence, AsyncCallback<List<JobOccurrencesModel>> callback);

	void getListOccurrencesJob(AsyncCallback<List<JobOccurrencesModel>> callback);

	void saveOccurrenceJob(JobOccurrencesModel jobModel,
			String destinationFolderId, String fileName, String scientificName,
			String dataSourceName, AsyncCallback<Boolean> callback);

	void cancelOccurrenceJob(String jobIdentifier, AsyncCallback<Boolean> callback);

	void resubmitTaxonomyJob(String jobIdentifier,
			AsyncCallback<JobTaxonomyModel> callback);

	void resubmitOccurrencesJob(String jobIdentifier, AsyncCallback<List<JobOccurrencesModel>> callback);

	void loadStructuresForResultRowClustering(
			AsyncCallback<ClusterStructuresForResultRow> callback);

	void loadStructuresForTaxonomyClustering(
			AsyncCallback<ClusterStructuresForTaxonomyRow> callback);

	void changeStatusOccurrenceJob(String jobIdentifier, DownloadState state,
			AsyncCallback<Boolean> callback);

	void changeStatusTaxonomyJob(String jobIdentifier, DownloadState state,
			AsyncCallback<Boolean> callback);

	void loadDataSourceForResultRow(boolean selected, boolean distinct,
			AsyncCallback<List<DataSource>> callback);

	void createTaxonomyJobByIds(String search, List<DataSourceModel> dataSources, AsyncCallback<JobTaxonomyModel> callback);

	void createTaxonomyJobByChildren(String taxonomyServiceId,
			String taxonomyName, String taxonomyRank, String dataSourceName,
			AsyncCallback<JobTaxonomyModel> callback);

	void saveTaxonomyJobError(String jobIdentifier, String destinationFolderId,
			String fileName, String scientificName, String dataSourceName,
			AsyncCallback<Boolean> callback);

	void saveOccurrenceJobError(JobOccurrencesModel jobModel,
			String destinationFolderId, String fileName, String scientificName,
			String dataSourceName, AsyncCallback<Boolean> callback);

	void isAvailableOccurrenceJobReportError(String jobIdentifier,
			AsyncCallback<Boolean> callback);

	void isAvailableTaxonomyJobReportError(String jobIdentifier,
			AsyncCallback<Boolean> callback);

	void getLastQuery(AsyncCallback<String> callback);

	void loadClusterCommonNameForResultRowByScientificName(
			String scientificName,
			AsyncCallback<ClusterCommonNameDataSourceForResultRow> callback);
	
	void loadClusterCommonNameForTaxonomyRowByScientificName(
			String scientificName,
			AsyncCallback<ClusterCommonNameDataSourceForTaxonomyRow> callback);
	
}
