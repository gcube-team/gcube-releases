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
import org.gcube.portlets.user.td.gwtservice.shared.excel.ExcelExportSession;
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
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareRule;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTabResource;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTemplate;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.DataMinerOperationSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResubmitSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResumeSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsTasksMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateApplySession;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateColumnData;
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
 * 
 * @author Giancarlo Panichi
 *
 */
@RemoteServiceRelativePath("TDGWTService")
public interface TDGWTService extends RemoteService {

	/**
	 * Get informations on the current user
	 * 
	 * @return User Info
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public UserInfo hello() throws TDGWTServiceException;

	/**
	 * Set locale on server
	 * 
	 * @param infoLocale
	 *            Info locale
	 * @throws TDGWTServiceException
	 *             exception
	 */
	public void setLocale(InfoLocale infoLocale) throws TDGWTServiceException;

	/**
	 * Retrieve pending Tasks and set them as background tasks
	 * 
	 * @throws TDGWTServiceException
	 */
	/**
	 * 
	 * @return Pendings Tasks
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public Integer pendingTasksRetrieve() throws TDGWTServiceException;

	/**
	 * Restore UI session
	 * 
	 * 
	 * @param startTRId
	 *            Start TR id
	 * @return Tabular Resource id
	 * @throws TDGWTServiceException
	 *             exception
	 */
	public TRId restoreUISession(TRId startTRId) throws TDGWTServiceException;

