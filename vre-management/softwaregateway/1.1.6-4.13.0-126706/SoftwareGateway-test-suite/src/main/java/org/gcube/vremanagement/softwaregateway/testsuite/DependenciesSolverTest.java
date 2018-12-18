package org.gcube.vremanagement.softwaregateway.testsuite;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException;
//import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

//import org.apache.maven.model.Model;
//import org.apache.maven.project.MavenProject;
import org.gcube.common.core.resources.service.Package.ScopeLevel;
import org.gcube.vremanagement.softwaregateway.impl.is.ISProxy;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.FileUtilsExtended;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.MavenExecutor;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.MavenResultTreeParser;
import org.junit.Before;
import org.junit.Test;

public class DependenciesSolverTest {

	File pomFile;
	String repository; 
	String scope;
	MavenProject project;
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

	File location;
	@Before
	public void initialize(){
		 pomFile=new File("target/pom.xml");
		 repository=""; 
		 scope= "compile";
		 Model model=new Model();
//		 model.setGroupId("org.gcube.resourcemanagement");
//		 model.setArtifactId("deployer");
//		 model.setVersion("2.3.0-SNAPSHOT");
//		 model.setPomFile(new File("target/pom.xml"));
		 project=new MavenProject(model);
		 location=new File("target/test1.txt");
	}
	
	
	@Test
	public void dependenciesSolverFromPom() throws Exception {
		Date d = new Date();
		
		final String resultFileName = "resultTree.txt";
		
		String serviceID="";
		File projectDir = new File("target");
		if(!projectDir.exists()){
			projectDir.mkdirs();
		};

		
		File resultTree = null;
		
		MavenExecutor mavenExecutor = null;
		String goal = null;
		try {
			System.out.println("MAVEN EXECUTOR WITH projectDir "+projectDir+"    repository  "+repository);
			mavenExecutor = new MavenExecutor(projectDir, repository, new File("target/settings.xml"));
		}catch (Exception e) {
				String error = "Error instantiating Maven Executor ";
				throw e;
		}
		try {
			goal = "dependency:tree";
			
			resultTree = new File(projectDir,resultFileName);
			
			String[][] properties = new String[1][2];
			properties[0][0] = "outputFile";
			properties[0][1] = resultTree.getAbsolutePath();
//			properties[1][0] = "scope";
//			properties[1][1] = "runtime,compile";
			
			System.out.println("executing goals : " + goal + " with outputFile=" + properties[0][1]);
			mavenExecutor.exec(goal, properties);
			
		} catch (Exception e) {
//			e.printStackTrace();
			String error = "Error executing goals : " + goal;
			try{
				System.out.println("error,"+ e.getMessage());
				if(e instanceof org.apache.maven.lifecycle.LifecycleExecutionException) {
					LifecycleExecutionException lifecycleExecutionException = (LifecycleExecutionException) e;
					if (lifecycleExecutionException.getCause() instanceof org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException) {
						MultipleArtifactsNotFoundException multipleArtifactsNotFoundException = (MultipleArtifactsNotFoundException) lifecycleExecutionException.getCause();
						List resolvedArtifacts = multipleArtifactsNotFoundException.getResolvedArtifacts();
						List missingArtifacts = multipleArtifactsNotFoundException.getMissingArtifacts();
						if(resolvedArtifacts!=null && missingArtifacts!=null){
							System.out.println("entered in th first if ...");
							StringBuffer sb = new StringBuffer();
							sb.append("<").append(DEPENDENCY_RESOLUTION).append(">\n");
							int j;
							sb.append("<").append(RESOLVED_DEPENDECIES).append(">\n");
							System.out.println("the resolved dependencies are "+resolvedArtifacts.size());
							for (j=0; j<resolvedArtifacts.size(); j++){
								DefaultArtifact defaultArtifact = (DefaultArtifact) resolvedArtifacts.get(j);
								System.out.println("Resolved Artifact n. " + j + " : " + defaultArtifact);
								try {
									//ret += TreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("Dependency");
									sb.append(MavenResultTreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("Dependency", defaultArtifact));
								} catch (Exception e1) {
									System.out.println(e1);
									throw e1;
								}
							}
							sb.append("</").append(RESOLVED_DEPENDECIES).append(">\n");
							sb.append("<").append(MISSING_DEPENDENCIES).append(">\n");
							for (int k=0; k<missingArtifacts.size(); k++){
								DefaultArtifact defaultArtifact = (DefaultArtifact) missingArtifacts.get(k);
								System.out.println("Missing Artifact n. " + k + " : " + defaultArtifact);
								try {
									//ret[j++] = TreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("MissingDependency");
									sb.append(MavenResultTreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("MissingDependency", defaultArtifact));
								} catch (Exception e1) {
									System.out.println(e1);
									e1.printStackTrace();
									throw e1;
								}
							}
							sb.append("</").append(MISSING_DEPENDENCIES).append(">\n");
							//stringArray.setItems(ret);
							//return stringArray;
							sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
							System.out.println("OUT:    "+sb.toString());
						} 	
					} else if(lifecycleExecutionException.getCause() instanceof org.apache.maven.artifact.versioning.OverConstrainedVersionException ){
						org.apache.maven.artifact.versioning.OverConstrainedVersionException cause = (org.apache.maven.artifact.versioning.OverConstrainedVersionException) lifecycleExecutionException.getCause();
						DefaultArtifact defaultArtifact = (DefaultArtifact) cause.getArtifact();
						StringBuffer sb = new StringBuffer();
						sb.append("<").append(DEPENDENCY_RESOLUTION).append(">\n");
						sb.append("<").append(MISSING_DEPENDENCIES).append(">\n");
						try {
							System.out.println("Missing Artifact " + " : " + defaultArtifact);
							sb.append(MavenResultTreeParser.getArtifactInfo(defaultArtifact.toString()).toXML("MissingDependency"));
						} catch (Exception e1) {
							System.out.println(e1);
							throw e1;
						}
						sb.append("</").append(MISSING_DEPENDENCIES).append(">\n");
						sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
						System.out.println("OUT:    "+sb.toString());
							//stringArray.setItems(ret);
							//return stringArray;
							sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
							System.out.println("OUT:    "+sb.toString());
						} 	
					}	
			}catch(Exception pe){
				System.out.println("null pointer Exception"+pe);
				pe.printStackTrace();
			}
			throw e;
		}
		MavenResultTreeParser treeParser;
		try {
			System.out.println("Parsing dependency tree");
			treeParser = new MavenResultTreeParser(resultTree);
			/* Scope can be null. If null no scope filter is used during resolution */
			treeParser.getScopedDependecy(scope);
			
			StringBuffer sb = new StringBuffer();
			sb.append("<").append(DEPENDENCY_RESOLUTION).append(">\n");
			sb.append(treeParser.listDependency());
			sb.append("</").append(DEPENDENCY_RESOLUTION).append(">\n");
			System.out.println("OUT:    "+sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			String error = "Error resolving tree dependency";
			System.out.println("error,"+e.getMessage());
			throw e;
		} finally {
			System.out.println(projectDir.getAbsoluteFile() + " not removed for debug issue");
		}

	}
//	@Test
////	@requiresDependencyResolution
//	 public void copyArtifacts() throws Exception {
//         
//         Artifact mainArtifact = project().getArtifact();
//         File mainArtifactFile = mainArtifact.getFile();
//         if (mainArtifactFile.exists() && !mainArtifactFile.isDirectory() && mainArtifact.getArtifactHandler().isAddedToClasspath()) {
////                 getLog().info("copying main artifact "+mainArtifact+" to "+location);
//                 FileUtils.copyFile(mainArtifactFile,new File(location, mainArtifactFile.getName()));
//         }
//         
////         for (Artifact artifact: project().getAttachedArtifacts())
////                 if (artifact.getArtifactHandler().isAddedToClasspath()) {
//////                         getLog().info("copying attached artefact "+artifact+" to "+location);
////                         FileUtils.copyFile(artifact.getFile(),new File(location,artifact.getFile().getName()));
////                 }
//         
//         for (Object object : project().getArtifacts()) {
//                  Artifact artifact = (Artifact) object;
////                  getLog().info("copying dependency "+artifact+" to "+location);
//                  FileUtils.copyFile(artifact.getFile(),new File(location,artifact.getFile().getName()));
//          }
//         
// }
//	 public MavenProject project() {
//         return project;
// }
	
	
}
