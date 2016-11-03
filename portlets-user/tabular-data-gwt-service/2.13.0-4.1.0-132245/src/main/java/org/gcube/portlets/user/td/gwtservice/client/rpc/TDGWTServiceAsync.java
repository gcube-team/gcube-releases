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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */

public interface TDGWTServiceAsync {

	public static TDGWTServiceAsync INSTANCE = (TDGWTServiceAsync) GWT
			.create(TDGWTService.class);

	void hello(AsyncCallback<UserInfo> callback);
	
	void setLocale(InfoLocale infoLocale,AsyncCallback<Void> callback);

	void pendingTasksRetrieve(AsyncCallback<Integer> callback);

	void restoreUISession(TRId startTRId, AsyncCallback<TRId> callback);

	void getUriFromResolver(UriResolverSession uriResolverSession,
			AsyncCallback<String> callback);
	
	void getVREFolderIdByScope(AsyncCallback<String> callback);
	
	// PeriodDataType
	void getPeriodDataTypes(AsyncCallback<ArrayList<PeriodDataType>> callback);

	void getHierarchicalRelationshipForPeriodDataTypes(
			PeriodDataType periodDataType,
			AsyncCallback<ArrayList<PeriodDataType>> callback);

	// ValueDataFormat
	void getValueDataFormatsMap(
			AsyncCallback<HashMap<ColumnDataType, ArrayList<ValueDataFormat>>> callback);

	void getValueDataFormatsOfColumnDataType(ColumnDataType columnDataType,
			AsyncCallback<ArrayList<ValueDataFormat>> callback);

	// TabularResource
	void getCurrentTRId(AsyncCallback<TRId> callback);

	void createTabularResource(TabResource tabResource,
			AsyncCallback<TabResource> callback);

	void removeTabularResource(TRId trId, AsyncCallback<Void> callback);

	void isTabularResourceValid(TRId trId, AsyncCallback<Boolean> callback);

	void getInSessionTabResourceInfo(AsyncCallback<TabResource> callback);

	void getTabResourceInformation(AsyncCallback<TabResource> callback);

	void getTabResourceInformation(TRId trId,
			AsyncCallback<TabResource> callback);

	void setTabResourceInformation(TabResource tabResource,
			AsyncCallback<Void> callback);

	void setTabResourceToFinal(TRId trId, AsyncCallback<Void> callback);

	void getTRCreationDate(TRId trId, AsyncCallback<String> callback);

	void setTabResource(TabResource tabResource, AsyncCallback<Void> callback);

	void getTabularResources(AsyncCallback<ArrayList<TabResource>> callback);

	void getTabularResourcesAndLastTables(
			AsyncCallback<ArrayList<TabResource>> callback);

	void getLastTable(TRId trId, AsyncCallback<TableData> callback);

	void getTable(TRId trId, AsyncCallback<TableData> callback);

	void getTableMetadata(TRId trId,
			AsyncCallback<ArrayList<TabMetadata>> callback);

	void getTRMetadata(TRId trId, AsyncCallback<ArrayList<TRMetadata>> callback);

	void getTimeTableId(PeriodDataType periodDataType,
			AsyncCallback<Long> callback);

	void getColumns(AsyncCallback<ArrayList<ColumnData>> callback);

	void getColumns(TRId trId, AsyncCallback<ArrayList<ColumnData>> callback);

	void getColumnWithViewColumnIncluded(TRId trId,
			AsyncCallback<ArrayList<ColumnData>> callback);
	
	void getColumnWithOnlyViewColumnInRel(TRId trId,
			AsyncCallback<ArrayList<ColumnData>> callback);
	
	void getColumnsForStatistical(TRId trId,
			AsyncCallback<ArrayList<ColumnData>> callback);

	void getColumnsForDimension(TRId trId,
			AsyncCallback<ArrayList<ColumnData>> callback);

	void getColumn(TRId trId, String columnName,
			AsyncCallback<ColumnData> callback);

	void getColumn(String columnLocalId, TRId trId,
			AsyncCallback<ColumnData> callback);

	void getConnection(RefColumn refColumn, AsyncCallback<ColumnData> callback);

	void closeAllTabularResources(AsyncCallback<Void> callback);

	void closeTabularResourceAndOpen(TRId openTRId, TRId closeTRId,
			AsyncCallback<Void> callback);

	void closeTabularResource(TRId closeTRId, AsyncCallback<Void> callback);

	void setActiveTabularResource(TRId activeTRId, AsyncCallback<TRId> callback);

	// Task
	void startTaskResubmit(TaskResubmitSession taskResubmitSession,
			AsyncCallback<String> callback);

