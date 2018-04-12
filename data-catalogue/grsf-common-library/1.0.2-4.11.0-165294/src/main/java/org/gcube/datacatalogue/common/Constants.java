package org.gcube.datacatalogue.common;

/**
 * Constants shared between manager panel and service
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Constants {

	// base record (only custom field key)
	public static final String CATALOG_ID = "catalog_id";
	public static final String DESCRIPTION = "description";
	public static final String LICENSE_ID = "license_id";
	public static final String AUTHOR = "author";
	public static final String AUTHOR_CONTACT = "author_contact";
	public static final String VERSION = "version";
	public static final String MAINTAINER = "maintainer";
	public static final String MAINTAINER_CONTACT = "maintainer_contact";
	public static final String EXTRAS_FIELD = "extras_fields";
	public static final String EXTRAS_RESOURCES = "extras_resources";

	// common record
	public static final String UUID_KB_CUSTOM_KEY = "GRSF UUID";
	public static final String UUID_KB_JSON_KEY = "grsf_uuid";
	public static final String DOMAIN_CUSTOM_KEY  = "Domain";
	public static final String DATA_OWNER_CUSTOM_KEY = "Data owner";
	public static final String DATA_OWNER_JSON_KEY = "data_owner";
	public static final String DATABASE_SOURCES_JSON_KEY = "database_sources";
	public static final String SOURCES_OF_INFORMATION_JSON_KEY  = "source_of_information";
	public static final String REFERS_TO_JSON_KEY = "refers_to";
	public static final String SHORT_NAME_CUSTOM_KEY = "Short Name";
	public static final String SHORT_NAME_JSON_KEY = "short_name";
	public static final String TRACEABILITY_FLAG_CUSTOM_KEY = "Traceability Flag";
	public static final String TRACEABILITY_FLAG_JSON_KEY = "traceability_flag";
	public static final String SDG_FLAG_CUSTOM_KEY = "SDG Flag";
	public static final String SDG_FLAG_JSON_KEY = "sdg_flag";
	public static final String CATCHES_CUSTOM_KEY = "Catches";
	public static final String CATCHES_JSON_KEY = "catches";
	public static final String LANDINGS_CUSTOM_KEY = "Landings";
	public static final String LANDINGS_JSON_KEY = "landings";
	public static final String SPECIES_CUSTOM_KEY = "Species";
	public static final String SPECIES_JSON_KEY = "species";
	public static final String STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY = "Status of the GRSF record";
	public static final String STATUS_OF_THE_GRSF_RECORD_JSON_KEY = "status_grsf_record";
	public static final String SIMILAR_GRSF_RECORDS_CUSTOM_KEY = "Similar GRSF Record";
	public static final String SIMILAR_GRSF_RECORDS_JSON_KEY = "similar_grsf_records";
	public static final String SIMILAR_SOURCE_RECORDS_CUSTOM_KEY = "Similar Source Record";
	public static final String SIMILAR_SOURCE_RECORDS_JSON_KEY = "similar_source_records";
	public static final String GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY = "GRSF Semantic identifier";
	public static final String GRSF_SEMANTIC_IDENTIFIER_JSON_KEY = "grsf_semantic_identifier";
	public static final String GRSF_TYPE_CUSTOM_KEY = "GRSF Type";
	public static final String GRSF_TYPE_JSON_KEY = "grsf_type";
	public static final String MANAGEMENT_ENTITIES_CUSTOM_KEY = "Management Body/Authority(ies)";
	public static final String MANAGEMENT_ENTITIES_JSON_KEY = "management_body_authorities";

	// for connected records
	public static final String CONNECTED_CUSTOM_KEY = "Connected Record";
	public static final String CONNECTED_JSON_KEY =  "connected";

	// stock record
	public static final String STOCK_NAME_CUSTOM_KEY = "Stock Name";
	public static final String STOCK_NAME_JSON_KEY = "stock_name";
	public static final String ASSESSMENT_AREA_CUSTOM_KEY = "Assessment Area";
	public static final String ASSESSMENT_AREA_JSON_KEY = "assessment_area";
	public static final String EXPLOITING_FISHERY_CUSTOM_KEY = "Exploiting Fishery";
	public static final String EXPLOITING_FISHERY_JSON_KEY = "exploiting_fishery";
	public static final String ASSESSMENT_METHODS_CUSTOM_KEY = "Assessment Methods";
	public static final String ASSESSMENT_METHODS_JSON_KEY = "assessment_methods";
	public static final String FIRMS_ABUNDANCE_LEVEL_CUSTOM_KEY = "Abundance Level (FIRMS Standard)";
	public static final String FIRMS_ABUNDANCE_LEVEL_JSON_KEY = "firms_standard_abundance_level";
	public static final String ABUNDANCE_LEVEL_CUSTOM_KEY = "Abundance Level";
	public static final String ABUNDANCE_LEVEL_JSON_KEY = "abundance_level";
	public static final String FISHING_PRESSURE_FIRMS_CUSTOM_KEY = "Fishing Pressure (FIRMS Standard)";
	public static final String FISHING_PRESSURE_FIRMS_JSON_KEY = "firms_standard_fishing_pressure";
	public static final String FISHING_PRESSURE_CUSTOM_KEY = "Fishing Pressure";
	public static final String FISHING_PRESSURE_JSON_KEY = "fishing_pressure";
	public static final String STATE_AND_TREND_MARINE_RESOURCE_CUSTOM_KEY = "State and trend of Marine Resource";
	public static final String STATE_AND_TREND_MARINE_RESOURCE_JSON_KEY = "state_and_trend_of_marine_resources";
	public static final String FAO_CATEGORIES_CUSTOM_KEY = "Fao Categories";
	public static final String FAO_CATEGORIES_JSON_KEY = "fao_categories";
	public static final String BIOMASS_CUSTOM_KEY = "Biomass";
	public static final String BIOMASS_JSON_KEY = "biomass";
	public static final String SCIENTIFIC_ADVICE_CUSTOM_KEY = "Scientific advice";
	public static final String SCIENTIFIC_ADVICE_JSON_KEY = "scientific_advice";
	public static final String ASSESSOR_CUSTOM_KEY = "Assessor";
	public static final String ASSESSOR_JSON_KEY = "assessor";
	public static final String SPATIAL_GEOJSON_JSON_KEY = "spatial";
	public static final String SPATIAL_GEOJSON_CUSTOM_KEY = "spatial";
	public static final String FISHERY_URI = "fishery_uri";
	public static final String STOCK_URI = "stock_uri";

	// fishery record
	public static final String FISHERY_NAME_CUSTOM_KEY = "Fishery Name";
	public static final String FISHERY_NAME_JSON_KEY = "fishery_name";
	public static final String FISHING_AREA_CUSTOM_KEY = "Fishing area";
	public static final String FISHING_AREA_JSON_KEY = "fishing_area";
	public static final String RESOURCES_EXPLOITED_CUSTOM_KEY = "Resources Exploited";
	public static final String RESOURCES_EXPLOITED_JSON_KEY = "resources_exploited";
	public static final String FLAG_STATE_CUSTOM_KEY = "Flag State";
	public static final String FLAG_STATE_JSON_KEY = "flag_state";
	public static final String JURISDICTION_AREA_CUSTOM_KEY = "Jurisdiction Area";
	public static final String JURISDICTION_AREA_JSON_KEY = "jurisdiction_area";
	public static final String PRODUCTION_SYSTEM_TYPE_CUSTOM_KEY = "Type of Production System";
	public static final String PRODUCTION_SYSTEM_TYPE_JSON_KEY = "production_system_type";
	public static final String FISHING_GEAR_CUSTOM_KEY = "Fishing gear";
	public static final String FISHING_GEAR_JSON_KEY = "fishing_gear";

	// similar records bean sub-fields
	public static final String SIMILAR_RECORDS_BEAN_FIELD_URL = "url";
	public static final String SIMILAR_RECORDS_BEAN_FIELD_DESCRIPTION = "description";
	public static final String SIMILAR_RECORDS_BEAN_FIELD_NAME = "name";
	public static final String SIMILAR_RECORDS_BEAN_FIELD_IDENTIFIER = "id";

	// other fields/regex
	public static final String DEFAULT_LICENSE = "CC-BY-SA-4.0";
	public static final String SYSTEM_TYPE_CUSTOM_KEY = "system:type";
	public static final String SYSTEM_TYPE_FOR_SOURCES_VALUE = "Legacy";
	public static final String GRSF_DATABASE_SOURCE = "Database Source";
	public static final int TIME_SERIES_TAKE_LAST_VALUES = 5;
	public static final String REGEX_TAGS = "[^\\s\\w-_.]";
	public static final String ITEM_URL_FIELD = "Record URL";
	public static final String GRSF_ADMIN_ORGANIZATION_NAME = "grsf_admin";
	public static final String GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK = "GRSF Stock";
	public static final String GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY = "GRSF Fishery";

	// response for patch/creation
	public static final String RESPONSE_CREATE_PATCH_ID = "id";
	public static final String RESPONSE_CREATE_KNOWLEDGE_BASE_ID = "knowledge_base_id";
	public static final String RESPONSE_CREATE_PRODUCT_URL = "product_url";
	public static final String RESPONSE_CREATE_ERROR_MESSAGE = "error";

	// management constants area
	public static final String MANAGEMENT_AREA_NAMESPACE = "management_area:";
	public static final String ANNOTATION_CUSTOM_KEY= "Annotation";

	// Resource information set
	public static final String RESOURCE_URL = "url";
	public static final String RESOURCE_DESCRIPTION = "description";
	public static final String RESOURCE_NAME = "name";

	// Refers to information
	public static final String REFERS_TO_URL = "url";
	public static final String REFERS_TO_ID = "id";

	// delete record
	public static final String DELETE_RECORD_ID = "id";

	// Time series
	public static final String TIME_SERIES_YEAR_FIELD = "reference_year";
	public static final String TIME_SERIES_VALUE_FIELD = "value";
	public static final String TIME_SERIES_UNIT_FIELD = "unit";
	public static final String TIME_SERIES_DB_SOURCE_FIELD = "db_source";
	public static final String TIME_SERIES_DATA_OWNER_FIELD = "data_owner";
	public static final String TIME_SERIES_ASSESSMENT_FIELD = "reporting_year_or_assessment_id";

	// request post fields FORTH Service
	public static final String CATALOGUE_ID = "catalog_id";
	public static final String KB_ID = "knowledge_base_id";
	public static final String NEW_STATUS = "new_status";
	public static final String OLD_STATUS = "old_status";
	public static final String ANNOTATION = "annotation_message";
	public static final String SHORT_NAME_OLD = "short_name_old";
	public static final String SHORT_NAME_NEW = "short_name_new";
	public static final String SDG_FLAG = "sdg_flag";
	public static final String TRACEABILITY_FLAG = "traceability_flag";
	public static final String GRSF_TYPE_OLD = "grsf_type_old";
	public static final String GRSF_TYPE_NEW = "grsf_type_new";
	public static final String SIMILAR_GRSF_RECORDS = "similar_grsf_records";
	public static final String SUGGESTED = "suggested";
	public static final String MERGE = "merge";
	public static final String CONNECTIONS = "connections";
	public static final String CONNECTION_TO_REMOVE = "remove";
	public static final String SOURCE_KNOWLEDGE_BASE_ID = "source_knowledge_base_id";
	public static final String DEST_KNOWLEDGE_BASE_ID = "dest_knowledge_base_id";
	public static final String SOURCE_DOMAIN = "source_domain";
	public static final String UPDATE_RESULT = "update_result";
	public static final String ERROR_MESSAGE = "error_message";
	public static final String ADMINISTRATOR_FULLNAME = "administrator_name";

	// for the annotation messages at server side
	public static final String ANNOTATION_PUBLISHER_CUSTOM_KEY = "Annotation";
	public static final String ANNOTATION_PUBLISHER_JSON_KEY = "annotations";
	public static final String ANNOTATION_ADMIN_JSON_KEY = "admin";
	public static final String ANNOTATION_MESSAGE_JSON_KEY = ANNOTATION;
	public static final String ANNOTATION_TIME_JSON_KEY = "time";

	// discover the endpoint of the grsf updater on IS
	public static final String GRSF_UPDATER_SERVICE  = "GRSFUpdaterEndPoint";
	public static final String SERVICE_POST_UPDATER_METHOD = "/service/updater/post";
	public static final String SERVICE_POST_REVERT_METHOD = "/service/revert/post";
	public static final String SERVICE_NAME = "GRSF Updater";
	public static final String SERVICE_CATEGORY = "Service";

	// request url
	public static final String GCUBE_REQUEST_URL = "gcube-request-url";

	// session info for user
	public static final String GRSF_ADMIN_SESSION_KEY = "IS_GRSF_ADMIN";
	public static final String GRSF_CATALOGUE_EDITOR_ROLE = "Catalogue Editor"; // managed as Team Role
	public static final String GRSF_CATALOGUE_REVIEWER_ROLE = "Catalogue Reviewer"; // they control editors

	//	No connections/similar grsf records yet
	public static final String NO_SIMILAR_GRSF_RECORDS = "No Similar Records";
	public static final String NO_CONNECTED_RECORDS = "No Connected Records";
	
	// groups for traceability and sdg flags
	public static final String TRACEABILITY_FLAG_GROUP_NAME = "traceability-flag";
	public static final String SDG_FLAG_GROUP_NAME = "sdg-flag";

}
