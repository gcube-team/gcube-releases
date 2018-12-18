package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;


import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Profile;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 *  Maven Embedder to perform predefined goals
 *
 * @author Manuele Simi (CNR)
 *
 */
public class MavenEmbedder {

    private final Maven maven;

    private final MavenExecutionRequestPopulator populator;

    final String mavenCoreRealmId = "gcube.maven.core";
    
    private final GCUBELog logger = new GCUBELog(MavenEmbedder.class);
    
    /**
     * Constructs an embedder object and loads the appropriate libraries so that we can use an embedded maven
     * @throws ComponentLookupException
     * @throws PlexusContainerException
     */
    public MavenEmbedder() throws ComponentLookupException, PlexusContainerException {
       // ClassLoader cl = this.getClass().getClassLoader();   
        ContainerConfiguration cc = new
        		DefaultContainerConfiguration();
        //cc.setClassWorld(new ClassWorld(mavenCoreRealmId, cl));
        //cc.setName("gCubeMavenCore");
        PlexusContainer plexus = new DefaultPlexusContainer(cc);
        this.populator = plexus.lookup(MavenExecutionRequestPopulator.class);
        this.maven = plexus.lookup(Maven.class);
        System.out.println("Maven is instance of " + this.maven.getClass().getName());
    }

    /**
     * Executes the given request
     * @param request the {@link MavenExecutionRequest} to process
     * @param settings the {@link Settings} to use in the execution
     * @return a {@link MavenExecutionResult} for valid requests; null otherwise
     */
    public MavenExecutionResult execute(MavenExecutionRequest request, Settings settings) {
        if (request == null) {
            throw new IllegalArgumentException("Maven request can't be null");
        }
        MavenExecutionResult result;
        try {        	
            this.populator.populateFromSettings(request, settings);
			System.out.println("ACTIVE PROFILE: " + request.getActiveProfiles().toString());
			for (Profile profile : request.getProfiles()) {
				for (org.apache.maven.model.Repository rep : profile.getRepositories()) {
					System.out.println("ACTIVE REPOSITORY Id: " + rep.getId());
					System.out.println("ACTIVE REPOSITORY URL: " + rep.getUrl());
		     	}
			}
            result = maven.execute(request);
        } catch (MavenExecutionRequestPopulationException ex) {
            logger.error("Error while populating the Maven Request", ex);
            result = new DefaultMavenExecutionResult();
            result.addException(ex);
        } catch (Exception e) {
            logger.error("", e);
            result = new DefaultMavenExecutionResult();
            result.addException(e);
        }
        return result;
    }
}
