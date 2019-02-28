/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.util;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public final class Costants {
	
	public static final String NO_SSL_VARIABLE_NAME="NO-SSL";
	// allowed value are: "NO-SSL", "SSL"
	public static final String DEFAULT_CONNECTION_MODE="SSL";
	public static final int CONNECTION_PER_HOST=30;
//millisecond	
	public static final int CONNECT_TIMEOUT=30000;
	 /** Report type - used by : Report factory class */
	public static final int ACCOUNTING_TYPE = 1;
// used by MyFile class	
	public static final boolean DEFAULT_REPLACE_OPTION=false;
// used by BucketCoding class and operation package	
	public static final String SEPARATOR="_-_";
//	used by Encrypter class
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
//	used by Encrypter class
	public static final String DES_ENCRYPTION_SCHEME = "DES";
//  used by ServiceEngine class
	public static final String FILE_SEPARATOR = "/";
	public static final int CONNECTION_RETRY_THRESHOLD=5;
	public static final String DEFAULT_SCOPE = "private";
	public static final long TTL=180000;
	public static final boolean DEFAULT_CHUNK_OPTION=false;
	public static final int TTL_RENEW = 5;
    public static final String DEFAULT_RESOLVER_HOST= "data.d4science.org";
// *****
//  used by Operation class
	public static final String COUNT_IDENTIFIER="count";
	public static final String LINK_IDENTIFIER="link";

// used by MongoIOManager class
	public static final String DEFAULT_META_COLLECTION="fs.files";
	public static final String DEFAULT_DB_NAME="remotefs";
	public static final String ROOT_PATH_PATCH_V1=Costants.FILE_SEPARATOR+"home"+Costants.FILE_SEPARATOR+"null"+Costants.FILE_SEPARATOR;
	public static final String ROOT_PATH_PATCH_V2=Costants.FILE_SEPARATOR+"public"+Costants.FILE_SEPARATOR;
	public static final String DEFAULT_CHUNKS_COLLECTION = "fs.chunks";
//	public static final WriteConcern DEFAULT_WRITE_TYPE=WriteConcern.NORMAL;
	public static final WriteConcern DEFAULT_WRITE_TYPE=WriteConcern.REPLICA_ACKNOWLEDGED;
	public static final ReadPreference DEFAULT_READ_PREFERENCE=ReadPreference.primaryPreferred();
//	public static final boolean DEFAULT_READWRITE_PREFERENCE= false;
	public static final boolean DEFAULT_READWRITE_PREFERENCE= true;

// used by GetHttpsUrl class
	public static final String URL_SEPARATOR="/";
	public static final String VOLATILE_URL_IDENTIFICATOR = "-VLT";

// used by OperationManager class
	//COSTANT CLIENT FACTORY CLIENT	
		public static final String CLIENT_TYPE="mongo";
	// COSTANTS  FOR THREAD MANAGEMENT	(not used by mongodb)
		public static final int MIN_THREAD=1;
		public static final int MAX_THREAD=10;
	// COSTANTS FOR CHUNK  MANAGEMENT	(not used by mongodb)
		public static final int sogliaNumeroMassimo=400;
		public static final int sogliaNumeroMinimo=4;
	// dimension is express in byte
		public static final int sogliaDimensioneMinima=1024*1024;
	// dimension is express in byte
		public static final int sogliaDimensioneMassima= 4*1024*1024;
		
// used by DuplicateOperator class
		public static final String DUPLICATE_SUFFIX="-dpl";
 
// unused by GetPayload map
		public static final String MAP_FIELD="";

// used by TransportManager class
		public static final String DEFAULT_TRANSPORT_MANAGER="MongoDB"; 






}
