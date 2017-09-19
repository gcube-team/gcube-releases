/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.server;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.Licence;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ViewColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.DescriptionsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ExportMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GenericMapMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ImportMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.VersionMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Job;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.operation.TaskResult;
import org.gcube.data.analysis.tabulardata.service.rules.RuleId;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStep;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStepId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.AgencyMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.LicenceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.RightsMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.ValidSinceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.ValidUntilMetadata;
import org.gcube.data.analysis.tabulardata.service.template.TemplateId;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTService;
import org.gcube.portlets.user.td.gwtservice.server.encoding.EncodingPGSupported;
import org.gcube.portlets.user.td.gwtservice.server.file.CSVFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.server.file.CodelistMappingFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.server.file.FileUtil;
import org.gcube.portlets.user.td.gwtservice.server.is.ISUtils;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4AddColumn;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4CSVExport;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4CSVImport;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ChangeColumnType;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ChangeColumnsPosition;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ChangeTableType;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ChartTopRating;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4Clone;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4CodelistMapping;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4DataMinerOperation;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4DeleteColumn;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4DeleteRows;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4Denormalization;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4Duplicates;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4EditRow;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ExtractCodelist;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4FilterColumn;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4GeometryCreatePoint;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4GeospatialCreateCoordinates;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4GeospatialDownscaleCSquare;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4GroupBy;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4JSONExport;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4LabelColumn;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4MapCreation;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4MergeColumn;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4Normalization;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ReplaceBatch;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ReplaceByExternal;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ReplaceColumn;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4ReplaceColumnByExpression;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4SDMXCodelistExport;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4SDMXCodelistImport;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4SDMXDatasetExport;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4SDMXTemplateExport;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4SplitColumn;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4TimeAggregation;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecution4Union;
import org.gcube.portlets.user.td.gwtservice.server.opexecution.OpExecutionDirector;
import org.gcube.portlets.user.td.gwtservice.server.resource.ResourceTDCreator;
import org.gcube.portlets.user.td.gwtservice.server.resource.ResourceTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.social.TDMNotifications;
import org.gcube.portlets.user.td.gwtservice.server.storage.FilesStorage;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnDataTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnTypeCodeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ExtractReferences;
import org.gcube.portlets.user.td.gwtservice.server.trservice.JobClassifierMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.LicenceMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.PeriodTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.QueryService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TDTypeValueMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TabularResourceTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TaskStateMap;
import org.gcube.portlets.user.td.gwtservice.server.uriresolver.UriResolverTDClient;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.AvailableCharsetList;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVFileUtil;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVParserConfiguration;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CheckCSVSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFlowException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.gwtservice.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.file.FileUploadState;
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
import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitorCreator;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitorSession;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitorCreator;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitorSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyAndDetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyTableRuleSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachTableRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareRule;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTabResource;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTemplate;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.gwtservice.shared.source.SourceType;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.DataMinerOperationSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.InvocationS;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobS;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;
import org.gcube.portlets.user.td.gwtservice.shared.task.State;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResubmitSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResumeSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskS;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskWrapper;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsTasksMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateApplySession;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateColumnData;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateDeleteSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCodeMap;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
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
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.LabelColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.MergeColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.SplitColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.type.ChangeColumnTypeSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.GroupBySession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.TimeAggregationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRAgencyMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRDescriptionMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRLicenceMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRLocalizedText;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRNameMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRRightsMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRValidSinceMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRValidUntilMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.DenormalizationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.NormalizationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.open.TDOpenSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.CodelistPagingLoadConfig;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.CodelistPagingLoadResult;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.Direction;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.OrderInfo;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.InternalURITD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.RemoveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.StringResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.TableResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DeleteRowsSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DuplicatesSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.EditRowSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.ChangeTableTypeSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.Validations;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabDescriptionsMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabExportMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabGenericMapMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabImportMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabNamesMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabValidationsMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabVersionMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Dataset;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.mime.MimeTypeSupport;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.UIOperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnViewData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.PeriodDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.RelationshipData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ValueDataFormat;
import org.gcube.portlets.user.td.widgetcommonevent.shared.uriresolver.ApplicationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TDGWTServiceImpl extends RemoteServiceServlet implements TDGWTService {
	private static final long serialVersionUID = -5707400086333186368L;
	private static Logger logger = LoggerFactory.getLogger(TDGWTServiceImpl.class);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static SimpleDateFormat sdfPerformance = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	@Override
	public void init() throws ServletException {
		super.init();
		// Handler.activateProtocol();
		// logger.debug("Activated SMP Handler");
		/*
		 * ConfigurableStreamHandlerFactory confStreamHandlerFactory = new
		 * ConfigurableStreamHandlerFactory( "smp", new Handler());
		 * 
		 * URL.setURLStreamHandlerFactory(confStreamHandlerFactory);
		 * logger.debug("Activated SMP Handler");
		 */
		/*
		 * Properties props = System.getProperties();
		 * logger.debug("System Properties: " + props);
		 */

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public UserInfo hello() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(), serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(), serviceCredentials.getScope(), serviceCredentials.getEmail(),
					serviceCredentials.getFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;

		} catch (TDGWTServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setLocale(InfoLocale infoLocale) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			if (infoLocale == null || infoLocale.getLanguage() == null || infoLocale.getLanguage().isEmpty()) {
				infoLocale = new InfoLocale("en");
			}
			SessionUtil.setInfoLocale(httpRequest, serviceCredentials, infoLocale);

		} catch (TDGWTServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("setLocale(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}
	}

	protected ResourceBundle getResourceBundle(HttpServletRequest httpRequest) {
		InfoLocale infoLocale;
		if (httpRequest == null) {
			infoLocale = new InfoLocale("en");
		} else {
			ServiceCredentials serviceCredentials;
			try {
				serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
				infoLocale = SessionUtil.getInfoLocale(httpRequest, serviceCredentials);
			} catch (TDGWTServiceException e) {
				logger.info("No locale found set default!", e);
				infoLocale = new InfoLocale("en");
			}
		}
		Locale locale = new Locale(infoLocale.getLanguage());
		ResourceBundle messages = ResourceBundle.getBundle(TDGWTServiceMessagesConstants.TDGWTServiceMessages, locale);
		return messages;

	}

	@Override
	public String getVREFolderIdByScope() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			FilesStorage filesStorage = new FilesStorage();
			String folderId = filesStorage.getVREFolderIdByScope(serviceCredentials.getUserName(),
					serviceCredentials.getScope());

			return folderId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getValueDataFormats(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving value data formats: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<PeriodDataType> getPeriodDataTypes() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			ArrayList<PeriodDataType> periodDataTypes = new ArrayList<PeriodDataType>();

			for (PeriodType period : PeriodType.values()) {

				ArrayList<ValueDataFormat> valueDataFormats = new ArrayList<ValueDataFormat>();
				List<ValueFormat> listValueFormat = period.getAcceptedFormats();
				for (ValueFormat valueF : listValueFormat) {
					ValueDataFormat valueDataFormat = new ValueDataFormat(valueF.getId(), valueF.getExample(),
							valueF.getRegExpr());
					valueDataFormats.add(valueDataFormat);
				}

				PeriodDataType periodDataType = new PeriodDataType(period.name(), period.getName(), valueDataFormats);
				periodDataTypes.add(periodDataType);

			}
			logger.debug("PeriodDataTypes: " + periodDataTypes);
			return periodDataTypes;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));

		} catch (Throwable e) {
			logger.error("getPeriodDataTypes(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving period types: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<PeriodDataType> getHierarchicalRelationshipForPeriodDataTypes(PeriodDataType periodDataType)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			ArrayList<PeriodDataType> hierarchicalPeriodDataTypes = new ArrayList<PeriodDataType>();

			PeriodType periodType = PeriodTypeMap.map(periodDataType);

			List<PeriodType> hierarchicalList = PeriodType.getHierarchicalRelation().get(periodType);

			for (PeriodType period : hierarchicalList) {
				ArrayList<ValueDataFormat> valueDataFormats = new ArrayList<ValueDataFormat>();
				List<ValueFormat> listValueFormat = period.getAcceptedFormats();
				for (ValueFormat valueF : listValueFormat) {
					ValueDataFormat valueDataFormat = new ValueDataFormat(valueF.getId(), valueF.getExample(),
							valueF.getRegExpr());
					valueDataFormats.add(valueDataFormat);
				}
				PeriodDataType periodDT = new PeriodDataType(period.name(), period.getName(), valueDataFormats);
				hierarchicalPeriodDataTypes.add(periodDT);
			}

			logger.debug("hierarchicalPeriodDataTypes: " + hierarchicalPeriodDataTypes);
			return hierarchicalPeriodDataTypes;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getPeriodDataTypes(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException(
					"Error retrieving hierarchical list of period types: " + e.getLocalizedMessage());
		}
	}

	@Override
	public HashMap<ColumnDataType, ArrayList<ValueDataFormat>> getValueDataFormatsMap() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			HashMap<ColumnDataType, ArrayList<ValueDataFormat>> columnDataTypeFormats = new HashMap<ColumnDataType, ArrayList<ValueDataFormat>>();

			for (ColumnDataType columnDataType : ColumnDataType.values()) {

				ArrayList<ValueDataFormat> valueDataFormats = new ArrayList<ValueDataFormat>();

				Class<? extends DataType> dataType = ColumnDataTypeMap.mapToDataTypeClass(columnDataType);

				for (ValueFormat valueF : DataTypeFormats.getFormatsPerDataType(dataType)) {
					ValueDataFormat valueDataFormat = new ValueDataFormat(valueF.getId(), valueF.getExample(),
							valueF.getRegExpr());
					valueDataFormats.add(valueDataFormat);
				}

				columnDataTypeFormats.put(columnDataType, valueDataFormats);

			}

			logger.debug("getValueDataFormats(): " + columnDataTypeFormats);
			return columnDataTypeFormats;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getValueDataFormats(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving value data formats: " + e.getLocalizedMessage());
		}
	}

	@Override
	public ArrayList<ValueDataFormat> getValueDataFormatsOfColumnDataType(ColumnDataType columnDataType)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			ArrayList<ValueDataFormat> valueDataFormats = new ArrayList<ValueDataFormat>();

			Class<? extends DataType> dataType = ColumnDataTypeMap.mapToDataTypeClass(columnDataType);

			for (ValueFormat valueF : DataTypeFormats.getFormatsPerDataType(dataType)) {
				ValueDataFormat valueDataFormat = new ValueDataFormat(valueF.getId(), valueF.getExample(),
						valueF.getRegExpr());
				valueDataFormats.add(valueDataFormat);
			}

			logger.debug("getValueDataFormatsOfColumnDataType(): [" + columnDataType + ", " + valueDataFormats + "]");
			return valueDataFormats;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getValueDataFormatsOfColumnDataType(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving value data formats: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Integer pendingTasksRetrieve() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("pendingTasksRetrieve()");
			TabularDataService service = TabularDataServiceFactory.getService();

			ArrayList<TabularResource> pendingTR = new ArrayList<TabularResource>();
			List<TabularResource> trs = service.getTabularResources();
			for (TabularResource tr : trs) {
				if (tr.isLocked()) {
					pendingTR.add(tr);
				}
			}

			Integer pendingNumber = 0;

			SessionUtil.removeAllTasksInBackground(httpRequest, serviceCredentials);
			for (TabularResource tr : pendingTR) {
				logger.debug("Pending Task:" + tr.getId());
				List<Task> tasksInitializing = service.getTasks(tr.getId(), TaskStatus.INITIALIZING);
				List<Task> tasksInProgress = service.getTasks(tr.getId(), TaskStatus.IN_PROGRESS);
				List<Task> tasksValidatingRules = service.getTasks(tr.getId(), TaskStatus.VALIDATING_RULES);
				HashMap<String, Task> tasks = new HashMap<String, Task>();
				for (Task t : tasksInitializing) {
					tasks.put(t.getId().getValue(), t);
				}
				for (Task t : tasksInProgress) {
					tasks.put(t.getId().getValue(), t);
				}
				for (Task t : tasksValidatingRules) {
					tasks.put(t.getId().getValue(), t);
				}

				TRId trId = new TRId(String.valueOf(tr.getId().getValue()));
				for (String key : tasks.keySet()) {
					TaskWrapper taskWrapper = new TaskWrapper(tasks.get(key), UIOperationsId.Pending, trId);
					SessionUtil.setTaskInBackground(httpRequest, serviceCredentials, taskWrapper);
					pendingNumber++;
				}
			}

			logger.debug("Pending number: " + pendingNumber);
			return pendingNumber;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("pendingTaskRetrieve(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving pending tasks: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public TRId restoreUISession(TRId startTRId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			if (startTRId == null || startTRId.getId() == null || startTRId.getId().isEmpty()) {

				TRId trId = SessionUtil.getTRId(httpRequest, serviceCredentials);
				logger.debug("restoreUISession()");
				if (trId == null) {
					logger.error("No UI Session");
					return null;
				} else {
					logger.debug("Restore UI Session():" + trId);
					SessionUtil.removeAllFromCurrentTabularResourcesOpen(httpRequest, serviceCredentials);
					TabResource tabResource = SessionUtil.getTabResource(httpRequest, serviceCredentials);
					SessionUtil.addToCurrentTabularResourcesOpen(httpRequest, serviceCredentials, tabResource);
				}
				return trId;
			} else {
				logger.debug("Restore UI Session() request TabularResource:" + startTRId);
				SessionUtil.removeAllFromCurrentTabularResourcesOpen(httpRequest, serviceCredentials);

				TabularDataService service = TabularDataServiceFactory.getService();

				TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(startTRId.getId()));
				TabularResource tabularResource = service.getTabularResource(tabularResourceId);

				checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

				TabResource tabResource = retrieveTRMetadataFromServiceAndLastTable(service, tabularResource, 1);

				if (tabResource.getTrId() == null || tabResource.getTrId().getId() == null
						|| tabResource.getTrId().getId().isEmpty()) {
					return null;
				} else {
					setCurrentTabResource(httpRequest, serviceCredentials, tabResource);
				}

				return tabResource.getTrId();
			}

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("restoreUISession(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in UI Session Restore: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setTabResource(TabResource tabResource) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			setCurrentTabResource(httpRequest, serviceCredentials, tabResource);
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error setting TabResource parameter: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error setting TabResource parameter: " + e.getLocalizedMessage());
		}

	}

	private void setCurrentTabResource(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TabResource tabResource) throws TDGWTServiceException, TDGWTSessionExpiredException {
		if (tabResource == null) {
			logger.error("Error setting TabResource: null");
			throw new TDGWTServiceException("Error setting TabResource: null");
		}

		SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
		SessionUtil.setTRId(httpRequest, serviceCredentials, tabResource.getTrId());
		SessionUtil.addToCurrentTabularResourcesOpen(httpRequest, serviceCredentials, tabResource);
		return;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public TRId getCurrentTRId() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TRId trId = SessionUtil.getTRId(httpRequest, serviceCredentials);
			logger.debug("getCurrentTRId()");
			if (trId == null) {
				logger.error("Current Tabular Resource is null");
				throw new TDGWTServiceException("Current Tabular Resource is null");
			}
			logger.debug("getCurrentTRId():" + trId);

			return trId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getCurrentTRID(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving TR id: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void closeAllTabularResources() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setTabResource(httpRequest, serviceCredentials, null);
			SessionUtil.setTRId(httpRequest, serviceCredentials, null);
			SessionUtil.removeAllFromCurrentTabularResourcesOpen(httpRequest, serviceCredentials);
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getCurrentTRID(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving TR id: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void closeTabularResourceAndOpen(TRId openTRId, TRId closeTRId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("closeTabularResourceAndOpen(): Open[" + openTRId + "], Close[" + closeTRId + "]");
			SessionUtil.removeFromCurrentTabularResourcesOpen(httpRequest, serviceCredentials, closeTRId);

			if (openTRId != null) {
				TabResource tabResource = SessionUtil.getFromCurrentTabularResourcesOpen(httpRequest,
						serviceCredentials, openTRId);
				if (tabResource != null) {
					SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
					SessionUtil.setTRId(httpRequest, serviceCredentials, tabResource.getTrId());
				} else {
					logger.error(
							"Set Active Tabular Resource failed, no valid id!: Open[" + openTRId + ", " + tabResource);
					throw new TDGWTServiceException(
							"Set Active Tabular Resource failed, no valid id!: Open[" + openTRId + ", " + tabResource);
				}
			} else {
				SessionUtil.setTabResource(httpRequest, serviceCredentials, null);
				SessionUtil.setTRId(httpRequest, serviceCredentials, null);
			}
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getCurrentTRID(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving TR id: " + e.getLocalizedMessage());
		}

	}

	@Override
	public void closeTabularResource(TRId closeTRId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("closeTabularResource(): " + closeTRId);

			SessionUtil.removeFromCurrentTabularResourcesOpen(httpRequest, serviceCredentials, closeTRId);

			SessionUtil.setTabResource(httpRequest, serviceCredentials, null);
			SessionUtil.setTRId(httpRequest, serviceCredentials, null);

			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getCurrentTRID(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving TR id: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public TRId setActiveTabularResource(TRId activeTRId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("setActiveTabularResource(): " + activeTRId);
			TabResource tabResource = SessionUtil.getFromCurrentTabularResourcesOpen(httpRequest, serviceCredentials,
					activeTRId);
			if (tabResource != null) {
				TabularDataService service = TabularDataServiceFactory.getService();

				Table table = service.getLastTable(new TabularResourceId(Long.valueOf(tabResource.getTrId().getId())));
				logger.debug("Table retrieved: " + table);
				if (table == null) {
					logger.error("Last Table is Null: " + Long.valueOf(tabResource.getTrId().getId()));
					new TDGWTServiceException("Last table is null");
				}

				Table viewTable = null;
				if (table.contains(DatasetViewTableMetadata.class)) {
					DatasetViewTableMetadata dwm = table.getMetadata(DatasetViewTableMetadata.class);
					logger.debug("DatasetViewTableMetadata: " + dwm);
					try {
						viewTable = service.getTable(dwm.getTargetDatasetViewTableId());
					} catch (Exception e) {
						logger.error("View table not found: " + e.getLocalizedMessage());
					}
				} else {
					logger.debug("Table not contains DataseViewTableMetadata");
				}
				TRId trIdNew = tabResource.getTrId();
				if (viewTable == null) {
					logger.debug("ViewTable is null");
					String tableId = String.valueOf(table.getId().getValue());

					if (tabResource.getTrId().getTableId().compareTo(tableId) == 0) {
						trIdNew.setTableId(tableId);
						trIdNew.setTableType(table.getTableType().getName());
						trIdNew.setViewTable(false);
						tabResource.setTrId(trIdNew);
					} else {
						trIdNew.setTableId(tableId);
						trIdNew.setTableType(table.getTableType().getName());
						trIdNew.setViewTable(false);
						tabResource.setTrId(trIdNew);
						SessionUtil.addToCurrentTabularResourcesOpen(httpRequest, serviceCredentials, tabResource);

					}
				} else {
					String tableId = String.valueOf(viewTable.getId().getValue());
					if (tabResource.getTrId().getTableId().compareTo(tableId) == 0) {
						trIdNew.setTableId(tableId);
						trIdNew.setTableType(viewTable.getTableType().getName());
						trIdNew.setReferenceTargetTableId(String.valueOf(table.getId().getValue()));
						trIdNew.setViewTable(true);
						tabResource.setTrId(trIdNew);
					} else {
						trIdNew.setTableId(tableId);
						trIdNew.setTableType(viewTable.getTableType().getName());
						trIdNew.setReferenceTargetTableId(String.valueOf(table.getId().getValue()));
						trIdNew.setViewTable(true);
						tabResource.setTrId(trIdNew);
						SessionUtil.addToCurrentTabularResourcesOpen(httpRequest, serviceCredentials, tabResource);
					}
				}

				//
				SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
				SessionUtil.setTRId(httpRequest, serviceCredentials, tabResource.getTrId());
				logger.debug("New Active: " + tabResource.getTrId());
			} else {
				logger.error("Set Active Tabular Resource failed, no valid id: " + activeTRId);
				throw new TDGWTServiceException("Set Active Tabular Resource failed, no valid id: ");
			}
			return tabResource.getTrId();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getCurrentTRID(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving TR id: " + e.getLocalizedMessage());
		}

	}

	@Override
	public TabResource getInSessionTabResourceInfo() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TabResource currentTR = SessionUtil.getTabResource(httpRequest, serviceCredentials);

			return currentTR;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error setting TabResource parameter: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error setting TabResource parameter: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public TabResource getTabResourceInformation() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TabResource currentTR = SessionUtil.getTabResource(httpRequest, serviceCredentials);
			if (currentTR == null) {
				logger.error("Current Tabular Resource is null");
				throw new TDGWTServiceException("Current Tabular Resource is null");
			}
			logger.debug("GetTabResourceInformation():" + currentTR.toString());

			if (currentTR.getTrId() == null) {
				logger.error("Current Tabular Resource has TRId null");
				throw new TDGWTServiceException("Current Tabular Resource has TRId null");
			}
			logger.debug("Current TRId: " + currentTR.getTrId());

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource tr = service
					.getTabularResource(new TabularResourceId(Long.valueOf(currentTR.getTrId().getId())));

			ArrayList<TRMetadata> trMetadatas = getTRMetadata(tr);
			updateTabResourceInformation(currentTR, trMetadatas);

			currentTR.setDate(tr.getCreationDate().getTime());
			currentTR.setValid(tr.isValid());
			currentTR.setFinalized(tr.isFinalized());
			currentTR.setLocked(tr.isLocked());
			Contacts owner = new Contacts("", tr.getOwner(), false);
			currentTR.setOwner(owner);
			currentTR.setContacts(retrieveShareInfo(tr));

			SessionUtil.setTabResource(httpRequest, serviceCredentials, currentTR);
			logger.debug("GetTabResourceInformation() updated information:" + currentTR.toString());
			return currentTR;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error setting TabResource parameter: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error setting TabResource parameter: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public TabResource getTabResourceInformation(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			if (trId == null) {
				logger.error("GetTabularREsourceInformation TRId is null");
				throw new TDGWTServiceException("GetTabularREsourceInformation TRId is  null");
			}

			logger.debug("GetTabResourceInformation:" + trId);
			TabResource currentTR = new TabResource();
			currentTR.setTrId(trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource tr = service
					.getTabularResource(new TabularResourceId(Long.valueOf(currentTR.getTrId().getId())));
			logger.debug("GetTabResourceInformation() TR on service: " + tr);

			ArrayList<TRMetadata> trMetadatas = getTRMetadata(tr);
			updateTabResourceInformation(currentTR, trMetadatas);

			currentTR.setDate(tr.getCreationDate().getTime());
			currentTR.setValid(tr.isValid());
			currentTR.setFinalized(tr.isFinalized());
			currentTR.setLocked(tr.isLocked());
			Contacts owner = new Contacts("", tr.getOwner(), false);
			currentTR.setOwner(owner);
			currentTR.setContacts(retrieveShareInfo(tr));

			logger.debug("GetTabResourceInformation() updated information:" + currentTR);
			return currentTR;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error setting TabResource parameter: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error setting TabResource parameter: " + e.getLocalizedMessage());
		}
	}

	private void updateTabResourceInformation(TabResource tabResource, ArrayList<TRMetadata> trMetadatas) {

		for (TRMetadata trMetadata : trMetadatas) {
			if (trMetadata instanceof TRDescriptionMetadata) {
				tabResource.setDescription(((TRDescriptionMetadata) trMetadata).getValue());
			} else {
				if (trMetadata instanceof TRNameMetadata) {
					tabResource.setName(((TRNameMetadata) trMetadata).getValue());
				} else {
					if (trMetadata instanceof TRAgencyMetadata) {
						tabResource.setAgency(((TRAgencyMetadata) trMetadata).getValue());
					} else {
						if (trMetadata instanceof TRRightsMetadata) {
							tabResource.setRight(((TRRightsMetadata) trMetadata).getValue());
						} else {
							if (trMetadata instanceof TRValidSinceMetadata) {
								tabResource.setValidFrom(((TRValidSinceMetadata) trMetadata).getValue());
							} else {
								if (trMetadata instanceof TRValidUntilMetadata) {
									tabResource.setValidUntilTo(((TRValidUntilMetadata) trMetadata).getValue());
								} else {
									if (trMetadata instanceof TRLicenceMetadata) {
										tabResource.setLicence(((TRLicenceMetadata) trMetadata).getValue());
									} else {

									}
								}
							}
						}

					}
				}
			}
		}
	}

	public Long getTimeTableId(PeriodDataType periodDataType) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTimeTable(PeriodTypeMap.map(periodDataType));
			if (table == null || table.getId() == null) {
				throw new TDGWTServiceException("Error retrieving Time Table: " + table);
			}

			return table.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Time Table: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Time Table: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Returns ArrayList of ColumnData other than IdColumnType,
	 * ValidationColumnType and ViewColumn
	 * 
	 * {@inheritDoc}
	 */
	public ArrayList<ColumnData> getColumns() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TabResource currentTR = SessionUtil.getTabResource(httpRequest, serviceCredentials);
			if (currentTR == null) {
				logger.error("Current Tabular Resource is null");
				throw new TDGWTServiceException("Current Tabular Resource is null");
			}
			TRId trId = currentTR.getTrId();

			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getLastTable(new TabularResourceId(Long.valueOf(trId.getId())));

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;

			for (Column c : cols) {
				if (c.getColumnType() instanceof IdColumnType || c.getColumnType() instanceof ValidationColumnType) {

				} else {
					if (c.contains(ViewColumnMetadata.class)) {

					} else {
						ColumnData cData = new ColumnData();
						cData.setId(Integer.toString(i));
						cData.setColumnId(c.getLocalId().getValue());
						cData.setName(c.getName());
						cData.setTypeCode(c.getColumnType().getCode());
						cData.setTypeName(c.getColumnType().getName());
						cData.setDataTypeName(c.getDataType().getName());

						PeriodTypeMetadata periodTypeMetadata = null;
						if (c.contains(PeriodTypeMetadata.class)) {
							periodTypeMetadata = c.getMetadata(PeriodTypeMetadata.class);
							PeriodType periodType = periodTypeMetadata.getType();
							cData.setPeriodDataType(PeriodTypeMap.map(periodType));
						}

						ColumnRelationship rel = c.getRelationship();
						if (rel != null) {
							RelationshipData relData = retrieveRelationship(service, table, c, periodTypeMetadata, rel);
							cData.setRelationship(relData);
						}

						NamesMetadata labelsMetadata = null;
						if (c.contains(NamesMetadata.class)) {
							labelsMetadata = c.getMetadata(NamesMetadata.class);
						}

						if (labelsMetadata == null) {
							cData.setLabel("nolabel");
							logger.debug("LabelsMetadata no labels");
						} else {
							LocalizedText cl = null;
							cl = labelsMetadata.getTextWithLocale("en");
							if (cl == null) {
								cData.setLabel("nolabel");
								logger.debug("ColumnLabel no label in en");
							} else {
								if (cl.getValue() == null || cl.getValue().isEmpty()) {
									cData.setLabel("nolabel");
									logger.debug("ColumnLabel no label in en");
								} else {
									cData.setLabel(cl.getValue());
									logger.debug("Column Set Label: " + cl.getValue());
								}
							}
						}

						DataLocaleMetadata dataLocaleMetadata = null;
						if (c.contains(DataLocaleMetadata.class)) {
							dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
							cData.setLocale(dataLocaleMetadata.getLocale());
						}

						cData.setTrId(trId);
						columns.add(cData);
						i++;
					}
				}
			}

			return columns;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Columns: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Returns ArrayList of ColumnData other than IdColumnType,
	 * ValidationColumnType and ViewColumn
	 * 
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ColumnData> getColumns(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			logger.debug("getColumns():" + trId);

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;
			for (Column c : cols) {
				if (c.getColumnType() instanceof IdColumnType || c.getColumnType() instanceof ValidationColumnType) {

				} else {
					if (c.contains(ViewColumnMetadata.class)) {

					} else {
						ColumnData cData = new ColumnData();
						cData.setId(Integer.toString(i));
						cData.setColumnId(c.getLocalId().getValue());
						cData.setName(c.getName());
						cData.setTypeCode(c.getColumnType().getCode());
						cData.setTypeName(c.getColumnType().getName());
						cData.setDataTypeName(c.getDataType().getName());

						PeriodTypeMetadata periodTypeMetadata = null;
						if (c.contains(PeriodTypeMetadata.class)) {
							periodTypeMetadata = c.getMetadata(PeriodTypeMetadata.class);
							PeriodType periodType = periodTypeMetadata.getType();
							cData.setPeriodDataType(PeriodTypeMap.map(periodType));
						}

						ColumnRelationship rel = c.getRelationship();
						if (rel != null) {
							RelationshipData relData = retrieveRelationship(service, table, c, periodTypeMetadata, rel);
							cData.setRelationship(relData);

						}

						NamesMetadata labelsMetadata = null;
						if (c.contains(NamesMetadata.class)) {
							labelsMetadata = c.getMetadata(NamesMetadata.class);
						}

						if (labelsMetadata == null) {
							cData.setLabel("nolabel");
							logger.debug("LabelsMetadata no labels");
						} else {
							LocalizedText cl = null;
							cl = labelsMetadata.getTextWithLocale("en");
							if (cl == null) {
								cData.setLabel("nolabel");
								logger.debug("ColumnLabel no label in en");
							} else {
								if (cl.getValue() == null || cl.getValue().isEmpty()) {
									cData.setLabel("nolabel");
									logger.debug("ColumnLabel no label in en");
								} else {
									cData.setLabel(cl.getValue());
									logger.debug("Column Set Label: " + cl.getValue());
								}
							}
						}

						DataLocaleMetadata dataLocaleMetadata = null;
						if (c.contains(DataLocaleMetadata.class)) {
							dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
							cData.setLocale(dataLocaleMetadata.getLocale());
						}

						cData.setTrId(trId);
						columns.add(cData);
						i++;

					}
				}
			}

			return columns;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Columns: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Returns ArrayList of ColumnData other than IdColumnType,
	 * ValidationColumnType
	 * 
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ColumnData> getColumnWithViewColumnIncluded(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			logger.debug("getColumns():" + trId.toString());

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;
			for (Column c : cols) {
				if (c.getColumnType() instanceof IdColumnType || c.getColumnType() instanceof ValidationColumnType) {

				} else {
					ColumnData cData = new ColumnData();
					cData.setId(Integer.toString(i));
					cData.setColumnId(c.getLocalId().getValue());
					cData.setName(c.getName());
					cData.setTypeCode(c.getColumnType().getCode());
					cData.setTypeName(c.getColumnType().getName());
					cData.setDataTypeName(c.getDataType().getName());

					PeriodTypeMetadata periodTypeMetadata = null;
					if (c.contains(PeriodTypeMetadata.class)) {
						periodTypeMetadata = c.getMetadata(PeriodTypeMetadata.class);
						PeriodType periodType = periodTypeMetadata.getType();
						cData.setPeriodDataType(PeriodTypeMap.map(periodType));
					}

					ColumnRelationship rel = c.getRelationship();
					if (rel != null) {
						RelationshipData relData = retrieveRelationship(service, table, c, periodTypeMetadata, rel);
						cData.setRelationship(relData);

					}

					NamesMetadata labelsMetadata = null;
					if (c.contains(NamesMetadata.class)) {
						labelsMetadata = c.getMetadata(NamesMetadata.class);
					}

					if (labelsMetadata == null) {
						cData.setLabel("nolabel");
						logger.debug("LabelsMetadata no labels");
					} else {
						LocalizedText cl = null;
						cl = labelsMetadata.getTextWithLocale("en");
						if (cl == null) {
							cData.setLabel("nolabel");
							logger.debug("ColumnLabel no label in en");
						} else {
							if (cl.getValue() == null || cl.getValue().isEmpty()) {
								cData.setLabel("nolabel");
								logger.debug("ColumnLabel no label in en");
							} else {
								cData.setLabel(cl.getValue());
								logger.debug("Column Set Label: " + cl.getValue());
							}
						}
					}

					DataLocaleMetadata dataLocaleMetadata = null;
					if (c.contains(DataLocaleMetadata.class)) {
						dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
						cData.setLocale(dataLocaleMetadata.getLocale());
					}

					if (c.contains(ViewColumnMetadata.class)) {
						ColumnViewData columnViewData = retrieveColumnViewData(service, table, c, periodTypeMetadata);
						cData.setColumnViewData(columnViewData);
						cData.setViewColumn(true);
					} else {
						cData.setViewColumn(false);
					}

					cData.setTrId(trId);
					columns.add(cData);
					i++;

				}

			}

			return columns;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Columns: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Returns ArrayList of ColumnData other than IdColumnType,
	 * ValidationColumnType, DimensionColumnType and TimeDimensionColumnType
	 * 
	 * 
	 * {@inheritDoc}
	 */

	@Override
	public ArrayList<ColumnData> getColumnWithOnlyViewColumnInRel(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			logger.debug("getColumns():" + trId.toString());

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();
			ArrayList<ColumnData> dimensions = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;
			for (Column c : cols) {
				if (c.getColumnType() instanceof IdColumnType || c.getColumnType() instanceof ValidationColumnType) {

				} else {
					ColumnData cData = new ColumnData();
					cData.setId(Integer.toString(i));
					cData.setColumnId(c.getLocalId().getValue());
					cData.setName(c.getName());
					cData.setTypeCode(c.getColumnType().getCode());
					cData.setTypeName(c.getColumnType().getName());
					cData.setDataTypeName(c.getDataType().getName());

					PeriodTypeMetadata periodTypeMetadata = null;
					if (c.contains(PeriodTypeMetadata.class)) {
						periodTypeMetadata = c.getMetadata(PeriodTypeMetadata.class);
						PeriodType periodType = periodTypeMetadata.getType();
						cData.setPeriodDataType(PeriodTypeMap.map(periodType));
					}

					ColumnRelationship rel = c.getRelationship();
					if (rel != null) {
						RelationshipData relData = retrieveRelationship(service, table, c, periodTypeMetadata, rel);
						cData.setRelationship(relData);

					}

					NamesMetadata labelsMetadata = null;
					if (c.contains(NamesMetadata.class)) {
						labelsMetadata = c.getMetadata(NamesMetadata.class);
					}

					if (labelsMetadata == null) {
						cData.setLabel("nolabel");
						logger.debug("LabelsMetadata no labels");
					} else {
						LocalizedText cl = null;
						cl = labelsMetadata.getTextWithLocale("en");
						if (cl == null) {
							cData.setLabel("nolabel");
							logger.debug("ColumnLabel no label in en");
						} else {
							if (cl.getValue() == null || cl.getValue().isEmpty()) {
								cData.setLabel("nolabel");
								logger.debug("ColumnLabel no label in en");
							} else {
								cData.setLabel(cl.getValue());
								logger.debug("Column Set Label: " + cl.getValue());
							}
						}
					}

					DataLocaleMetadata dataLocaleMetadata = null;
					if (c.contains(DataLocaleMetadata.class)) {
						dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
						cData.setLocale(dataLocaleMetadata.getLocale());
					}

					if (c.contains(ViewColumnMetadata.class)) {
						ColumnViewData columnViewData = retrieveColumnViewData(service, table, c, periodTypeMetadata);
						cData.setColumnViewData(columnViewData);
						cData.setViewColumn(true);
					} else {
						cData.setViewColumn(false);
					}

					cData.setTrId(trId);

					if (cData.getTypeCode().compareTo(ColumnTypeCode.DIMENSION.toString()) == 0
							|| cData.getTypeCode().compareTo(ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
						dimensions.add(cData);
					}

					columns.add(cData);
					i++;

				}

			}

			ArrayList<ColumnData> removable = new ArrayList<ColumnData>();
			for (int k = 0; k < columns.size(); k++) {
				ColumnData col = columns.get(k);
				if (col.getColumnId() != null && col.isViewColumn()) {
					ColumnViewData colViewData = col.getColumnViewData();
					String sourceTableDimensionColumnId = colViewData.getSourceTableDimensionColumnId();

					for (int j = 0; j < dimensions.size(); j++) {
						ColumnData dim = dimensions.get(j);
						if (dim.getColumnId().compareTo(sourceTableDimensionColumnId) == 0) {
							RelationshipData rel = dim.getRelationship();
							if (rel != null) {
								String cId = rel.getTargetColumnId();
								if (cId == null || cId.compareTo(col.getColumnId()) != 0) {
									removable.add(col);
								}
							} else {
								removable.add(col);
							}
							break;
						}
					}
				}
			}
			columns.removeAll(removable);
			columns.removeAll(dimensions);
			return columns;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Columns: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Returns ArrayList of ColumnData other than IdColumnType,
	 * ValidationColumnType, DimensionColumnType, TimeDimensionColumnType and
	 * only View Columns related
	 * 
	 * 
	 * {@inheritDoc}
	 */
	public ArrayList<ColumnData> getColumnsForStatistical(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			logger.debug("getColumns():" + trId.toString());

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();
			ArrayList<ColumnData> columnsDimension = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;
			for (Column c : cols) {
				if (c.getColumnType() instanceof IdColumnType || c.getColumnType() instanceof ValidationColumnType) {

				} else {
					ColumnData cData = new ColumnData();
					cData.setId(Integer.toString(i));
					cData.setColumnId(c.getLocalId().getValue());
					cData.setName(c.getName());
					cData.setTypeCode(c.getColumnType().getCode());
					cData.setTypeName(c.getColumnType().getName());
					cData.setDataTypeName(c.getDataType().getName());

					PeriodTypeMetadata periodTypeMetadata = null;
					if (c.contains(PeriodTypeMetadata.class)) {
						periodTypeMetadata = c.getMetadata(PeriodTypeMetadata.class);
						PeriodType periodType = periodTypeMetadata.getType();
						cData.setPeriodDataType(PeriodTypeMap.map(periodType));
					}

					ColumnRelationship rel = c.getRelationship();
					if (rel != null) {
						RelationshipData relData = retrieveRelationship(service, table, c, periodTypeMetadata, rel);
						cData.setRelationship(relData);
					}

					NamesMetadata labelsMetadata = null;
					if (c.contains(NamesMetadata.class)) {
						labelsMetadata = c.getMetadata(NamesMetadata.class);
					}

					if (labelsMetadata == null) {
						cData.setLabel("nolabel");
						logger.debug("LabelsMetadata no labels");
					} else {
						LocalizedText cl = null;
						cl = labelsMetadata.getTextWithLocale("en");
						if (cl == null) {
							cData.setLabel("nolabel");
							logger.debug("ColumnLabel no label in en");
						} else {
							if (cl.getValue() == null || cl.getValue().isEmpty()) {
								cData.setLabel("nolabel");
								logger.debug("ColumnLabel no label in en");
							} else {
								cData.setLabel(cl.getValue());
								logger.debug("Column Set Label: " + cl.getValue());
							}
						}
					}

					DataLocaleMetadata dataLocaleMetadata = null;
					if (c.contains(DataLocaleMetadata.class)) {
						dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
						cData.setLocale(dataLocaleMetadata.getLocale());
					}

					if (c.contains(ViewColumnMetadata.class)) {
						ColumnViewData columnViewData = retrieveColumnViewData(service, table, c, periodTypeMetadata);
						cData.setColumnViewData(columnViewData);
						cData.setViewColumn(true);
					} else {
						cData.setViewColumn(false);
					}

					cData.setTrId(trId);
					if (c.getColumnType() instanceof DimensionColumnType
							|| c.getColumnType() instanceof TimeDimensionColumnType) {
						columnsDimension.add(cData);
					} else {
						columns.add(cData);
					}
					i++;

				}

			}

			for (ColumnData colDimension : columnsDimension) {
				RelationshipData rel = colDimension.getRelationship();

				if (rel != null) {
					String cId = rel.getTargetColumnId();
					if (cId != null) {
						for (int j = 0; j < columns.size(); j++) {
							ColumnData col = columns.get(j);
							if (col.getColumnId() != null && !col.getColumnId().isEmpty() && col.isViewColumn()
									&& col.getColumnId().compareTo(cId) != 0
									&& col.getColumnViewData().getSourceTableDimensionColumnId()
											.compareTo(colDimension.getColumnId()) == 0) {
								columns.remove(col);
								break;
							}
						}
					}
				}
			}

			return columns;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Columns: " + e.getLocalizedMessage());
		}
	}

	private RelationshipData retrieveRelationship(TabularDataService service, Table table, Column c,
			PeriodTypeMetadata periodTypeMetadata, ColumnRelationship rel) throws TDGWTServiceException {
		RelationshipData relData = null;
		if (c.getColumnType().getCode().compareTo(ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
			Table timeTable = service.getTimeTable(periodTypeMetadata.getType());
			if (timeTable == null || timeTable.getId() == null) {
				throw new TDGWTServiceException("Error retrieving Time Table: " + table);
			}
			logger.debug("Time Table Id: " + timeTable.getId());
			Column timeColumn = timeTable.getColumnByName(periodTypeMetadata.getType().getName());
			relData = new RelationshipData(timeTable.getId().getValue(), timeColumn.getLocalId().getValue());

		} else {
			relData = new RelationshipData(rel.getTargetTableId().getValue(), rel.getTargetColumnId().getValue());
		}
		return relData;
	}

	private ColumnViewData retrieveColumnViewData(TabularDataService service, Table table, Column c,
			PeriodTypeMetadata periodTypeMetadata) throws TDGWTServiceException {
		ViewColumnMetadata viewMetadata = c.getMetadata(ViewColumnMetadata.class);
		logger.debug("ViewColumnMetadata: " + viewMetadata.toString());

		ColumnViewData columnViewData = null;
		Column sourceColumn = table.getColumnById(viewMetadata.getSourceTableDimensionColumnId());
		if (sourceColumn.getColumnType().getCode().compareTo(ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {

			PeriodTypeMetadata periodTypeMetadataSourceColumn = null;
			if (sourceColumn.contains(PeriodTypeMetadata.class)) {
				periodTypeMetadataSourceColumn = sourceColumn.getMetadata(PeriodTypeMetadata.class);

				Table timeTable = service.getTimeTable(periodTypeMetadataSourceColumn.getType());

				if (timeTable == null || timeTable.getId() == null) {
					throw new TDGWTServiceException("Error retrieving Time Table: " + table);
				}
				logger.debug("Time Table Id: " + timeTable.getId());

				Column timeColumn = timeTable.getColumnByName(periodTypeMetadataSourceColumn.getType().getName());

				String sourceTableDimensionColumnId = viewMetadata.getSourceTableDimensionColumnId().getValue();

				String targetTableColumnId = timeColumn.getLocalId().getValue();

				long targetTableId = timeTable.getId().getValue();
				columnViewData = new ColumnViewData(sourceTableDimensionColumnId, targetTableColumnId, targetTableId);

			} else {
				logger.error("Error retrieving Time Table for view column:" + c
						+ " , source column do not have a PeriodTypeMetadata: " + sourceColumn);
				throw new TDGWTServiceException(
						"Error retrieving Time Table, source column do not have a PeriodTypeMetadata");
			}

		} else {
			String sourceTableDimensionColumnId = viewMetadata.getSourceTableDimensionColumnId().getValue();

			String targetTableColumnId = viewMetadata.getTargetTableColumnId().getValue();
			long targetTableId = viewMetadata.getTargetTableId().getValue();
			columnViewData = new ColumnViewData(sourceTableDimensionColumnId, targetTableColumnId, targetTableId);
		}
		return columnViewData;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public ColumnData getColumn(String columnLocalId, TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			ColumnData cData = new ColumnData();
			ColumnLocalId columnId = new ColumnLocalId(columnLocalId);
			Column c = table.getColumnById(columnId);
			if (c.getColumnType() instanceof IdColumnType) {

			} else {
				cData.setId(Integer.toString(0));
				cData.setColumnId(c.getLocalId().getValue());
				cData.setName(c.getName());

				PeriodTypeMetadata periodTypeMetadata = null;
				if (c.contains(PeriodTypeMetadata.class)) {
					periodTypeMetadata = c.getMetadata(PeriodTypeMetadata.class);
					PeriodType periodType = periodTypeMetadata.getType();
					cData.setPeriodDataType(PeriodTypeMap.map(periodType));
				}

				if (c.contains(ViewColumnMetadata.class)) {
					ColumnViewData columnViewData = retrieveColumnViewData(service, table, c, periodTypeMetadata);
					cData.setColumnViewData(columnViewData);
					cData.setViewColumn(true);
				} else {
					cData.setViewColumn(false);
				}

				cData.setTypeCode(c.getColumnType().getCode());
				cData.setTypeName(c.getColumnType().getName());
				cData.setDataTypeName(c.getDataType().getName());
				ColumnRelationship rel = c.getRelationship();
				if (rel != null) {
					RelationshipData relData = retrieveRelationship(service, table, c, periodTypeMetadata, rel);
					cData.setRelationship(relData);
				}

				NamesMetadata labelsMetadata = null;
				if (c.contains(NamesMetadata.class)) {
					labelsMetadata = c.getMetadata(NamesMetadata.class);
				}

				if (labelsMetadata == null) {
					cData.setLabel("nolabel");
					logger.debug("LabelsMetadata no labels");
				} else {
					LocalizedText cl = null;
					cl = labelsMetadata.getTextWithLocale("en");
					if (cl == null) {
						cData.setLabel("nolabel");
						logger.debug("ColumnLabel no label in en");
					} else {
						if (cl.getValue() == null || cl.getValue().isEmpty()) {
							cData.setLabel("nolabel");
							logger.debug("ColumnLabel no label in en");
						} else {
							cData.setLabel(cl.getValue());
							logger.debug("Column Set Label: " + cl.getValue());
						}
					}
				}

				DataLocaleMetadata dataLocaleMetadata = null;
				if (c.contains(DataLocaleMetadata.class)) {
					dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
					cData.setLocale(dataLocaleMetadata.getLocale());
				}

				cData.setTrId(trId);

			}

			return cData;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Column: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Column: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public ColumnData getColumn(TRId trId, String columnName) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			ColumnData cData = new ColumnData();

			Column c = table.getColumnByName(columnName);
			if (c.getColumnType() instanceof IdColumnType) {

			} else {
				cData.setId(Integer.toString(0));
				cData.setColumnId(c.getLocalId().getValue());
				cData.setName(c.getName());

				PeriodTypeMetadata periodTypeMetadata = null;
				if (c.contains(PeriodTypeMetadata.class)) {
					periodTypeMetadata = c.getMetadata(PeriodTypeMetadata.class);
					PeriodType periodType = periodTypeMetadata.getType();
					cData.setPeriodDataType(PeriodTypeMap.map(periodType));
				}

				if (c.contains(ViewColumnMetadata.class)) {
					ColumnViewData columnViewData = retrieveColumnViewData(service, table, c, periodTypeMetadata);
					cData.setColumnViewData(columnViewData);
					cData.setViewColumn(true);
				} else {
					cData.setViewColumn(false);
				}
				cData.setTypeCode(c.getColumnType().getCode());
				cData.setTypeName(c.getColumnType().getName());
				cData.setDataTypeName(c.getDataType().getName());
				ColumnRelationship rel = c.getRelationship();
				if (rel != null) {
					RelationshipData relData = retrieveRelationship(service, table, c, periodTypeMetadata, rel);
					cData.setRelationship(relData);
				}
				NamesMetadata labelsMetadata = null;
				if (c.contains(NamesMetadata.class)) {
					labelsMetadata = c.getMetadata(NamesMetadata.class);
				}

				if (labelsMetadata == null) {
					cData.setLabel("nolabel");
					logger.debug("LabelsMetadata no labels");
				} else {
					LocalizedText cl = null;
					cl = labelsMetadata.getTextWithLocale("en");
					if (cl == null) {
						cData.setLabel("nolabel");
						logger.debug("ColumnLabel no label in en");
					} else {
						if (cl.getValue() == null || cl.getValue().isEmpty()) {
							cData.setLabel("nolabel");
							logger.debug("ColumnLabel no label in en");
						} else {
							cData.setLabel(cl.getValue());
							logger.debug("Column Set Label: " + cl.getValue());
						}
					}
				}

				DataLocaleMetadata dataLocaleMetadata = null;
				if (c.contains(DataLocaleMetadata.class)) {
					dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
					cData.setLocale(dataLocaleMetadata.getLocale());
				}

				cData.setTrId(trId);

			}

			return cData;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Column: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Column: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ColumnData> getValidationColumns(String columnId, TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			logger.debug("getValidationColumns():[" + trId.toString() + " columnLocalId: " + columnId + "]");

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));
			logger.debug("getValidationColumns() on Table: " + table.toString());

			ColumnLocalId id = new ColumnLocalId(columnId);

			Column columnSource = table.getColumnById(id);
			if (columnSource == null) {
				logger.error("Column not present on table");
				throw new TDGWTServiceException("Column not present on table");
			}

			logger.debug("getValidationColumns(): columnSource " + columnSource.getLocalId());
			String columnSourceId = columnSource.getLocalId().getValue();

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;
			for (Column c : cols) {
				if (c.getColumnType() instanceof ValidationColumnType) {
					logger.debug("ValidationColumn present: [" + c.getColumnType() + " " + c.getLocalId() + "]");
					if (c.contains(ValidationReferencesMetadata.class)) {
						logger.debug("ValidationReferencesMetadata present");
						ValidationReferencesMetadata validationReferenceMetadata = c
								.getMetadata(ValidationReferencesMetadata.class);
						List<ColumnLocalId> valColumnList = validationReferenceMetadata.getValidationReferenceColumn();
						for (ColumnLocalId columnLocalId : valColumnList) {
							if (columnLocalId.getValue().compareTo(columnSourceId) == 0) {
								ColumnData cData = new ColumnData();
								cData.setId(Integer.toString(i));
								cData.setColumnId(c.getLocalId().getValue());
								cData.setName(c.getName());
								cData.setTypeCode(c.getColumnType().getCode());
								cData.setTypeName(c.getColumnType().getName());
								cData.setDataTypeName(c.getDataType().getName());
								NamesMetadata labelsMetadata = null;
								try {
									labelsMetadata = c.getMetadata(NamesMetadata.class);
								} catch (NoSuchMetadataException e) {
									logger.debug("labelMetadata: NoSuchMetadataException " + e.getLocalizedMessage());
								}

								if (labelsMetadata == null) {
									cData.setLabel("nolabel");
									logger.debug("LabelsMetadata no labels");
								} else {
									LocalizedText cl = null;
									cl = labelsMetadata.getTextWithLocale("en");
									if (cl == null) {
										cData.setLabel("nolabel");
										logger.debug("ColumnLabel no label in en");
									} else {
										cData.setLabel(cl.getValue());
										logger.debug("Column Set Label: " + cl.getValue());
									}
								}
								cData.setTrId(trId);
								cData.setValidationColumn(true);
								ArrayList<String> validatedColumnReferences = new ArrayList<String>();
								for (ColumnLocalId cLocalId : valColumnList) {
									validatedColumnReferences.add(cLocalId.getValue());
								}
								cData.setValidatedColumns(validatedColumnReferences);
								columns.add(cData);
								i++;
								break;
							}
						}

					}
				}

			}
			logger.debug("Validation Column: " + columns.size());
			return columns;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Validation Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Validation Columns: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ArrayList<ColumnData> getValidationColumns(TRId trId, String columnName) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			logger.debug("getValidationColumns():[" + trId.toString() + " columnName: " + columnName + "]");

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));
			logger.debug("getValidationColumns() on Table: " + table.toString());

			Column columnSource = table.getColumnByName(columnName);
			if (columnSource == null) {
				logger.error("Column not present on table");
				throw new TDGWTServiceException("Column not present on table");
			}

			logger.debug("getValidationColumns(): columnSource " + columnSource.getLocalId());
			String columnSourceId = columnSource.getLocalId().getValue();

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;
			for (Column c : cols) {
				if (c.getColumnType() instanceof ValidationColumnType) {
					logger.debug("ValidationColumn present: [" + c.getColumnType() + " " + c.getLocalId() + "]");
					if (c.contains(ValidationReferencesMetadata.class)) {
						logger.debug("ValidationReferencesMetadata present");
						ValidationReferencesMetadata validationReferenceMetadata = c
								.getMetadata(ValidationReferencesMetadata.class);
						List<ColumnLocalId> valColumnList = validationReferenceMetadata.getValidationReferenceColumn();
						for (ColumnLocalId columnLocalId : valColumnList) {
							if (columnLocalId.getValue().compareTo(columnSourceId) == 0) {
								ColumnData cData = new ColumnData();
								cData.setId(Integer.toString(i));
								cData.setColumnId(c.getLocalId().getValue());
								cData.setName(c.getName());
								cData.setTypeCode(c.getColumnType().getCode());
								cData.setTypeName(c.getColumnType().getName());
								cData.setDataTypeName(c.getDataType().getName());
								NamesMetadata labelsMetadata = null;
								try {
									labelsMetadata = c.getMetadata(NamesMetadata.class);
								} catch (NoSuchMetadataException e) {
									logger.debug("labelMetadata: NoSuchMetadataException " + e.getLocalizedMessage());
								}

								if (labelsMetadata == null) {
									cData.setLabel("nolabel");
									logger.debug("LabelsMetadata no labels");
								} else {
									LocalizedText cl = null;
									cl = labelsMetadata.getTextWithLocale("en");
									if (cl == null) {
										cData.setLabel("nolabel");
										logger.debug("ColumnLabel no label in en");
									} else {
										cData.setLabel(cl.getValue());
										logger.debug("Column Set Label: " + cl.getValue());
									}
								}
								cData.setTrId(trId);
								cData.setValidationColumn(true);
								ArrayList<String> validatedColumnReferences = new ArrayList<String>();
								for (ColumnLocalId cLocalId : valColumnList) {
									validatedColumnReferences.add(cLocalId.getValue());
								}
								cData.setValidatedColumns(validatedColumnReferences);
								columns.add(cData);
								i++;
								break;
							}
						}

					}
				}

			}
			logger.debug("Validation Column: " + columns.size());
			return columns;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving Validation Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Validation Columns: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public TableData getLastTable(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getLastTable(): " + trId);
			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getLastTable(new TabularResourceId(Long.valueOf(trId.getId())));
			logger.debug("Table retrieved: " + table);
			if (table == null) {
				logger.error("Last Table is Null: " + Long.valueOf(trId.getId()));
				new TDGWTServiceException("Last Table is Null");
			}

			Table viewTable = null;

			if (table.contains(DatasetViewTableMetadata.class)) {
				DatasetViewTableMetadata dwm = table.getMetadata(DatasetViewTableMetadata.class);
				logger.debug("DatasetViewTableMetadata: " + dwm);
				try {
					viewTable = service.getTable(dwm.getTargetDatasetViewTableId());
				} catch (Exception e) {
					logger.error("View table not found: " + e.getLocalizedMessage());
				}
			} else {
				logger.debug("Table not contains DataseViewTableMetadata");
			}

			TableData tData = new TableData();
			TRId newTRId;

			if (viewTable == null) {
				logger.debug("ViewTable is null");
				newTRId = new TRId(trId.getId());
				newTRId.setTabResourceType(trId.getTabResourceType());
				newTRId.setTableTypeName(trId.getTableTypeName());
				newTRId.setTableId(String.valueOf(table.getId().getValue()));
				newTRId.setTableType(table.getTableType().getName());
				newTRId.setViewTable(false);
				logger.debug("NewTRId: " + newTRId);
				tData.setTrId(newTRId);

				tData.setName(table.getName());
				tData.setTypeName(table.getTableType().getName());
				tData.setTypeCode(table.getTableType().getCode());
				Collection<TableMetadata> cMeta = table.getAllMetadata();

				tData.setMetaData(cMeta.toString());

				ArrayList<ColumnData> lColumnData = new ArrayList<ColumnData>();
				for (Column column : table.getColumns()) {
					ColumnData colData = new ColumnData();
					colData.setColumnId(column.getLocalId().getValue());
					colData.setName(column.getName());
					colData.setTypeName(column.getColumnType().getName());
					colData.setTypeCode(column.getColumnType().getCode());
					colData.setDataTypeName(column.getDataType().getName());
					colData.setTrId(newTRId);

					PeriodTypeMetadata periodTypeMetadata = null;
					if (column.contains(PeriodTypeMetadata.class)) {
						periodTypeMetadata = column.getMetadata(PeriodTypeMetadata.class);
						PeriodType periodType = periodTypeMetadata.getType();
						colData.setPeriodDataType(PeriodTypeMap.map(periodType));
					}

					ColumnRelationship rel = column.getRelationship();
					if (rel != null) {
						RelationshipData relData = retrieveRelationship(service, table, column, periodTypeMetadata,
								rel);
						colData.setRelationship(relData);

					}
					if (column.contains(ViewColumnMetadata.class)) {
						ColumnViewData columnViewData = retrieveColumnViewData(service, table, column,
								periodTypeMetadata);
						colData.setColumnViewData(columnViewData);
						colData.setViewColumn(true);

					} else {
						colData.setViewColumn(false);
					}

					lColumnData.add(colData);
				}

				tData.setListColumnData(lColumnData);

			} else {
				logger.debug("ViewTable is not null");
				newTRId = new TRId(trId.getId());
				newTRId.setTabResourceType(trId.getTabResourceType());
				newTRId.setTableTypeName(trId.getTableTypeName());
				newTRId.setTableId(String.valueOf(viewTable.getId().getValue()));
				newTRId.setTableType(viewTable.getTableType().getName());
				newTRId.setReferenceTargetTableId(String.valueOf(table.getId().getValue()));
				newTRId.setViewTable(true);
				logger.debug("NewTRId: " + newTRId);
				tData.setTrId(newTRId);

				tData.setName(viewTable.getName());
				tData.setTypeName(viewTable.getTableType().getName());
				tData.setTypeCode(viewTable.getTableType().getCode());
				Collection<TableMetadata> cMeta = viewTable.getAllMetadata();

				tData.setMetaData(cMeta.toString());

				ArrayList<ColumnData> lColumnData = new ArrayList<ColumnData>();
				for (Column column : viewTable.getColumns()) {
					ColumnData colData = new ColumnData();
					colData.setColumnId(column.getLocalId().getValue());
					colData.setName(column.getName());
					colData.setTypeName(column.getColumnType().getName());
					colData.setTypeCode(column.getColumnType().getCode());
					colData.setDataTypeName(column.getDataType().getName());
					colData.setTrId(newTRId);

					PeriodTypeMetadata periodTypeMetadata = null;
					if (column.contains(PeriodTypeMetadata.class)) {
						periodTypeMetadata = column.getMetadata(PeriodTypeMetadata.class);
						PeriodType periodType = periodTypeMetadata.getType();
						colData.setPeriodDataType(PeriodTypeMap.map(periodType));
					}

					ColumnRelationship rel = column.getRelationship();
					if (rel != null) {
						RelationshipData relData = retrieveRelationship(service, viewTable, column, periodTypeMetadata,
								rel);
						colData.setRelationship(relData);

					}
					if (column.contains(ViewColumnMetadata.class)) {
						ColumnViewData columnViewData = retrieveColumnViewData(service, viewTable, column,
								periodTypeMetadata);
						colData.setColumnViewData(columnViewData);
						colData.setViewColumn(true);

					} else {
						colData.setViewColumn(false);
					}

					lColumnData.add(colData);
				}

				tData.setListColumnData(lColumnData);

			}

			logger.debug("getLastTable: " + tData);
			return tData;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in getLastTable(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in getLastTable(): " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public TableData getTable(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getTable(): " + trId);
			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			Table viewTable = null;

			if (table.contains(DatasetViewTableMetadata.class)) {
				DatasetViewTableMetadata dwm = table.getMetadata(DatasetViewTableMetadata.class);
				try {
					viewTable = service.getTable(dwm.getTargetDatasetViewTableId());
				} catch (Exception e) {
					logger.error("view table not found");
				}
			}

			TableData tData = new TableData();

			if (viewTable == null) {
				logger.debug("ViewTable is null");
				TRId newTRId = new TRId(trId.getId());
				newTRId.setTabResourceType(trId.getTabResourceType());
				newTRId.setTableTypeName(trId.getTableTypeName());
				newTRId.setTableId(String.valueOf(table.getId().getValue()));
				newTRId.setTableType(table.getTableType().getName());
				newTRId.setViewTable(false);
				logger.debug("NewTRId: " + newTRId);
				tData.setTrId(newTRId);

				tData.setName(table.getName());
				tData.setTypeName(table.getTableType().getName());
				tData.setTypeCode(table.getTableType().getCode());
				Collection<TableMetadata> cMeta = table.getAllMetadata();

				tData.setMetaData(cMeta.toString());

				ArrayList<ColumnData> lColumnData = new ArrayList<ColumnData>();
				for (Column column : table.getColumns()) {
					ColumnData colData = new ColumnData();
					colData.setColumnId(column.getLocalId().getValue());
					colData.setName(column.getName());
					colData.setTypeName(column.getColumnType().getName());
					colData.setTypeCode(column.getColumnType().getCode());
					colData.setDataTypeName(column.getDataType().getName());
					colData.setTrId(newTRId);

					PeriodTypeMetadata periodTypeMetadata = null;
					if (column.contains(PeriodTypeMetadata.class)) {
						periodTypeMetadata = column.getMetadata(PeriodTypeMetadata.class);
						PeriodType periodType = periodTypeMetadata.getType();
						colData.setPeriodDataType(PeriodTypeMap.map(periodType));
					}

					ColumnRelationship rel = column.getRelationship();
					if (rel != null) {
						RelationshipData relData = retrieveRelationship(service, table, column, periodTypeMetadata,
								rel);
						colData.setRelationship(relData);

					}
					if (column.contains(ViewColumnMetadata.class)) {
						ColumnViewData columnViewData = retrieveColumnViewData(service, table, column,
								periodTypeMetadata);
						colData.setColumnViewData(columnViewData);
						colData.setViewColumn(true);

					} else {
						colData.setViewColumn(false);
					}

					lColumnData.add(colData);
				}

				tData.setListColumnData(lColumnData);

			} else {
				logger.debug("ViewTable is not null");
				TRId newTRId = new TRId(trId.getId());
				newTRId.setTabResourceType(trId.getTabResourceType());
				newTRId.setTableTypeName(trId.getTableTypeName());
				newTRId.setTableId(String.valueOf(viewTable.getId().getValue()));
				newTRId.setTableType(viewTable.getTableType().getName());
				newTRId.setReferenceTargetTableId(String.valueOf(table.getId().getValue()));
				newTRId.setViewTable(true);
				logger.debug("NewTRId: " + newTRId);
				tData.setTrId(newTRId);

				tData.setName(viewTable.getName());
				tData.setTypeName(viewTable.getTableType().getName());
				tData.setTypeCode(viewTable.getTableType().getCode());
				Collection<TableMetadata> cMeta = viewTable.getAllMetadata();

				tData.setMetaData(cMeta.toString());

				ArrayList<ColumnData> lColumnData = new ArrayList<ColumnData>();
				for (Column column : viewTable.getColumns()) {
					ColumnData colData = new ColumnData();
					colData.setColumnId(column.getLocalId().getValue());
					colData.setName(column.getName());
					colData.setTypeName(column.getColumnType().getName());
					colData.setTypeCode(column.getColumnType().getCode());
					colData.setDataTypeName(column.getDataType().getName());
					colData.setTrId(newTRId);

					PeriodTypeMetadata periodTypeMetadata = null;
					if (column.contains(PeriodTypeMetadata.class)) {
						periodTypeMetadata = column.getMetadata(PeriodTypeMetadata.class);
						PeriodType periodType = periodTypeMetadata.getType();
						colData.setPeriodDataType(PeriodTypeMap.map(periodType));
					}

					ColumnRelationship rel = column.getRelationship();
					if (rel != null) {
						RelationshipData relData = retrieveRelationship(service, viewTable, column, periodTypeMetadata,
								rel);
						colData.setRelationship(relData);
					}

					if (column.contains(ViewColumnMetadata.class)) {
						ColumnViewData columnViewData = retrieveColumnViewData(service, viewTable, column,
								periodTypeMetadata);
						colData.setColumnViewData(columnViewData);
						colData.setViewColumn(true);

					} else {
						colData.setViewColumn(false);
					}

					lColumnData.add(colData);
				}

				tData.setListColumnData(lColumnData);

			}

			logger.debug("getTable: " + tData);
			return tData;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in getTable(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in getTable(): " + e.getLocalizedMessage());
		}
	}

	/**
	 * Not used now, but it will be necessary in the future to retrieve task
	 * pending
	 * 
	 * {@inheritDoc}
	 */
	public void tdOpen(TDOpenSession s) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setTDOpenSession(httpRequest, serviceCredentials, s);
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error setting TDOpenSession parameter: " + e.getLocalizedMessage());
			throw new TDGWTServiceException("Error setting TDOpenSession parameter: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * 
	 * @param tr
	 * @param tabResource
	 */
	private void syncTRMetaData(TabularResource tr, TabResource tabResource) {

		logger.debug("TRservice [id:" + tr.getId() + " ,creationDate:" + tr.getCreationDate() + "]");
		logger.debug("TabResource [name:" + tabResource.getName() + " ,description:" + tabResource.getDescription()
				+ " ,agency:" + tabResource.getAgency() + " ,rights:" + tabResource.getRight() + "]");
		tabResource.setDate(tr.getCreationDate().getTime());
		tabResource.setValid(tr.isValid());
		tabResource.setFinalized(tr.isFinalized());
		tabResource.setLocked(tr.isLocked());
		ArrayList<TabularResourceMetadata<?>> meta = new ArrayList<TabularResourceMetadata<?>>();

		meta.add(new NameMetadata(tabResource.getName()));
		meta.add(new DescriptionMetadata(tabResource.getDescription()));

		if (tabResource.getAgency() != null && !tabResource.getAgency().isEmpty()) {
			meta.add(new AgencyMetadata(tabResource.getAgency()));
		}
		meta.add(new RightsMetadata(tabResource.getRight()));

		if (tabResource.getValidFrom() != null) {
			try {
				Date dateF = tabResource.getValidFrom();
				GregorianCalendar validFromC = new GregorianCalendar();
				validFromC.setTime(dateF);
				ValidSinceMetadata validSince = new ValidSinceMetadata();
				validSince.setValue(validFromC);
				meta.add(validSince);
			} catch (Throwable e) {
				logger.info("ValidFromMetadata is not set, no valid calendar present");
			}
		}

		if (tabResource.getValidUntilTo() != null) {
			try {
				Date dateU = tabResource.getValidUntilTo();
				GregorianCalendar validUntilToC = new GregorianCalendar();
				validUntilToC.setTime(dateU);
				ValidUntilMetadata validUntil = new ValidUntilMetadata(validUntilToC);
				meta.add(validUntil);
			} catch (Throwable e) {
				logger.info("ValidUntilMetadata is not set, no valid calendar present");
			}
		}

		if (tabResource.getLicence() != null && !tabResource.getLicence().isEmpty()) {
			LicenceMetadata licenceMetadata = new LicenceMetadata();
			Licence licence = LicenceMap.map(tabResource.getLicence());
			if (licence != null) {
				licenceMetadata.setValue(licence);
				meta.add(licenceMetadata);
			} else {
				logger.error("Licence type not found: " + tabResource.getLicence());
			}

		}

		tr.setAllMetadata(meta);

	}

	/**
	 * 
	 * @param service
	 * @param tr
	 * @param i
	 * @return
	 * @throws TDGWTServiceException
	 */
	private TabResource retrieveTRMetadataFromService(TabularDataService service, TabularResource tr, int i)
			throws TDGWTServiceException {
		try {
			TabResource t = new TabResource();
			TRId trId = new TRId(String.valueOf(tr.getId().getValue()));
			trId.setTabResourceType(TabularResourceTypeMap.map(tr.getTabularResourceType()));
			trId.setTableTypeName(tr.getTableType());
			t.setId(String.valueOf(i));
			t.setTrId(trId);
			t.setValid(tr.isValid());
			t.setFinalized(tr.isFinalized());
			t.setLocked(tr.isLocked());
			Contacts owner = new Contacts("", tr.getOwner(), false);
			t.setOwner(owner);

			if (tr.contains(NameMetadata.class)) {
				NameMetadata nameMeta = tr.getMetadata(NameMetadata.class);
				if (nameMeta != null && nameMeta.getValue() != null) {
					t.setName(nameMeta.getValue());
				} else {
					t.setName("Unknown_" + trId.getId());
				}
			} else {
				t.setName("Unknown_" + trId.getId());
			}
			if (tr.contains(AgencyMetadata.class)) {
				AgencyMetadata agencyMeta = tr.getMetadata(AgencyMetadata.class);
				if (agencyMeta != null && agencyMeta.getValue() != null) {
					t.setAgency(agencyMeta.getValue());
				} else {
					t.setAgency("");
				}
			} else {
				t.setAgency("");
			}
			t.setDate(tr.getCreationDate().getTime());

			if (tr.contains(ValidSinceMetadata.class)) {
				ValidSinceMetadata validSinceMeta = tr.getMetadata(ValidSinceMetadata.class);
				if (validSinceMeta != null && validSinceMeta.getValue() != null) {
					Calendar validSinceC = validSinceMeta.getValue();
					t.setValidFrom(validSinceC.getTime());
				} else {

				}
			} else {

			}

			if (tr.contains(ValidUntilMetadata.class)) {
				ValidUntilMetadata validUntilMeta = tr.getMetadata(ValidUntilMetadata.class);
				if (validUntilMeta != null && validUntilMeta.getValue() != null) {
					Calendar validUntilC = validUntilMeta.getValue();
					t.setValidUntilTo(validUntilC.getTime());
				} else {

				}
			} else {

			}

			if (tr.contains(LicenceMetadata.class)) {
				LicenceMetadata licenceMeta = tr.getMetadata(LicenceMetadata.class);
				if (licenceMeta != null && licenceMeta.getValue() != null) {
					t.setLicence(licenceMeta.getValue().toString());
				} else {

				}
			} else {

			}

			// logger.debug("TabResource: "+t);
			return t;

		} catch (Throwable e) {
			logger.error("Error retrieving tabular resources metadata in retrieveTRMetadataFromService(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error retrieving tabular resources metadata in retrieveTRMetadataFromService() on server");
		}
	}

	/**
	 * Retrieves the informations for a specific Tabular Resource and the Last
	 * Table from service
	 * 
	 * 
	 * @param service
	 * @param tr
	 * @param i
	 * @return
	 * @throws TDGWTServiceException
	 */
	private TabResource retrieveTRMetadataFromServiceAndLastTable(TabularDataService service, TabularResource tr, int i)
			throws TDGWTServiceException {
		Table table = null;
		try {
			table = service.getLastTable(tr.getId());
		} catch (Throwable e) {
			logger.error("Error retrieving last table: " + e.getMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving last table: " + e.getMessage());
		}

		TableId tableId = null;
		Table viewTable = null;
		TRId trId;
		TabResource t;

		if (table == null) {
			logger.info("Tabular resource " + tr.getId() + " has no table.");
			t = new TabResource();
			t.setTrId(null);
		} else {
			tableId = table.getId();
			if (tableId == null) {
				logger.info("Tabular Resource " + tr.getId() + " has last table with id null.");
				t = new TabResource();
				t.setTrId(null);
			} else {
				if (table.contains(DatasetViewTableMetadata.class)) {
					DatasetViewTableMetadata dwm = table.getMetadata(DatasetViewTableMetadata.class);
					try {
						viewTable = service.getTable(dwm.getTargetDatasetViewTableId());
					} catch (Exception e) {
						logger.error("view table not found");
					}
				}

				if (viewTable == null) {
					trId = new TRId(String.valueOf(tr.getId().getValue()),
							TabularResourceTypeMap.map(tr.getTabularResourceType()), tr.getTableType(),
							String.valueOf(tableId.getValue()), table.getTableType().getName());
				} else {
					trId = new TRId(String.valueOf(tr.getId().getValue()),
							TabularResourceTypeMap.map(tr.getTabularResourceType()), tr.getTableType(),
							String.valueOf(viewTable.getId().getValue()), viewTable.getTableType().getName(),
							String.valueOf(tableId.getValue()), true);

				}
				t = getTabResourceInformation(trId);

			}

		}

		t.setId(String.valueOf(i));

		return t;
	}

	/**
	 * Retrieves codelists without table id (fast)
	 * 
	 * {@inheritDoc}
	 */
	public void setCodelistsPagingLoader() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			CodelistTableType codType = new CodelistTableType();

			List<TabularResource> trs = service.getTabularResourcesByType(codType.getName());
			SessionUtil.setTabularResources(httpRequest, serviceCredentials, trs);

			ArrayList<TabResource> ltr = new ArrayList<TabResource>();

			int i;
			for (i = 0; i < trs.size(); i++) {
				TabularResource tr = trs.get(i);
				try {
					TabResource t = retrieveTRMetadataFromService(service, tr, i);
					if (t.getTrId() != null && t.isValid() && t.isFinalized()) {
						ltr.add(t);

					}
				} catch (Throwable e) {
					logger.error("TabResource discarded: " + tr + " cause: " + e.getMessage());
				}

			}

			logger.debug("Codelists retrived: " + ltr);
			SessionUtil.setCodelistsPagingLoaded(httpRequest, serviceCredentials, ltr);

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Error retrieving Codelist in setCodelistsPagingLoader(): " + e.getLocalizedMessage());
			throw new TDGWTServiceException("Error retrieving Codelist: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public CodelistPagingLoadResult getCodelistsPagingLoader(CodelistPagingLoadConfig plc)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug(plc.toString());

			ArrayList<TabResource> ltr = SessionUtil.getCodelistsPagingLoaded(httpRequest, serviceCredentials);

			if (ltr == null) {
				logger.error("Error no codelist present in session");
				throw new TDGWTServiceException("Error no codelist present in session");
			}

			ArrayList<TabResource> ltrTemp = new ArrayList<TabResource>();

			if (ltr.size() != 0) {
				ArrayList<TabResource> ltrCopy = filterPagingRow(plc, ltr, httpRequest, serviceCredentials);
				ltrCopy = OrderPagingRow(plc, ltrCopy, httpRequest, serviceCredentials);
				if (ltrCopy.size() != 0) {
					if (plc.getOffset() < 0 || plc.getOffset() >= ltrCopy.size()) {
						logger.error(
								"Error CodelistPagingLoadConfig no valid range request, listsize: " + ltrCopy.size());
						throw new TDGWTServiceException("Error CodelistPagingLoadConfig no valid range request");
					}

					int request_end = plc.getOffset() + plc.getLimit();
					if (request_end > ltrCopy.size()) {
						request_end = ltrCopy.size();
					}

					for (int i = plc.getOffset(); i < request_end; i++) {
						try {
							// ltrCopy.get(i);
							ltrTemp.add(ltrCopy.get(i));

						} catch (IndexOutOfBoundsException e) {
							logger.debug("OutOfBounds in getCodelistsPagingLoader() size:" + ltrCopy.size() + " index: "
									+ i + " Error:" + e.getMessage() + "\n");
						}
					}
				}

			}

			CodelistPagingLoadResult codelistPagingLoadResult = new CodelistPagingLoadResult();
			codelistPagingLoadResult.setFilter(plc.getFilter());
			codelistPagingLoadResult.setListOrderInfo(plc.getListOrderInfo());
			codelistPagingLoadResult.setLimit(plc.getLimit());
			codelistPagingLoadResult.setOffset(plc.getOffset());
			codelistPagingLoadResult.setTotalLenght(ltrTemp.size());
			codelistPagingLoadResult.setLtr(ltrTemp);

			logger.debug("Codelists retrieved: " + ltrTemp);
			return codelistPagingLoadResult;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Error retrieving Codelist in getCodelistsPagingLoader(): " + e.getLocalizedMessage());
			throw new TDGWTServiceException("Error retrieving Codelist: " + e.getLocalizedMessage());
		}

	}

	private ArrayList<TabResource> filterPagingRow(CodelistPagingLoadConfig plc, ArrayList<TabResource> ltr,
			HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) throws TDGWTServiceException {

		String filter = SessionUtil.getCodelistsPagingLoadedFilter(httpRequest, serviceCredentials);
		ArrayList<TabResource> ltrCopy = new ArrayList<TabResource>();
		SessionUtil.setCodelistsPagingLoadedFilter(httpRequest, serviceCredentials, plc.getFilter());
		if (filter == null) {
			logger.debug("No filter filter present in session");
			logger.debug("New Filter: " + plc.getFilter());
			if (plc.getFilter() == null || plc.getFilter().isEmpty()) {

				SessionUtil.setCodelistsPagingLoadedFilteredCopy(httpRequest, serviceCredentials, ltr);
				return ltr;
			} else {
				for (TabResource tr : ltr) {
					if (tr.getName() != null && !tr.getName().isEmpty() && tr.getName().contains(plc.getFilter())) {
						ltrCopy.add(tr);
						continue;
					} else {
						if (tr.getAgency() != null && !tr.getAgency().isEmpty()
								&& tr.getAgency().contains(plc.getFilter())) {
							ltrCopy.add(tr);
							continue;
						} else {

						}

					}
				}
				SessionUtil.setCodelistsPagingLoadedFilteredCopy(httpRequest, serviceCredentials, ltrCopy);
				return ltrCopy;
			}

		} else {

			logger.debug("Old Filter:" + filter);
			logger.debug("New Filter: " + plc.getFilter());

			if (plc.getFilter() == null || plc.getFilter().isEmpty()) {
				SessionUtil.setCodelistsPagingLoadedFilteredCopy(httpRequest, serviceCredentials, ltr);
				return ltr;
			} else {
				if (filter.compareTo(plc.getFilter()) == 0) {
					ArrayList<TabResource> ltrFilteredCopy = SessionUtil
							.getCodelistsPagingLoadedFilteredCopy(httpRequest, serviceCredentials);
					return ltrFilteredCopy;
				} else {
					for (TabResource tr : ltr) {
						if (tr.getName() != null && !tr.getName().isEmpty() && tr.getName().contains(plc.getFilter())) {
							ltrCopy.add(tr);
							continue;
						} else {
							if (tr.getAgency() != null && !tr.getAgency().isEmpty()
									&& tr.getAgency().contains(plc.getFilter())) {
								ltrCopy.add(tr);
								continue;
							} else {

							}

						}
					}
					SessionUtil.setCodelistsPagingLoadedFilteredCopy(httpRequest, serviceCredentials, ltrCopy);
					return ltrCopy;
				}
			}

		}

	}

	private ArrayList<TabResource> OrderPagingRow(CodelistPagingLoadConfig plc, ArrayList<TabResource> ltrCopy,
			HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) {

		ArrayList<OrderInfo> orders = plc.getListOrderInfo();
		for (OrderInfo order : orders) {
			if (order.getField().compareTo("name") == 0) {
				Direction direction = order.getDirection();
				if (direction == Direction.ASC) {
					Collections.sort(ltrCopy, new Comparator<TabResource>() {
						@Override
						public int compare(TabResource tr1, TabResource tr2) {
							int comp = 0;
							if (tr1.getName() == null) {
								comp = -1;
							} else {
								if (tr2.getName() == null) {
									comp = 1;
								} else {
									comp = tr1.getName().compareTo(tr2.getName());
								}
							}
							return comp;
						}
					});
				} else {
					Collections.sort(ltrCopy, new Comparator<TabResource>() {
						@Override
						public int compare(TabResource tr1, TabResource tr2) {
							int comp = 0;
							if (tr1.getName() == null) {
								comp = -1;
							} else {
								if (tr2.getName() == null) {
									comp = 1;
								} else {
									comp = tr1.getName().compareTo(tr2.getName());
								}
							}
							return -comp;
						}
					});
				}

				logger.debug("LTR Ordered by name;");

			} else {
				if (order.getField().compareTo("agency") == 0) {
					Direction direction = order.getDirection();
					if (direction == Direction.ASC) {
						Collections.sort(ltrCopy, new Comparator<TabResource>() {
							@Override
							public int compare(TabResource tr1, TabResource tr2) {
								int comp = 0;
								if (tr1.getAgency() == null) {
									comp = -1;
								} else {
									if (tr2.getAgency() == null) {
										comp = 1;
									} else {
										comp = tr1.getAgency().compareTo(tr2.getAgency());
									}
								}
								return comp;
							}
						});
					} else {
						Collections.sort(ltrCopy, new Comparator<TabResource>() {
							@Override
							public int compare(TabResource tr1, TabResource tr2) {
								int comp = 0;
								if (tr1.getAgency() == null) {
									comp = -1;
								} else {
									if (tr2.getAgency() == null) {
										comp = 1;
									} else {
										comp = tr1.getAgency().compareTo(tr2.getAgency());
									}
								}
								return -comp;
							}
						});
					}
					logger.debug("LTR Ordered by agency;");
				} else {
					if (order.getField().compareTo("date") == 0) {
						Direction direction = order.getDirection();
						if (direction == Direction.ASC) {
							Collections.sort(ltrCopy, new Comparator<TabResource>() {
								@Override
								public int compare(TabResource tr1, TabResource tr2) {
									int comp = 0;
									if (tr1.getDate() == null) {
										comp = -1;
									} else {
										if (tr2.getDate() == null) {
											comp = 1;
										} else {
											comp = tr1.getDate().compareTo(tr2.getDate());
										}
									}

									return comp;
								}
							});
						} else {
							Collections.sort(ltrCopy, new Comparator<TabResource>() {
								@Override
								public int compare(TabResource tr1, TabResource tr2) {
									int comp = 0;
									if (tr1.getDate() == null) {
										comp = -1;
									} else {
										if (tr2.getDate() == null) {
											comp = 1;
										} else {
											comp = tr1.getDate().compareTo(tr2.getDate());
										}
									}

									return -comp;
								}
							});
						}
						logger.debug("LTR Ordered by date;");
					} else {
					}
				}

			}

		}
		return ltrCopy;

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<TabResource> getTabularResourcesAndLastTables() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTabularResources");
			TabularDataService service = TabularDataServiceFactory.getService();

			List<TabularResource> trs = service.getTabularResources();
			SessionUtil.setTabularResources(httpRequest, serviceCredentials, trs);

			ArrayList<TabResource> ltr = new ArrayList<TabResource>();

			int i;
			for (i = 0; i < trs.size(); i++) {
				TabularResource tr = trs.get(i);
				logger.debug("GetTabularResources RetrieveMetadataAndLastTables");
				try {
					TabResource t = retrieveTRMetadataFromServiceAndLastTable(service, tr, i);

					if (t.getTrId() != null) {
						ltr.add(t);
					}
				} catch (Throwable e) {
					logger.error("TabResource discarded: " + tr + " cause: " + e.getMessage());
				}
			}

			logger.debug("Tabular Resources retrived: " + ltr);
			return ltr;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Error retrieving TabularResources: " + e.getLocalizedMessage());
			throw new TDGWTServiceException("Error retrieving TabularResources: " + e.getLocalizedMessage());
		}

	}

	/**
	 * Retrieves tabular resource without table id (fast)
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<TabResource> getTabularResources() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTabularResources");
			TabularDataService service = TabularDataServiceFactory.getService();

			List<TabularResource> trs = service.getTabularResources();
			SessionUtil.setTabularResources(httpRequest, serviceCredentials, trs);

			ArrayList<TabResource> ltr = new ArrayList<TabResource>();

			int i;
			for (i = 0; i < trs.size(); i++) {
				TabularResource tr = trs.get(i);
				// logger.debug("GetTabularResources RetrieveMetadata");
				try {
					TabResource t = retrieveTRMetadataFromService(service, tr, i);

					if (t.getTrId() != null) {
						ltr.add(t);
					}
				} catch (Throwable e) {
					logger.error("TabResource discarded: " + tr + " cause: " + e.getMessage());
				}
			}

			logger.debug("Tabular Resources retrieved: " + ltr);
			return ltr;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Error retrieving TabularResources: " + e.getLocalizedMessage());
			throw new TDGWTServiceException("Error retrieving TabularResources: " + e.getLocalizedMessage());
		}

	}

	@Override
	public void removeTabularResource(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			if (trId == null) {
				logger.error("Error removing TabularResource: trId is null");
				throw new TDGWTServiceException("Error removing TabularResource no parameters set");
			}
			TabularDataService service = TabularDataServiceFactory.getService();
			checkTRId(trId, httpRequest, serviceCredentials);
			TabularResourceId tabResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabResourceId);

			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			String owner = tabularResource.getOwner();
			if (owner != null && owner.compareTo(serviceCredentials.getUserName()) == 0) {
				service.removeTabularResource(tabResourceId);
				SessionUtil.removeTaskInBackgroundOnTRId(httpRequest, serviceCredentials, trId);
			} else {
				throw new TDGWTServiceException(
						"You are not the owner of this tabular resource (owner: " + owner + ")");
			}
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error deleting the tabular resource: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error deleting the tabular resource: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public TabResource createTabularResource(TabResource tabResource) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			if (tabResource == null) {
				logger.error("Error creating new TabularResource: tabResource is null");
				throw new TDGWTServiceException("Error creating new TabularResource no parameters set");
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource serviceTR = service.createTabularResource();
			Table table = service.getLastTable(serviceTR.getId());
			syncTRMetaData(serviceTR, tabResource);

			TRId trId = new TRId(String.valueOf(serviceTR.getId().getValue()),
					TabularResourceTypeMap.map(serviceTR.getTabularResourceType()), serviceTR.getTableType(),
					String.valueOf(table.getId().getValue()), table.getTableType().getName());
			tabResource.setTrId(trId);

			return tabResource;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error creating new TabularResource: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error creating new TabularResource: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Codelist> getCodelists() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			// Remove this for multiple source
			SDMXRegistrySource sdmxRegistrySource = new SDMXRegistrySource();
			SessionUtil.setSDMXRegistrySource(httpRequest, serviceCredentials, sdmxRegistrySource);
			//

			return SessionUtil.retrieveCodelists(httpRequest, serviceCredentials);

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving codelists: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Dataset> getDatasets() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			return SessionUtil.retrieveDatasets(httpRequest, serviceCredentials);

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving datasets: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Agencies> getAgencies() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			return SessionUtil.retrieveAgencies(httpRequest, serviceCredentials);

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving datasets: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String startSDMXImport(SDMXImportSession sdmxImportSession) throws TDGWTServiceException {

		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setSDMXImportSession(httpRequest, serviceCredentials, sdmxImportSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource serviceTR = service.createTabularResource();

			TabResource sdmxImportTabResource = sdmxImportSession.getTabResource();

			syncTRMetaData(serviceTR, sdmxImportTabResource);

			TRId trId = new TRId(String.valueOf(serviceTR.getId().getValue()));
			sdmxImportTabResource.setTrId(trId);
			logger.debug(sdmxImportTabResource.toString());

			SessionUtil.setSDMXImportTabResource(httpRequest, serviceCredentials, sdmxImportTabResource);

			OpExecution4SDMXCodelistImport opEx = new OpExecution4SDMXCodelistImport(service, sdmxImportSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error SDMX Codelist Import: Operation not supported!");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, serviceTR.getId());
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.SDMXImport, trId);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in SDMX Import: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Retrieve and set Tabular Resource Type
	 * 
	 * @param trId
	 * @return
	 * @throws TDGWTServiceException
	 */
	private TRId retrieveTabularResourceBasicData(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();
			TabularResourceId tabularResourceId = new TabularResourceId(new Long(trId.getId()));

			TabularResource tr = service.getTabularResource(tabularResourceId);
			Table table = service.getLastTable(tabularResourceId);

			Table viewTable = null;

			if (table.contains(DatasetViewTableMetadata.class)) {
				DatasetViewTableMetadata dwm = table.getMetadata(DatasetViewTableMetadata.class);
				try {
					viewTable = service.getTable(dwm.getTargetDatasetViewTableId());
				} catch (Exception e) {
					logger.error("view table not found");
				}
			}

			TRId newTRId;
			if (viewTable == null) {
				newTRId = new TRId(String.valueOf(tr.getId().getValue()),
						TabularResourceTypeMap.map(tr.getTabularResourceType()), tr.getTableType(),
						String.valueOf(table.getId().getValue()), table.getTableType().getName());

			} else {
				newTRId = new TRId(String.valueOf(tr.getId().getValue()),
						TabularResourceTypeMap.map(tr.getTabularResourceType()), tr.getTableType(),
						String.valueOf(viewTable.getId().getValue()), viewTable.getTableType().getName(),
						String.valueOf(table.getId().getValue()), true);

			}

			logger.debug("Retrieved TRId basic info:" + newTRId.toString());
			return newTRId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error on Service: " + e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSDMXRegistrySource(SDMXRegistrySource sdmxRegistrySource) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setSDMXRegistrySource(httpRequest, serviceCredentials, sdmxRegistrySource);
			return;

		} catch (TDGWTServiceException e) {
			throw e;

		} catch (Throwable e) {
			logger.error("Error setting SDMXRegistrySource parameter: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error setting SDMXRegistrySource parameter: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void setCSVSession(CSVImportSession importSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setCSVImportSession(httpRequest, serviceCredentials, importSession);
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error setting SDMXImportSession parameter: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error setting SDMXImportSession parameter: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileUploadMonitor getFileUploadMonitor() throws TDGWTServiceException {

		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

		if (httpRequest == null) {
			throw new TDGWTServiceException("Error retrieving the request: null");
		}

		FileUploadMonitor fileUploadMonitor = SessionUtil.getFileUploadMonitor(httpRequest, serviceCredentials);
		if (fileUploadMonitor == null) {
			throw new TDGWTServiceException("Error retrieving the fileUploadMonitor: null");
		}

		logger.debug("FileUploadMonitor: " + fileUploadMonitor);

		return fileUploadMonitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AvailableCharsetList getAvailableCharset() throws TDGWTServiceException {
		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		SessionUtil.getServiceCredentials(httpRequest);

		if (httpRequest == null) {
			throw new TDGWTServiceException("Error retrieving the httpRequest: null");
		}

		String defaultEncoding = Charset.defaultCharset().displayName();
		ArrayList<String> charsetList = new ArrayList<String>(Charset.availableCharsets().keySet());

		return new AvailableCharsetList(charsetList, defaultEncoding);
	}

	@Override
	public AvailableCharsetList getAvailableCharsetForExport() throws TDGWTServiceException {
		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		SessionUtil.getServiceCredentials(httpRequest);
		if (httpRequest == null) {
			throw new TDGWTServiceException("Error retrieving the httpRequest: null");
		}
		ArrayList<String> charsetList = EncodingPGSupported.getEncodidingStringList();
		String defaultEncoding = EncodingPGSupported.getDefaultEncoding();

		return new AvailableCharsetList(charsetList, defaultEncoding);
	}

	@Override
	public ArrayList<String> configureCSVParser(String encoding, HeaderPresence headerPresence, char delimiter,
			char comment) throws TDGWTServiceException {
		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

		if (httpRequest == null) {
			throw new TDGWTServiceException("Error retrieving the httpRequest: null");
		}
		logger.debug("configureCSVParser  encoding: " + encoding + " headerPresence: " + headerPresence + " delimiter: "
				+ delimiter + " comment: " + comment);

		CSVFileUploadSession fileUploadSession = SessionUtil.getCSVFileUploadSession(httpRequest, serviceCredentials);
		if (fileUploadSession == null) {
			throw new TDGWTServiceException("Error retrieving the fileUploadSession: null");
		}
		CSVParserConfiguration parserConfiguration = fileUploadSession.getParserConfiguration();
		if (parserConfiguration == null) {
			parserConfiguration = new CSVParserConfiguration(Charset.forName(encoding), delimiter, comment,
					headerPresence);
			fileUploadSession.setParserConfiguration(parserConfiguration);
		} else {
			parserConfiguration.update(encoding, delimiter, comment, headerPresence);
		}
		SessionUtil.setCSVFileUploadSession(httpRequest, serviceCredentials, fileUploadSession);
		try {
			return CSVFileUtil.getHeader(fileUploadSession.getCsvFile(), fileUploadSession.getParserConfiguration());
		} catch (Throwable e) {
			logger.error("Error retrieving the CSV header", e);
			throw new TDGWTServiceException("Error calculating the CSV header: " + e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CheckCSVSession checkCSV(long errorsLimit) throws TDGWTServiceException {

		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

		if (httpRequest == null) {
			throw new TDGWTServiceException("Error retrieving the httpRequest: null");
		}
		CSVFileUploadSession fileUploadSession = SessionUtil.getCSVFileUploadSession(httpRequest, serviceCredentials);
		if (fileUploadSession == null) {
			throw new TDGWTServiceException("Error retrieving the fileUploadSession: null");
		}

		try {
			return CSVFileUtil.checkCSV(fileUploadSession.getCsvFile(), fileUploadSession.getParserConfiguration(),
					errorsLimit);
		} catch (Throwable e) {
			logger.error("Error checking the CSV file", e);
			throw new TDGWTServiceException("Error checking the CSV file: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String startCSVImport(CSVImportSession csvImportSession) throws TDGWTServiceException {
		logger.debug("StartCSVImport: " + csvImportSession);
		CSVFileUploadSession fileUploadSession = null;

		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			fileUploadSession = SessionUtil.getCSVFileUploadSession(httpRequest, serviceCredentials);
			if (fileUploadSession == null) {
				throw new TDGWTServiceException("Error retrieving the fileUploadSession: null");
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			OpExecution4CSVImport opEx = new OpExecution4CSVImport(serviceCredentials, service, csvImportSession,
					fileUploadSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			TabularResource tabularResource = service.createTabularResource();
			TabResource csvImportTabResource = csvImportSession.getTabResource();
			syncTRMetaData(tabularResource, csvImportTabResource);
			TRId trId = new TRId(String.valueOf(tabularResource.getId().getValue()));
			csvImportTabResource.setTrId(trId);
			logger.debug(csvImportTabResource.toString());
			SessionUtil.setCSVImportTabResource(httpRequest, serviceCredentials, csvImportTabResource);
			Task trTask;
			try {
				trTask = service.execute(invocation, tabularResource.getId());
			} catch (Throwable e) {
				e.printStackTrace();
				throw new TDGWTServiceException(
						"Tabular Data Service error creating TabularResource: " + e.getLocalizedMessage());
			}

			logger.debug("Start Task on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.CSVImport, trId);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			try {
				if (fileUploadSession != null && fileUploadSession.getCsvFile() != null
						&& fileUploadSession.getCsvFile().exists()) {
					fileUploadSession.getCsvFile().delete();
				}
			} catch (Throwable e1) {
				logger.error(e1.getLocalizedMessage());
			}
			throw e;
		} catch (SecurityException e) {
			try {
				if (fileUploadSession != null && fileUploadSession.getCsvFile() != null
						&& fileUploadSession.getCsvFile().exists()) {
					fileUploadSession.getCsvFile().delete();
				}
			} catch (Throwable e1) {
				logger.error(e1.getLocalizedMessage());
			}
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			try {
				if (fileUploadSession != null && fileUploadSession.getCsvFile() != null
						&& fileUploadSession.getCsvFile().exists()) {
					fileUploadSession.getCsvFile().delete();
				}
			} catch (Throwable e1) {
				logger.error(e1.getLocalizedMessage());
			}
			e.printStackTrace();
			throw new TDGWTServiceException("Error in CSV Import: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void getFileFromWorkspace(CSVImportSession csvImportSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

		Workspace w = null;
		WorkspaceItem wi = null;

		try {
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();

			HomeManager manager = factory.getHomeManager();

			Home home = manager.getHome();

			w = home.getWorkspace();
			wi = w.getItem(csvImportSession.getItemId());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error in importCSV getFileFromWorkspace accessing the workspace: " + e.getLocalizedMessage(), e);
		}

		if (wi == null) {
			logger.error("Error retrieving the item on workspace" + csvImportSession.getItemId());
			throw new TDGWTServiceException("Error retrieving the item on workspace" + csvImportSession.getItemId());
		}

		try {
			logger.debug("WorkspaceItem [id:" + wi.getId() + " name:" + wi.getName() + " remotePath:"
					+ wi.getRemotePath() + "]");
		} catch (InternalErrorException e1) {
			e1.printStackTrace();
			throw new TDGWTServiceException("Error retrieving the item on workspace" + wi);
		}

		CSVFileUploadSession fileUploadSession = new CSVFileUploadSession();
		// CSVImportMonitor csvImportMonitor = new CSVImportMonitor();
		FileUploadMonitor fileUploadMonitor = new FileUploadMonitor();

		SessionUtil.setFileUploadMonitor(httpRequest, serviceCredentials, fileUploadMonitor);

		fileUploadSession.setId(UUID.randomUUID().toString());
		fileUploadSession.setFileUploadState(FileUploadState.STARTED);
		// fileUploadSession.setCsvImportMonitor(csvImportMonitor);

		SessionUtil.setCSVFileUploadSession(httpRequest, serviceCredentials, fileUploadSession);

		try {
			FilesStorage filesStorage = new FilesStorage();
			InputStream is = filesStorage.retrieveInputStream(serviceCredentials.getUserName(), wi);

			FileUtil.setImportFileCSV(fileUploadSession, is, wi.getName(), Constants.FILE_CSV_MIMETYPE);
		} catch (Throwable e) {
			FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(httpRequest, serviceCredentials);
			fum.setFailed("An error occured elaborating the file", FileUtil.exceptionDetailMessage(e));
			SessionUtil.setFileUploadMonitor(httpRequest, serviceCredentials, fum);
			fileUploadSession.setFileUploadState(FileUploadState.FAILED);
			SessionUtil.setCSVFileUploadSession(httpRequest, serviceCredentials, fileUploadSession);
			logger.error("Error elaborating the input stream", e);
			throw new TDGWTServiceException("Error in importCSV getFileFromWorkspace: " + e.getLocalizedMessage(), e);
		}

		logger.trace("changing state");
		FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(httpRequest, serviceCredentials);
		fum.setState(FileUploadState.COMPLETED);
		SessionUtil.setFileUploadMonitor(httpRequest, serviceCredentials, fum);
		SessionUtil.setCSVFileUploadSession(httpRequest, serviceCredentials, fileUploadSession);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String getTRCreationDate(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTRMetadata on " + trId.toString());

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource tr = service.getTabularResource(new TabularResourceId(Long.valueOf(trId.getId())));

			return sdf.format(tr.getCreationDate().getTime());

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error in getTRCreationDate(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in getTRCreationDate(): " + e.getLocalizedMessage());
		}
	}

	@Override
	public Boolean isTabularResourceValid(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("IsTabularResourceValid: " + trId.toString());

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource tr = service.getTabularResource(new TabularResourceId(Long.valueOf(trId.getId())));
			logger.debug("IsTabularResourceValid: " + tr.isValid());
			return tr.isValid();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error checking if it is a valid tabular resource: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException(
					"Error checking if it is a valid tabular resource: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Check finalized status of a tabular resource
	 *
	 * @param trId
	 *            TR id
	 * @return True if is finalized
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public Boolean isTabularResourceFinalized(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("IsTabularResourceFinalized: " + trId.toString());

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource tr = service.getTabularResource(new TabularResourceId(Long.valueOf(trId.getId())));
			logger.debug("IsTabularResourceFinalized: " + tr.isFinalized());
			return tr.isFinalized();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error checking if it is a finalized tabular resource: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException(
					"Error checking if it is a finalized tabular resource: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Check locked status of a tabular resource
	 * 
	 * @param trId
	 *            TR id
	 * @return True if is locked
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public Boolean isTabularResourceLocked(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("IsTabularResourceLocked: " + trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource tr = service.getTabularResource(new TabularResourceId(Long.valueOf(trId.getId())));
			logger.debug("IsTabularResourceLocked: " + tr.isLocked());
			return tr.isLocked();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error checking if it is a locked tabular resource: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException(
					"Error checking if it is a locked tabular resource: " + e.getLocalizedMessage());
		}
	}

	private ArrayList<TRMetadata> getTRMetadata(TabularResource tr) throws TDGWTServiceException {
		try {

			Collection<TabularResourceMetadata<?>> trMetas = tr.getAllMetadata();

			logger.debug("GetTRMetadata size: " + trMetas.size());
			ArrayList<TRMetadata> listTRMetadata = new ArrayList<TRMetadata>();

			for (TabularResourceMetadata<?> trMetadata : trMetas) {
				if (trMetadata instanceof org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata) {
					TRDescriptionMetadata trDescriptionMetadata = new TRDescriptionMetadata();
					trDescriptionMetadata.setValue(
							((org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata) trMetadata)
									.getValue());
					listTRMetadata.add(trDescriptionMetadata);
				} else {
					if (trMetadata instanceof org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata) {
						TRNameMetadata trNameMetadata = new TRNameMetadata();
						trNameMetadata.setValue(
								((org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata) trMetadata)
										.getValue());
						listTRMetadata.add(trNameMetadata);
					} else {
						if (trMetadata instanceof AgencyMetadata) {
							TRAgencyMetadata trAgencyMetadata = new TRAgencyMetadata();
							trAgencyMetadata.setValue(((AgencyMetadata) trMetadata).getValue());
							listTRMetadata.add(trAgencyMetadata);
						} else {
							if (trMetadata instanceof RightsMetadata) {
								TRRightsMetadata trRightsMetadata = new TRRightsMetadata();
								trRightsMetadata.setValue(((RightsMetadata) trMetadata).getValue());
								listTRMetadata.add(trRightsMetadata);
							} else {
								if (trMetadata instanceof ValidSinceMetadata) {
									TRValidSinceMetadata validSinceMetadata = new TRValidSinceMetadata();
									Calendar cal = ((ValidSinceMetadata) trMetadata).getValue();
									validSinceMetadata.setValue(cal.getTime());
									listTRMetadata.add(validSinceMetadata);
								} else {
									if (trMetadata instanceof ValidUntilMetadata) {
										TRValidUntilMetadata validUntilMetadata = new TRValidUntilMetadata();
										Calendar cal = ((ValidUntilMetadata) trMetadata).getValue();
										validUntilMetadata.setValue(cal.getTime());
										listTRMetadata.add(validUntilMetadata);
									} else {
										if (trMetadata instanceof LicenceMetadata) {
											TRLicenceMetadata licenceMetadata = new TRLicenceMetadata();
											licenceMetadata
													.setValue(((LicenceMetadata) trMetadata).getValue().toString());
											listTRMetadata.add(licenceMetadata);
										} else {

										}
									}
								}
							}

						}
					}
				}
			}

			logger.debug("GetTRMetadata retrived: " + listTRMetadata.size());
			logger.debug("GetTRMetadata: [" + listTRMetadata + "]");
			return listTRMetadata;

		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(null);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in getTRMetadata(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in getTRMetadata(): " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<TRMetadata> getTRMetadata(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTRMetadata on " + trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResource tr = service.getTabularResource(new TabularResourceId(Long.valueOf(trId.getId())));

			Collection<TabularResourceMetadata<?>> trMetas = tr.getAllMetadata();

			logger.debug("GetTRMetadata size: " + trMetas.size());
			ArrayList<TRMetadata> listTRMetadata = new ArrayList<TRMetadata>();

			for (TabularResourceMetadata<?> trMetadata : trMetas) {
				if (trMetadata instanceof org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata) {
					TRDescriptionMetadata trDescriptionMetadata = new TRDescriptionMetadata();
					trDescriptionMetadata.setValue(
							((org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata) trMetadata)
									.getValue());
					listTRMetadata.add(trDescriptionMetadata);
				} else {
					if (trMetadata instanceof org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata) {
						TRNameMetadata trNameMetadata = new TRNameMetadata();
						trNameMetadata.setValue(
								((org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata) trMetadata)
										.getValue());
						listTRMetadata.add(trNameMetadata);
					} else {
						if (trMetadata instanceof AgencyMetadata) {
							TRAgencyMetadata trAgencyMetadata = new TRAgencyMetadata();
							trAgencyMetadata.setValue(((AgencyMetadata) trMetadata).getValue());
							listTRMetadata.add(trAgencyMetadata);
						} else {
							if (trMetadata instanceof RightsMetadata) {
								TRRightsMetadata trRightsMetadata = new TRRightsMetadata();
								trRightsMetadata.setValue(((RightsMetadata) trMetadata).getValue());
								listTRMetadata.add(trRightsMetadata);
							} else {
								if (trMetadata instanceof ValidSinceMetadata) {
									TRValidSinceMetadata validSinceMetadata = new TRValidSinceMetadata();
									Calendar cal = ((ValidSinceMetadata) trMetadata).getValue();
									validSinceMetadata.setValue(cal.getTime());
									listTRMetadata.add(validSinceMetadata);
								} else {
									if (trMetadata instanceof ValidUntilMetadata) {
										TRValidUntilMetadata validUntilMetadata = new TRValidUntilMetadata();
										Calendar cal = ((ValidUntilMetadata) trMetadata).getValue();
										validUntilMetadata.setValue(cal.getTime());
										listTRMetadata.add(validUntilMetadata);
									} else {
										if (trMetadata instanceof LicenceMetadata) {
											TRLicenceMetadata licenceMetadata = new TRLicenceMetadata();
											licenceMetadata
													.setValue(((LicenceMetadata) trMetadata).getValue().toString());
											listTRMetadata.add(licenceMetadata);
										} else {

										}
									}
								}
							}

						}
					}
				}
			}

			logger.debug("GetTRMetadata retrived: " + listTRMetadata.size());
			logger.debug("GetTRMetadata: [" + listTRMetadata + "]");
			return listTRMetadata;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in getTRMetadata(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in getTRMetadata(): " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param trId
	 *            TR id
	 * @return Table validations metadata
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public TabValidationsMetadata getTableValidationsMetadata(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTableValidationsMetadata on " + trId.toString());
			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));
			TabValidationsMetadata tabValidationsMetadata = new TabValidationsMetadata();
			if (table.contains(ValidationsMetadata.class)) {
				ValidationsMetadata validationsMetadata = table.getMetadata(ValidationsMetadata.class);
				if (validationsMetadata != null) {
					List<Validation> vals = validationsMetadata.getValidations();
					if (vals != null && vals.size() > 0) {
						Validations valid = null;
						ArrayList<Validations> vList = new ArrayList<Validations>();
						int i = 0;
						for (Validation v : vals) {
							valid = new Validations(String.valueOf(i), "", v.getDescription(), v.isValid(), null, null);
							vList.add(valid);
							i++;
						}
						tabValidationsMetadata.setValidations(vList);
					}
				}
			}

			logger.debug("TabValidationsMetadata: " + tabValidationsMetadata);
			return tabValidationsMetadata;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in getTableValidationMetadata(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in getTableValidationMetadata: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public ArrayList<TabMetadata> getTableMetadata(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTableMetadata on " + trId.toString());
			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			Collection<TableMetadata> cMeta = table.getAllMetadata();

			ArrayList<TabMetadata> listTabMetadata = new ArrayList<TabMetadata>();
			logger.debug("Metadata size:" + cMeta.size());
			for (TableMetadata tMetadata : cMeta) {
				if (tMetadata instanceof DescriptionsMetadata) {
					TabDescriptionsMetadata trDescriptionsMetadata = new TabDescriptionsMetadata();
					ArrayList<TRLocalizedText> listTRLocalizedText = new ArrayList<TRLocalizedText>();
					List<LocalizedText> lLocalizedText = ((DescriptionsMetadata) tMetadata).getTexts();
					int i = 0;
					for (LocalizedText lt : lLocalizedText) {
						TRLocalizedText trLocalizedText = new TRLocalizedText();
						trLocalizedText.setId(i);
						trLocalizedText.setValue(lt.getValue());
						trLocalizedText.setLocaleCode(lt.getLocale());
						listTRLocalizedText.add(trLocalizedText);
						i++;
					}
					trDescriptionsMetadata.setListTRLocalizedText(listTRLocalizedText);
					listTabMetadata.add(trDescriptionsMetadata);

				} else {
					if (tMetadata instanceof NamesMetadata) {
						TabNamesMetadata trNamesMetadata = new TabNamesMetadata();
						ArrayList<TRLocalizedText> listTRLocalizedText = new ArrayList<TRLocalizedText>();
						List<LocalizedText> lLocalizedText = ((NamesMetadata) tMetadata).getTexts();
						int i = 0;
						for (LocalizedText lt : lLocalizedText) {
							TRLocalizedText trLocalizedText = new TRLocalizedText();
							trLocalizedText.setId(i);
							trLocalizedText.setValue(lt.getValue());
							trLocalizedText.setLocaleCode(lt.getLocale());
							listTRLocalizedText.add(trLocalizedText);
							i++;
						}
						trNamesMetadata.setListTRLocalizedText(listTRLocalizedText);
						listTabMetadata.add(trNamesMetadata);

					} else {
						if (tMetadata instanceof VersionMetadata) {
							TabVersionMetadata trVersionMetadata = new TabVersionMetadata();
							trVersionMetadata.setVersion(((VersionMetadata) tMetadata).getVersion());
							listTabMetadata.add(trVersionMetadata);
						} else {
							if (tMetadata instanceof ExportMetadata) {
								TabExportMetadata trExportMetadata = new TabExportMetadata();
								trExportMetadata.setDestinationType(((ExportMetadata) tMetadata).getDestinationType());
								trExportMetadata
										.setExportDate(sdf.format(((ExportMetadata) tMetadata).getExportDate()));
								trExportMetadata.setUrl(((ExportMetadata) tMetadata).getUri());
								listTabMetadata.add(trExportMetadata);
							} else {
								if (tMetadata instanceof ImportMetadata) {
									TabImportMetadata trImportMetadata = new TabImportMetadata();
									trImportMetadata.setSourceType(((ImportMetadata) tMetadata).getSourceType());
									trImportMetadata
											.setImportDate(sdf.format(((ImportMetadata) tMetadata).getImportDate()));
									trImportMetadata.setUrl(((ImportMetadata) tMetadata).getUri());
									listTabMetadata.add(trImportMetadata);
								} else {
									if (tMetadata instanceof GenericMapMetadata) {
										TabGenericMapMetadata trGenericMapMetadata = new TabGenericMapMetadata();
										trGenericMapMetadata.setMetadataMap(
												(HashMap<String, String>) ((GenericMapMetadata) tMetadata)
														.getMetadataMap());
										listTabMetadata.add(trGenericMapMetadata);
									} else {

									}

								}
							}
						}
					}
				}
			}

			return listTabMetadata;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error in getTableMetadata(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in getTableMetadata(): " + e.getLocalizedMessage());
		}
	}

	protected void checkTRId(TRId trId, HttpServletRequest httpRequest, ServiceCredentials serviceCredentials)
			throws TDGWTServiceException {
		if (trId == null) {
			ResourceBundle messages = getResourceBundle(httpRequest);
			logger.error("TRId is null");
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.noValidTabularResourceIdPresent));
		}

		if (trId.getId() == null || trId.getId().isEmpty()) {
			ResourceBundle messages = getResourceBundle(httpRequest);
			logger.error("TRId not valid: " + trId);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.noValidTabularResourceIdPresent));
		}
	}

	protected void checkTabularResourceLocked(TabularResource tabularResource, HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		try {

			if (tabularResource.isLocked()) {
				ResourceBundle messages = getResourceBundle(httpRequest);
				logger.error("Tabular Resource Is Locked!");
				throw new TDGWTIsLockedException(
						messages.getString(TDGWTServiceMessagesConstants.tabularResourceIsLocked));
			}
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}
	}

	protected void checkTabularResourceIsFinal(TabularResource tabularResource, HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		try {

			if (tabularResource.isFinalized()) {
				ResourceBundle messages = getResourceBundle(httpRequest);
				logger.error("Tabular Resource Is Final!");
				throw new TDGWTIsFinalException(
						messages.getString(TDGWTServiceMessagesConstants.tabularResourceIsFinal));
			}

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}
	}

	protected void checkTabularResourceIsFlow(TabularResource tabularResource, HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		try {

			if (tabularResource.getTabularResourceType().compareTo(TabularResourceType.FLOW) == 0) {
				ResourceBundle messages = getResourceBundle(httpRequest);
				logger.error("Operation not allowed on a tabular resource of type flow");
				throw new TDGWTIsFlowException(
						messages.getString(TDGWTServiceMessagesConstants.operationNotAllowedOnFlow));
			}
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public String startSDMXExport(SDMXExportSession exportSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("Start SDMX Export");

			TRId trId = SessionUtil.getTRId(httpRequest, serviceCredentials);
			if (trId == null) {
				throw new TDGWTServiceException("Error no tabular resource in session");
			}

			if (trId.getTableId() == null) {
				throw new TDGWTServiceException("Error no table present in session");
			}

			if (exportSession == null) {
				logger.error("Error in SDMX Export: exportSession is null!");
				throw new TDGWTServiceException("Error in SDMX Export: exportSession is null!!");

			}

			SessionUtil.setSDMXExportSession(httpRequest, serviceCredentials, exportSession);

			logger.debug("Tabular Data Service");

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecutionDirector director = new OpExecutionDirector();
			// /
			switch (exportSession.getExportType()) {
			case CODELIST:
				OpExecution4SDMXCodelistExport opExC = new OpExecution4SDMXCodelistExport(httpRequest,
						serviceCredentials, service, exportSession);
				director.setOperationExecutionBuilder(opExC);
				break;
			case DATASET:
				OpExecution4SDMXDatasetExport opExD = new OpExecution4SDMXDatasetExport(httpRequest, serviceCredentials,
						service, exportSession);
				director.setOperationExecutionBuilder(opExD);
				break;
			case GENERIC:
			default:
				logger.error("Error in SDMX Export: Only Codelist and Dataset are supported!");
				throw new TDGWTServiceException("Error in SDMXExport: Only Codelist and Dataset are supported!");
			}

			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in SDMXExport: Operation not supported for now!");
			}
			logger.debug("OperationInvocation: \n" + invocation.toString());

			Task trTask = service.execute(invocation, new TabularResourceId(Long.valueOf(trId.getId())));
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.SDMXExport, trId);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in SDMXExport: " + e.getLocalizedMessage());
		}

	}

	@Override
	public String startSDMXTemplateExport(SDMXTemplateExportSession sdmxTemplateExportSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("Start SDMX Template Export");

			if (sdmxTemplateExportSession == null) {
				logger.error("Error in SDMX Template Export : templateExportSession is null!");
				throw new TDGWTServiceException("Error in SDMX Template Export: templateExportSession is null!!");

			}

			SessionUtil.setSDMXTemplateExportSession(httpRequest, serviceCredentials, sdmxTemplateExportSession);

			TRId trId = SessionUtil.getTRId(httpRequest, serviceCredentials);

			logger.debug("Tabular Data Service");

			TabularDataService service = TabularDataServiceFactory.getService();

			OpExecutionDirector director = new OpExecutionDirector();
			// /
			OpExecution4SDMXTemplateExport opExD = new OpExecution4SDMXTemplateExport(httpRequest, serviceCredentials,
					service, sdmxTemplateExportSession);
			director.setOperationExecutionBuilder(opExD);

			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in SDMX Template Export: Operation not supported for now!");
			}
			logger.debug("OperationInvocation: \n" + invocation.toString());

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));

			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.SDMXTemplateExport, trId);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			logger.error("Error in SDMX Template Export: " + e.getLocalizedMessage(), e);
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in SDMX Template Export: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in SDMX Template Export: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startCSVExport(CSVExportSession exportSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("Start CSV Export");
			TRId trId = SessionUtil.getTRId(httpRequest, serviceCredentials);
			if (trId == null) {
				throw new TDGWTServiceException("Error no tabular resource in session");
			}

			if (trId.getTableId() == null) {
				throw new TDGWTServiceException("Error no table present in session");
			}

			SessionUtil.setCSVExportSession(httpRequest, serviceCredentials, exportSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecution4CSVExport opEx = new OpExecution4CSVExport(httpRequest, serviceCredentials, service,
					exportSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in CSVExport: Operation not supported for now!");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.CSVExport, trId);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in CSV Export: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startJSONExport(JSONExportSession exportSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("Start JSON Export");

			TRId trId = SessionUtil.getTRId(httpRequest, serviceCredentials);
			if (trId == null) {
				throw new TDGWTServiceException("Error no tabular resource in session");
			}

			if (trId.getTableId() == null) {
				throw new TDGWTServiceException("Error no table present in session");
			}

			SessionUtil.setJSONExportSession(httpRequest, serviceCredentials, exportSession);

			TabularDataService service = TabularDataServiceFactory.getService();
			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecution4JSONExport opEx = new OpExecution4JSONExport(httpRequest, serviceCredentials, service,
					exportSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in JSONExport: Operation not supported for now!");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.JSONExport, trId);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in JSON Export: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startChangeColumnType(ChangeColumnTypeSession changeColumnTypeSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setChangeColumnTypeSession(httpRequest, serviceCredentials, changeColumnTypeSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(changeColumnTypeSession.getColumnData().getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(changeColumnTypeSession.getColumnData().getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ChangeColumnType opEx = new OpExecution4ChangeColumnType(service, changeColumnTypeSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in ChangeColumnType: Operation not supported for now!");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ChangeColumnType,
					changeColumnTypeSession.getColumnData().getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in ChangeColumnType: " + e.getLocalizedMessage());
		}

	}

	public String startAddColumn(AddColumnSession addColumnSession, Expression expression,
			HttpServletRequest httpRequest) throws TDGWTServiceException {

		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setAddColumnSession(httpRequest, serviceCredentials, addColumnSession);

			TabularDataService service = TabularDataServiceFactory.getService();
			checkTRId(addColumnSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(addColumnSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4AddColumn opEx = new OpExecution4AddColumn(service, addColumnSession, expression);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Add Column: Operation not supported for now!");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.AddColumn, addColumnSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in AddColumn: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startAddColumn(AddColumnSession addColumnSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		return startAddColumn(addColumnSession, null, httpRequest);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public String startDeleteColumn(DeleteColumnSession deleteColumnSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setDeleteColumnSession(httpRequest, serviceCredentials, deleteColumnSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(deleteColumnSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(deleteColumnSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4DeleteColumn opEx = new OpExecution4DeleteColumn(service, deleteColumnSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();

			ArrayList<OperationExecution> invocation = director.getListOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Delete Column: Operation not supported for now!");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.executeBatch(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.DeleteColumn,
					deleteColumnSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in DeleteColumn: " + e.getLocalizedMessage());
		}

	}

	public String startFilterColumn(FilterColumnSession filterColumnSession, Expression expression,
			HttpServletRequest httpRequest) throws TDGWTServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setFilterColumnSession(httpRequest, serviceCredentials, filterColumnSession);

			if (filterColumnSession == null) {
				logger.error("FilterColumnSession is null");
				throw new TDGWTServiceException("Error in filter column: FilterColumnSession is null");
			}

			logger.debug("StartFilterColumn: " + filterColumnSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(filterColumnSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(filterColumnSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4FilterColumn opEx = new OpExecution4FilterColumn(service, filterColumnSession, expression);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}
			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Filter Column on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.FilterColumn,
					filterColumnSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in filter column: " + e.getLocalizedMessage());
		}

	}

	public String startReplaceColumnByExpression(ReplaceColumnByExpressionSession replaceColumnByExpressionSession,
			Expression conditionExpression, Expression replaceExpression, HttpServletRequest httpRequest)
			throws TDGWTServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setReplaceColumnByExpressionSession(httpRequest, serviceCredentials,
					replaceColumnByExpressionSession);
			if (replaceColumnByExpressionSession == null) {
				logger.error("ReplaceColumnByExpressionSession is null");
				throw new TDGWTServiceException(
						"Error in replace column by expression: ReplaceColumnByExpressionSession is null");
			}

			logger.debug("StartReplaceColumnByExpression: " + replaceColumnByExpressionSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(replaceColumnByExpressionSession.getColumn().getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(replaceColumnByExpressionSession.getColumn().getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ReplaceColumnByExpression opEx = new OpExecution4ReplaceColumnByExpression(service,
					replaceColumnByExpressionSession, conditionExpression, replaceExpression);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());

			logger.debug("Start Replace on Service:" + sdfPerformance.format(new Date()));
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Replace returned task:" + sdfPerformance.format(new Date()));

			logger.debug("Replace Column by Expression on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ReplaceByExpression,
					replaceColumnByExpressionSession.getColumn().getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in replace column by expression: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public String startLabelColumn(LabelColumnSession labelColumnSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setLabelColumnSession(httpRequest, serviceCredentials, labelColumnSession);
			logger.debug(labelColumnSession.toString());

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(labelColumnSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(labelColumnSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4LabelColumn opEx = new OpExecution4LabelColumn(service, labelColumnSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			/*
			 * List<OperationExecution> invocations = director
			 * .getListOperationExecution();
			 * 
			 * if (invocations == null) { throw new
			 * TDGWTServiceException("Operation not supported"); }
			 * 
			 * logger.debug("OperationInvocation: \n" + invocations);
			 */
			OperationExecution invocation = director.getOperationExecution();

			logger.debug("OperationInvocation: \n" + invocation);

			service.executeSynchMetadataOperation(invocation, tabularResourceId);

			/*
			 * for(OperationExecution op:invocations){
			 * service.executeSynchMetadataOperation(op, tabularResourceId);
			 * //executeBatch(invocations, tabularResourceId); }
			 * 
			 * /* Task trTask = service.executeBatch(invocations,
			 * tabularResourceId); logger.debug("Start Task on service: TaskId "
			 * + trTask.getId()); TaskWrapper taskWrapper = new
			 * TaskWrapper(trTask, UIOperationsId.ChangeColumnLabel,
			 * labelColumnSession.getTrId());
			 * SessionUtil.setStartedTask(httpRequest, serviceCredentials,
			 * taskWrapper);
			 * 
			 * return trTask.getId().getValue();
			 */
			return "ok";

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error Changing The Column Label: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startChangeTableType(ChangeTableTypeSession changeTableTypeSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setChangeTableTypeSession(httpRequest, serviceCredentials, changeTableTypeSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(changeTableTypeSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(changeTableTypeSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ChangeTableType opEx = new OpExecution4ChangeTableType(service, changeTableTypeSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Change Table Type invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ChangeTableType,
					changeTableTypeSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error Changing Table Type: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Returns only CodeColumnType, CodeNameColumnType,
	 * CodeDescriptionColumnType and AnnotationColumnType
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ColumnData> getColumnsForDimension(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getColumnsForDimension():" + trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTable(new TableId(Long.valueOf(trId.getTableId())));

			ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

			List<Column> cols = table.getColumns();
			int i = 0;
			for (Column c : cols) {
				ColumnType ctype = c.getColumnType();
				if (c.getColumnType() instanceof IdColumnType || c.getColumnType() instanceof ValidationColumnType
						|| c.getColumnType() instanceof DimensionColumnType
						|| c.getColumnType() instanceof TimeDimensionColumnType) {

				} else {
					if (c.contains(ViewColumnMetadata.class)) {

					} else {
						if (ctype instanceof CodeColumnType || ctype instanceof CodeNameColumnType
								|| ctype instanceof CodeDescriptionColumnType
								|| ctype instanceof AnnotationColumnType) {

							ColumnData cData = new ColumnData();
							cData.setId(Integer.toString(i));
							cData.setColumnId(c.getLocalId().getValue());
							cData.setName(c.getName());
							cData.setTypeCode(c.getColumnType().getCode());
							cData.setTypeName(c.getColumnType().getName());
							cData.setDataTypeName(c.getDataType().getName());

							NamesMetadata labelsMetadata = null;
							if (c.contains(NamesMetadata.class)) {
								labelsMetadata = c.getMetadata(NamesMetadata.class);
							}

							if (labelsMetadata == null) {
								cData.setLabel("nolabel");
								logger.debug("LabelsMetadata no labels");
							} else {
								LocalizedText cl = null;
								cl = labelsMetadata.getTextWithLocale("en");
								if (cl == null) {
									cData.setLabel("nolabel");
									logger.debug("ColumnLabel no label in en");
								} else {
									if (cl.getValue() == null || cl.getValue().isEmpty()) {
										cData.setLabel("nolabel");
										logger.debug("ColumnLabel no label in en");
									} else {
										cData.setLabel(cl.getValue());
										logger.debug("Column Set Label: " + cl.getValue());
									}
								}
							}

							DataLocaleMetadata dataLocaleMetadata = null;
							if (c.contains(DataLocaleMetadata.class)) {
								dataLocaleMetadata = c.getMetadata(DataLocaleMetadata.class);
								cData.setLocale(dataLocaleMetadata.getLocale());
							}

							cData.setTrId(trId);
							columns.add(cData);
							i++;
						}
					}
				}
			}
			return columns;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in getColumnsForDimension() retrieving Columns: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Columns: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startDeleteRows(DeleteRowsSession deleteRowsSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setDeleteRowsSession(httpRequest, serviceCredentials, deleteRowsSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(deleteRowsSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(deleteRowsSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4DeleteRows opEx = new OpExecution4DeleteRows(service, deleteRowsSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Delete Rows invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.DeleteRow, deleteRowsSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error Deleting Rows: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */

	@Override
	public String startCloneTabularResource(CloneTabularResourceSession cloneTabularResourceSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("CloneTabularResourceSession: " + cloneTabularResourceSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(cloneTabularResourceSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(cloneTabularResourceSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecution4Clone opEx = new OpExecution4Clone(service, cloneTabularResourceSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			TabularResource cloneTR = service.createTabularResource();
			cloneTR.setAllMetadata(tabularResource.getAllMetadata());
			NameMetadata nameMetadata = cloneTR.getMetadata(NameMetadata.class);
			String name;
			if (nameMetadata != null) {
				name = nameMetadata.getValue();
				if (name != null) {
					name = name + "_cloned";
				} else {
					name = "cloned";
				}
				nameMetadata.setValue(name);
			} else {
				name = "cloned";
				nameMetadata = new NameMetadata("cloned");
			}
			cloneTR.setMetadata(nameMetadata);
			TRId trIdClone = new TRId(String.valueOf(cloneTR.getId().getValue()));

			cloneTabularResourceSession.setTrIdClone(trIdClone);
			SessionUtil.setCloneTabularResourceSession(httpRequest, serviceCredentials, cloneTabularResourceSession);

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, cloneTR.getId());
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.Clone, trIdClone);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error Cloning: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startDuplicates(DuplicatesSession duplicatesSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setDuplicatesSession(httpRequest, serviceCredentials, duplicatesSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(duplicatesSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(duplicatesSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4Duplicates opEx = new OpExecution4Duplicates(service, duplicatesSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Delete Rows invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.DuplicateTuples,
					duplicatesSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in operation for duplicates: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startValidationsDelete(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("startValidationDelete():" + trId);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			Task trTask = service.removeValidations(tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ValidationsDelete, trId);
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error deleting validations: " + e.getLocalizedMessage());
		}

	}

	private ArrayList<Contacts> retrieveShareInfo(TabularResource tr) throws TDGWTServiceException {
		try {

			ArrayList<Contacts> contacts = new ArrayList<Contacts>();
			List<String> sharedWithUsers = tr.getSharedWithUsers();
			logger.debug("Shared with Users: " + sharedWithUsers);
			if (sharedWithUsers != null) {
				for (String user : sharedWithUsers) {
					Contacts cont = new Contacts(user, user, false);
					contacts.add(cont);
				}
			}

			List<String> sharedWithGroups = tr.getSharedWithGroups();
			logger.debug("Shared with Groups: " + sharedWithUsers);
			if (sharedWithGroups != null) {
				for (String group : sharedWithGroups) {
					Contacts cont = new Contacts(group, group, true);
					contacts.add(cont);
				}
			}
			return contacts;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(null);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in retrieveShareInfo: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setShare(ShareTabResource shareInfo) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("ShareInfo: " + shareInfo);
			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(shareInfo.getTabResource().getTrId().getId()));

			List<SharingEntity> users = new ArrayList<>();
			for (Contacts cont : shareInfo.getContacts()) {
				SharingEntity sharingEntity;
				if (cont.isGroup()) {
					sharingEntity = SharingEntity.group(cont.getLogin());

				} else {
					sharingEntity = SharingEntity.user(cont.getLogin());
				}
				users.add(sharingEntity);
			}
			SharingEntity[] usersArray = users.toArray(new SharingEntity[0]);

			logger.debug("Share with Users: " + users);
			service.share(tabularResourceId, usersArray);

			TDMNotifications tdmNotifications = new TDMNotifications(this.getThreadLocalRequest(), serviceCredentials,
					shareInfo);
			tdmNotifications.start();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error on service");
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setShareTemplate(ShareTemplate shareTemplate) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("ShareTemplate: " + shareTemplate);
			TabularDataService service = TabularDataServiceFactory.getService();

			TemplateId templateId = new TemplateId(Long.valueOf(shareTemplate.getTemplateData().getId()));

			List<SharingEntity> users = new ArrayList<>();
			for (Contacts cont : shareTemplate.getContacts()) {
				SharingEntity sharingEntity;
				if (cont.isGroup()) {
					sharingEntity = SharingEntity.group(cont.getLogin());
				} else {
					sharingEntity = SharingEntity.user(cont.getLogin());
				}
				users.add(sharingEntity);
			}
			SharingEntity[] usersArray = users.toArray(new SharingEntity[0]);

			logger.debug("Share with Users: " + users);
			service.share(templateId, usersArray);

			TDMNotifications tdmNotifications = new TDMNotifications(this.getThreadLocalRequest(), serviceCredentials,
					shareTemplate);
			tdmNotifications.start();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error on service");
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setShareRule(ShareRule shareRule) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("ShareRule: " + shareRule);
			TabularDataService service = TabularDataServiceFactory.getService();

			RuleId ruleId = new RuleId(Long.valueOf(shareRule.getRuleDescriptionData().getId()));

			List<SharingEntity> users = new ArrayList<>();
			for (Contacts cont : shareRule.getContacts()) {
				SharingEntity sharingEntity;
				if (cont.isGroup()) {
					sharingEntity = SharingEntity.group(cont.getLogin());
				} else {
					sharingEntity = SharingEntity.user(cont.getLogin());
				}
				users.add(sharingEntity);
			}
			SharingEntity[] usersArray = users.toArray(new SharingEntity[0]);

			logger.debug("Share with Users: " + users);
			service.share(ruleId, usersArray);

			TDMNotifications tdmNotifications = new TDMNotifications(this.getThreadLocalRequest(), serviceCredentials,
					shareRule);
			tdmNotifications.start();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error on service");
		}
	}

	public boolean checkTabularResourceNotFinal(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("checkTabularResourceNotFinal: " + trId);
			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResourceId tabularResourceId = new TabularResourceId(new Long(trId.getId()));
			TabularResource tr = service.getTabularResource(tabularResourceId);

			return tr.isFinalized();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error in checkTabularResourceNotFinal: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in checkTabularResourceNotFinal: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setTabResourceInformation(TabResource tabResource) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("SetTabResourceInformation: " + tabResource);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(tabResource.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(tabResource.getTrId().getId()));
			TabularResource tr = service.getTabularResource(tabularResourceId);

			checkTabularResourceLocked(tr, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tr, httpRequest, serviceCredentials);

			logger.debug("setTabResourceInformation - old information:" + tr);

			ArrayList<TabularResourceMetadata<?>> meta = new ArrayList<TabularResourceMetadata<?>>();

			if (tabResource.getName() != null) {
				NameMetadata name = new NameMetadata(tabResource.getName());
				meta.add(name);
			}

			if (tabResource.getDescription() != null) {
				DescriptionMetadata description = new DescriptionMetadata(tabResource.getDescription());
				meta.add(description);

			}

			if (tabResource.getAgency() != null) {
				AgencyMetadata agency = new AgencyMetadata(tabResource.getAgency());
				meta.add(agency);
			}

			if (tabResource.getRight() != null) {
				RightsMetadata rights = new RightsMetadata(tabResource.getRight());
				meta.add(rights);
			}

			if (tabResource.getValidFrom() != null) {
				try {
					Date dateF = tabResource.getValidFrom();
					GregorianCalendar validFromC = new GregorianCalendar();
					validFromC.setTime(dateF);
					ValidSinceMetadata validSince = new ValidSinceMetadata();
					validSince.setValue(validFromC);
					meta.add(validSince);
				} catch (Throwable e) {
					logger.info("ValidFromMetadata is not set, no valid calendar present");
				}
			}

			if (tabResource.getValidUntilTo() != null) {
				try {
					Date dateU = tabResource.getValidUntilTo();
					GregorianCalendar validUntilToC = new GregorianCalendar();
					validUntilToC.setTime(dateU);
					ValidUntilMetadata validUntil = new ValidUntilMetadata(validUntilToC);
					meta.add(validUntil);
				} catch (Throwable e) {
					logger.info("ValidUntilMetadata is not set, no valid calendar present");
				}
			}

			if (tabResource.getLicence() != null && !tabResource.getLicence().isEmpty()) {
				LicenceMetadata licenceMetadata = new LicenceMetadata();
				Licence licence = LicenceMap.map(tabResource.getLicence());
				if (licence != null) {
					licenceMetadata.setValue(licence);
					meta.add(licenceMetadata);
				} else {
					logger.error("Licence type not found: " + tabResource.getLicence());
				}

			}

			tr.setAllMetadata(meta);

			if (tabResource.isFinalized()) {
				if (tr.isValid()) {
					if (!tr.isFinalized()) {
						tr.finalize();
					}
				} else {
					throw new TDGWTServiceException("Only valid tabular resource can be finalized!");
				}
			}
			logger.debug("setTabResourceInformation - new information:" + tr);

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error in setTabResourceInformation: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in setTabResourceInformation: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setTabResourceToFinal(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("SetTabResourceToFinal: " + trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tr = service.getTabularResource(tabularResourceId);

			checkTabularResourceLocked(tr, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tr, httpRequest, serviceCredentials);

			logger.debug("setTabResourceToFinal: " + tr);

			if (tr.isValid()) {
				if (!tr.isFinalized()) {
					tr.finalize();
				}
			} else {
				throw new TDGWTServiceException("Only valid tabular resource can be finalized!");
			}

			logger.debug("setTabResourceToFinal - new information:" + tr);

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error in setTabResourceToFinal: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in setTabResourceToFinal: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Occurrences> getOccurrencesForBatchReplace(
			OccurrencesForReplaceBatchColumnSession occurrencesForReplaceBatchColumnSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetOccurencesForBatchReplace: " + occurrencesForReplaceBatchColumnSession.toString());

			TabularDataService service = TabularDataServiceFactory.getService();

			ArrayList<Occurrences> occurences = QueryService.queryOccurences(service,
					occurrencesForReplaceBatchColumnSession, Direction.ASC);

			logger.debug("Retrieved Occurences");
			return occurences;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error in GetOccurencesForBatchReplace: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in GetOccurencesForBatchReplace: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<TemplateData> getTemplates() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTemplates");

			TabularDataService service = TabularDataServiceFactory.getService();

			List<TemplateDescription> templateDescriptorList = service.getTemplates();
			ArrayList<TemplateData> templateDataList = new ArrayList<TemplateData>();
			TemplateData templateData;
			for (TemplateDescription desc : templateDescriptorList) {
				templateData = new TemplateData();
				templateData.setId(desc.getId());
				templateData.setName(desc.getName());
				templateData.setDescription(desc.getDescription());
				templateData.setAgency(desc.getAgency());
				switch (desc.getTemplate().getCategory()) {
				case CODELIST:
					templateData.setCategory("Codelist");
					break;
				case DATASET:
					templateData.setCategory("Dataset");
					break;
				case GENERIC:
					templateData.setCategory("Generic");
					break;
				default:
					break;

				}

				Contacts owner = new Contacts("", desc.getOwner(), false);
				templateData.setOwner(owner);
				templateData.setContacts(retrieveTemplateShareInfo(desc));
				if (desc.getCreationdDate() != null) {
					templateData.setCreationDate(desc.getCreationdDate().getTime());
				} else {
					templateData.setCreationDate(null);
				}
				templateDataList.add(templateData);
			}
			logger.debug("Retrieved TemplateData List");

			return templateDataList;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error in GetTemplates: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in GetTemplates: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<TemplateData> getTemplatesForDSDExport() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTemplatesForDSDExport");

			TabularDataService service = TabularDataServiceFactory.getService();

			List<TemplateDescription> templateDescriptorList = service.getTemplates();

			ArrayList<TemplateData> templateDataList = new ArrayList<TemplateData>();
			TemplateData templateData;
			for (TemplateDescription desc : templateDescriptorList) {
				if (desc.getTemplate().getCategory().compareTo(TemplateCategory.DATASET) == 0) {

					List<TemplateColumn<?>> columns = desc.getTemplate().getColumns();
					boolean hasMeasure = false;
					for (TemplateColumn<?> col : columns) {
						if (col.getColumnType() != null && col.getColumnType().compareTo(ColumnCategory.MEASURE) == 0) {
							hasMeasure = true;
							break;
						}

					}

					if (hasMeasure) {
						templateData = new TemplateData();
						templateData.setId(desc.getId());
						templateData.setName(desc.getName());
						templateData.setDescription(desc.getDescription());
						templateData.setAgency(desc.getAgency());
						switch (desc.getTemplate().getCategory()) {
						case CODELIST:
							templateData.setCategory("Codelist");
							break;
						case DATASET:
							templateData.setCategory("Dataset");
							break;
						case GENERIC:
							templateData.setCategory("Generic");
							break;
						default:
							break;

						}

						Contacts owner = new Contacts("", desc.getOwner(), false);
						templateData.setOwner(owner);
						templateData.setContacts(retrieveTemplateShareInfo(desc));
						if (desc.getCreationdDate() != null) {
							templateData.setCreationDate(desc.getCreationdDate().getTime());
						} else {
							templateData.setCreationDate(null);
						}
						templateDataList.add(templateData);
					}
				}
			}
			logger.debug("Retrieved TemplateData List");

			return templateDataList;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error in GetTemplatesForDSDExport: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in GetTemplateForDSDExport: " + e.getLocalizedMessage());
		}

	}

	@Override
	public ArrayList<TemplateColumnData> getTemplateColumnsData(String templateId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getTemplateColumnsData: TemplateId=" + templateId);

			if (templateId == null || templateId.isEmpty()) {
				logger.error("Error in GetTemplateColumnsData: invalid template id=" + templateId);
				throw new TDGWTServiceException("Error in GetTemplateColumnsData: invalid template id=" + templateId);
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			TemplateDescription templateDescriptor = service.getTemplate(new TemplateId(Long.valueOf(templateId)));

			ArrayList<TemplateColumnData> templateColumnsDataList = new ArrayList<TemplateColumnData>();
			List<TemplateColumn<?>> columns = templateDescriptor.getTemplate().getColumns();

			int i = 1;
			for (TemplateColumn<?> c : columns) {

				TemplateColumnData templateColumnData = new TemplateColumnData(Integer.toString(i), c.getId(),
						ColumnTypeCodeMap.getColumnType(c.getColumnType()),
						ColumnDataTypeMap.mapFromDataTypeClass(c.getValueType()), c.getLabel());
				templateColumnsDataList.add(templateColumnData);
				i++;
			}
			logger.debug("Retrieved TemplateColumnData List");

			return templateColumnsDataList;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			logger.error("Security Exception: " + e.getLocalizedMessage(), e);
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error in GetTemplateColumnsData: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in GetTemplateColumnsData: " + e.getLocalizedMessage());
		}

	}

	private ArrayList<Contacts> retrieveTemplateShareInfo(TemplateDescription templateDescription)
			throws TDGWTServiceException {
		try {

			ArrayList<Contacts> contacts = new ArrayList<Contacts>();
			List<String> sharedWithUsers = templateDescription.getSharedWithUsers();
			logger.debug("Shared with Users: " + sharedWithUsers);
			if (sharedWithUsers != null) {
				for (String user : sharedWithUsers) {
					Contacts cont = new Contacts(user, user, false);
					contacts.add(cont);
				}
			}

			List<String> sharedWithGroups = templateDescription.getSharedWithGroups();
			logger.debug("Shared with Groups: " + sharedWithUsers);
			if (sharedWithGroups != null) {
				for (String group : sharedWithGroups) {
					Contacts cont = new Contacts(group, group, true);
					contacts.add(cont);
				}
			}
			return contacts;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(null);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in retrieveShareInfo: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startTemplateApply(TemplateApplySession templateApplySession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("startTemplateApply: " + templateApplySession);
			SessionUtil.setTemplateApplySession(httpRequest, serviceCredentials, templateApplySession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(templateApplySession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(templateApplySession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			TemplateId templateId = new TemplateId(templateApplySession.getTemplateData().getId());

			Task trTask = service.applyTemplate(templateId, tabularResourceId);
			logger.debug("startTemplateApply task start");

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ApplyTemplate,
					templateApplySession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TemplateNotCompatibleException e) {
			ResourceBundle messages = getResourceBundle(httpRequest);
			logger.error("Template not compatible with this tabular resource!");
			e.printStackTrace();
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.templateNotCompatibleException));
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error StartTemplateApply: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error StartTemplateApply: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void templateDelete(TemplateDeleteSession templateDeleteSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("startTemplateDelete: " + templateDeleteSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			TemplateId templateId;
			for (TemplateData template : templateDeleteSession.getTemplates()) {
				templateId = new TemplateId(template.getId());
				service.remove(templateId);
			}

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (NoSuchTemplateException e) {
			logger.debug("Error StartTemplateDelete: No such Template");
			e.printStackTrace();
			throw new TDGWTServiceException("Error StartTemplateDelete: No such Template");
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("Error StartTemplateDelete: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error StartTemplateDelete: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startReplaceColumn(ReplaceColumnSession replaceColumnSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setReplaceColumnSession(httpRequest, serviceCredentials, replaceColumnSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(replaceColumnSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(replaceColumnSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ReplaceColumn opEx = new OpExecution4ReplaceColumn(service, replaceColumnSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Replace Column Value invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ReplaceValue,
					replaceColumnSession.getColumnData().getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in operation for replace column value: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startReplaceBatchColumn(ReplaceBatchColumnSession replaceBatchColumnSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setReplaceBatchColumnSession(httpRequest, serviceCredentials, replaceBatchColumnSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(replaceBatchColumnSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(replaceBatchColumnSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ReplaceBatch opEx = new OpExecution4ReplaceBatch(service, replaceBatchColumnSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();

			List<OperationExecution> invocations = director.getListOperationExecution();

			if (invocations == null) {
				throw new TDGWTServiceException("Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocations.toString());
			Task trTask = service.executeBatch(invocations, tabularResourceId);
			if (trTask == null) {
				logger.error("Error on service Task null");
				throw new TDGWTServiceException("Task not started");
			}
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ReplaceBatch,
					replaceBatchColumnSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error in operation for batch replace on column: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<String> getLocales() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getLocales()");
			List<String> listLocales = Arrays.asList(Locales.ALLOWED_LOCALES);
			ArrayList<String> locales = new ArrayList<String>();
			locales.addAll(listLocales);

			logger.debug("locales :" + locales);
			return locales;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.debug("getLocales(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving locales: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public OpHistory getLastOperationInfo() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getLastOperationInfo()");
			TabResource currentTR = SessionUtil.getTabResource(httpRequest, serviceCredentials);
			if (currentTR == null) {
				logger.error("Current Tabular Resource is null");
				throw new TDGWTServiceException("Current Tabular Resource is null");
			}
			TRId trId = currentTR.getTrId();

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);
			List<HistoryStep> history = tabularResource.getHistory();
			long historyId = 0;
			String opDesc = null, opName = null;
			HistoryStep lastStep = null;
			OpHistory op = null;

			if (history != null && history.size() > 0) {
				lastStep = history.get(0);
				if (lastStep != null) {
					opDesc = lastStep.getOperationDescription();
					opName = opDesc;
					historyId = lastStep.getId().getValue();
					op = new OpHistory(historyId, opName, opDesc, sdf.format(lastStep.getExecutionDate().getTime()));
				}

			}

			logger.debug("Last Operation Info :" + op);
			return op;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("getLastOperationInfo(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving last operation info: " + e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public OpHistory getLastOperationInfo(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getLastOperationInfo: " + trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			List<HistoryStep> history = tabularResource.getHistory();
			long historyId = 0;
			String opDesc = null, opName = null;
			HistoryStep lastStep = null;
			OpHistory op = null;

			if (history != null && history.size() > 0) {
				lastStep = history.get(0);
				if (lastStep != null) {
					opDesc = lastStep.getOperationDescription();
					opName = opDesc;
					historyId = lastStep.getId().getValue();
					op = new OpHistory(historyId, opName, opDesc, sdf.format(lastStep.getExecutionDate().getTime()));
				}

			}
			logger.debug("Last Operation Info :" + op);
			return op;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("getLastOperationInfo(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving last operation info: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<OpHistory> getHistory() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getHistory()");
			TabResource currentTR = SessionUtil.getTabResource(httpRequest, serviceCredentials);
			if (currentTR == null) {
				logger.error("Current Tabular Resource is null");
				throw new TDGWTServiceException("Current Tabular Resource is null");
			}
			TRId trId = currentTR.getTrId();

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);
			List<HistoryStep> history = tabularResource.getHistory();

			ArrayList<OpHistory> opHistoryList = new ArrayList<OpHistory>();

			long historyId = 0;
			String opDesc = null, opName = null;
			OpHistory op = null;

			for (HistoryStep step : history) {
				if (step != null) {
					historyId = step.getId().getValue();
					opDesc = step.getOperationDescription();
					opName = step.getOperationDescription();
					op = new OpHistory(historyId, opName, opDesc, sdf.format(step.getExecutionDate().getTime()));
					opHistoryList.add(op);
				}
			}

			logger.debug("History :" + opHistoryList);
			return opHistoryList;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("getHistory(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving history: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<OpHistory> getHistory(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getHistory(): " + trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);
			List<HistoryStep> history = tabularResource.getHistory();

			ArrayList<OpHistory> opHistoryList = new ArrayList<OpHistory>();

			long historyId = 0;
			String opDesc = null, opName = null;
			OpHistory op = null;

			for (HistoryStep step : history) {
				if (step != null) {
					historyId = step.getId().getValue();
					opDesc = step.getOperationDescription();
					opName = step.getOperationDescription();
					op = new OpHistory(historyId, opName, opDesc, sdf.format(step.getExecutionDate().getTime()));
					opHistoryList.add(op);
				}
			}

			logger.debug("History :" + opHistoryList);
			return opHistoryList;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("getHistory(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving history: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startDiscard(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("StartDiscard: " + trId);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			List<HistoryStep> history = tabularResource.getHistory();

			long historyId = 0;
			String opDesc = null, opName = null;
			OpHistory op = null;
			RollBackSession rollBackSession = null;
			String taskId = null;
			if (history != null) {
				if (history.size() > 1) {
					HistoryStep step = history.get(history.size() - 2);
					if (step != null) {
						historyId = step.getId().getValue();
						opDesc = step.getOperationDescription();
						opName = opDesc;
						op = new OpHistory(historyId, opName, opDesc, sdf.format(step.getExecutionDate().getTime()));
						logger.debug("Discard :" + op);

						rollBackSession = new RollBackSession(trId, historyId);
						logger.debug("Start Discard:" + sdfPerformance.format(new Date()));

						//
						logger.debug("rollBack(): " + rollBackSession);
						SessionUtil.setRollBackSession(httpRequest, serviceCredentials, rollBackSession);

						HistoryStepId historyStepId = new HistoryStepId(rollBackSession.getHistoryId());
						logger.debug("Start RollBack task:" + sdfPerformance.format(new Date()));
						Task trTask = service.rollbackTo(tabularResourceId, historyStepId);
						logger.debug("Start RollBack returned task:" + sdfPerformance.format(new Date()));

						if (trTask == null) {
							logger.error("Error on service Task null");
							throw new TDGWTServiceException("Task not started");
						}
						logger.debug("Start Task on service: " + trTask.getId());

						TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.RollBack,
								rollBackSession.getTrId());
						SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
						taskId = trTask.getId().getValue();

						//
						logger.debug("Start Discard returned task:" + sdfPerformance.format(new Date()));

					} else {
						logger.debug("Discard : step null");
					}
				} else {
					logger.debug("Discard : no previous step exist");
				}

			} else {
				logger.debug("Discard : not present");
			}

			return taskId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("discard(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Discard: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startRollBack(RollBackSession rollBackSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("rollBack(): " + rollBackSession);
			SessionUtil.setRollBackSession(httpRequest, serviceCredentials, rollBackSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(rollBackSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(rollBackSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			HistoryStepId historyStepId = new HistoryStepId(rollBackSession.getHistoryId());
			logger.debug("Start RollBack task:" + sdfPerformance.format(new Date()));
			Task trTask = service.rollbackTo(tabularResourceId, historyStepId);
			logger.debug("Start RollBack returned task:" + sdfPerformance.format(new Date()));

			if (trTask == null) {
				logger.error("Error on service Task null");
				throw new TDGWTServiceException("Task not started");
			}
			logger.debug("Start Task on service: " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.RollBack, rollBackSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.debug("rollBack(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("RollBack: " + e.getLocalizedMessage());
		}
	}

	private String retrieveTabularResourceIdFromTable(TabularDataService service, TableId tableId)
			throws TDGWTServiceException {
		try {

			Table table = service.getTable(tableId);

			if (table.contains(TableDescriptorMetadata.class)) {
				TableDescriptorMetadata tdm = table.getMetadata(TableDescriptorMetadata.class);
				return String.valueOf(tdm.getRefId());
			} else {
				throw new TDGWTServiceException("No TableDescriptorMetadata present in tableId: " + tableId);
			}

		} catch (Exception e) {
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public ValidationsTasksMetadata getValidationsTasksMetadata(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetTableValidationsMetadata on " + trId.toString());
			TabularDataService service = TabularDataServiceFactory.getService();
			TabularResourceId tabularResourceId = new TabularResourceId(new Long(trId.getId()));

			ArrayList<TaskS> taskSList = new ArrayList<TaskS>();
			List<Task> tasks = service.getTasks(tabularResourceId);
			if (tasks.size() > 0) {
				Task task = tasks.get(0);
				ArrayList<JobS> jobSList = new ArrayList<JobS>();
				int j = 1;
				for (Job job : task.getTaskJobs()) {
					int i = 1;
					ArrayList<Validations> validations = new ArrayList<Validations>();
					for (ValidationDescriptor val : job.getValidations()) {
						Validations validation = new Validations(String.valueOf(i), val.getTitle(),
								val.getDescription(), val.isValid(),
								ConditionCodeMap.mapConditionCode(val.getConditionCode()), val.getValidationColumn());

						validations.add(validation);
						i++;
					}

					InvocationS invocationS = null;
					if (job.getInvocation() != null) {
						OperationExecution operationExecution = job.getInvocation();
						HashMap<String, Object> mapSent = new HashMap<String, Object>();
						Map<String, Object> map = operationExecution.getParameters();
						Set<String> keys = map.keySet();
						Iterator<String> iterator = keys.iterator();
						while (iterator.hasNext()) {
							String key = iterator.next();
							Object o = map.get(key);
							mapSent.put(key, o.toString());
						}

						RefColumn refColumn = ExtractReferences.extract(operationExecution);

						invocationS = new InvocationS(j, operationExecution.getColumnId(),
								operationExecution.getIdentifier(), operationExecution.getOperationId(), mapSent,
								task.getId().getValue(), refColumn);
					}

					JobSClassifier jobClassifier = JobClassifierMap.map(job.getJobClassifier());

					JobS jobS = new JobS(String.valueOf(j), job.getProgress(), job.getHumaReadableStatus(),
							jobClassifier, job.getDescription(), validations, invocationS);
					jobSList.add(jobS);
					j++;

				}

				ArrayList<TRId> collateralTRIds = new ArrayList<TRId>();
				TaskResult taskResult = task.getResult();
				if (taskResult != null) {
					List<TableId> collateral = taskResult.getCollateralTables();
					for (TableId tId : collateral) {
						String id = retrieveTabularResourceIdFromTable(service, tId);
						TRId tabularRId = new TRId(id);
						tabularRId.setTableId(String.valueOf(tId));
						collateralTRIds.add(tabularRId);
					}

				}

				State state = TaskStateMap.map(task.getStatus());
				TaskS taskS = new TaskS(task.getId().getValue(), task.getProgress(), state, task.getErrorCause(),
						task.getSubmitter(), task.getStartTime(), task.getEndTime(), jobSList, collateralTRIds, null);
				taskSList.add(taskS);
			}

			ValidationsTasksMetadata validationsTasksMetadata = new ValidationsTasksMetadata(taskSList);

			logger.debug("ValidationsTasksMetadata: " + validationsTasksMetadata);
			return validationsTasksMetadata;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in  getValidationsTasksMetadata(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error in  getValidationsTasksMetadata: " + e.getLocalizedMessage());
		}
	}

	@Override
	public String startEditRow(EditRowSession editRowSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setEditRowSession(httpRequest, serviceCredentials, editRowSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(editRowSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(editRowSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4EditRow opEx = new OpExecution4EditRow(service, editRowSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();

			ArrayList<OperationExecution> invocations = director.getListOperationExecution();

			if (invocations == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocations.toString());
			Task trTask = service.executeBatch(invocations, tabularResourceId);

			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.EditRow, editRowSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in operation: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ColumnData getConnection(RefColumn refColumn) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("GetConnection on " + refColumn.toString());
			TabularDataService service = TabularDataServiceFactory.getService();
			TableId tableId = new TableId(new Long(refColumn.getTableId()));

			Table table = service.getTable(tableId);

			TableDescriptorMetadata tableDesc = null;
			TRId trId;

			if (table.contains(TableDescriptorMetadata.class)) {
				tableDesc = table.getMetadata(TableDescriptorMetadata.class);
				if (tableDesc.getRefId() == 0) {
					logger.error("Error refId=0 for Table:" + table.toString());
					throw new TDGWTServiceException("Error refId=0 for Table:" + table.toString());
				} else {
					logger.debug("Table connect to tabular resource: " + tableDesc.getRefId());
					TRId tId = new TRId(String.valueOf(tableDesc.getRefId()));
					trId = retrieveTabularResourceBasicData(tId);
				}

			} else {
				logger.debug(
						"Attention: No TableDescriptorMetadata found for table. Supposed Time Dimension :" + table);
				trId = new TRId();
				trId.setTableId(refColumn.getTableId());
			}

			ColumnData columnData = getColumn(refColumn.getColumnId(), trId);

			return columnData;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.debug("Error in getConnection(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error: " + e.getLocalizedMessage());

		}
	}

	@Override
	public String startTaskResubmit(TaskResubmitSession taskResubmitSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setTaskResubmitSession(httpRequest, serviceCredentials, taskResubmitSession);
			logger.debug("StartTaskResubmit: " + taskResubmitSession);
			if (taskResubmitSession == null) {
				logger.error("TaskResubmitSession is null");
				throw new TDGWTServiceException("Error in resubmit task: TaskResubmitSession is null");
			}
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(taskResubmitSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(taskResubmitSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			if (taskResubmitSession.getTaskId() == null || taskResubmitSession.getTaskId().isEmpty()) {
				logger.error("Task Id is: " + taskResubmitSession.getTaskId());
				throw new TDGWTServiceException(
						"Error in resubmit task, Task Id is: " + taskResubmitSession.getTaskId());
			}

			TaskId taskId = new TaskId(taskResubmitSession.getTaskId());

			Task trTask = service.resubmit(taskId);
			logger.debug("Resubmit Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ResubmitTask,
					taskResubmitSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in resubmit task: " + e.getLocalizedMessage());
		}

	}

	@Override
	public String startTaskResume(TaskResumeSession taskResumeSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setTaskResumeSession(httpRequest, serviceCredentials, taskResumeSession);
			logger.debug("StartTaskResume: " + taskResumeSession);
			if (taskResumeSession == null) {
				logger.error("TaskResumeSession is null");
				throw new TDGWTServiceException("Error in resume: TaskResumeSession is null");
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(taskResumeSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(taskResumeSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			if (taskResumeSession.getTaskId() == null || taskResumeSession.getTaskId().isEmpty()) {
				logger.error("Task Id is: " + taskResumeSession.getTaskId());
				throw new TDGWTServiceException("Error in resume task, Task Id is: " + taskResumeSession.getTaskId());
			}

			TaskId taskId = new TaskId(taskResumeSession.getTaskId());

			Map<String, Object> map = new HashMap<String, Object>();
			ArrayList<ColumnMappingData> columnMapping = taskResumeSession.getColumnMapping();

			Task trTask;
			if (columnMapping != null && columnMapping.size() > 0) {
				HashMap<TDTypeValue, Long> mapping = new HashMap<TDTypeValue, Long>();
				logger.debug("New Mapping");
				for (ColumnMappingData columnMappingData : columnMapping) {
					if (columnMappingData.getSourceArg() != null && columnMappingData.getTargetArg() != null) {
						DimensionRow source = columnMappingData.getSourceArg();
						DimensionRow target = columnMappingData.getTargetArg();

						TDTypeValue tdValue = TDTypeValueMap.map(taskResumeSession.getColumn().getDataTypeName(),
								source.getValue());
						logger.debug("Key = " + tdValue + " - " + new Long(target.getRowId()));
						mapping.put(tdValue, new Long(target.getRowId()));

					}

				}

				InvocationS invocationS = taskResumeSession.getInvocationS();

				if (invocationS != null) {
					if (invocationS.getJobNumber() != null) {
						if (invocationS.getTaskId() != null) {
							Integer jobNumber = invocationS.getJobNumber();
							TaskId previousTaskId = new TaskId(invocationS.getTaskId());
							Task previousTask = service.getTask(previousTaskId);
							List<Job> previousJobs = previousTask.getTaskJobs();
							if (previousJobs != null) {
								Job previousJob = previousJobs.get(jobNumber - 1);
								if (previousJob != null) {
									Map<String, Object> mapParameters = previousJob.getInvocation().getParameters();
									if (mapParameters != null) {
										@SuppressWarnings("unchecked")
										Map<TDTypeValue, Long> mappingPrevious = (Map<TDTypeValue, Long>) mapParameters
												.get(Constants.PARAMETER_COLUMN_MAPPING);
										logger.debug("Previous Mapping");
										if (mappingPrevious != null) {
											for (TDTypeValue key : mappingPrevious.keySet()) {
												logger.debug("Key = " + key + " - " + mappingPrevious.get(key));
												mapping.put(key, mappingPrevious.get(key));
											}
										} else {
											logger.debug("Previous Mapping is null");
										}
									} else {
										logger.debug("Parameters is null");
									}
								} else {
									logger.debug("PreviousJob is null");
								}
							} else {
								logger.debug("PreviousJobs is null");
							}
						} else {
							logger.debug("TaskId is null");
						}
					} else {
						logger.debug("JobNumber is null");
					}
				} else {
					logger.debug("InvocationS is null");
				}

				map.put(Constants.PARAMETER_COLUMN_MAPPING, mapping);
				trTask = service.resume(taskId, map);
				logger.debug("Resume Task on service: [TaskId:" + trTask.getId() + ", Map:" + map + "]");
			} else {
				trTask = service.resume(taskId);
				logger.debug("Resume Task on service: [TaskId:" + trTask.getId() + "]");

			}

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ResumeTask, taskResumeSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in resume task: " + e.getLocalizedMessage());
		}

	}

	@Override
	public String startExtractCodelist(ExtractCodelistSession extractCodelistSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setExtractCodelistSession(httpRequest, serviceCredentials, extractCodelistSession);
			logger.debug("StartExtractCodelist: " + extractCodelistSession);
			if (extractCodelistSession == null) {
				logger.error("ExtractCodelistSession is null");
				throw new TDGWTServiceException("Error in extract codelist: ExtractCodelistSession is null");
			}
			TabularDataService service = TabularDataServiceFactory.getService();
			checkTRId(extractCodelistSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(extractCodelistSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ExtractCodelist opEx = new OpExecution4ExtractCodelist(service, extractCodelistSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Extract Codelist on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ExtractCodelist,
					extractCodelistSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in extract codelist: " + e.getLocalizedMessage());
		}

	}

	public String startSplitColumn(SplitColumnSession splitColumnSession, HttpServletRequest httpRequest)
			throws TDGWTServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setSplitColumnSession(httpRequest, serviceCredentials, splitColumnSession);
			if (splitColumnSession == null) {
				logger.error("SplitColumnSession is null");
				throw new TDGWTServiceException("Error in split column: SplitColumnSession is null");
			}

			logger.debug("StartSplitColumn: " + splitColumnSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(splitColumnSession.getColumnData().getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(splitColumnSession.getColumnData().getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4SplitColumn opEx = new OpExecution4SplitColumn(service, splitColumnSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			ArrayList<OperationExecution> invocations = director.getListOperationExecution();

			if (invocations == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocations.toString());
			Task trTask = service.executeBatch(invocations, tabularResourceId);
			logger.debug("Split Column on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.SplitColumn,
					splitColumnSession.getColumnData().getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in split column: " + e.getLocalizedMessage());
		}

	}

	//
	public String startMergeColumn(MergeColumnSession mergeColumnSession, HttpServletRequest httpRequest)
			throws TDGWTServiceException {

		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setMergeColumnSession(httpRequest, serviceCredentials, mergeColumnSession);

			if (mergeColumnSession == null) {
				logger.error("MergeColumnSession is null");
				throw new TDGWTServiceException("Error in split column: MergeColumnSession is null");
			}

			logger.debug("StartMergeColumn: " + mergeColumnSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(mergeColumnSession.getColumnDataSource1().getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(mergeColumnSession.getColumnDataSource1().getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4MergeColumn opEx = new OpExecution4MergeColumn(service, mergeColumnSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			ArrayList<OperationExecution> invocations = director.getListOperationExecution();

			if (invocations == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocations.toString());
			Task trTask = service.executeBatch(invocations, tabularResourceId);
			logger.debug("Merge Column on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.MergeColumn,
					mergeColumnSession.getColumnDataSource1().getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in split column: " + e.getLocalizedMessage());
		}

	}

	@Override
	public void setCodelistMappingSession(CodelistMappingSession codelistMappingSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setCodelistMappingSession(httpRequest, serviceCredentials, codelistMappingSession);
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error setting CodelistMappingSession parameter: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException(
					"Error setting CodelistMappingSession parameter: " + e.getLocalizedMessage());
		}

	}

	@Override
	public void getFileFromWorkspace(CodelistMappingSession codelistMappingSession) throws TDGWTServiceException {

		Workspace w = null;
		WorkspaceItem wi = null;

		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

		try {
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();

			HomeManager manager = factory.getHomeManager();

			Home home = manager.getHome();

			w = home.getWorkspace();
			wi = w.getItem(codelistMappingSession.getItemId());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error in import Codelist Mapping getFileFromWorkspace accessing the workspace: "
							+ e.getLocalizedMessage(),
					e);
		}

		if (wi == null) {
			logger.error("Error retrieving the item on workspace" + codelistMappingSession.getItemId());
			throw new TDGWTServiceException(
					"Error retrieving the item on workspace" + codelistMappingSession.getItemId());
		}

		try {
			logger.debug("WorkspaceItem [id:" + wi.getId() + " name:" + wi.getName() + " remotePath:"
					+ wi.getRemotePath() + "]");
		} catch (InternalErrorException e1) {
			e1.printStackTrace();
			throw new TDGWTServiceException("Error retrieving the item on workspace" + wi);
		}

		CodelistMappingFileUploadSession fileUploadSession = new CodelistMappingFileUploadSession();
		// CodelistMappingMonitor codelistMappingMonitor = new
		// CodelistMappingMonitor();
		FileUploadMonitor fileUploadMonitor = new FileUploadMonitor();

		fileUploadSession.setId(UUID.randomUUID().toString());
		fileUploadSession.setFileUploadState(FileUploadState.STARTED);
		// fileUploadSession.setCodelistMappingMonitor(codelistMappingMonitor);

		SessionUtil.setFileUploadMonitor(httpRequest, serviceCredentials, fileUploadMonitor);
		SessionUtil.setCodelistMappingFileUploadSession(httpRequest, serviceCredentials, fileUploadSession);

		try {
			FilesStorage filesStorage = new FilesStorage();
			InputStream is = filesStorage.retrieveInputStream(serviceCredentials.getUserName(), wi);

			FileUtil.setImportFileCodelistMapping(fileUploadSession, is, wi.getName(), Constants.FILE_XML_MIMETYPE);
		} catch (Throwable e) {
			FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(httpRequest, serviceCredentials);
			fum.setFailed("An error occured elaborating the file", FileUtil.exceptionDetailMessage(e));
			SessionUtil.setFileUploadMonitor(httpRequest, serviceCredentials, fum);
			fileUploadSession.setFileUploadState(FileUploadState.FAILED);
			SessionUtil.setCodelistMappingFileUploadSession(httpRequest, serviceCredentials, fileUploadSession);
			logger.error("Error elaborating the input stream", e);
			throw new TDGWTServiceException(
					"Error in import Codelist Mapping getFileFromWorkspace: " + e.getLocalizedMessage(), e);
		}

		logger.trace("changing state");
		FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(httpRequest, serviceCredentials);
		fum.setState(FileUploadState.COMPLETED);
		SessionUtil.setFileUploadMonitor(httpRequest, serviceCredentials, fum);

		SessionUtil.setCodelistMappingFileUploadSession(httpRequest, serviceCredentials, fileUploadSession);

	}

	@Override
	public String startCodelistMappingImport(CodelistMappingSession codelistMappingSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			if (codelistMappingSession == null) {
				throw new TDGWTServiceException("CodelistMappingSession is null");
			}

			logger.debug("StartCodelistMappingImport: " + codelistMappingSession);

			SessionUtil.setCodelistMappingSession(httpRequest, serviceCredentials, codelistMappingSession);

			String taskId = importCodelistMappingFileOnService(httpRequest, serviceCredentials, codelistMappingSession);
			return taskId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in Codelist Mapping import: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * @param user
	 * @param codelistMappingFileUploadSession
	 * @param codelistMappingSession
	 * @throws TDGWTServiceException
	 */
	private String importCodelistMappingFileOnService(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, CodelistMappingSession codelistMappingSession) throws Throwable {

		String storageId = null;

		if (codelistMappingSession.getSource().getId().compareTo(SourceType.URL.toString()) == 0) {
			FilesStorage filesStorage = new FilesStorage();
			storageId = filesStorage.storageCodelistMappingTempFile(serviceCredentials.getUserName(),
					codelistMappingSession.getUrl());
		} else {
			CodelistMappingFileUploadSession codelistMappingFileUploadSession = SessionUtil
					.getCodelistMappingFileUploadSession(httpRequest, serviceCredentials);
			if (codelistMappingFileUploadSession == null) {
				throw new TDGWTServiceException("Error retrieving the codelistMappingFileUploadSession: null");
			}
			logger.debug("File Storage Access");
			FilesStorage filesStorage = new FilesStorage();
			String fileIdOnStorage = filesStorage.storageCodelistMappingTempFile(serviceCredentials.getUserName(),
					codelistMappingFileUploadSession.getCodelistMappingFile());
			logger.debug("File Url On Storage:" + fileIdOnStorage);
			codelistMappingFileUploadSession.getCodelistMappingFile().delete();

			if (fileIdOnStorage == null || fileIdOnStorage.isEmpty()) {
				throw new TDGWTServiceException("Tabular Data Service error loading file on storage");
			}
			storageId = fileIdOnStorage;
		}

		TabularDataService service = TabularDataServiceFactory.getService();

		checkTRId(codelistMappingSession.getTrId(), httpRequest, serviceCredentials);

		TabularResourceId tabularResourceId = new TabularResourceId(
				Long.valueOf(codelistMappingSession.getTrId().getId()));
		TabularResource tabularResource = service.getTabularResource(tabularResourceId);

		checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
		checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
		checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

		OpExecution4CodelistMapping opEx = new OpExecution4CodelistMapping(service, codelistMappingSession, storageId);
		OpExecutionDirector director = new OpExecutionDirector();
		director.setOperationExecutionBuilder(opEx);
		director.constructOperationExecution();
		OperationExecution invocation = director.getOperationExecution();

		if (invocation == null) {
			throw new TDGWTServiceException("Error in invocation: Operation not supported");
		}

		logger.debug("OperationInvocation: \n" + invocation.toString());
		Task trTask = service.execute(invocation, tabularResourceId);
		logger.debug("Codelist Mapping Import on service: TaskId " + trTask.getId());
		TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.CodelistMappingImport,
				codelistMappingSession.getTrId());
		SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
		return trTask.getId().getValue();

	}

	// TODO
	public String startGroupBy(GroupBySession groupBySession, HttpServletRequest httpRequest)
			throws TDGWTServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setGroupBySession(httpRequest, serviceCredentials, groupBySession);
			logger.debug("StartGroupBy: " + groupBySession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(groupBySession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(groupBySession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4GroupBy opEx = new OpExecution4GroupBy(service, groupBySession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("GroupBy start on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.GroupBy, groupBySession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in start group by: " + e.getLocalizedMessage());
		}
	}

	// TODO
	public String startTimeAggregation(TimeAggregationSession timeAggregationSession, HttpServletRequest httpRequest)
			throws TDGWTServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setTimeAggregationSession(httpRequest, serviceCredentials, timeAggregationSession);
			logger.debug("StartGroupBy: " + timeAggregationSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(timeAggregationSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(timeAggregationSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4TimeAggregation opEx = new OpExecution4TimeAggregation(service, timeAggregationSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("GroupBy start on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.TimeAggregation,
					timeAggregationSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in start Time Aggregation: " + e.getLocalizedMessage());
		}
	}

	@Override
	public String startNormalization(NormalizationSession normalizationSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setNormalizationSession(httpRequest, serviceCredentials, normalizationSession);
			if (normalizationSession == null) {
				logger.error("NormalizationSession is null");
				throw new TDGWTServiceException("Error in normalization : NormalizationSession is null");
			}

			logger.debug("StartNormalization: " + normalizationSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(normalizationSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(normalizationSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4Normalization opEx = new OpExecution4Normalization(service, normalizationSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Normalization start on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.Normalize, normalizationSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in start normalization: " + e.getLocalizedMessage());
		}

	}

	@Override
	public String startDenormalization(DenormalizationSession denormalizationSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setDenormalizationSession(httpRequest, serviceCredentials, denormalizationSession);
			if (denormalizationSession == null) {
				logger.error("DenormalizationSession is null");
				throw new TDGWTServiceException("Error in normalization : DenormalizationSession is null");
			}

			logger.debug("StartDenormalization: " + denormalizationSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(denormalizationSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(denormalizationSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4Denormalization opEx = new OpExecution4Denormalization(service, denormalizationSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Denormalization start on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.Denormalize,
					denormalizationSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in start denormalization: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startUnion(UnionSession unionSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setUnionSession(httpRequest, serviceCredentials, unionSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(unionSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(unionSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4Union opEx = new OpExecution4Union(service, unionSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Union invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.Union, unionSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in union: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void startChangeColumnsPosition(ChangeColumnsPositionSession changeColumnsPositionSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setChangeColumnsPositionSession(httpRequest, serviceCredentials, changeColumnsPositionSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(changeColumnsPositionSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(changeColumnsPositionSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ChangeColumnsPosition opEx = new OpExecution4ChangeColumnsPosition(service,
					changeColumnsPositionSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in Change Position invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			service.executeSynchMetadataOperation(invocation, tabularResourceId);

			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in change position: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startReplaceByExternal(ReplaceByExternalSession replaceByExternalSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setReplaceByExternalSession(httpRequest, serviceCredentials, replaceByExternalSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(replaceByExternalSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(replaceByExternalSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ReplaceByExternal opEx = new OpExecution4ReplaceByExternal(service, replaceByExternalSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error replace by external invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ReplaceByExternal,
					replaceByExternalSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in replace by external: " + e.getLocalizedMessage());
		}

	}

	@Override
	public String startDataMinerOperation(DataMinerOperationSession dataMinerOperationSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			logger.info("StartDataMinerOperation");
			logger.debug("StartDataMinerOperation: " + dataMinerOperationSession);
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setDataMinerOperationSession(httpRequest, serviceCredentials, dataMinerOperationSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(dataMinerOperationSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(dataMinerOperationSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecution4DataMinerOperation opEx = new OpExecution4DataMinerOperation(service, serviceCredentials,
					dataMinerOperationSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error DataMiner Operation invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());
			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.StatisticalOperation,
					dataMinerOperationSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in DataMiner operation: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public OperationMonitor getOperationMonitor(OperationMonitorSession operationMonitorSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TaskWrapper taskWrapper = SessionUtil.getStartedTask(httpRequest, serviceCredentials,
					operationMonitorSession.getTaskId());
			logger.debug("Start Monitor Time:" + sdfPerformance.format(new Date()));
			OperationMonitorCreator operationMonitorCreator = new OperationMonitorCreator(httpRequest,
					serviceCredentials, taskWrapper, operationMonitorSession);
			OperationMonitor operationMonitor = operationMonitorCreator.create();

			logger.debug("OperationMonitor(): " + operationMonitor);
			return operationMonitor;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in Operation Monitor: " + e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<LicenceData> getLicences() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			SessionUtil.getServiceCredentials(httpRequest);

			ArrayList<LicenceData> licences = new ArrayList<LicenceData>();
			Licence[] licencesArray = Licence.values();
			Licence licence;
			LicenceData licenceData;
			for (int i = 0; i < licencesArray.length; i++) {
				licence = licencesArray[i];
				licenceData = new LicenceData(i, licence.toString(), licence.getName());
				licences.add(licenceData);
			}

			logger.debug("Licences: " + licences.size());
			return licences;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in get Licences: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public OperationMonitor getBackgroundOperationMonitorForSpecificTask(
			OperationMonitorSession operationMonitorSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			OperationMonitor operationMonitor = null;

			if (operationMonitorSession != null && operationMonitorSession.getTaskId() != null
					&& !operationMonitorSession.getTaskId().isEmpty()) {
				HashMap<String, TaskWrapper> taskInBackgroundMap = SessionUtil.getTaskInBackgroundMap(httpRequest,
						serviceCredentials);
				if (taskInBackgroundMap != null) {
					TaskWrapper taskWrapper = taskInBackgroundMap.get(operationMonitorSession.getTaskId());
					if (taskWrapper != null) {
						// No foreground support
						operationMonitorSession.setInBackground(true);
						BackgroundOperationMonitorCreator backgroundOperationMonitorCreator = new BackgroundOperationMonitorCreator(
								httpRequest, serviceCredentials, taskWrapper, operationMonitorSession);
						operationMonitor = backgroundOperationMonitorCreator.create();
						logger.debug("BackgroundOperationMonitorForSpecificTask(): " + operationMonitor);
					} else {
						throw new TDGWTServiceException(
								"Error in Background Operation Monitor of specific task: " + "task not found");
					}

				} else {
					throw new TDGWTServiceException(
							"Error in Background Operation Monitor of specific task: " + "No tasks in session");
				}
			} else {
				throw new TDGWTServiceException("Error in Background Operation Monitor of specific task: "
						+ "operationMonitorSession invalid:" + operationMonitorSession);
			}

			return operationMonitor;

		} catch (TDGWTServiceException e) {
			e.printStackTrace();
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error in Background Operation Monitor of specific task: " + e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<BackgroundOperationMonitor> getBackgroundOperationMonitor(
			BackgroundOperationMonitorSession backgroundOperationMonitorSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getBackgroundOperationMonitor(): " + backgroundOperationMonitorSession);

			ArrayList<OperationMonitor> operationMonitorList = new ArrayList<OperationMonitor>();

			HashMap<String, TaskWrapper> taskInBackgroundMap = SessionUtil.getTaskInBackgroundMap(httpRequest,
					serviceCredentials);

			if (taskInBackgroundMap != null && taskInBackgroundMap.size() > 0) {
				logger.debug("Tasks In Background : " + taskInBackgroundMap.size());
				OperationMonitorSession operationMonitorSession;
				for (Map.Entry<String, TaskWrapper> taskInBackground : taskInBackgroundMap.entrySet()) {
					operationMonitorSession = new OperationMonitorSession(taskInBackground.getKey());
					operationMonitorSession.setInBackground(true);
					if (backgroundOperationMonitorSession != null) {
						ArrayList<OperationMonitorSession> operationMonitorSessionList = backgroundOperationMonitorSession
								.getOperationMonitorSessionList();
						for (OperationMonitorSession opMonitorSession : operationMonitorSessionList) {
							if (opMonitorSession.getTaskId().compareTo(taskInBackground.getKey()) == 0) {
								operationMonitorSession = opMonitorSession;
								break;
							}
						}

					}

					BackgroundOperationMonitorCreator backgroundOperationMonitorCreator = new BackgroundOperationMonitorCreator(
							httpRequest, serviceCredentials, taskInBackground.getValue(), operationMonitorSession);
					OperationMonitor operationMonitor = backgroundOperationMonitorCreator.create();
					logger.debug("getBackgroundOperationMonitor(): " + operationMonitor);
					operationMonitorList.add(operationMonitor);
				}

			} else {
				logger.debug("Tasks In Background : 0");
			}

			ArrayList<BackgroundOperationMonitor> backgroundOperationMonitorList = new ArrayList<BackgroundOperationMonitor>();

			for (OperationMonitor opM : operationMonitorList) {
				BackgroundOperationMonitor backgroundOperationMonitor = new BackgroundOperationMonitor(opM.getTaskId(),
						opM.getTask().getProgress(), opM.getTask().getState(), opM.getTask().getErrorCause(),
						opM.getTask().getSubmitter(), opM.getTask().getStartTime(), opM.getTask().getEndTime(),
						opM.isInBackground(), opM.isAbort(), opM.isHidden(), opM.getOperationId(), opM.getTrId(),
						opM.getTask().getTabularResourceId(), opM.getTabularResourceName());
				backgroundOperationMonitorList.add(backgroundOperationMonitor);

			}

			logger.debug("getBackgroundOperationMonitor(): " + backgroundOperationMonitorList.size()
					+ "operation monitor retrieved");
			return backgroundOperationMonitorList;

		} catch (TDGWTServiceException e) {
			e.printStackTrace();
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in Background Operation Monitor: " + e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ResourceTDDescriptor> getResourcesTD(TRId trId) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			List<ResourceDescriptor> resources = service.getResources(tabularResourceId);
			ResourceTDCreator resourceTDCreator = new ResourceTDCreator(serviceCredentials);

			ArrayList<ResourceTDDescriptor> resourcesTD = resourceTDCreator.createResourcesDescriptorTD(resources);

			return resourcesTD;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving resources: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving resources: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ResourceTDDescriptor> getResourcesTDByType(TRId trId, ResourceTDType resourceTDType)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(trId, httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			ResourceType resourceType = ResourceTypeMap.getResourceType(resourceTDType);

			List<ResourceDescriptor> resources = service.getResourcesByType(tabularResourceId, resourceType);
			ResourceTDCreator resourceTDCreator = new ResourceTDCreator(serviceCredentials);
			ArrayList<ResourceTDDescriptor> resourcesTD = resourceTDCreator.createResourcesDescriptorTD(resources);

			return resourcesTD;
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error retrieving resources by type: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving resources by type: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void removeResource(RemoveResourceSession removeResourceSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("Remove Resource: " + removeResourceSession);

			if (removeResourceSession == null) {
				logger.error("Error removing Resource: removeResourceSession null");
				throw new TDGWTServiceException("Error removing Resource no parameters set");
			}
			TRId trId = removeResourceSession.getTrId();
			if (trId == null) {
				logger.error("Error removing Resource: trId is null");
				throw new TDGWTServiceException("Error removing Resource no tabular resource set");
			}
			TabularDataService service = TabularDataServiceFactory.getService();
			checkTRId(trId, httpRequest, serviceCredentials);
			TabularResourceId tabResourceId = new TabularResourceId(Long.valueOf(trId.getId()));
			TabularResource tabularResource = service.getTabularResource(tabResourceId);

			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			String owner = tabularResource.getOwner();
			if (owner != null && owner.compareTo(serviceCredentials.getUserName()) == 0) {
				if (removeResourceSession.getResources() == null) {
					logger.error("Error removing Resource: descriptor null");
					throw new TDGWTServiceException("Error removing Resource no descriptor set");
				}
				for (ResourceTDDescriptor resourceTDDescriptor : removeResourceSession.getResources()) {
					service.removeResurce(resourceTDDescriptor.getId());
				}
			} else {
				throw new TDGWTServiceException(
						"You are not the owner of this tabular resource (owner: " + owner + ")");
			}
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error deleting the resource: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error deleting the resource: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startMapCreation(MapCreationSession mapCreationSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setMapCreationSession(httpRequest, serviceCredentials, mapCreationSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(mapCreationSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(mapCreationSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecution4MapCreation opEx = new OpExecution4MapCreation(service, mapCreationSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error Generate Map invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.GenerateMap, mapCreationSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error creating the map: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String getUriFromResolver(UriResolverSession uriResolverSession) throws TDGWTServiceException {
		String link = "";
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("Get uri from resolver: " + uriResolverSession);

			UriResolverTDClient uriResolverTDClient = new UriResolverTDClient();
			link = uriResolverTDClient.resolve(uriResolverSession, serviceCredentials);

			return link;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving uri from resolver: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startChartTopRating(ChartTopRatingSession chartTopRatingSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setChartTopRatingSession(httpRequest, serviceCredentials, chartTopRatingSession);

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(chartTopRatingSession.getColumn().getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(chartTopRatingSession.getColumn().getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);

			OpExecution4ChartTopRating opEx = new OpExecution4ChartTopRating(service, chartTopRatingSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in Top Rating Chart invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("Start Task on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.ChartTopRating,
					chartTopRatingSession.getColumn().getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);

			return trTask.getId().getValue();
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error creating top rating chart: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void saveResource(SaveResourceSession saveResourceSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setSaveResourceSession(httpRequest, serviceCredentials, saveResourceSession);
			logger.debug("SaveResource(): " + saveResourceSession);

			String mimeType;
			MimeTypeSupport mimeTypeSupport = saveResourceSession.getMime();
			if (mimeTypeSupport.compareTo(MimeTypeSupport._unknow) == 0) {
				mimeType = null;
			} else {
				mimeType = mimeTypeSupport.getMimeName();
			}

			ResourceTDDescriptor resourceTDDescriptor = saveResourceSession.getResourceTDDescriptor();

			ResourceTDType resourceTDType = resourceTDDescriptor.getResourceType();

			if (resourceTDType == null) {
				logger.error("Unknow resource type: null");
				throw new TDGWTServiceException("Resource has type null!");
			}

			switch (resourceTDType) {
			case CHART:
			case CODELIST:
			case CSV:
			case GENERIC_FILE:
			case GENERIC_TABLE:
			case GUESSER:
			case JSON:
			case SDMX:
				saveResourceByStorageId(saveResourceSession, serviceCredentials, mimeType, resourceTDDescriptor);
				break;
			case MAP:
				ApplicationType applicationType = ApplicationType.GIS;
				saveResourceByInputStream(saveResourceSession, serviceCredentials, mimeType, resourceTDDescriptor,
						applicationType);
				break;
			default:
				break;

			}

			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error saving the resource: " + e.getLocalizedMessage());
		}

	}

	protected void saveResourceByStorageId(SaveResourceSession saveResourceSession,
			ServiceCredentials serviceCredentials, String mimeType, ResourceTDDescriptor resourceTDDescriptor)
			throws TDGWTServiceException {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		String storageId = null;
		if (resource instanceof StringResourceTD) {
			StringResourceTD stringResourceTD = (StringResourceTD) resource;
			storageId = stringResourceTD.getStringValue();
		} else {
			if (resource instanceof InternalURITD) {
				InternalURITD internalURITD = (InternalURITD) resource;
				storageId = internalURITD.getId();
				mimeType = internalURITD.getMimeType();
			} else {
				if (resource instanceof TableResourceTD) {
					throw new TDGWTServiceException("Save is not supported for this resource!");
				} else {
					throw new TDGWTServiceException("Save is not supported for this resource!");

				}
			}
		}

		if (storageId == null || storageId.isEmpty()) {
			throw new TDGWTServiceException("This resource does not have valid storage id: " + storageId);
		}

		MimeTypeSupport mime = saveResourceSession.getMime();
		String fileName = "";

		if (mime == null || mime.compareTo(MimeTypeSupport._unknow) == 0) {
			fileName = saveResourceSession.getFileName();
		} else {
			fileName = saveResourceSession.getFileName() + mime.getExtension();
		}

		logger.debug("Create Item On Workspace: [storageId=" + storageId + " ,user: " + serviceCredentials.getUserName()
				+ " ,fileName: " + fileName + " ,fileDescription: " + saveResourceSession.getFileDescription()
				+ " ,mimetype:" + mimeType + " ,folder: " + saveResourceSession.getItemId() + "]");

		if (storageId.startsWith("http:") || storageId.startsWith("https:")) {
			FilesStorage storage = new FilesStorage();
			storage.createItemOnWorkspace(storageId, serviceCredentials.getUserName(), fileName,
					saveResourceSession.getFileDescription(), mimeType, saveResourceSession.getItemId());
		} else {
			FilesStorage storage = new FilesStorage();
			storage.createItemOnWorkspaceByStorageId(storageId, serviceCredentials.getUserName(), fileName,
					saveResourceSession.getFileDescription(), mimeType, saveResourceSession.getItemId());
		}
	}

	protected void saveResourceByInputStream(SaveResourceSession saveResourceSession,
			ServiceCredentials serviceCredentials, String mimeType, ResourceTDDescriptor resourceTDDescriptor,
			ApplicationType applicationType) throws TDGWTServiceException {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		UriResolverSession uriResolverSession;
		if (resource instanceof StringResourceTD) {
			StringResourceTD stringResourceTD = (StringResourceTD) resource;
			String uri = stringResourceTD.getStringValue();
			uriResolverSession = new UriResolverSession(uri, applicationType);
		} else {
			if (resource instanceof InternalURITD) {
				InternalURITD internalURITD = (InternalURITD) resource;
				uriResolverSession = new UriResolverSession(internalURITD.getId(), applicationType,
						resourceTDDescriptor.getName(), internalURITD.getMimeType());
				mimeType = internalURITD.getMimeType();
			} else {
				if (resource instanceof TableResourceTD) {
					throw new TDGWTServiceException("Save is not supported for this resource!");
				} else {
					throw new TDGWTServiceException("Save is not supported for this resource!");

				}
			}
		}

		UriResolverTDClient uriResolverTDClient = new UriResolverTDClient();
		String link = uriResolverTDClient.resolve(uriResolverSession, serviceCredentials);

		if (link == null || link.isEmpty()) {
			throw new TDGWTServiceException("This resource does not have valid link: " + link);
		}

		MimeTypeSupport mime = saveResourceSession.getMime();
		String fileName = "";

		if (mime == null || mime.compareTo(MimeTypeSupport._unknow) == 0) {
			fileName = saveResourceSession.getFileName();
		} else {
			fileName = saveResourceSession.getFileName() + mime.getExtension();
		}

		logger.debug("Create Item On Workspace: [uri=" + link + " ,user: " + serviceCredentials.getUserName()
				+ " ,fileName: " + fileName + " ,fileDescription: " + saveResourceSession.getFileDescription()
				+ " ,mimetype:" + mimeType + " ,folder: " + saveResourceSession.getItemId() + "]");

		FilesStorage storage = new FilesStorage();
		storage.createItemOnWorkspace(link, serviceCredentials.getUserName(), fileName,
				saveResourceSession.getFileDescription(), mimeType, saveResourceSession.getItemId());
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startGeospatialCreateCoordinates(
			GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession) throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setGeospatialCreateCoordinatesSession(httpRequest, serviceCredentials,
					geospatialCreateCoordinatesSession);
			if (geospatialCreateCoordinatesSession == null) {
				logger.error("GeospatialCreateCoordinatesSession is null");
				throw new TDGWTServiceException(
						"Error creating geospatial coordinates: GeospatialCreateCoordinatesSession is null");
			}

			logger.debug("StartGeospatialCreateCoordinates: " + geospatialCreateCoordinatesSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(geospatialCreateCoordinatesSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(geospatialCreateCoordinatesSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4GeospatialCreateCoordinates opEx = new OpExecution4GeospatialCreateCoordinates(
					serviceCredentials, service, geospatialCreateCoordinatesSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("GeospatialCreateCoordinates on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.GeospatialCreateCoordinates,
					geospatialCreateCoordinatesSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error creating geospatial coordinates: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startGeospatialDownscaleCSquare(GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setGeospatialDownscaleCSquareSession(httpRequest, serviceCredentials,
					geospatialDownscaleCSquareSession);
			if (geospatialDownscaleCSquareSession == null) {
				logger.error("GeospatialDownscaleCSquareSession is null");
				throw new TDGWTServiceException(
						"Error in downscale C-Square: GeospatialDownscaleCSquareSession is null");
			}

			logger.debug("StartGeospatialDownscaleCSquare: " + geospatialDownscaleCSquareSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(geospatialDownscaleCSquareSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(geospatialDownscaleCSquareSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4GeospatialDownscaleCSquare opEx = new OpExecution4GeospatialDownscaleCSquare(service,
					geospatialDownscaleCSquareSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.execute(invocation, tabularResourceId);
			logger.debug("GeospatialCreateCoordinates on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.DownscaleCSquare,
					geospatialDownscaleCSquareSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in downscale C-Square: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startGeometryCreatePoint(GeometryCreatePointSession geometryCreatePointSession)
			throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setGeometryCreatePointSession(httpRequest, serviceCredentials, geometryCreatePointSession);
			if (geometryCreatePointSession == null) {
				logger.error("GeometryCreatePointSession is null");
				throw new TDGWTServiceException("Error creating geometry point: GeometryCreatePointSession is null");
			}

			logger.debug("StartGeometryCreatePoint: " + geometryCreatePointSession);
			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(geometryCreatePointSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(geometryCreatePointSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			OpExecution4GeometryCreatePoint opEx = new OpExecution4GeometryCreatePoint(service,
					geometryCreatePointSession);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();

			ArrayList<OperationExecution> invocation = director.getListOperationExecution();

			if (invocation == null) {
				throw new TDGWTServiceException("Error in invocation: Operation not supported");
			}

			logger.debug("OperationInvocation: \n" + invocation.toString());
			Task trTask = service.executeBatch(invocation, tabularResourceId);
			logger.debug("GeometryCreatePoint on service: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.GeometryCreatePoint,
					geometryCreatePointSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error creating geometry point: " + e.getLocalizedMessage());
		}

	}

	public String startApplyAndDetachColumnnRules(ApplyAndDetachColumnRulesSession applyAndDetachColumnRulesSession,
			HttpServletRequest httpRequest) throws TDGWTServiceException {
		try {
			// HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setRulesOnColumnApplyAndDetachSession(httpRequest, serviceCredentials,
					applyAndDetachColumnRulesSession);

			if (applyAndDetachColumnRulesSession == null) {
				logger.error("ApplyAndDetachColumnRulesSession is null");
				throw new TDGWTServiceException(
						"Error in apply rules on column: ApplyAndDetachColumnRulesSession is null");
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(applyAndDetachColumnRulesSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(applyAndDetachColumnRulesSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			ColumnData column = applyAndDetachColumnRulesSession.getColumn();
			if (column == null) {
				logger.error("Error in apply and detach rules on column: No column selected.");
				throw new TDGWTServiceException("Error in apply rules on column: No column selected.");
			}

			ColumnLocalId columnLocalId = new ColumnLocalId(column.getColumnId());

			ArrayList<RuleDescriptionData> rulesThatWillBeDetach = applyAndDetachColumnRulesSession
					.getRulesThatWillBeDetached();
			if (rulesThatWillBeDetach != null && rulesThatWillBeDetach.size() > 0) {
				ArrayList<RuleId> ruleIdsDetach = new ArrayList<RuleId>();
				for (RuleDescriptionData r : rulesThatWillBeDetach) {
					RuleId ruleDetachId = new RuleId(r.getId());
					ruleIdsDetach.add(ruleDetachId);
				}
				service.detachColumnRules(tabularResourceId, columnLocalId, ruleIdsDetach);
			}

			ArrayList<RuleDescriptionData> rulesThatWillBeApplied = applyAndDetachColumnRulesSession
					.getRulesThatWillBeApplied();
			if (rulesThatWillBeApplied != null && rulesThatWillBeApplied.size() > 0) {
				ArrayList<RuleId> ruleIdsApplied = new ArrayList<RuleId>();
				for (RuleDescriptionData r : rulesThatWillBeApplied) {
					RuleId ruleAppliedId = new RuleId(r.getId());
					ruleIdsApplied.add(ruleAppliedId);
				}
				Task trTask = service.applyColumnRule(tabularResourceId, columnLocalId, ruleIdsApplied);
				logger.debug("Rules On Column Apply: TaskId " + trTask.getId());

				TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.RuleOnColumnApply,
						applyAndDetachColumnRulesSession.getTrId());
				SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
				return trTask.getId().getValue();
			} else {
				return null;
			}
		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in apply rules on column: " + e.getLocalizedMessage());
		}

	}

	public String startApplyTableRule(ApplyTableRuleSession applyTableRuleSession, HttpServletRequest httpRequest)
			throws TDGWTServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("StartApplyTableRule: " + applyTableRuleSession);

			SessionUtil.setApplyTableRuleSession(httpRequest, serviceCredentials, applyTableRuleSession);

			if (applyTableRuleSession == null) {
				logger.error("Apply Table Rule Session is null: ");
				throw new TDGWTServiceException("Error in apply rule on table: ApplyTableRuleSession is null");
			}

			if (applyTableRuleSession.getRuleDescriptionData() == null) {
				logger.error("Apply Table Rule Session: RuleDescriptionData is null: " + applyTableRuleSession);
				throw new TDGWTServiceException("Error in apply rule on table: rule description is null");
			}

			if (applyTableRuleSession.getPlaceHolderToColumnMap() == null) {
				logger.error("Apply Table Rule Session: PlaceHolderToColumnMap is null: " + applyTableRuleSession);
				throw new TDGWTServiceException("Error in apply rule on table: map is null");
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(applyTableRuleSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(applyTableRuleSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			RuleId ruleId = new RuleId(applyTableRuleSession.getRuleDescriptionData().getId());

			Task trTask = service.applyTableRule(tabularResourceId, applyTableRuleSession.getPlaceHolderToColumnMap(),
					ruleId);
			logger.debug("Rules On Table Apply: TaskId " + trTask.getId());

			TaskWrapper taskWrapper = new TaskWrapper(trTask, UIOperationsId.RuleOnTableApply,
					applyTableRuleSession.getTrId());
			SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
			return trTask.getId().getValue();

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in apply rules on table: " + e.getLocalizedMessage());
		}

	}

	public void setDetachColumnnRules(DetachColumnRulesSession detachColumnRulesSession, HttpServletRequest httpRequest)
			throws TDGWTServiceException {
		try {
			// HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setDetachColumnRulesSession(httpRequest, serviceCredentials, detachColumnRulesSession);

			if (detachColumnRulesSession == null) {
				logger.error("DetachColumnRulesSession is null");
				throw new TDGWTServiceException("Error in detach rules on column: DetachColumnRulesSession is null");
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(detachColumnRulesSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(detachColumnRulesSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			ColumnData column = detachColumnRulesSession.getColumn();
			if (column == null) {
				logger.error("Error in detach rules on column: No column selected.");
				throw new TDGWTServiceException("Error in detach rules on column: No column selected.");
			}

			ColumnLocalId columnLocalId = new ColumnLocalId(column.getColumnId());

			ArrayList<RuleDescriptionData> rules = detachColumnRulesSession.getRules();
			ArrayList<RuleId> ruleIds = new ArrayList<RuleId>();
			if (rules != null && rules.size() > 0) {
				for (RuleDescriptionData r : rules) {
					RuleId ruleId = new RuleId(r.getId());
					ruleIds.add(ruleId);
				}
				service.detachColumnRules(tabularResourceId, columnLocalId, ruleIds);
			}
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in detach rules on column: " + e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * @param detachTableRulesSession
	 *            Session
	 * @param httpRequest
	 *            Http servlet request
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void setDetachTableRules(DetachTableRulesSession detachTableRulesSession, HttpServletRequest httpRequest)
			throws TDGWTServiceException {
		try {
			// HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SessionUtil.setDetachTableRulesSession(httpRequest, serviceCredentials, detachTableRulesSession);

			if (detachTableRulesSession == null) {
				logger.error("DetachTableRulesSession is null");
				throw new TDGWTServiceException("Error in detach rules on table: DetachTableRulesSession is null");
			}

			TabularDataService service = TabularDataServiceFactory.getService();

			checkTRId(detachTableRulesSession.getTrId(), httpRequest, serviceCredentials);

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(detachTableRulesSession.getTrId().getId()));
			TabularResource tabularResource = service.getTabularResource(tabularResourceId);

			checkTabularResourceIsFlow(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceLocked(tabularResource, httpRequest, serviceCredentials);
			checkTabularResourceIsFinal(tabularResource, httpRequest, serviceCredentials);

			ArrayList<RuleDescriptionData> rules = detachTableRulesSession.getRules();
			List<RuleId> ruleIds = new ArrayList<RuleId>();
			if (rules != null && rules.size() > 0) {
				for (RuleDescriptionData r : rules) {
					RuleId ruleId = new RuleId(r.getId());
					ruleIds.add(ruleId);
				}
				service.detachTableRules(tabularResourceId, ruleIds);

			}
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in detach rules on column: " + e.getLocalizedMessage());
		}

	}

	@Override
	public String getDefaultSDMXRegistryURL() throws TDGWTServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			String defaultSDMXRegistryURL = SessionUtil.getInternalSDMXRegistryURLSession(httpRequest,
					serviceCredentials);

			if (defaultSDMXRegistryURL == null || defaultSDMXRegistryURL.isEmpty()) {
				defaultSDMXRegistryURL = ISUtils.retrieveInternalSDMXRegistryURL();
				if (defaultSDMXRegistryURL != null && !defaultSDMXRegistryURL.isEmpty()) {
					SessionUtil.setInternalSDMXRegistryURLSession(httpRequest, serviceCredentials,
							defaultSDMXRegistryURL);
					int pos = defaultSDMXRegistryURL.indexOf("FusionRegistry");
					defaultSDMXRegistryURL = defaultSDMXRegistryURL.substring(0, pos + 15);

				}
			} else {
				int pos = defaultSDMXRegistryURL.indexOf("FusionRegistry");
				defaultSDMXRegistryURL = defaultSDMXRegistryURL.substring(0, pos + 15);
			}

			logger.debug("Default SDMXRegistry URL: " + defaultSDMXRegistryURL);
			return defaultSDMXRegistryURL;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(httpRequest);
			throw new TDGWTServiceException(messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("getDefaultSDMXRegistryURL(): " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving default SDMX Regitry URL: " + e.getLocalizedMessage());
		}
	}

}