	/**
	 * Resolve Uri
	 * 
	 * 
	 * @param uriResolverSession
	 *            Uri resolver session
	 * @return Uri
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String getUriFromResolver(UriResolverSession uriResolverSession) throws TDGWTServiceException;

	/**
	 * Get VRE Folder Id
	 * 
	 * @return VRE Folder Id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String getVREFolderIdByScope() throws TDGWTServiceException;

	// PeriodDataType
	/**
	 * Get list of PeriodDataType
	 * 
	 * @return List of period data type
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<PeriodDataType> getPeriodDataTypes() throws TDGWTServiceException;

	/**
	 * Retrieves the hierarchical relationship for the specific period data type
	 * 
	 * @param periodDataType
	 *            Period data type
	 * @return List of period data type
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<PeriodDataType> getHierarchicalRelationshipForPeriodDataTypes(PeriodDataType periodDataType)
			throws TDGWTServiceException;

	// ValueDataFormats
	/**
	 * Retrieve ValueDataFormat for all ColumnDataType
	 * 
	 * @return Map
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public HashMap<ColumnDataType, ArrayList<ValueDataFormat>> getValueDataFormatsMap() throws TDGWTServiceException;

	/**
	 * Retrieve ValueDataFormat for specific ColumnDataType
	 * 
	 * @param columnDataType
	 *            Column data type
	 * @return List of value data format
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ValueDataFormat> getValueDataFormatsOfColumnDataType(ColumnDataType columnDataType)
			throws TDGWTServiceException;

	// TabularResource
	/**
	 * Get current TRId
	 * 
	 * @return TR id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TRId getCurrentTRId() throws TDGWTServiceException;

	/**
	 * Get information on the current tabular resource don't call service
	 * 
	 * @return Tabular resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TabResource getInSessionTabResourceInfo() throws TDGWTServiceException;

	/**
	 * Get informations on the current tabular resource call service
	 * 
	 * 
	 * @return Tabular resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TabResource getTabResourceInformation() throws TDGWTServiceException;

	/**
	 * Set tabular resource information
	 * 
	 * @param tabResource
	 *            Tabular resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setTabResourceInformation(TabResource tabResource) throws TDGWTServiceException;

	/**
	 * Set tabular resource to final
	 * 
	 * @param trId
	 *            Tabular Resource id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setTabResourceToFinal(TRId trId) throws TDGWTServiceException;

	/**
	 * Get informations on tabular resource
	 * 
	 * @param trId
	 *            TR id
	 * @return Tabular resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TabResource getTabResourceInformation(TRId trId) throws TDGWTServiceException;

	/**
	 * Return true if is a valid tabular resource
	 * 
	 * @param trId
	 *            Tabular Resouce id
	 * @return True if is valid
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public Boolean isTabularResourceValid(TRId trId) throws TDGWTServiceException;

	/**
	 * Get creation date on tabular resource
	 * 
	 * @param trId
	 *            TR id
	 * @return TR id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String getTRCreationDate(TRId trId) throws TDGWTServiceException;

	/**
	 * Set current tabular resource
	 * 
	 * @param tabResource
	 *            Tabular Resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setTabResource(TabResource tabResource) throws TDGWTServiceException;

	/**
	 * Creates a TabResource on Service
	 * 
	 * @param tabResource
	 *            Tabular Resource
	 * @return Tabukar Resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TabResource createTabularResource(TabResource tabResource) throws TDGWTServiceException;

	/**
	 * Return Last Table
	 * 
	 * @param trId
	 *            TR id
	 * @return Table data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TableData getLastTable(TRId trId) throws TDGWTServiceException;

	/**
	 * Return Table
	 * 
	 * @param trId
	 *            TR id
	 * @return Table data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TableData getTable(TRId trId) throws TDGWTServiceException;

	/**
	 * Return Metadata of Table
	 * 
	 * @param trId
	 *            TR id
	 * @return List of tabular metadata
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<TabMetadata> getTableMetadata(TRId trId) throws TDGWTServiceException;

	/**
	 * Return Metadata of Tabular Resources
	 * 
	 * @param trId
	 *            TR id
	 * @return List of tabular resource meta data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<TRMetadata> getTRMetadata(TRId trId) throws TDGWTServiceException;

	/**
	 * Retreive a ColumnData that is a connection to external table
	 * 
	 * @param refColumn
	 *            Reference column
	 * @return Column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ColumnData getConnection(RefColumn refColumn) throws TDGWTServiceException;

	/**
	 * Close All Tabular Resources
	 * 
	 * 
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void closeAllTabularResources() throws TDGWTServiceException;

	/**
	 * Close Tabular Resource with TRId equals to closeTRId and open if not null
	 * that with TRId equals to openTRId
	 * 
	 * 
	 * @param openTRId
	 *            Open TR id
	 * @param closeTRId
	 *            Close TR id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void closeTabularResourceAndOpen(TRId openTRId, TRId closeTRId) throws TDGWTServiceException;

	/**
	 * Close Tabular Resource with TRId equals to closeTRId
	 * 
	 * @param closeTRId
	 *            Close TR id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void closeTabularResource(TRId closeTRId) throws TDGWTServiceException;

	/**
	 * Set Current Tabular Resource that passed as parameter if not null
	 * 
	 * @param activeTRId
	 *            Active TR id
	 * @return TR id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TRId setActiveTabularResource(TRId activeTRId) throws TDGWTServiceException;

	// Task
	/**
	 * Resubmit task
	 * 
	 * @param taskResubmitSession
	 *            Session
	 * @return Task
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startTaskResubmit(TaskResubmitSession taskResubmitSession) throws TDGWTServiceException;

	/**
	 * Resume task
	 * 
	 * @param taskResumeSession
	 *            Session
	 * @return Task
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startTaskResume(TaskResumeSession taskResumeSession) throws TDGWTServiceException;

	// Validations
	/**
	 * Returns validations contained in the tasks
	 * 
	 * @param trId
	 *            TR id
	 * @return Validations
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ValidationsTasksMetadata getValidationsTasksMetadata(TRId trId) throws TDGWTServiceException;

	/**
	 * Return Validations Metadata of Table
	 * 
	 * @param trId
	 *            TR id
	 * @return Validations meta data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TabValidationsMetadata getTableValidationsMetadata(TRId trId) throws TDGWTServiceException;

	/**
	 * Returns ArrayList of ColumnData that contains all the validation columns
	 * of columnName
	 * 
	 * @param trId
	 *            TR id
	 * @param columnName
	 *            Column name
	 * @return List of column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ColumnData> getValidationColumns(TRId trId, String columnName) throws TDGWTServiceException;

	/**
	 * Returns ArrayListof ColumnData that contains all the validation columns
	 * of columnLocalId
	 * 
	 * @param columnLocalId
	 *            Column local id
	 * @param trId
	 *            TR id
	 * @return List of column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ColumnData> getValidationColumns(String columnLocalId, TRId trId) throws TDGWTServiceException;

	/**
	 * Remove Validations from tabular resource
	 * 
	 * @param trId
	 *            TR id
	 * @return Deleted
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startValidationsDelete(TRId trId) throws TDGWTServiceException;

	// RollBack
	/**
	 * Call rollback on tabular resource
	 * 
	 * @param rollBackSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startRollBack(RollBackSession rollBackSession) throws TDGWTServiceException;

	/**
	 * Call discard operation on tabular resource
	 * 
	 * @param trId
	 *            TR id
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startDiscard(TRId trId) throws TDGWTServiceException;

	// Share
	/**
	 * Share tabular resource
	 * 
	 * @param shareInfo
	 *            Share info
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setShare(ShareTabResource shareInfo) throws TDGWTServiceException;

	/**
	 * Share template
	 * 
	 * 
	 * @param shareTemplate
	 *            Share template
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setShareTemplate(ShareTemplate shareTemplate) throws TDGWTServiceException;

	/**
	 * Share Rule
	 * 
	 * @param shareRule
	 *            Share rule
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setShareRule(ShareRule shareRule) throws TDGWTServiceException;

	// Open

	/**
	 * Return all tabular resource of a user
	 * 
	 * 
	 * @return List of tabular resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<TabResource> getTabularResources() throws TDGWTServiceException;

	/**
	 * Return all tabular resource of a user and last tables
	 * 
	 * @return List of Tabular Resource
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<TabResource> getTabularResourcesAndLastTables() throws TDGWTServiceException;

	// Clone
	/**
	 * Start clone tabular resource
	 * 
	 * @param cloneTabularResourceSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startCloneTabularResource(CloneTabularResourceSession cloneTabularResourceSession)
			throws TDGWTServiceException;

	//
	/**
	 * Initialize Codelists Paging Loader
	 * 
	 * 
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setCodelistsPagingLoader() throws TDGWTServiceException;

	/**
	 * Retrieves a portion of Codelists
	 * 
	 * @param codelistPagingLoadConfig
	 *            Paging loading config
	 * @return Page result
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public CodelistPagingLoadResult getCodelistsPagingLoader(CodelistPagingLoadConfig codelistPagingLoadConfig)
			throws TDGWTServiceException;;

	/**
	 * Set open session for return all tabular resource of a user
	 * 
	 * 
	 * @param tdOpenSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void tdOpen(TDOpenSession tdOpenSession) throws TDGWTServiceException;

	/**
	 * Retrieve Time Table Id
	 * 
	 * 
	 * @param periodDataType
	 *            Period data type
	 * @return Table id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public Long getTimeTableId(PeriodDataType periodDataType) throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the current table
	 * 
	 * 
	 * @return List of column data
	 * @throws TDGWTServiceException
	 *             Excption
	 */
	public ArrayList<ColumnData> getColumns() throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId
	 * 
	 * @param trId
	 *            TR id
	 * @return List of Column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ColumnData> getColumns(TRId trId) throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId view columns
	 * included
	 * 
	 * @param trId
	 *            TR id
	 * @return List of column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ColumnData> getColumnWithViewColumnIncluded(TRId trId) throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId only view
	 * columns in relationship are included. TimeDimensionColumn and
	 * DimensionColumn are not included.
	 * 
	 * 
	 * @param trId
	 *            TR id
	 * @return List of column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ColumnData> getColumnWithOnlyViewColumnInRel(TRId trId) throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId for
	 * statistical
	 * 
	 * @param trId
	 *            TR id
	 * @return List of column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ColumnData> getColumnsForStatistical(TRId trId) throws TDGWTServiceException;

