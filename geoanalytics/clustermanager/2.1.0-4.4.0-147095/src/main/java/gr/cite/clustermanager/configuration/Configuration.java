package gr.cite.clustermanager.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration
{
	private static Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	public static final String GEOSPATIAL_OPERATION_SERVICE = "GeospatialOperationService";
	public static final String GEOSERVER_ENDPOINT_REQUEST = "geoserverEndpoint";

	public static String getFullGosEndpoint(String gosHost, String gosPort){
		return "http://"+gosHost+":"+gosPort+"/"+Configuration.GEOSPATIAL_OPERATION_SERVICE;
	}
	
}
