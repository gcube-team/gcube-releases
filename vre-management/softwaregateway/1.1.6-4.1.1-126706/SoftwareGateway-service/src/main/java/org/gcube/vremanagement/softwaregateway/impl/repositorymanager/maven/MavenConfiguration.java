package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.File;

import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;

/**
 * Maven configuration
 * 
 * @author Manuele Simi (CNR)
 *
 */
class MavenConfiguration {

	static final String userHome = System.getProperty("user.home");

	static final File USER_MAVEN_CONFIGURATION_HOME = new File(userHome,".m2");

	static final File USER_SETTINGS_FILE = 
			new File((String)ServiceContext.getContext().getProperty("configDir", false), "settings.xml");

	static final File DEFAULT_GLOBAL_SETTINGS_FILE = new File(
			System.getProperty("maven.home", System.getProperty("user.dir", "")),"conf/settings.xml");

	/**
	 * @return the globalSettingsFile
	 */
	static String getGlobalSettingsFile() {
		return DEFAULT_GLOBAL_SETTINGS_FILE.getAbsolutePath();
	}

	/**
	 * @return the userSettingsFile
	 */
	static String getUserSettingsFile() {
		return USER_SETTINGS_FILE.getAbsolutePath();
	}
	
	/**
	 * @return the local repository
	 */
	static String getLocalRepository() {
		return USER_MAVEN_CONFIGURATION_HOME.getAbsolutePath();
	}

}