	/**
	 * Retrieves the list of columns in the table provided by trId only CODE and
	 * CODENAME types
	 * 
	 * @param trId
	 *            TR id
	 * @return List of column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ColumnData> getColumnsForDimension(TRId trId) throws TDGWTServiceException;

	/**
	 * Retrieves information about a specific column of specific table
	 * 
	 * @param trId
	 *            TR id
	 * @param columnName
	 *            Column name
	 * @return Column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ColumnData getColumn(TRId trId, String columnName) throws TDGWTServiceException;

	/**
	 * Retrieves information about a specific column of specific table
	 * 
	 * 
	 * @param columnLocalId
	 *            Column Local Id
	 * @param trId
	 *            TR id
	 * @return Column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ColumnData getColumn(String columnLocalId, TRId trId) throws TDGWTServiceException;

	/**
	 * Remove Tabular Resource From Service
	 * 
	 * @param trId
	 *            TR id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void removeTabularResource(TRId trId) throws TDGWTServiceException;

	// SDMX

	/**
	 * Get Codelist on a registry
	 * 
	 * @return List of codelist
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<Codelist> getCodelists() throws TDGWTServiceException;

	/**
	 * Get Datasets on a registry
	 * 
	 * @return List of dataset
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<Dataset> getDatasets() throws TDGWTServiceException;

	/**
	 * Get Agecies on a registry
	 * 
	 * @return List of Agencies
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<Agencies> getAgencies() throws TDGWTServiceException;

	/**
	 * Set SDMX Registry Source
	 * 
	 * 
	 * @param sdmxRegistrySource
	 *            SDMX registry source
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setSDMXRegistrySource(SDMXRegistrySource sdmxRegistrySource) throws TDGWTServiceException;

	// Import SDMX
	/**
	 * Start SDMX Import and invokes the client library
	 * 
	 * @param sdmxImportSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startSDMXImport(SDMXImportSession sdmxImportSession) throws TDGWTServiceException;

	// CSV Import
	/**
	 * 
	 * @param csvImportSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setCSVSession(CSVImportSession csvImportSession) throws TDGWTServiceException;

	/**
	 * 
	 * @param csvImportSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void getFileFromWorkspace(CSVImportSession csvImportSession) throws TDGWTServiceException;

	/**
	 * 
	 * @return Available charset list
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public AvailableCharsetList getAvailableCharset() throws TDGWTServiceException;

	/**
	 * 
	 * @param encoding
	 *            Encoding
	 * @param headerPresence
	 *            Header presence
	 * @param delimiter
	 *            Delimiter
	 * @param comment
	 *            Comment
	 * @return List of configuration
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<String> configureCSVParser(String encoding, HeaderPresence headerPresence, char delimiter,
			char comment) throws TDGWTServiceException;

	/**
	 * 
	 * @param errorsLimit
	 *            Error limit
	 * @return Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public CheckCSVSession checkCSV(long errorsLimit) throws TDGWTServiceException;

	/**
	 * Start CSV Import and invokes the client library
	 * 
	 * @param csvImportSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startCSVImport(CSVImportSession csvImportSession) throws TDGWTServiceException;

	// Export CSV
	/**
	 * Available charset for export
	 * 
	 * @return Available charset list
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public AvailableCharsetList getAvailableCharsetForExport() throws TDGWTServiceException;

	/**
	 * Start CSV Export and invokes the client library
	 * 
	 * @param csvExportSession
	 *            Sesion
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startCSVExport(CSVExportSession csvExportSession) throws TDGWTServiceException;

	// Export SDMX
	/**
	 * Start SDMX Export and invokes the client library
	 * 
	 * @param exportSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startSDMXExport(SDMXExportSession exportSession) throws TDGWTServiceException;

	// Export Excel
	/**
	 * Start Excel Export and invokes the client library
	 * 
	 * @param exportSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startExcelExport(ExcelExportSession exportSession) throws TDGWTServiceException;

	// Export Template SDMX
	/**
	 * 
	 * Start SDMX Template Export and invokes the client library
	 * 
	 * @param sdmxTemplateExportSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startSDMXTemplateExport(SDMXTemplateExportSession sdmxTemplateExportSession)
			throws TDGWTServiceException;

	// Export JSON
	/**
	 * Start JSON Export and invokes the client library
	 * 
	 * @param jsonExportSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startJSONExport(JSONExportSession jsonExportSession) throws TDGWTServiceException;

	// Table Operations

	/**
	 * Start change table type
	 * 
	 * @param changeTableTypeSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startChangeTableType(ChangeTableTypeSession changeTableTypeSession) throws TDGWTServiceException;

	/**
	 * Start Union and invokes the client library
	 * 
	 * @param unionSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startUnion(UnionSession unionSession) throws TDGWTServiceException;

	// Rows Operations
	/**
	 * Start edit row or add row
	 * 
	 * @param editRowSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startEditRow(EditRowSession editRowSession) throws TDGWTServiceException;

	/**
	 * Start delete rows
	 * 
	 * @param deleteRowsSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startDeleteRows(DeleteRowsSession deleteRowsSession) throws TDGWTServiceException;

	/**
	 * Start operation on duplicates
	 * 
	 * @param duplicatesSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startDuplicates(DuplicatesSession duplicatesSession) throws TDGWTServiceException;

	// Column Operation

	/**
	 * Start change column type
	 * 
	 * 
	 * @param changeColumnTypeSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startChangeColumnType(ChangeColumnTypeSession changeColumnTypeSession) throws TDGWTServiceException;

	/**
	 * Start add column
	 * 
	 * @param addColumnSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startAddColumn(AddColumnSession addColumnSession) throws TDGWTServiceException;

	/**
	 * Start delete column
	 * 
	 * @param deleteColumnSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startDeleteColumn(DeleteColumnSession deleteColumnSession) throws TDGWTServiceException;

	/**
	 * Start change the column label
	 * 
	 * @param labelColumnSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Excetpion
	 */
	public String startLabelColumn(LabelColumnSession labelColumnSession) throws TDGWTServiceException;

