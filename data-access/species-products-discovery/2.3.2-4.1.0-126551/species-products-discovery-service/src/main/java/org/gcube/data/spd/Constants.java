package org.gcube.data.spd;

import javax.xml.namespace.QName;

public class Constants {

	
	/** Service name. */
	//public static final String SERVICE_NAME = "SpeciesProductsDiscovery";
	/** Service class. */
	//public static final String SERVICE_CLASS = "DataAccess";
	/** Namespace. */
	public static final String NS = "http://gcube-system.org/namespaces/data/speciesproductsdiscovery";
	
	/** JNDI Base Name. */
	public static final String JNDI_NAME = "gcube/data/speciesproductsdiscovery";
	
	/** Relative endpoint of the Occurrences port-type. */
	public static final String OCCURRENCES_PT_NAME = JNDI_NAME+"/occurrences";
	/** Relative endpoint of the Manager port-type. */
	public static final String MANAGER_PT_NAME = JNDI_NAME+"/manager";
	
	public static final String CLASSIFICATION_PT_NAME = JNDI_NAME+"/classification";
	
	/** Name of the plugin RP of the Binder resource. */
	public static final String PLUGIN_DESCRIPTION_RPNAME = "PluginMap";
	/** Fully qualified name of the Plugin RP of the Binder resource. */
	public static final QName BINDER_PLUGIN_RP = new QName(NS, PLUGIN_DESCRIPTION_RPNAME);
	
	public static final String FACTORY_RESORCE_NAME="manager";
	
	public static final String SERVICE_NAME="SpeciesProductsDiscovery";
	
	public static final String SERVICE_CLASS="DataAccess";
	
	public static final String TAXON_RETURN_TYPE = "taxon";
	
	public static final String OCCURRENCE_RETURN_TYPE = "occurrence";
	
	public static final String RESULITEM_RETURN_TYPE = "resultItem";
	
	public static final int JOB_CALL_RETRIES = 10;

	public static final long RETRY_JOBS_MILLIS = 2000;
	
	public static final int QUERY_CALL_RETRIES = 5;

	public static final long RETRY_QUERY_MILLIS = 1000;
	
}
