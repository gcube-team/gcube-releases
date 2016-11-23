package it.eng.rdlab.soa3.pm.connector.javaapi.configuration;

public interface ConfigurationConstants 
{
	// Classpath resources path
	public String CONFIGURATION_RES_FILE = "/it/eng/rdlab/soa3/pm/connector/javaapi/resources/configurationfiles.properties";
	public String DEFAULT_PROP_FILE = "/it/eng/rdlab/soa3/pm/connector/javaapi/resources/configuration.properties";

	// Classpath resources label
	public String CONFIGURATION_ROOT = "CONFIGURATION_ROOT";
	public String CONFIGURATION_FILE = "CONFIGURATION_FILE";

	// Configuration labels
	public String POLICY_REPOSITORY_URL = "POLICY_REPOSITORY_URL";
	public String AUTHZ_QUERY_ENDPOINT = "AUTHZ_QUERY_ENDPOINT";
	public String POLICY_LOADER_URL = "POLICY_LOADER_URL";
	public String EXPLICIT_FINAL_STATEMENT = "EXPLICIT_FINAL_STATEMENT";
	public String INDETERMINATE_DECISION = "INDETERMINATE_DECISION";
	
	// Default hardcoded values
	public String POLICY_REPOSITORY_DEFAULT_URL = "http://localhost:8568";
	public String AUTHZ_QUERY_DEFAULT_ENDPOINT = "https://localhost:8154/authz";
	

}
