/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.client.rpc;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.AvailableCharsetList;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CheckCSVSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.gwtservice.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.file.HeaderPresence;
import org.gcube.portlets.user.td.gwtservice.shared.geometry.GeometryCreatePointSession;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialCreateCoordinatesSession;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialDownscaleCSquareSession;
import org.gcube.portlets.user.td.gwtservice.shared.history.OpHistory;
import org.gcube.portlets.user.td.gwtservice.shared.history.RollBackSession;
import org.gcube.portlets.user.td.gwtservice.shared.i18n.InfoLocale;
import org.gcube.portlets.user.td.gwtservice.shared.json.JSONExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.licenses.LicenceData;
import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitorSession;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitorSession;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareRule;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTabResource;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTemplate;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.StatisticalOperationSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResubmitSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResumeSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsTasksMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateApplySession;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateDeleteSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.RefColumn;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.Occurrences;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.OccurrencesForReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.clone.CloneTabularResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.AddColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ChangeColumnsPositionSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.DeleteColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.LabelColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.type.ChangeColumnTypeSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.DenormalizationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.NormalizationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.open.TDOpenSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.CodelistPagingLoadConfig;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.CodelistPagingLoadResult;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.RemoveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DeleteRowsSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DuplicatesSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.EditRowSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.ChangeTableTypeSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabValidationsMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Dataset;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.PeriodDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ValueDataFormat;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * Implements the basic interfaces for access to the service.
 * 
 * <p>
 * Allows:  
 * <ul>
 * <li>Access to Tabular Resources</li>
 * <li>Import of CSV file</li>
 * <li>Import from SDMX Registry</li>
 * <ul>
 * </p>
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
@RemoteServiceRelativePath("TDGWTService")
public interface TDGWTService extends RemoteService {

	/**
	 * Get informations on the current user
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public UserInfo hello() throws TDGWTServiceException;

	
	/**
	 * Set locale on server
	 * 
	 * @param infoLocale
	 * @throws TDGWTServiceException
	 */
	public void setLocale(InfoLocale infoLocale) throws TDGWTServiceException;
	
	/**
	 * Retrieve pending Tasks and set them as background tasks
	 * 
	 * @throws TDGWTServiceException
	 */
	public Integer pendingTasksRetrieve() throws TDGWTServiceException;

	/**
	 * Restore UI session
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TRId restoreUISession(TRId startTRId) throws TDGWTServiceException;

	/**
	 * Resolve Uri
	 * 
	 * @param uriResolverSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String getUriFromResolver(UriResolverSession uriResolverSession)
			throws TDGWTServiceException;
	
	
	/**
	 * Get VRE Folder Id
	 * 
	 * @return VRE Folder Id
	 * @throws TDGWTServiceException
	 */
	public String getVREFolderIdByScope() throws TDGWTServiceException;
	
	
	// PeriodDataType
	/**
	 * Get list of PeriodDataType
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<PeriodDataType> getPeriodDataTypes()
			throws TDGWTServiceException;

	/**
	 * Retrieves the hierarchical relationship for the specific period data type
	 * 
	 * @param periodDataType
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<PeriodDataType> getHierarchicalRelationshipForPeriodDataTypes(
			PeriodDataType periodDataType) throws TDGWTServiceException;

	// ValueDataFormats
	/**
	 * Retrieve ValueDataFormat for all ColumnDataType
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public HashMap<ColumnDataType, ArrayList<ValueDataFormat>> getValueDataFormatsMap()
			throws TDGWTServiceException;

	/**
	 * Retrieve ValueDataFormat for specific ColumnDataType
	 * 
	 * @param columnDataType
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ValueDataFormat> getValueDataFormatsOfColumnDataType(
			ColumnDataType columnDataType) throws TDGWTServiceException;

	// TabularResource
	/**
	 * Get current TRId
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TRId getCurrentTRId() throws TDGWTServiceException;

	/**
	 * Get information on the current tabular resource don't call service
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TabResource getInSessionTabResourceInfo()
			throws TDGWTServiceException;

	/**
	 * Get informations on the current tabular resource call service
	 * 
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TabResource getTabResourceInformation() throws TDGWTServiceException;

	/**
	 * Set tabular resource information
	 * 
	 * @param tabResource
	 * @throws TDGWTServiceException
	 */
	public void setTabResourceInformation(TabResource tabResource)
			throws TDGWTServiceException;

