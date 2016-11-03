package org.gcube.smartgears;

import org.gcube.smartgears.extensions.resource.RemoteResource;
import org.gcube.smartgears.handlers.application.lifecycle.ProfileManager;
import org.gcube.smartgears.handlers.application.request.RequestValidator;
import org.gcube.smartgears.handlers.container.lifecycle.AccountingManager;

/**
 * Library-wide constants.
 * 
 * 
 * @author Fabio Simeoni
 *
 */
public class Constants {

	/**
	 * The environment variable that points to the container configuration directory.
	 */
	public static final String ghn_home_env = "GHN_HOME";
	
	/**
	 * The system property that points to the container configuration directory.
	 */
	public static final String ghn_home_property = "ghn.home";
	
	
	/**
	 * The container configuration file path, relative to the container configuration directory.
	 */
	public static final String container_configuraton_file_path = "container.xml";
	
	
	/**
	 * The path of the application profile file, relative to the container configuration directory.
	 */
	public static final String container_profile_file_path = "ghn.xml";
	
	public static final String container_profile_file_path_copy = "ghn.xml.copy";
	
	/**
	 * The container lifecycle configuration resource path.
	 */
	public static final String container_handlers_file_path = "/META-INF/container-handlers.xml";
	
	/**
	 * The library configuration resource path.
	 */
	public static final String library_configuration_file_path = "/META-INF/smartgears-config.xml";
	
	/**
	 * The name of the context property that contains the node profile.
	 */
	public static final String container_profile_property = "ghn-profile";
	
	
	/**
	 * The default value of for the container publication frequency.
	 */
	public static final long default_container_publication_frequency = 60;
	
	
	
	
	/**
	 * The application configuration resource path.
	 */
	public static final String configuration_file_path = "/WEB-INF/gcube-app.xml";
	
	/**
	 * The application lifecycle configuration resource path.
	 */
	public static final String handlers_file_path = "/WEB-INF/gcube-handlers.xml";
	
	/**
	 * The default application lifecycle configuration resource path.
	 */
	public static final String default_handlers_file_path = "/META-INF/default-handlers.xml";


	/**
	 * The wildcard exclude directive.
	 */
	public static final String EXCLUDE_ALL = "*";
	
	
	/**
	 * The mapping root of all extensions.
	 */
	public static final String root_mapping = "/gcube/resource";
	
	/**
	 * The application extensions configuration resource path.
	 */
	public static final String extensions_file_path = "/WEB-INF/gcube-extensions.xml";

	/**
	 * The default application extensions configuration resource path.
	 */
	public static final String default_extensions_file_path = "/META-INF/default-extensions.xml";

	/**
	 * The application frontpage resource path.
	 */
	public static final String frontpage_file_path = "/META-INF/frontpage.html";
	
	/**
	 * The configuration name of {@link ProfileManager}s.
	 */
	public static final String profile_management = "profile-management"; 
	
	/**
	 * The configuration name of {@link RequestValidator}s.
	 */
	public static final String request_validation = "request-validation";
	
	
	/**
	 * The configuration name of {@link AccountingManager}s.
	 */
	public static final String accounting_management = "accounting-management";
	
	/**
	 * The configuration name of {@link RequestAccounting}s.
	 */
	public static final String request_accounting = "request-accounting";
	
	
	/**
	 * The configuration name of {@link RemoteResource}s.
	 */
	public static final String remote_management = "remote-management";
	
	
	
	
	/**
	 * The path of the application profile file, relative to the service configuration directory.
	 */
	public static final String profile_file_path = "endpoint.xml";
	
	/**
	 * The name of the context property that contains the endpoint profile.
	 */
	public static final String profile_property = "endpoint-profile"; 
	

	/**
	 * The name of the attribute in the servlet context that contains the context of an application.
	 */
	public static final String context_attribute ="gcube-application-context";
	
	/**
	 * The name of the HTTP header that contains the scope of requests
	 */
	public static final String scope_header="gcube-scope";

	/**
	 * The name of the HTTP header that contains the authorization token of requests
	 */
	public static final String token_header="gcube-token";
	
	/**
	 * The event for token registration for app.
	 */
	public static final String token_registered = "token-registered";
	
	/**
	 * The event for token removal for app.
	 */
	public static final String token_removed = "token-removed";
	
	/**
	 * The name of the HTTP header for standard HTTP basic authorization
	 */
	public static final String authorization_header ="Authorization";
	
	/**
	 * The name of the HTTP header that contains the called method of the current request
	 */
	public static final String called_method_header="gcube-method";
	
	/**
	 * The name of the Content-Type HTTP header
	 */
	public static final String content_type="Content-Type";
	
	/**
	 * The name of the Accept HTTP header
	 */
	public static final String accept="Accept";
	
	/**
	 * The name of the Allow HTTP header
	 */
	public static final String allow="Allow";
	
	
	/**
	 * The name of the XML media type.
	 */
	public static final String plain_text="text/plain";
	
	/**
	 * The name of the XML media type.
	 */
	public static final String application_xml="application/xml";
	
	
	/**
	 * The name of the XHTML media type.
	 */
	public static final String application_xhtml="application/xhtml+xml";
	
	/**
	 * The name of the Json media type.
	 */
	public static final String application_json="application/json";
	
}
