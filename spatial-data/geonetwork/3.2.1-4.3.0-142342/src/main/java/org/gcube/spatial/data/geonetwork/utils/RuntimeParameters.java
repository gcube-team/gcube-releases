package org.gcube.spatial.data.geonetwork.utils;

import java.io.IOException;
import java.util.Properties;

import org.gcube.spatial.data.geonetwork.GeoNetwork;

public class RuntimeParameters {

	public static final String geonetworkCategory="geonetworkCategory";
	public static final String geonetworkPlatformName="geonetworkPlatformName";
	public static final String geonetworkEndpointName="geonetworkEndpointName";
	public static final String priorityProperty="priorityProperty";
	public static final String availableGroupSuffixList="availableGroupSuffixList";

	public static final String assignedScopePrefix="assignedScopePrefix";
	public static final String scopeUserPrefix="scopeUserPrefix";
	public static final String scopePasswordPrefix="scopePasswordPrefix";
	public static final String ckanUserPrefix="ckanUserPrefix";
	public static final String ckanPasswordPrefix="ckanPasswordPrefix";
	public static final String defaultGroupPrefix="defaultGroupPrefix";
	public static final String privateGroupPrefix="privateGroupPrefix";
	public static final String publicGroupPrefix="publicGroupPrefix";


	public static final String genericResourceSecondaryType="genericResourceSecondaryType";
	public static final String genericResourceName="genericResourceName";

	public static final String metadataConfigurationTtl="metadataConfigurationTtl";
	
	public static final String GNUniqueNameLength="GNUniqueNameLength";
	public static final String GNPasswordLength="GNPasswordLength";  
	
	public static final String isMaxWaitTimeMillis="isMaxWaitTimeMillis";
	
	
	public static final String geonetworkUpdateTimeout="geonetworkUpdateTimeout";
	public static final String geonetworkUpdateWait="geonetworkUpdateWait";
	
	
	private Properties props=new Properties();
	
	public RuntimeParameters() throws IOException {
		props.load(GeoNetwork.class.getResourceAsStream("query.properties"));
	}
	
	public Properties getProps() {
		return props;
	}
	
}