	/**
	 * Start change columns position
	 * 
	 * 
	 * @param changeColumnsPositionSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exeption
	 */
	public void startChangeColumnsPosition(ChangeColumnsPositionSession changeColumnsPositionSession)
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
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 *
	 */
	public ArrayList<Occurrences> getOccurrencesForBatchReplace(
			OccurrencesForReplaceBatchColumnSession occurrencesForReplaceBatchColumnSession)
			throws TDGWTServiceException;

	/**
	 * Start batch replace on column
	 * 
	 * @param replaceBatchColumnSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startReplaceBatchColumn(ReplaceBatchColumnSession replaceBatchColumnSession)
			throws TDGWTServiceException;

	// Replace Operation
	/**
	 * Start replace the column value
	 * 
	 * @param replaceColumnSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startReplaceColumn(ReplaceColumnSession replaceColumnSession) throws TDGWTServiceException;

	/**
	 * Start replace column by external tabular resource
	 * 
	 * @param replaceByExternalSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startReplaceByExternal(ReplaceByExternalSession replaceByExternalSession)
			throws TDGWTServiceException;

	// Templates
	/**
	 * Retrieves templates of user
	 * 
	 * 
	 * @return List of template data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<TemplateData> getTemplates() throws TDGWTServiceException;

	/**
	 * 
	 * Retrieves templates for export as DSD in SDMX registry. Template must
	 * have at least a measure column.
	 * 
	 * @return List of template data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<TemplateData> getTemplatesForDSDExport() throws TDGWTServiceException;

	/**
	 * 
	 * Retrieves the columns of the specific template. Template must have at
	 * least a measure column.
	 * 
	 * @param templateId
	 *            Template id
	 * @return List of template column data
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<TemplateColumnData> getTemplateColumnsData(String templateId) throws TDGWTServiceException;

	/**
	 * Start Apply Template
	 * 
	 * @param templateApplySession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startTemplateApply(TemplateApplySession templateApplySession) throws TDGWTServiceException;

	/**
	 * Start Delete Template
	 * 
	 * @param templateDeleteSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void templateDelete(TemplateDeleteSession templateDeleteSession) throws TDGWTServiceException;

	// Locales
	/**
	 * Retrieve locales supported
	 * 
	 * @return List of locale
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<String> getLocales() throws TDGWTServiceException;

	// Licences
	/**
	 * Retrieve licences supported
	 * 
	 * @return List of licences
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<LicenceData> getLicences() throws TDGWTServiceException;

	// History
	/**
	 * Retrieve History
	 * 
	 * @return Operation history
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<OpHistory> getHistory() throws TDGWTServiceException;

	/**
	 * Retrieve History for specific tabular resource
	 * 
	 * @param trId
	 *            TR id
	 * @return Operation history
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<OpHistory> getHistory(TRId trId) throws TDGWTServiceException;

	/**
	 * 
	 * 
	 * @return Operation history
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public OpHistory getLastOperationInfo() throws TDGWTServiceException;

	/**
	 * 
	 * @param trId
	 *            TR id
	 * @return Operation history
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public OpHistory getLastOperationInfo(TRId trId) throws TDGWTServiceException;

	// Helper Extract Codelist
	/**
	 * Start Extract Codelist and invokes the client library
	 * 
	 * 
	 * @param extractCodelistSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startExtractCodelist(ExtractCodelistSession extractCodelistSession) throws TDGWTServiceException;

	// Helper Codelist Mapping Import
	/**
	 * 
	 * @param codelistMappingSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setCodelistMappingSession(CodelistMappingSession codelistMappingSession) throws TDGWTServiceException;

	/**
	 * 
	 * @param codelistMappingSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void getFileFromWorkspace(CodelistMappingSession codelistMappingSession) throws TDGWTServiceException;

	/**
	 * Start Codelist Mapping Import
	 * 
	 * @param codelistMappingSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startCodelistMappingImport(CodelistMappingSession codelistMappingSession)
			throws TDGWTServiceException;

	// Normalization
	/**
	 * Start Normalization and invokes the client library
	 * 
	 * 
	 * @param normalizationSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startNormalization(NormalizationSession normalizationSession) throws TDGWTServiceException;

	// Denormalization
	/**
	 * Start Denormalization and invokes the client library
	 * 
	 * 
	 * @param denormalizationSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startDenormalization(DenormalizationSession denormalizationSession) throws TDGWTServiceException;

	// Operation Monitor
	/**
	 * Get Operation Monitor
	 * 
	 * 
	 * @param operationMonitorSession
	 *            Session
	 * @return Operation Monitor
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public OperationMonitor getOperationMonitor(OperationMonitorSession operationMonitorSession)
			throws TDGWTServiceException;

	/**
	 * Get List of Background Operation Monitor
	 * 
	 * @param backgroundOperationMonitorSession
	 *            Session
	 * @return List of Operation Monitor
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<BackgroundOperationMonitor> getBackgroundOperationMonitor(
			BackgroundOperationMonitorSession backgroundOperationMonitorSession) throws TDGWTServiceException;

	/**
	 * 
	 * Retrieve Background Operation Monitor for specific task
	 * 
	 * @param operationMonitorSession
	 *            Session
	 * @return Operation monitor
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public OperationMonitor getBackgroundOperationMonitorForSpecificTask(
			OperationMonitorSession operationMonitorSession) throws TDGWTServiceException;

	// File Upload Monitor
	/**
	 * Get File Upload Monitor during the file upload operation in Import CSV
	 * 
	 * @return File uploader monitor
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public FileUploadMonitor getFileUploadMonitor() throws TDGWTServiceException;

	// Resources TD
	/**
	 * Retrieves the resources of a specific tabular resource
	 * 
	 * @param trId
	 *            TR id
	 * @return List of Resource TD description
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ResourceTDDescriptor> getResourcesTD(TRId trId) throws TDGWTServiceException;

	/**
	 * Retrieves the resources of a specific tabular resource by type
	 * 
	 * @param trId
	 *            TR id
	 * @param resourceTDType
	 *            Resource TD type
	 * @return List of Resource TD descriptor
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public ArrayList<ResourceTDDescriptor> getResourcesTDByType(TRId trId, ResourceTDType resourceTDType)
			throws TDGWTServiceException;

	/**
	 * Remove resource
	 * 
	 * @param removeResourceSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void removeResource(RemoveResourceSession removeResourceSession) throws TDGWTServiceException;

	/**
	 * Save resource on Workspace
	 * 
	 * @param saveResourceSession
	 *            Session
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void saveResource(SaveResourceSession saveResourceSession) throws TDGWTServiceException;

	// GIS MAP
	/**
	 * Start Map Creation and invokes the client library
	 * 
	 * @param mapCreationSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startMapCreation(MapCreationSession mapCreationSession) throws TDGWTServiceException;

	// DataMiner
	/**
	 * 
	 * @param dataMinerOperationSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startDataMinerOperation(DataMinerOperationSession dataMinerOperationSession)
			throws TDGWTServiceException;

	// Chart
	/**
	 * Start Chart Top Rating Creation and invokes the client library
	 * 
	 * @param chartTopRatingSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startChartTopRating(ChartTopRatingSession chartTopRatingSession) throws TDGWTServiceException;

	// Geospatial
	/**
	 * Start Geospatial Coordinates Creation
	 * 
	 * @param geospatialCreateCoordinatesSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startGeospatialCreateCoordinates(
			GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession) throws TDGWTServiceException;

	/**
	 * Start Downscale C-Square
	 * 
	 * 
	 * @param geospatialDownscaleCSquareSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startGeospatialDownscaleCSquare(GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession)
			throws TDGWTServiceException;

	// Geometry
	/**
	 * Point Creation
	 * 
	 * @param geometryCreatePointSession
	 *            Session
	 * @return Operation
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String startGeometryCreatePoint(GeometryCreatePointSession geometryCreatePointSession)
			throws TDGWTServiceException;

	/**
	 * Retrieve the URL of default SDMX Registry
	 * 
	 * 
	 * @return URL of the default SDMX Registry
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String getDefaultSDMXRegistryURL() throws TDGWTServiceException;

}
