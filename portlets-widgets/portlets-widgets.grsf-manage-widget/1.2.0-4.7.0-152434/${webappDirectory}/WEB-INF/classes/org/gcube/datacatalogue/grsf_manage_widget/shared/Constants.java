package org.gcube.datacatalogue.grsf_manage_widget.shared;

public class Constants {
	
	// info of the record to be shown at client-side
	public static final String STATUS_CUSTOM_FIELD_KEY = "Status of the GRSF record";
	public static final String KB_UUID_FIELD_KEY = "GRSF UUID";
	public static final String SEMANTIC_IDENTIFIER = "GRSF Semantic identifier";
	public static final String GRSF_TYPE_FIELD_KEY = "GRSF Type";
	public static final String SHORT_NAME_FIELD_KEY = "Short Name";
	public static final String STOCK_GRSF_NAME = "Stock Name";
	public static final String FISHERY_GRSF_NAME = "Fishery Name";
	public static final String SOURCES_TITLE_FIELD_KEY = "Sources";
	public static final String GRSF_DOMAIN = "GRSF Domain";
	public static final String TRACEABILITY_FLAG = "Traceability Flag";
	public static final String SYSTEM_TYPE = "system:type";
	public static final String GRSF = "GRSF";
	public static final String FIRMS = "FIRMS";
	public static final String RAM = "RAM";
	public static final String FISHSOURCE = "FishSource";
	public static final String ANNOTATION_KEY = "Annotation on update";
	public static final String GRSF_DATABASE_SOURCE = "Database Source";
	
	// stock or fishery
	public static final String STOCK = "Stock";
	public static final String FISHERY = "Fishery";

	// request post fields FORTH Service
	public static final String CATALOGUE_ID = "catalog_id";
	public static final String KB_ID = "record_id";
	public static final String PRODUCT_TYPE = "type";
	public static final String STATUS = "status";
	public static final String ANNOTATION = "annotation_msg";
	public static final String ERROR = "error";

	// discover the endpoint of the grsf updater on IS
	public static final String GRSF_UPDATER_SERVICE  = "GRSFUpdaterEndPoint";
	public static final String SERVICE_POST_METHOD = "/service/updater/post";

	// the error of the update on success
	public static final int STATUS_SUCCESS = 200;

	// GRSF update service information
	public static final String SERVICE_NAME = "GRSF Updater";
	public static final String SERVICE_CATEGORY = "Service";

	// request url
	public static final String GCUBE_REQUEST_URL = "gcube-request-url";

	// session info for user
	public static final String GRSF_ADMIN_SESSION_KEY = "IS_GRSF_ADMIN";
	public static final String GRSF_CATALOGUE_MANAGER_ROLE = "Catalogue Manager"; // managed as Team Role

}