	/**
	 * Set tabular resource to final
	 * 
	 * @param trId
	 * @throws TDGWTServiceException
	 */
	public void setTabResourceToFinal(TRId trId) throws TDGWTServiceException;

	/**
	 * Get informations on tabular resource
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TabResource getTabResourceInformation(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Return true if is a valid tabular resource
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public Boolean isTabularResourceValid(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Get creation date on tabular resource
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String getTRCreationDate(TRId trId) throws TDGWTServiceException;

	/**
	 * Set current tabular resource
	 * 
	 * @param tr
	 * @throws TDGWTServiceException
	 */
	public void setTabResource(TabResource tabResource)
			throws TDGWTServiceException;

	/**
	 * Creates a TabResource on Service
	 * 
	 * @param tabResource
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TabResource createTabularResource(TabResource tabResource)
			throws TDGWTServiceException;

	/**
	 * Return Last Table
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TableData getLastTable(TRId trId) throws TDGWTServiceException;

	/**
	 * Return Table
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TableData getTable(TRId trId) throws TDGWTServiceException;

	/**
	 * Return Metadata of Table
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<TabMetadata> getTableMetadata(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Return Metadata of Tabular Resources
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<TRMetadata> getTRMetadata(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Retreive a ColumnData that is a connection to external table
	 * 
	 * @param refColumn
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ColumnData getConnection(RefColumn refColumn)
			throws TDGWTServiceException;

	/**
	 * Close All Tabular Resources
	 * 
	 * 
	 * @throws TDGWTServiceException
	 */
	public void closeAllTabularResources() throws TDGWTServiceException;

	/**
	 * Close Tabular Resource with TRId equals to closeTRId and open if not null
	 * that with TRId equals to openTRId
	 * 
	 * @param openTRId
	 * @throws TDGWTServiceException
	 */
	public void closeTabularResourceAndOpen(TRId openTRId, TRId closeTRId)
			throws TDGWTServiceException;

	/**
	 * Close Tabular Resource with TRId equals to closeTRId
	 * 
	 * @param closeTRId
	 * @throws TDGWTServiceException
	 */
	public void closeTabularResource(TRId closeTRId)
			throws TDGWTServiceException;

	/**
	 * Set Current Tabular Resource that passed as parameter if not null
	 * 
	 * @param activeTRId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TRId setActiveTabularResource(TRId activeTRId)
			throws TDGWTServiceException;

	// Task
	/**
	 * Resubmit task
	 * 
	 * @param taskResubmitSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startTaskResubmit(TaskResubmitSession taskResubmitSession)
			throws TDGWTServiceException;

	/**
	 * Resume task
	 * 
	 * @param taskResumeSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startTaskResume(TaskResumeSession taskResumeSession)
			throws TDGWTServiceException;

	// Validations
	/**
	 * Returns validations contained in the tasks
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ValidationsTasksMetadata getValidationsTasksMetadata(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Return Validations Metadata of Table
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public TabValidationsMetadata getTableValidationsMetadata(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Returns ArrayList<ColumnData> that contains all the validation columns of
	 * columnName
	 * 
	 * @param trId
	 * @param columnName
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getValidationColumns(TRId trId,
			String columnName) throws TDGWTServiceException;

	/**
	 * Returns ArrayList<ColumnData> that contains all the validation columns of
	 * columnLocalId
	 * 
	 * @param columnLocalId
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getValidationColumns(String columnLocalId,
			TRId trId) throws TDGWTServiceException;

	/**
	 * Remove Validations from tabular resource
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startValidationsDelete(TRId trId)
			throws TDGWTServiceException;

	// RollBack
	/**
	 * Call rollback on tabular resource
	 * 
	 * @param rollBackSession
	 * @throws TDGWTServiceException
	 */
	public String startRollBack(RollBackSession rollBackSession)
			throws TDGWTServiceException;

