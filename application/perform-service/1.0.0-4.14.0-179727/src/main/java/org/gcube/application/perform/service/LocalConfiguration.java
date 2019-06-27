package org.gcube.application.perform.service;

import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LocalConfiguration {

	private static final Logger log= LoggerFactory.getLogger(LocalConfiguration.class);
	
	public static final String POOL_MAX_IDLE="db.pools.max_idle";
	public static final String POOL_MAX_TOTAL="db.pools.max_total";
	public static final String POOL_MIN_IDLE="db.pools.min_total";
	
	
	public static final String MAPPING_DB_ENDPOINT_NAME="mapping-db.ep.name";
	public static final String MAPPING_DB_ENDPOINT_CATEGORY="mapping-db.ep.category";
	

	public static final String IMPORTER_COMPUTATION_ID="dm.importer.computationid";
	
	
	public static final String LOAD_SCHEMA="schema.load";
	public static final String SKIP_ON_SCHEMA_ERROR="schema.load.skipError";
	
	public static final String COMMIT_SCHEMA="schema.load.commit";
	
	static LocalConfiguration instance=null;
	
	
	
	public synchronized static LocalConfiguration init(URL propertiesURL){
		if(instance==null)
			instance=new LocalConfiguration(propertiesURL);
		return instance; 
	}
	
	private Properties props=new Properties();
	
	private LocalConfiguration(URL propertiesURL) {
		try{
			log.debug("Loading {} ",propertiesURL);
			props.load(propertiesURL.openStream());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	
	
	public static final String getProperty(String propertyName) {
		return instance.props.getProperty(propertyName);
	}
	
	
	
	
}
