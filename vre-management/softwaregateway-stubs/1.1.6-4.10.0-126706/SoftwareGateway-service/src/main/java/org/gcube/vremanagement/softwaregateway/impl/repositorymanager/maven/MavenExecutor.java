package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.settings.Settings;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Executor of Maven Requests
 * @author Manuele Simi (CNR)
 *
 */
class MavenExecutor {

	private static final GCUBELog logger = new GCUBELog(MavenExecutor.class);

	static void exec(MavenExecutionRequest request, Settings settings) throws Exception {
		MavenEmbedder mavenEmbedder = new MavenEmbedder();
//		mavenEmbedder.setClassLoader(
//				Thread.currentThread().getContextClassLoader() );

		MavenExecutionResult mavenResult = mavenEmbedder.execute(request, settings);
		checkResults(mavenResult);
	}

	private static void checkResults(MavenExecutionResult mavenResult)
			throws Exception {
		
		if (mavenResult.hasExceptions()) {
			List<Throwable> exceptionList = mavenResult.getExceptions();
			int i = 1;
			for (Throwable exception : exceptionList) {
				logger.info("Maven Exception n." + i++ + ":\n" + exception);
				if (exception instanceof org.apache.maven.lifecycle.LifecycleExecutionException) {
					logger.info("exception instanceof org.apache.maven.lifecycle.LifecycleExecutionException ");
					LifecycleExecutionException lifecycleExecutionException = (LifecycleExecutionException) exception;
					if (lifecycleExecutionException.getCause() instanceof org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException) {
						MultipleArtifactsNotFoundException multipleArtifactsNotFoundException = (MultipleArtifactsNotFoundException) lifecycleExecutionException.getCause();
						logger.info("MultipleArtifactsNotFoundException ");
						int j = 1;
						List<Artifact> resolvedArtifacts = multipleArtifactsNotFoundException.getResolvedArtifacts();
						for (Iterator<Artifact> iter = resolvedArtifacts
								.iterator(); iter.hasNext();) {
							DefaultArtifact defaultArtifact = (DefaultArtifact) iter.next();
							logger.info("Resolved Artifact n. " + j++ + " : "+ defaultArtifact);
						}
						j = 1;
						List<Artifact> missingArtifacts = multipleArtifactsNotFoundException.getMissingArtifacts();
						for (Iterator<Artifact> iter = missingArtifacts.iterator(); iter.hasNext();) {
							DefaultArtifact defaultArtifact = (DefaultArtifact) iter.next();
							logger.info("Missing Artifact n. " + j++ + " : "+ defaultArtifact);

						}
					}
				}

			}
			// return the first exception found
			throw new Exception(exceptionList.get(0));
		}
		logger.info("MavenExecutionResults are OK, no Exception was returned from the execution of the request");
	}
}
