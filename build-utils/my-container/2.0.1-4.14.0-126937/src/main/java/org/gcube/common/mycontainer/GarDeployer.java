/**
 * 
 */
package org.gcube.common.mycontainer;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * @author Fabio Simeoni
 *
 */
public class GarDeployer {

	private static Logger logger =  Logger.getLogger(GarDeployer.class);
	
	static final String DEPLOY_INTERFACE_TARGET = "deployInterface";
	static final String DEPLOY_CONFIGURATION_TARGET = "deployConfiguration";
	static final String DEPLOY_STUBS_TARGET = "deployStubs";
	static final String UNDEPLOY_TARGET = "undeploy";
	static final String MAKE_AND_DEPLOY_GAR_TARGET = "makeAndDeployGar";
	static final String GENERATE_WSDL_TARGET = "generateWsdl";
	static final String DEPLOY_GAR_TARGET = "deployGar";
	static final String UNDEPLOY_GAR_TARGET = "undeployGar";
	static final String CONTAINER_LOCATION_BUILD_PROPERTY = "container.dir";
	static final String SERVICE_LOCATION_BUILD_PROPERTY = "service.dir";
	static final String TARGET_PATH = "share/gcore_tools";
	static final String BUILDFILE = TARGET_PATH+"/build.xml";

	static final String SCHEMA_DIR = "share/schema";
	static final String WSRF_SCHEMA_DIR = "wsrf";
	static final String WS_SCHEMA_DIR = "ws";
	static final String GCUBE_SCHEMA_DIR = "gcube";
	
	final MyContainer container;
	
	/**
	 * 
	 */
	GarDeployer(MyContainer container) {
		
		this.container=container;
		
	}
	
	void deploy(Gar gar) {
		
		
		File garFile = gar.file();
		if (garFile==null) 
			buildAndDeploy(gar);
		else
			deploy(gar.file());
		
	}
	
	void deploy(File garFile) {
		
		Project project = project();
		String fileName = garFile.getAbsolutePath();
		project.setProperty("gar.name",fileName);
		init(project);
		
		logger.info("deploying "+fileName+"...");
		project.executeTarget(DEPLOY_GAR_TARGET);
	}
	
	/**
	 * Undeploys a Gar from the container.
	 * @param id the Gar's name.
	 * @throws IllegalStateException if the container is not running
	 */
	void undeploy(String garName) {
		
		Project project = project();
		project.setProperty("gar.id",garName);
		init(project);
		
		logger.trace("undeploying "+garName+"...");
		
		project.executeTarget(UNDEPLOY_GAR_TARGET);
		
	}
	
	void buildAndDeploy(Gar gar) {
		
		try {

			//copy Gar assets in target folder
			File targetDir = new File(new File(container.location(),TARGET_PATH),"target");
			
			File interfaceDir = new File(targetDir,"wsdls");
			interfaceDir.mkdirs();
			for (File inter_face : gar.interfaces())
				FileUtils.copyFileToDirectory(inter_face,interfaceDir);
			
			//are there wsdls to pre-process?
			FilenameFilter filter = new FilenameFilter() {
				@Override public boolean accept(File dir, String name) {
					return name.endsWith(".wsdl");
				}
			};
			
			for (File wsdl : interfaceDir.listFiles(filter)) {
				
				Project project = project();
				
				project.setProperty("target.dir",targetDir.getAbsolutePath());
				project.setProperty("wsdl.dir",interfaceDir.getAbsolutePath());
				project.setProperty("gar.id", gar.id());
				project.setProperty("wsdl", wsdl.getName().substring(0,wsdl.getName().lastIndexOf(".wsdl")));
				
				init(project);
				
				logger.info("pre-processing wsdl "+wsdl.getName()+"...");
				
				project.executeTarget(GENERATE_WSDL_TARGET);
			}
					
			//copy remaining assets in target directory
			File libDir = new File(targetDir,"libs");
			libDir.mkdirs();
			for (File lib : gar.libs())
				if (lib.isDirectory())
					FileUtils.copyDirectory(lib,libDir);
				else
					FileUtils.copyFileToDirectory(lib,libDir);

						
			File configDir = new File(targetDir,"configuration");
			configDir.mkdirs();
			for (File configuration : gar.configuration())
				if (configuration.isDirectory())
					FileUtils.copyDirectory(configuration,configDir);
				else
					FileUtils.copyFileToDirectory(configuration,configDir);
		
			Project project = project();
			
			project.setProperty("target.dir",targetDir.getAbsolutePath());
			project.setProperty("target.schema.dir",new File(targetDir,"schema").getAbsolutePath());
			project.setProperty("lib.dir",libDir.getAbsolutePath());
			project.setProperty("configuration.dir",configDir.getAbsolutePath());
			project.setProperty("gar.id",gar.id());
						
			File deploymentFile = new File(configDir,"deploy-server.wsdd");
			project.setProperty("deploymentFile",deploymentFile.getAbsolutePath());
			
			File deploymentNoSecFile = new File(configDir,"deploy-server.wsdd_NOSEC");
			project.setProperty("deploymentNOSECFile",deploymentNoSecFile.getAbsolutePath());
			
			File clientDeploymentFile = new File(configDir,"deploy-client.wsdd");
			project.setProperty("clientdeploymentFile",clientDeploymentFile.getAbsolutePath());
	
			File clientServerDeploymentFile = new File(configDir,"deploy-client-server.wsdd");
			project.setProperty("clientserverdeploymentFile",clientServerDeploymentFile.getAbsolutePath());
			
			File jndiFile = new File(configDir,"deploy-jndi-config.xml");
			project.setProperty("jndiFile",jndiFile.getAbsolutePath());
			
			init(project);
				
			logger.info("making and deploying gar "+gar.id()+"...");
			project.executeTarget(MAKE_AND_DEPLOY_GAR_TARGET);
		}
		catch(Throwable t){
			throw new RuntimeException("could not make gar file",t);
		}
			
	}
	
	private Project project() {
		
		Project project = new Project();
		
		project.setProperty(CONTAINER_LOCATION_BUILD_PROPERTY, container.location().getAbsolutePath());
		project.setProperty("java.class.path", System.getProperty("java.class.path"));
			
		return project;
	}
	
	private void init(Project project) {
	
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_WARN);
		project.addBuildListener(consoleLogger);
		ProjectHelper helper = ProjectHelper.getProjectHelper();
	
		File buildFile = new File(container.location(),BUILDFILE);
		if (!buildFile.exists())
			throw new IllegalStateException("corrupt container installation? It does not include "+BUILDFILE);
		
		project.init();
	
		project.addReference("ant.projectHelper", helper);
		helper.parse(project, buildFile);

	}

}
