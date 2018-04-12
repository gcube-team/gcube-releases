package org.gcube.content.storage.rest.utils;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
/**
 * 
 * @author Roberto Cirillo (ISTI-CNR) 2017
 *
 */
public class Costants {
	
	public static final  WriteConcern DEFAULT_WRITE_TYPE=WriteConcern.W2;
	public static final ReadPreference DEFAULT_READ_PREFERENCE=ReadPreference.primaryPreferred();
	public static final int DEFAULT_CONNECTION_PER_HOST= 30; 
	public static final int DEFAULT_CONNECT_TIMEOUT= 30000; 
	public static final String SERVICE_CLASS_DEFAULT="DataStorage";
	public static final String SERVICE_NAME_DEFAULT="SmartStorageREST";
	public static final String COLLECTION_PROPERTY_NAME = "collectionName";
	public static final String ALL_IN_COLLECTION_NAME= "cc";
	public static final String ALL_IN_DB_NAME="All-In-Db";


}