	void startTaskResume(TaskResumeSession taskResumeSession,
			AsyncCallback<String> callback);

	// Validations
	void getValidationsTasksMetadata(TRId trId,
			AsyncCallback<ValidationsTasksMetadata> callback);

	void getTableValidationsMetadata(TRId trId,
			AsyncCallback<TabValidationsMetadata> callback);

	void getValidationColumns(TRId trId, String columnName,
			AsyncCallback<ArrayList<ColumnData>> callback);
	
	void getValidationColumns(String columnLocalId, TRId trId,
			AsyncCallback<ArrayList<ColumnData>> callback);

	void startValidationsDelete(TRId trId, AsyncCallback<String> callback);
	
	// RollBack
	void startRollBack(RollBackSession rollBackSession,
			AsyncCallback<String> callback);

	void startDiscard(TRId trId, AsyncCallback<String> callback);

	// Share
	void setShare(ShareTabResource shareInfo, AsyncCallback<Void> callback);

	void setShareTemplate(ShareTemplate shareTemplate,
			AsyncCallback<Void> callback);
	
	void setShareRule(ShareRule shareRule,
			AsyncCallback<Void> callback);

	// CodelistPagingLoaded
	void setCodelistsPagingLoader(AsyncCallback<Void> callback);

	void getCodelistsPagingLoader(
			CodelistPagingLoadConfig codelistPagingLoadConfig,
			AsyncCallback<CodelistPagingLoadResult> callback);

	// Open
	void tdOpen(TDOpenSession tdOpenSession, AsyncCallback<Void> callback);

	// Clone
	void startCloneTabularResource(
			CloneTabularResourceSession cloneTabularResourceSession,
			AsyncCallback<String> callback);

	// SDMX
	void getCodelists(AsyncCallback<ArrayList<Codelist>> callback);

	void getDatasets(AsyncCallback<ArrayList<Dataset>> callback);

	void getAgencies(AsyncCallback<ArrayList<Agencies>> callback);

	void setSDMXRegistrySource(SDMXRegistrySource sdmxRegistrySource,
			AsyncCallback<Void> callback);

	// Import SDMX
	void startSDMXImport(SDMXImportSession sdmxImportSession,
			AsyncCallback<String> callback);

	// Import CSV
	void setCSVSession(CSVImportSession csvImportSession,
			AsyncCallback<Void> callback);

	void getFileFromWorkspace(CSVImportSession csvImportSession,
			AsyncCallback<Void> callback);

	void getAvailableCharset(AsyncCallback<AvailableCharsetList> callback);

	void configureCSVParser(String encoding, HeaderPresence headerPresence,
			char delimiter, char comment,
			AsyncCallback<ArrayList<String>> callback);

	void checkCSV(long errorsLimit, AsyncCallback<CheckCSVSession> callback);

	void startCSVImport(CSVImportSession csvImportSession,
			AsyncCallback<String> callback);

	// Export CSV
	void getAvailableCharsetForExport(AsyncCallback<AvailableCharsetList> callback);
	
	void startCSVExport(CSVExportSession csvExportSession,
			AsyncCallback<String> callback);

	// Export SDMX
	void startSDMXExport(SDMXExportSession exportSession,
			AsyncCallback<String> callback);

	// Export JSON
	void startJSONExport(JSONExportSession jsonExportSession,
			AsyncCallback<String> callback);

	// Table Operation
	void startChangeTableType(ChangeTableTypeSession changeTableTypeSession,
			AsyncCallback<String> callback);

	void startUnion(UnionSession unionSession, AsyncCallback<String> callback);

	// Rows Operation
	void startEditRow(EditRowSession editRowSession,
			AsyncCallback<String> callback);

	void startDeleteRows(DeleteRowsSession deleteRowsSession,
			AsyncCallback<String> callback);

	void startDuplicates(DuplicatesSession duplicatesSession,
			AsyncCallback<String> callback);

	// Column Operation
	void startChangeColumnType(ChangeColumnTypeSession changeColumnTypeSession,
			AsyncCallback<String> callback);

	void startAddColumn(AddColumnSession addColumnSession,
			AsyncCallback<String> callback);

	void startDeleteColumn(DeleteColumnSession deleteColumnSession,
			AsyncCallback<String> callback);

	void startLabelColumn(LabelColumnSession labelColumnSession,
			AsyncCallback<String> callback);
	
