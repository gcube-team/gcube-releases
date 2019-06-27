package org.gcube.portlets.user.statisticalalgorithmsimporter.shared;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectConfig;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class Constants {

	public static final boolean DEBUG_MODE = false;
	public static final boolean TEST_ENABLE = false;

	public static final String APPLICATION_ID = "org.gcube.portlets.user.statisticalalgorithmsimporter.server.portlet.StatAlgoImporterPortlet";
	public static final String STATISTICAL_ALGORITHMS_IMPORTER_ID = "SAIId";
	public static final String STATISTICAL_ALGORITHMS_IMPORTER_COOKIE = "SAILangCookie";
	public static final String STATISTICAL_ALGORITHMS_IMPORTER_LANG = "SAILang";

	public static final String DEFAULT_USER = "giancarlo.panichi";
	public final static String DEFAULT_SCOPE = "/gcube/preprod/preVRE";
	public final static String DEFAULT_TOKEN = "";
	public static final String DEFAULT_ROLE = "OrganizationMember";

	public static final String FILE_UPLOADED_FIELD = "FileUploadedField";
	public static final String STATISTICAL_ALGORITHMS_IMPORTER_JAR_PUBLIC_LINK = "JarPublicLink";
	public static final String DEFAULT_RECIPIENTS = "DEFAULT_RECIPIENTS";

	// Session
	public static final String CURR_GROUP_ID = "CURR_GROUP_ID";

	// Download
	public static final String DOWNLOAD_SERVLET = "DownloadServlet";
	
	// Upload
	public static final String LOCAL_UPLOAD_SERVLET = "LocalUploadServlet";

	// IS Resource
	public static final String SAI_NAME = "SAIProfile";
	public static final String SAI_CATEGORY = "SAI";

	public static final String SOCIAL_NETWORKING_NAME = "SocialNetworking";
	public static final String SOCIAL_NETWORKING_CLASS = "Portal";

	public static final String POOL_MANAGER_SERVICE_NAME = "dataminer-pool-manager";
	public static final String POOL_MANAGER_SERVICE_CLASS = "DataAnalysis";

	// Main Generator
	public static final String PRE_INSTALLED = "Pre-Installed";
	public static final String REMOTE_TEMPLATE_FILE = "http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/RConfiguration/RD4SFunctions/SAITemplateForExternalInvocation.R";
	public static final String ECOLOGICAL_ENGINE_JAR_URL = "http://maven.research-infrastructures.eu/nexus/content/repositories/gcube-staging/org/gcube/dataanalysis/ecological-engine/1.12.0-4.13.1-154785/ecological-engine-1.12.0-4.13.1-154785.jar";
	public static final String ECOLOGICAL_ENGINE_SMART_EXECUTOR_JAR_URL = "http://maven.research-infrastructures.eu/nexus/content/repositories/gcube-staging/org/gcube/dataanalysis/ecological-engine-smart-executor/1.6.2-4.13.1-167535/ecological-engine-smart-executor-1.6.2-4.13.1-167535.jar";
		
	// DataMiner Pool Manager
	public static final int CLIENT_MONITOR_PERIODMILLIS = 2000;
	
	//
	public static final Project TEST_PROJECT = new Project(null, new ProjectConfig("", new ProjectSupportBashEdit()));
	
	
	
}
