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
import org.gcube.portlets.user.speciesdiscovery.shared.SearchServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("taxonomy")
public interface TaxonomySearchService extends RemoteService {

	public void searchByScientificName(String searchTerm,
			SearchFilters searchFilters) throws SearchServiceException;

	public void searchByCommonName(String searchTerm,
			SearchFilters searchFilters) throws SearchServiceException;

	public SearchByQueryParameter searchByQuery(String query) throws SearchServiceException;

	public SearchStatus getSearchStatus(boolean onlySelected,
			boolean isActiveFilterOnResult) throws SearchServiceException;

	public void stopSearch() throws SearchServiceException;

	public void updateRowSelection(int rowId, boolean selection)
			throws SearchServiceException;

	public Integer updateRowSelections(boolean selection, ResultFilter activeFiltersObject) throws SearchServiceException;

	public int retrieveOccurencesFromSelection() throws SearchServiceException;

	public OccurrenceBatch getOccurrencesBatch(int start, int count)
			throws SearchServiceException;

	public void stopRetrievingOccurrences() throws SearchServiceException;

	public String generateMapFromSelectedOccurrencePoints()
			throws SearchServiceException;

	// Added by Francesco M.
	public void saveSelectedOccurrencePoints(String destinationFolderId,
			String fileName, SaveFileFormat fileFormat,
			OccurrencesSaveEnum typeCSV) throws SearchServiceException;

	public List<DataSourceModel> loadDataSourceList() throws Exception;

	public HashMap<String, Integer> getFilterCounterById(GridField field)
			throws Exception;

	public List<Taxon> getParentsList(Taxon taxon) throws Exception;

	public HashMap<String, ClassificationModel> getFilterCounterForClassification(
			String rank) throws Exception;

	public ArrayList<CommonName> loadListCommonNameByRowId(String resultRowId)
			throws Exception;

	public int countOfSelectedRow() throws SearchServiceException;

	public SearchResult<ResultRow> getSearchResultRows(int start, int limit,
			ResultFilter activeFiltersObject, boolean onlySelected)
			throws SearchServiceException;

	public SearchResult<TaxonomyRow> getSearchTaxonomyRow(int start, int limit,
			ResultFilter activeFiltersObject, boolean showOnlySelected)
			throws SearchServiceException;

	ArrayList<LightTaxonomyRow> loadListChildrenByParentId(String parentId) throws Exception;

	void saveSelectedTaxonomyPoints(String destinationFolderId,
			String fileName, SaveFileFormat fileFormat)
			throws SearchServiceException;

	public OccurrencesStatus getCountOfOccurrencesBatch()
			throws SearchServiceException;

	public List<JobTaxonomyModel> getListTaxonomyJobs() throws Exception;

//	public JobTaxonomyModel createTaxonomyJobByChildren(TaxonomyRow taxonomy,
//			String dataSourceName) throws Exception;

	public boolean cancelTaxonomyJob(String jobIdentifier) throws Exception;

	public boolean saveTaxonomyJob(String jobIdentifier,
			String destinationFolderId, String fileName, String scientificName,
			String dataSourceName) throws Exception;

	public List<LightTaxonomyRow> retrieveTaxonomyByIds(List<String> ids) throws Exception;

	public List<LightTaxonomyRow> retrieveSynonymsByRefId(String refId) throws Exception;

	public List<JobOccurrencesModel> createOccurrencesJob(
			List<JobOccurrencesModel> listJobOccurrenceModel,
			SaveFileFormat saveFileFormat, OccurrencesSaveEnum csvType,
			boolean isByDataSource, int expectedOccurrence) throws Exception;

	public List<JobOccurrencesModel> getListOccurrencesJob() throws Exception;

	public boolean saveOccurrenceJob(JobOccurrencesModel jobModel,
			String destinationFolderId, String fileName, String scientificName,
			String dataSourceName) throws Exception;

	public boolean cancelOccurrenceJob(String jobIdentifier) throws Exception;

	public List<JobOccurrencesModel> resubmitOccurrencesJob(String jobIdentifier)
			throws Exception;

	public JobTaxonomyModel resubmitTaxonomyJob(String jobIdentifier)
			throws Exception;
	
	public ClusterStructuresForResultRow loadStructuresForResultRowClustering() throws Exception;

	public ClusterStructuresForTaxonomyRow loadStructuresForTaxonomyClustering()
			throws Exception;
	
	public boolean changeStatusOccurrenceJob(String jobIdentifier, DownloadState state) throws Exception;
	
	public boolean changeStatusTaxonomyJob(String jobIdentifier, DownloadState state) throws Exception;
	
	
	public List<DataSource> loadDataSourceForResultRow(boolean selected, boolean distinct) throws Exception;

	public JobTaxonomyModel createTaxonomyJobByIds(String search,
			List<DataSourceModel> dataSources) throws Exception;

	
	public JobTaxonomyModel createTaxonomyJobByChildren(String taxonomyServiceId,
			String taxonomyName, String taxonomyRank, String dataSourceName)
			throws Exception;

	/**
	 * @param jobIdentifier
	 * @param destinationFolderId
	 * @param fileName
	 * @param scientificName
	 * @param dataSourceName
	 * @return
	 * @throws Exception
	 */
	boolean saveTaxonomyJobError(String jobIdentifier,
			String destinationFolderId, String fileName, String scientificName,
			String dataSourceName) throws Exception;

	boolean isAvailableTaxonomyJobReportError(String jobIdentifier) throws Exception;
	
	/**
	 * @param jobModel
	 * @param destinationFolderId
	 * @param fileName
	 * @param scientificName
	 * @param dataSourceName
	 * @return
	 * @throws Exception
	 */
	boolean saveOccurrenceJobError(JobOccurrencesModel jobModel,
			String destinationFolderId, String fileName, String scientificName,
			String dataSourceName) throws Exception;

	boolean isAvailableOccurrenceJobReportError(String jobIdentifier) throws Exception;

	/**
	 * @return
	 */
	String getLastQuery();

	/**
	 * @param scientificName
	 * @return
	 * @throws Exception
	 */
	ClusterCommonNameDataSourceForResultRow loadClusterCommonNameForResultRowByScientificName(
			String scientificName) throws Exception;

	ClusterCommonNameDataSourceForTaxonomyRow loadClusterCommonNameForTaxonomyRowByScientificName(
			String scientificName);
	
}