	void startChangeColumnsPosition(ChangeColumnsPositionSession changeColumnsPositionSession,
			AsyncCallback<Void> callback);
	
	
	/*
	 * void startSplitColumn(SplitColumnSession
	 * splitColumnSession,AsyncCallback<Void> callback);
	 */
	/*
	 * void startSplitColumn(MergeColumnSession
	 * mergeColumnSession,AsyncCallback<Void> callback);
	 */
	/*
	 * void startGroupBy(GroupBySession groupBySession,AsyncCallback<Void>
	 * callback);
	 */

	// BatchReplace Operation
	void getOccurrencesForBatchReplace(
			OccurrencesForReplaceBatchColumnSession occurrencesForReplaceBatchColumnSession,
			AsyncCallback<ArrayList<Occurrences>> callback);

	void startReplaceBatchColumn(
			ReplaceBatchColumnSession replaceBatchColumnSession,
			AsyncCallback<String> callback);

	// Replace Operation
	void startReplaceColumn(ReplaceColumnSession replaceColumnSession,
			AsyncCallback<String> callback);

	void startReplaceByExternal(
			ReplaceByExternalSession replaceByExternalSession,
			AsyncCallback<String> callback);

	// Templates
	void getTemplates(AsyncCallback<ArrayList<TemplateData>> callback);

	void startTemplateApply(TemplateApplySession templateDeleteSession,
			AsyncCallback<String> callback);

	void templateDelete(TemplateDeleteSession templateDeleteSession,
			AsyncCallback<Void> callback);

	// Locales
	void getLocales(AsyncCallback<ArrayList<String>> callback);

	// Licences
	void getLicences(AsyncCallback<ArrayList<LicenceData>> callback);

	// History
	void getHistory(AsyncCallback<ArrayList<OpHistory>> callback);

	void getHistory(TRId trId, AsyncCallback<ArrayList<OpHistory>> callback);

	void getLastOperationInfo(AsyncCallback<OpHistory> callback);

	void getLastOperationInfo(TRId trId, AsyncCallback<OpHistory> callback);

	// Helper Extract Codelist
	void startExtractCodelist(ExtractCodelistSession extractCodelistSession,
			AsyncCallback<String> callback);

	// Helper Codelist Mapping Import
	void setCodelistMappingSession(
			CodelistMappingSession codelistMappingSession,
			AsyncCallback<Void> callback);

	void getFileFromWorkspace(CodelistMappingSession codelistMappingSession,
			AsyncCallback<Void> callback);

	void startCodelistMappingImport(
			CodelistMappingSession codelistMappingSession,
			AsyncCallback<String> callback);

	// Normalization
	void startNormalization(NormalizationSession normalizationSession,
			AsyncCallback<String> callback);

	// Denormalization
	void startDenormalization(DenormalizationSession denormalizationSession,
			AsyncCallback<String> callback);

	// Operation Monitor
	void getOperationMonitor(OperationMonitorSession operationMonitorSession,
			AsyncCallback<OperationMonitor> callback);

	void getBackgroundOperationMonitor(
			BackgroundOperationMonitorSession backgroundOperationMonitorSession,
			AsyncCallback<ArrayList<BackgroundOperationMonitor>> callback);
	
	void getBackgroundOperationMonitorForSpecificTask(
			OperationMonitorSession operationMonitorSession,
			AsyncCallback<OperationMonitor> callback);
			
	
	// File Upload Monitor
	void getFileUploadMonitor(AsyncCallback<FileUploadMonitor> callback);

	// ResourceTD
	void getResourcesTD(TRId trId,
			AsyncCallback<ArrayList<ResourceTDDescriptor>> callback);

	void getResourcesTDByType(TRId trId, ResourceTDType reourceTDType,
			AsyncCallback<ArrayList<ResourceTDDescriptor>> callback);

	void removeResource(RemoveResourceSession removeResourceSession,
			AsyncCallback<Void> callback);

	void saveResource(SaveResourceSession saveResourceSession,
			AsyncCallback<Void> callback);

	// GIS MAP
	void startMapCreation(MapCreationSession mapCreationSession,
			AsyncCallback<String> callback);

	// Statistical
	void startStatisticalOperation(
			StatisticalOperationSession statisticalOperationSession,
			AsyncCallback<String> callback);

	// Chart
	void startChartTopRating(ChartTopRatingSession chartTopRatingSession,
			AsyncCallback<String> callback);

	// Geospatial
	void startGeospatialCreateCoordinates(
			GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession,
			AsyncCallback<String> callback);

	void startGeospatialDownscaleCSquare(
			GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession,
			AsyncCallback<String> callback);
		
	// Geometry
	void startGeometryCreatePoint(
			GeometryCreatePointSession geometryCreatePointSession,
			AsyncCallback<String> callback);

}
