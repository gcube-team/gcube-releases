/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class Constants {
	public static final boolean DEBUG_MODE = false;
	public static final boolean TEST_ENABLE = false;

	public static final String CURR_GROUP_ID = "CURR_GROUP_ID";
	// public static final String CURR_USER_ID = "CURR_USER_ID";

	public static final String DEFAULT_USER = "giancarlo.panichi";
	public static final String DEFAULT_SCOPE = "/gcube/devNext/NextNext";
	public static final String DEFAULT_TOKEN = "ae1208f0-210d-47c9-9b24-d3f2dfcce05f-98187548";

	// public static final String DEFAULT_SCOPE = "/gcube/devNext";
	// public static final String DEFAULT_TOKEN =
	// "16e65d4f-11e0-4e4a-84b9-351688fccc12-98187548";

	public static final String DEFAULT_ROLE = "OrganizationMember";

	public final static String FILE_XML_MIMETYPE = "application/xml";
	public final static String FILE_CSV_MIMETYPE = "text/csv";

	public static final String SDMX_CODELIST_EXPORT_DEFAULT_ID = "NEW_CL_DIVISION";
	public static final String SDMX_CODELIST_EXPORT_DEFAULT_AGENCY_ID = "SDMX";
	public static final String SDMX_CODELIST_EXPORT_DEFAULT_VERSION = "1.0";

	public static final String SDMX_DATASET_EXPORT_DEFAULT_ID = "NEW_DS_DIVISION";
	public static final String SDMX_DATASET_EXPORT_DEFAULT_AGENCY_ID = "SDMX";
	public static final String SDMX_DATASET_EXPORT_DEFAULT_VERSION = "1.0";

	public static final String SDMX_TEMPLATE_EXPORT_DEFAULT_ID = "NEW_DSD_DIVISION_TEMPLATE";
	public static final String SDMX_TEMPLATE_EXPORT_DEFAULT_AGENCY_ID = "SDMX";
	public static final String SDMX_TEMPLATE_EXPORT_DEFAULT_VERSION = "1.0";

	public static final String PARAMETER_ENCODING = "encoding";
	public static final String PARAMETER_HASHEADER = "hasHeader";
	public static final String PARAMETER_FIELDMASK = "fieldMask";
	public static final String PARAMETER_SKIPERROR = "skipError";
	public static final String PARAMETER_SEPARATOR = "separator";
	public static final String PARAMETER_VIEW_COLUMNS = "useView";
	public static final String PARAMETER_COLUMNS = "columns";
	public static final String PARAMETER_URL = "url";
	public static final String PARAMETER_ID = "id";

	public static final String PARAMETER_REGISTRYBASEURL = "registryBaseUrl";
	public static final String PARAMETER_AGENCY = "agency";
	public static final String PARAMETER_VERSION = "version";
	public static final String PARAMETER_OBSVALUECOLUMN = "obsValueColumn";
	public static final String PARAMETER_TEMPLATE = "template";

	public static final String PARAMETER_DATA_TYPE = "dataType";
	public static final String PARAMETER_TARGET_DATA_TYPE = "targetDataType";
	public static final String PARAMETER_ADDITIONAL_META = "additionalMeta";

	public static final String PARAMETER_REPLACE_ID_COLUMN_VALUE = "value";
	public static final String PARAMETER_REPLACE_ID_COLUMN_ID = "id";

	public static final String PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_CONDITION = "condition";
	public static final String PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_VALUE = "value";

	public static final String PARAMETER_UNION_COMPOSITE_SOURCE = "source";
	public static final String PARAMETER_UNION_COMPOSITE_TARGET = "target";
	public static final String PARAMETER_UNION_COMPOSITE = "mappings";

	public static final String PARAMETER_STATISTICAL_OPERATION_USER = "user";
	public static final String PARAMETER_STATISTICAL_OPERATION_ALGORITHM = "algorithm";
	public static final String PARAMETER_STATISTICAL_OPERATION_PARAMETERS = "smEntries";
	public static final String PARAMETER_STATISTICAL_OPERATION_DESCRIPTION = "description";
	public static final String PARAMETER_STATISTICAL_OPERATION_TITLE = "title";

	public static final String PARAMETER_DATAMINER_GCUBETOKEN = "gcubetoken";
	public static final String PARAMETER_DATAMINER_OPERATOR = "operator";

	public static final String PARAMETER_ADD_ROW_COMPOSITE_FIELD = "field";
	public static final String PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE = "toSetValue";
	public static final String PARAMETER_ADD_ROW_COMPOSITE = "mapping";

	public static final String PARAMETER_IMPORT_CODELIST_MAPPING_OLDCODES = "old_codes";

	public static final String PARAMETER_EXTRACT_CODELIST_SOURCE = "source";
	public static final String PARAMETER_EXTRACT_CODELIST_TARGET_CODE_COLUMN = "target_code_column";
	public static final String PARAMETER_EXTRACT_CODELIST_COLUMN_DEFINITION = "column_definition";
	public static final String PARAMETER_EXTRACT_CODELIST_COLUMN_TYPE = "column_type";
	public static final String PARAMETER_EXTRACT_CODELIST_METADATA = "metadata";
	public static final String PARAMETER_EXTRACT_CODELIST_DEFAULT = "default";
	public static final String PARAMETER_EXTRACT_CODELIST_COMPOSITE = "mapping";
	public static final String PARAMETER_EXTRACT_CODELIST_RESOURCE_NAME = "resource_name";

	public static final String PARAMETER_EDIT_ROW_CONDITION = "condition";

	public static final String NAME_PARAMETER_ID = "NAME_PARAMETER_ID";
	public static final String PARAMETER_KEY = "key";
	public static final String PARAMETER_REFERENCE_COLUMN = "refColumn";
	public static final String PARAMETER_COLUMN_MAPPING = "mapping";

	public static final String PARAMETER_PERIOD_FORMAT = "periodFormat";
	public static final String PARAMETER_PERIOD_INPUT_FORMAT_ID = "inputFormatId";

	public static final String PARAMETER_EXPRESSION = "expression";

	public static final String PARAMETER_TABLE_TYPE = "tableType";

	public static final String PARAMETER_ROW_ID = "rowId";

	public static final String PARAMETER_ADD_COLUMN_COLUMN_TYPE = "columnType";
	public static final String PARAMETER_ADD_COLUMN_LABEL = "label";
	public static final String PARAMETER_ADD_COLUMN_DATA_TYPE = "dataType";
	public static final String PARAMETER_ADD_COLUMN_VALUE = "value";
	public static final String PARAMETER_ADD_COLUMN_META = "meta";

	public static final String PARAMETER_NORMALIZATION_TO_NORMALIZE = "to_normalize";
	public static final String PARAMETER_NORMALIZATION_NORM_LABEL = "norm_label";
	public static final String PARAMETER_NORMALIZATION_QUANT_LABEL = "quant_label";

	public static final String PARAMETER_DENORMALIZATION_VALUE_COLUMN = "value_column";
	public static final String PARAMETER_DENORMALIZATION_ATTRIBUTE_COLUMN = "attribute_column";

	public static final String PARAMETER_CLONE_TABLE = "table";

	public static final String PARAMETER_GENERATEMAP_MAPNAME = "mapName";
	public static final String PARAMETER_GENERATEMAP_FEATURE = "feature";
	public static final String PARAMETER_GENERATEMAP_GEOM = "geom";
	public static final String PARAMETER_GENERATEMAP_USEVIEW = "useView";
	public static final String PARAMETER_GENERATEMAP_METAABSTRACT = "metaAbstract";
	public static final String PARAMETER_GENERATEMAP_METAPURPOSE = "metaPurpose";
	public static final String PARAMETER_GENERATEMAP_USER = "User";
	public static final String PARAMETER_GENERATEMAP_METACREDITS = "metaCredits";
	public static final String PARAMETER_GENERATEMAP_METAKEYWORDS = "metaKeywords";

	public static final String PARAMETER_CHART_TOPRATING_SAMPLESIZE = "sampleSize";
	public static final String PARAMETER_CHART_TOPRATING_VALUEOPERATION = "valueOperation";

	public static final String PARAMETER_GEOSPATIAL_CREATE_COORDINATES_LATITUDE = "latitude";
	public static final String PARAMETER_GEOSPATIAL_CREATE_COORDINATES_LONGITUDE = "longitude";
	public static final String PARAMETER_GEOSPATIAL_CREATE_COORDINATES_FEATURE = "feature";
	public static final String PARAMETER_GEOSPATIAL_CREATE_COORDINATES_RESOLUTION = "resolution";
	public static final String PARAMETER_GEOSPATIAL_CREATE_COORDINATES_USER = "user";
	public static final String PARAMETER_GEOSPATIAL_CREATE_COORDINATES_QUADRANT = "quadrant";

	public static final String PARAMETER_DOWNSCALE_CSQUARE_RESOLUTION = "resolution";

	public static final String PARAMETER_EXPRESSION_VALIDATION_EXPRESSION = "expression";
	public static final String PARAMETER_EXPRESSION_VALIDATION_DESCRIPTION = "description";
	public static final String PARAMETER_EXPRESSION_VALIDATION_TITLE = "title";

	public static final String PARAMETER_CHANGE_COLUMN_POSITION_ORDER = "order";

	public static final String PARAMETER_RESOURCE_NAME = "name";
	public static final String PARAMETER_RESOURCE_DESCRIPTION = "description";

	public static final String CODELIST_MAPPING_UPLOAD_SERVLET = "CodelistMappingUploadServlet";
	public static final String LOCAL_UPLOAD_SERVLET = "LocalUploadServlet";
	public static final String CSV_IMPORT_FILE_SERVLET = "CSVImportFileServlet";
	public static final String RETRIEVE_CHART_FILE_SERVLET = "RetrieveChartFileServlet";
	public static final String RETRIEVE_FILE_AND_DISCOVER_MIME_TYPE_SERVLET = "RetrieveFileAndDiscoverMimeTypeServlet";
	public static final String TD_RSTUDIO_SERVLET = "TDRStudioServlet";
	public static final String TD_LOGS_SERVLET = "TDLogsServlet";

}
