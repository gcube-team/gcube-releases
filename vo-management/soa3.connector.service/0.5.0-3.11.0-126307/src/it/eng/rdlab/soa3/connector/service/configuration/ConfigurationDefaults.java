package it.eng.rdlab.soa3.connector.service.configuration;

public interface ConfigurationDefaults 
{
	
	long DEFAULT_AUTH_VALIDITY = 5; //min
	boolean DEFAULT_AUTHZ_ENABLED = true;
	String DEFAULT_SOA3_URL = "http://localhost:8080";
	String DEFAULT_SERVICE_NAME = "soa3";
	String DEFAULT_ORGANIZATION_NAME = "imarine";
	String DEFAULT_SCOPE = "/testing";

}
