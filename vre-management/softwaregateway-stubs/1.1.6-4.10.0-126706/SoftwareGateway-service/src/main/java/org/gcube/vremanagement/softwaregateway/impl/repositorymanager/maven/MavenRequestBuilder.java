package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

/**
 * Creates an execution request for {@link MavenEmbedder}
 * @author Manuele Simi (CNR)
 *
 */
class MavenRequestBuilder {
	
	private String goal;
	private Properties properties;
	private File folder;
	private File pom;
	private String localRepositoryPath;

	MavenRequestBuilder() {}
	
	/**
	 * Sets the user properties for the request
	 * @param props the properties
	 * @return the builder
	 */
	MavenRequestBuilder setUserProperties(String[][] props) {
		properties = new Properties();
		for(int i=0; i<props.length; i++){
			properties.setProperty(props[i][0], props[i][1]);
		}
		return this;
	}

	/**
	 * 
	 * @param goal
	 * @return the builder
	 */
	MavenRequestBuilder execThisGoal(String goal) {
		this.goal = goal;
		return this;
	}
	
	MavenRequestBuilder setProjectFolder(File folder) {
		this.folder = folder;
		return this;
	}
	
	MavenRequestBuilder setPom(File pom) {
		this.pom = pom;
		return this;
	}
	
	MavenRequestBuilder useThisRepository(String path) {
		this.localRepositoryPath = path;
		return this;
	}
	
	/**
	 * Builds the request
	 * @return the request
	 */
	MavenExecutionRequest build() {
		MavenExecutionRequest request = new DefaultMavenExecutionRequest();
		request.setGoals(Arrays.asList(new String[]{goal}));
		request.setUserProperties(properties);
		request.setLocalRepositoryPath(localRepositoryPath);
		request.setBaseDirectory(folder);
		request.setPom(pom);
		request.setRecursive(true);
		request.setOffline(false);
		request.setShowErrors( true );
		request.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_DEBUG );
		
		return request;
	}
}
