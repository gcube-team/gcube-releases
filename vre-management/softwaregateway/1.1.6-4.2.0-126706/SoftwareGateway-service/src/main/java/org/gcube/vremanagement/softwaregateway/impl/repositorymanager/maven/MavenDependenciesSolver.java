package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.gcube.common.core.utils.logging.GCUBELog;

public class MavenDependenciesSolver {
	
	/**
	 * DEPENDENCY_RESOLUTION
	 */
	public static final String DEPENDENCY_RESOLUTION = "DependencyResolutionReport";
	/**
	 * RESOLVED_DEPENDECIES
	 */
	public static final String RESOLVED_DEPENDECIES = "ResolvedDependencies";
	/**
	 * MISSING_DEPENDENCIES
	 */
	public static final String MISSING_DEPENDENCIES = "MissingDependencies";

	
	protected final GCUBELog logger = new GCUBELog(MavenDependenciesSolver.class);
	
	/**
	 * Invokes  a maven embedded and exec a dependencies:tree plugin
	 * @param pomFile
	 * @param projectDir
	 * @param repository
	 * @param scope
	 * @return
	 * @throws Throwable 
	 */
	public String dependenciesSolverFromPom(File pomFile, File projectDir, String repository, String scope) throws Exception {
		logger.debug("dependenciesSolverFromPom method ");
		//String resultFileName = "resultTree.txt";
		
		//prepare the project folder
		logger.debug("try to create the following dir: "+projectDir.getAbsolutePath());
		if(!projectDir.exists()){
			projectDir.mkdirs();
		}

		File resultTree =  new File(projectDir,"resultTree.txt");
		
	//	MavenExecutor mavenExecutor = null;
	/*	try {
			logger.debug("MAVEN EXECUTOR WITH projectDir "+projectDir.getAbsolutePath()+"    repository  "+repository);
			mavenExecutor = new GCUBEMavenExecutor(projectDir, repository, null);
		}catch (Exception e) {
				logger.error("Error instantiating the GCUBE Maven Executor ");
				throw e;
		}
		*/
		
		// prepare the properties
		String[][] properties = new String[1][2];
		properties[0][0] = "outputFile";
		properties[0][1] = resultTree.getAbsolutePath();
		
		// prepare the goal
		String goal = "dependency:tree";
		logger.debug("executing goals : " + goal + " with outputFile=" + properties[0][1]);
		
		
		// prepare the full request
		MavenRequestBuilder builder = new MavenRequestBuilder();
		builder.setProjectFolder(projectDir).setPom(pomFile)
			.setUserProperties(properties).execThisGoal(goal)
			.useThisRepository(MavenConfiguration.getLocalRepository());
	
		// build & send the request
		try {			
			MavenExecutor.exec(builder.build(), MavenSettingsReader.getSettings(new File(MavenConfiguration.getUserSettingsFile())));	
		} catch (Throwable e) {
			try{
				logger.error("error,"+ e.getMessage());
				if(e instanceof org.apache.maven.lifecycle.LifecycleExecutionException) {
					logger.debug(" the exception is an istance of org.apache.maven.lifecycle.LifecycleExecutionException ");
					LifecycleExecutionException lifecycleExecutionException = (LifecycleExecutionException) e;
					if (lifecycleExecutionException.getCause() instanceof org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException) {
						MultipleArtifactsNotFoundException multipleArtifactsNotFoundException = (MultipleArtifactsNotFoundException) lifecycleExecutionException.getCause();
						List<Artifact> resolvedArtifacts = multipleArtifactsNotFoundException.getResolvedArtifacts();
						List<Artifact> missingArtifacts = multipleArtifactsNotFoundException.getMissingArtifacts();
						if(resolvedArtifacts!=null && missingArtifacts!=null){
							logger.info("entered in th first if ...");
							StringBuffer sb = new StringBuffer();
							sb.append("<").append(DEPENDENCY_RESOLUTION).append(">\n");
							sb.append("<").append(RESOLVED_DEPENDECIES).append(">\n");
							logger.info("the resolved dependencies are "+resolvedArtifacts.size());
							for (int j=0; j<resolvedArtifacts.size(); j++){
								DefaultArtifact defaultArtifact = (DefaultArtifact) resolvedArtifacts.get(j);
								logger.info("Resolved Artifact n. " + j + " : " + defaultArtifact);
								try {
									//ret += TreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("Dependency");
									sb.append(MavenResultTreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("Dependency", defaultArtifact));
								} catch (Exception e1) {
									logger.info(e1);
									throw e1;
								}
							}
							sb.append("</").append(RESOLVED_DEPENDECIES).append(">\n");

							sb.append("<").append(MISSING_DEPENDENCIES).append(">\n");
							for (int k=0; k<missingArtifacts.size(); k++){
								DefaultArtifact defaultArtifact = (DefaultArtifact) missingArtifacts.get(k);
								logger.info("Missing Artifact n. " + k + " : " + defaultArtifact);

								try {
									//ret[j++] = TreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("MissingDependency");
									sb.append(MavenResultTreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("MissingDependency", defaultArtifact));
								} catch (Exception e1) {
									logger.info(e1);
									e1.printStackTrace();
									throw e1;
								}

							}
							sb.append("</").append(MISSING_DEPENDENCIES).append(">\n");

							//stringArray.setItems(ret);
							//return stringArray;
							sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
//							System.out.println("OUT:    "+sb.toString());
							return sb.toString();
						} 	
					}else if(lifecycleExecutionException.getCause() instanceof org.apache.maven.artifact.versioning.OverConstrainedVersionException ){
						org.apache.maven.artifact.versioning.OverConstrainedVersionException cause = (org.apache.maven.artifact.versioning.OverConstrainedVersionException) lifecycleExecutionException.getCause();
						DefaultArtifact defaultArtifact = (DefaultArtifact) cause.getArtifact();
						StringBuffer sb = new StringBuffer();
						sb.append("<").append(DEPENDENCY_RESOLUTION).append(">\n");
						sb.append("<").append(MISSING_DEPENDENCIES).append(">\n");
						try {
							logger.info("Missing Artifact " + " : " + defaultArtifact);
							sb.append(MavenResultTreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("MissingDependency"));
						} catch (Exception e1) {
							logger.info(e1);
							throw e1;
						}
						sb.append("</").append(MISSING_DEPENDENCIES).append(">\n");
						sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
						logger.info("OUT:    "+sb.toString());

							//stringArray.setItems(ret);
							//return stringArray;
							sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
//							System.out.println("OUT:    "+sb.toString());
							return sb.toString();
						} 	
					}else{
						logger.debug(" the exception is not an istance of org.apache.maven.lifecycle.LifecycleExecutionException ");
					}
			}catch(Exception pe){
				logger.error("null pointer Exception", pe);
				//pe.printStackTrace();
			}
			throw new Exception(e);
		}
		MavenResultTreeParser treeParser;
		try {
			logger.info("Parsing dependency tree");
			treeParser = new MavenResultTreeParser(resultTree);
			/* Scope can be null. If null no scope filter is used during resolution */
			treeParser.getScopedDependecy(scope);
			StringBuffer sb = new StringBuffer();
			sb.append("<").append(DEPENDENCY_RESOLUTION).append(">\n");
			sb.append(treeParser.listDependency());
			sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
			logger.info("OUT:    "+sb.toString());
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			//String error = "Error resolving tree dependency";
			logger.info("error,"+e.getMessage());
			throw e;
		} finally {
			logger.info(projectDir.getAbsoluteFile() + " not removed for debug issue");
		}
		
	}
}
