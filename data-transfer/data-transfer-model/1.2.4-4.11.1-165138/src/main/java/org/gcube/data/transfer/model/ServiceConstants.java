package org.gcube.data.transfer.model;

public class ServiceConstants {

	public static final String APPLICATION_PATH="/gcube/service/";
	
	//Servlets
	public static final String CAPABILTIES_SERVLET_NAME="Capabilities";
	public static final String REQUESTS_SERVLET_NAME="Requests";
	public static final String STATUS_SERVLET_NAME="TransferStatus";
	
	public static final String REST_SERVLET_NAME="REST";
	
	// Methods
	public static final String REST_HTTP_DOWNLOAD="HttpDownload";
	public static final String REST_DIRECT_TRANSFER="DirectTransfer";
	public static final String REST_FILE_UPLOAD="FileUpload";
	
	
	
	
	
	
	//Params
	public static final String TRANSFER_REQUEST_OBJECT="request-object";
	public static final String TRANSFER_ID="transfer-id";
	
	public static final String DESTINATION_FILE_NAME="destination-file-name";
	public static final String CREATE_DIRS="create-dirs";
	public static final String ON_EXISTING_FILE="on-existing-file";
	public static final String ON_EXISTING_DIR="on-existing-dir";
	public static final String SOURCE_ID="source-id";
	public static final String MULTIPART_FILE="uploadedFile";
}