	/**
	 * Call discard operation on tabular resource
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startDiscard(TRId trId) throws TDGWTServiceException;

	// Share
	/**
	 * Share tabular resource
	 * 
	 * @param shareInfo
	 * @return
	 * @throws TDGWTServiceException
	 */
	public void setShare(ShareTabResource shareInfo)
			throws TDGWTServiceException;

	/**
	 * Share template
	 * 
	 * @param shareTemplate
	 * @return
	 * @throws TDGWTServiceException
	 */
	public void setShareTemplate(ShareTemplate shareTemplate)
			throws TDGWTServiceException;

	/**
	 * Share Rule
	 * 
	 * @param shareRule
	 * @return
	 * @throws TDGWTServiceException
	 */
	public void setShareRule(ShareRule shareRule) throws TDGWTServiceException;

	// Open

	/**
	 * Return all tabular resource of a user
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<TabResource> getTabularResources()
			throws TDGWTServiceException;

	/**
	 * Return all tabular resource of a user and last tables
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<TabResource> getTabularResourcesAndLastTables()
			throws TDGWTServiceException;

	// Clone
	/**
	 * Start clone tabular resource
	 * 
	 * @param cloneTabularResourceSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startCloneTabularResource(
			CloneTabularResourceSession cloneTabularResourceSession)
			throws TDGWTServiceException;

	//
	/**
	 * Initialize Codelists Paging Loader
	 * 
	 * @throws TDGWTServiceException
	 */
	public void setCodelistsPagingLoader() throws TDGWTServiceException;

	/**
	 * Retrieves a portion of Codelists
	 * 
	 * @param codelistPagingLoadConfig
	 * @return
	 * @throws TDGWTServiceException
	 */
	public CodelistPagingLoadResult getCodelistsPagingLoader(
			CodelistPagingLoadConfig codelistPagingLoadConfig)
			throws TDGWTServiceException;;

	/**
	 * Set open session for return all tabular resource of a user
	 * 
	 * @param s
	 * @throws TDGWTServiceException
	 */
	public void tdOpen(TDOpenSession tdOpenSession)
			throws TDGWTServiceException;

	/**
	 * Retrieve Time Table Id
	 * 
	 * @param periodDataType
	 * @return
	 * @throws TDGWTServiceException
	 */
	public Long getTimeTableId(PeriodDataType periodDataType)
			throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the current table
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getColumns() throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getColumns(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId view columns
	 * included
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getColumnWithViewColumnIncluded(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId only view
	 * columns in relationship are included. TimeDimensionColumn and
	 * DimensionColumn are not included.
	 * 
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getColumnWithOnlyViewColumnInRel(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId for
	 * statistical
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getColumnsForStatistical(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId only CODE and
	 * CODENAME types
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ColumnData> getColumnsForDimension(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Retrieves information about a specific column of specific table
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ColumnData getColumn(TRId trId, String columnName)
			throws TDGWTServiceException;

	/**
	 * Retrieves information about a specific column of specific table
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ColumnData getColumn(String columnLocalId, TRId trId)
			throws TDGWTServiceException;

	/**
	 * Remove Tabular Resource From Service
	 * 
	 * @param trId
	 * @throws TDGWTServiceException
	 */
	public void removeTabularResource(TRId trId) throws TDGWTServiceException;

	// SDMX

	/**
	 * Get Codelist on a registry
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<Codelist> getCodelists() throws TDGWTServiceException;

	/**
	 * Get Datasets on a registry
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<Dataset> getDatasets() throws TDGWTServiceException;

	/**
	 * Get Agecies on a registry
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<Agencies> getAgencies() throws TDGWTServiceException;

	/**
	 * Set SDMX Registry Source
	 * 
	 * @param s
	 * @throws TDGWTServiceException
	 */
	public void setSDMXRegistrySource(SDMXRegistrySource sdmxRegistrySource)
			throws TDGWTServiceException;

	// Import SDMX
	/**
	 * Start SDMX Import and invokes the client library
	 * 
	 * @param sdmxImportSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startSDMXImport(SDMXImportSession sdmxImportSession)
			throws TDGWTServiceException;

	// CSV Import
	/**
	 * 
	 * @param s
	 * @throws TDGWTServiceException
	 */
	public void setCSVSession(CSVImportSession csvImportSession)
			throws TDGWTServiceException;

	/**
	 * 
	 * @param s
	 * @throws TDGWTServiceException
	 */
	public void getFileFromWorkspace(CSVImportSession csvImportSession)
			throws TDGWTServiceException;

	/**
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public AvailableCharsetList getAvailableCharset()
			throws TDGWTServiceException;

	/**
	 * 
	 * @param encoding
	 * @param headerPresence
	 * @param delimiter
	 * @param comment
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<String> configureCSVParser(String encoding,
			HeaderPresence headerPresence, char delimiter, char comment)
			throws TDGWTServiceException;

	/**
	 * 
	 * @param errorsLimit
	 * @return
	 * @throws TDGWTServiceException
	 */
	public CheckCSVSession checkCSV(long errorsLimit)
			throws TDGWTServiceException;

	/**
	 * Start CSV Import and invokes the client library
	 * 
	 * @param s
	 * @throws TDGWTServiceException
	 */
	public String startCSVImport(CSVImportSession csvImportSession)
			throws TDGWTServiceException;

	// Export CSV
	/**
	 * Available charset for export
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public AvailableCharsetList getAvailableCharsetForExport()
			throws TDGWTServiceException;

	
	
	/**
	 * Start CSV Export and invokes the client library
	 * 
	 * @param csvExportSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startCSVExport(CSVExportSession csvExportSession)
			throws TDGWTServiceException;

	// Export SDMX
	/**
	 * Start SDMX Export and invokes the client library
	 * 
	 * @param exportSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startSDMXExport(SDMXExportSession exportSession)
			throws TDGWTServiceException;

	// Export JSON
	/**
	 * Start JSON Export and invokes the client library
	 * 
	 * @param jsonExportSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startJSONExport(JSONExportSession jsonExportSession)
			throws TDGWTServiceException;

	// Table Operations

	/**
	 * Start change table type
	 * 
	 * @param changeTableTypeSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startChangeTableType(
			ChangeTableTypeSession changeTableTypeSession)
			throws TDGWTServiceException;

	/**
	 * Start Union and invokes the client library
	 * 
	 * @param unionSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startUnion(UnionSession unionSession)
			throws TDGWTServiceException;

	// Rows Operations
	/**
	 * Start edit row or add row
	 * 
	 * @param editRowSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startEditRow(EditRowSession editRowSession)
			throws TDGWTServiceException;

	/**
	 * Start delete rows
	 * 
	 * @param deleteRowsSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startDeleteRows(DeleteRowsSession deleteRowsSession)
			throws TDGWTServiceException;

	/**
	 * Start operation on duplicates
	 * 
	 * @param duplicatesSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startDuplicates(DuplicatesSession duplicatesSession)
			throws TDGWTServiceException;

	// Column Operation

	/**
	 * Start change column type
	 * 
	 * 
	 * @param changeColumnTypeSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startChangeColumnType(
			ChangeColumnTypeSession changeColumnTypeSession)
			throws TDGWTServiceException;

	/**
	 * Start add column
	 * 
	 * @param addColumnSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startAddColumn(AddColumnSession addColumnSession)
			throws TDGWTServiceException;

	/**
	 * Start delete column
	 * 
	 * @param deleteColumnSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startDeleteColumn(DeleteColumnSession deleteColumnSession)
			throws TDGWTServiceException;

	/**
	 * Start change the column label
	 * 
	 * @param labelColumnSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startLabelColumn(LabelColumnSession labelColumnSession)
			throws TDGWTServiceException;

	/**
	 * Start change columns position
	 * 
	 * 
	 * @param changeColumnsPositionSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public void startChangeColumnsPosition(
			ChangeColumnsPositionSession changeColumnsPositionSession)
			throws TDGWTServiceException;

	/**
	 * Start split column
	 * 
	 * @param splitColumnSession
	 * @throws TDGWTServiceException
	 * 
	 *             public void startSplitColumn(SplitColumnSession
	 *             splitColumnSession) throws TDGWTServiceException;
	 */

	/**
	 * Start merge column
	 * 
	 * @param splitColumnSession
	 * @throws TDGWTServiceException
	 * 
	 *             public void startMergeColumn(MergeColumnSession
	 *             mergeColumnSession) throws TDGWTServiceException;
	 */

	/**
	 * Start group by
	 * 
	 * @param groupBySession
	 * @throws TDGWTServiceException
	 * 
	 *             public void startGroupBy(GroupBySession groupBySession)
	 *             throws TDGWTServiceException;
	 */

	// BatchReplace Operations
	/**
	 * Retrieves the values ​​in a column grouped by number of occurrences
	 * 
	 * 
	 * @param occurrencesForReplaceBatchColumnSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<Occurrences> getOccurrencesForBatchReplace(
			OccurrencesForReplaceBatchColumnSession occurrencesForReplaceBatchColumnSession)
			throws TDGWTServiceException;

	/**
	 * Start batch replace on column
	 * 
	 * @param replaceBatchColumnSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startReplaceBatchColumn(
			ReplaceBatchColumnSession replaceBatchColumnSession)
			throws TDGWTServiceException;

	// Replace Operation
	/**
	 * Start replace the column value
	 * 
	 * @param replaceColumnSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startReplaceColumn(ReplaceColumnSession replaceColumnSession)
			throws TDGWTServiceException;

	/**
	 * Start replace column by external tabular resource
	 * 
	 * @param replaceByExternalSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startReplaceByExternal(
			ReplaceByExternalSession replaceByExternalSession)
			throws TDGWTServiceException;

	// Templates
	/**
	 * Retrieves templates of user
	 * 
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<TemplateData> getTemplates() throws TDGWTServiceException;

	/**
	 * Start Apply Template
	 * 
	 * @param templateApplySession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startTemplateApply(TemplateApplySession templateApplySession)
			throws TDGWTServiceException;

	/**
	 * Start Delete Template
	 * 
	 * @param templateDeleteSession
	 * @throws TDGWTServiceException
	 */
	public void templateDelete(TemplateDeleteSession templateDeleteSession)
			throws TDGWTServiceException;

	// Locales
	/**
	 * Retrieve locales supported
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<String> getLocales() throws TDGWTServiceException;

	// Licences
	/**
	 * Retrieve licences supported
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<LicenceData> getLicences() throws TDGWTServiceException;

	// History
	/**
	 * Retrieve History
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<OpHistory> getHistory() throws TDGWTServiceException;

	/**
	 * Retrieve History for specific tabular resource
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<OpHistory> getHistory(TRId trId)
			throws TDGWTServiceException;

	/**
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public OpHistory getLastOperationInfo() throws TDGWTServiceException;

	/**
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public OpHistory getLastOperationInfo(TRId trId)
			throws TDGWTServiceException;

	// Helper Extract Codelist
	/**
	 * Start Extract Codelist and invokes the client library
	 * 
	 * 
	 * @param extractCodelistSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startExtractCodelist(
			ExtractCodelistSession extractCodelistSession)
			throws TDGWTServiceException;

	// Helper Codelist Mapping Import
	/**
	 * 
	 * @param codelistMappingSession
	 * @throws TDGWTServiceException
	 */
	public void setCodelistMappingSession(
			CodelistMappingSession codelistMappingSession)
			throws TDGWTServiceException;

	/**
	 * 
	 * @param codelistMappingSession
	 * @throws TDGWTServiceException
	 */
	public void getFileFromWorkspace(
			CodelistMappingSession codelistMappingSession)
			throws TDGWTServiceException;

	/**
	 * Start Codelist Mapping Import
	 * 
	 * @param codelistMappingSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startCodelistMappingImport(
			CodelistMappingSession codelistMappingSession)
			throws TDGWTServiceException;

	// Normalization
	/**
	 * Start Normalization and invokes the client library
	 * 
	 * @param normalizationSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startNormalization(NormalizationSession normalizationSession)
			throws TDGWTServiceException;

	// Denormalization
	/**
	 * Start Denormalization and invokes the client library
	 * 
	 * 
	 * @param denormalizationSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startDenormalization(
			DenormalizationSession denormalizationSession)
			throws TDGWTServiceException;

	// Operation Monitor
	/**
	 * Get Operation Monitor
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public OperationMonitor getOperationMonitor(
			OperationMonitorSession operationMonitorSession)
			throws TDGWTServiceException;

	/**
	 * Get List of Background Operation Monitor
	 * 
	 * @param backgroundOperationMonitorSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<BackgroundOperationMonitor> getBackgroundOperationMonitor(
			BackgroundOperationMonitorSession backgroundOperationMonitorSession)
			throws TDGWTServiceException;

	/**
	 * 
	 * Retrieve Background Operation Monitor for specific task
	 * 
	 * @param operationMonitorSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public OperationMonitor getBackgroundOperationMonitorForSpecificTask(
			OperationMonitorSession operationMonitorSession)
			throws TDGWTServiceException;

	// File Upload Monitor
	/**
	 * Get File Upload Monitor during the file upload operation in Import CSV
	 * 
	 * @return
	 * @throws TDGWTServiceException
	 */
	public FileUploadMonitor getFileUploadMonitor()
			throws TDGWTServiceException;

	// Resources TD
	/**
	 * Retrieves the resources of a specific tabular resource
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ResourceTDDescriptor> getResourcesTD(TRId trId)
			throws TDGWTServiceException;

	/**
	 * Retrieves the resources of a specific tabular resource by type
	 * 
	 * @param trId
	 * @param resourceTDType
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ResourceTDDescriptor> getResourcesTDByType(TRId trId,
			ResourceTDType resourceTDType) throws TDGWTServiceException;

	/**
	 * Remove resource
	 * 
	 * @param removeResourceSession
	 * @throws TDGWTServiceException
	 */
	public void removeResource(RemoveResourceSession removeResourceSession)
			throws TDGWTServiceException;

	/**
	 * Save resource on Workspace
	 * 
	 * @param saveResourceSession
	 * @throws TDGWTServiceException
	 */
	public void saveResource(SaveResourceSession saveResourceSession)
			throws TDGWTServiceException;

	// GIS MAP
	/**
	 * Start Map Creation and invokes the client library
	 * 
	 * @param mapCreationSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startMapCreation(MapCreationSession mapCreationSession)
			throws TDGWTServiceException;

	// Statistical
	/**
	 * 
	 * @param statisticalOperationSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startStatisticalOperation(
			StatisticalOperationSession statisticalOperationSession)
			throws TDGWTServiceException;

	// Chart
	/**
	 * Start Chart Top Rating Creation and invokes the client library
	 * 
	 * @param chartTopRatingSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startChartTopRating(
			ChartTopRatingSession chartTopRatingSession)
			throws TDGWTServiceException;

	// Geospatial
	/**
	 * Start Geospatial Coordinates Creation
	 * 
	 * @param geospatialCreateCoordinatesSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startGeospatialCreateCoordinates(
			GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession)
			throws TDGWTServiceException;

	/**
	 * Start Downscale C-Square
	 * 
	 * 
	 * @param geospatialDownscaleCSquareSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startGeospatialDownscaleCSquare(
			GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession)
			throws TDGWTServiceException;

	// Geometry
	/**
	 * Point Creation
	 * 
	 * @param geometryCreatePointSession
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String startGeometryCreatePoint(
			GeometryCreatePointSession geometryCreatePointSession)
			throws TDGWTServiceException;

}
