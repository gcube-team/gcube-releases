package org.gcube.datatransformation.datatransformationlibrary.deployer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.model.SoftwarePackage;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <tt>DTSProgramDeployer</tt> deploys packages containing {@link Program}s.
 */
public class DTSProgramDeployer {

	/**
	 * Just a testing method.
	 * 
	 * @param args Arguments of main method.
	 * @throws Exception If deployer failed to deploy packages.
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<SoftwarePackage> packages = new ArrayList<SoftwarePackage>();
		SoftwarePackage pkg1 = new SoftwarePackage();
		pkg1.setId("package_apache_poi");
		pkg1.setLocation(new URL("http://dl07.di.uoa.gr:8080/programs/apachepoi_library.tar.gz"));
		packages.add(pkg1);
		
		SoftwarePackage pkg2 = new SoftwarePackage();
		pkg2.setId("package_apache_pdf");
		pkg2.setLocation(new URL("http://dl07.di.uoa.gr:8080/programs/apachepdf_library.tar.gz"));
		packages.add(pkg2);
		
		SoftwarePackage pkg3 = new SoftwarePackage();
		pkg3.setId("dts_programs_bundle");
		pkg3.setLocation(new URL("http://dl07.di.uoa.gr:8080/programs/dts_programs_bundle.tar.gz"));
		packages.add(pkg3);
		
		SoftwarePackage pkg4 = new SoftwarePackage();
		pkg4.setId("package_ij_lib");
		pkg4.setLocation(new URL("http://dl07.di.uoa.gr:8080/programs/ij_library.tar.gz"));
		packages.add(pkg4);
		
		new DTSProgramDeployer(args[0]).deployPackages(packages);
	}
	
	/**
	 * The location in which the deployer will put the jar files of the deployed package.
	 */
	private String DTS_LIBS_PATH;

	/**
	 * Loads jars deployed by <tt>DTSProgramDeployer</tt>. This class loader searches for jars in <tt>DTS_LIBS_PATH</tt>. 
	 */
	private URLClassLoader depClassLoader;

	/**
	 * Returns the <tt>DTS_LIBS_PATH</tt>.
	 * @return The <tt>DTS_LIBS_PATH</tt>.
	 */
	public String getDTSLibsPath() {
		return DTS_LIBS_PATH;
	}

	/**
	 * The file of <tt>DTS_LIBS_PATH</tt>.
	 */
	private File dtsLibsPathFile;
	/**
	 * File containing the deployed packages.
	 */
	private File dtsDeployedPkgsFile;
	
	/**
	 * Initializes the <tt>DTSProgramDeployer</tt>.
	 * 
	 * @param DTS_LIBS_PATH Sets the the <tt>DTS_LIBS_PATH</tt>.
	 * @throws Exception If <tt>DTSProgramDeployer</tt> could not be initialized.
	 */
	public DTSProgramDeployer(String DTS_LIBS_PATH) throws Exception {
		if(!DTS_LIBS_PATH.endsWith(File.separator)){
			DTS_LIBS_PATH=DTS_LIBS_PATH+File.separator;
		}
		
		dtsLibsPathFile = new File(DTS_LIBS_PATH);
		if(!dtsLibsPathFile.exists()){
			if(dtsLibsPathFile.mkdirs()==false){
				throw new Exception("Did not manage to create DTS_LIBS_PATH for given path: "+DTS_LIBS_PATH);
			}
		}
		dtsDeployedPkgsFile = new File(DTS_LIBS_PATH+DEPLOYED_PKGS_FILE_NAME);
		if(!dtsDeployedPkgsFile.exists()){
			if(dtsDeployedPkgsFile.createNewFile()==false){
				throw new Exception("Did not manage to create dtsDeployedPkgsFile: "+DTS_LIBS_PATH+DEPLOYED_PKGS_FILE_NAME);
			}
		}else{
			checkInstalledPackages();
		}
		this.DTS_LIBS_PATH = DTS_LIBS_PATH;
		this.updateClassLoader();
	}

	/**
	 * The name of the file which contains the deployed packages.
	 */
	private static final String DEPLOYED_PKGS_FILE_NAME = "dts_deployed_pkgs";
	
	/**
	 * Logs operations performed by <tt>DTSProgramDeployer</tt>.
	 */
	private static Logger log = LoggerFactory.getLogger(DTSProgramDeployer.class);
	
	/**
	 * Refreshes the list with the deployed packages.
	 * 
	 * @throws Exception If checking could not be performed.
	 */
	private void checkInstalledPackages() throws Exception {
		try{
			FileInputStream fstream = new FileInputStream(dtsDeployedPkgsFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String deployedPackage;
			//Reading file. Each line contains one deployed package... 
			while ((deployedPackage = br.readLine()) != null){
				log.debug("Found installed package: "+deployedPackage);
				deployedPackages.add(deployedPackage);
			}
			in.close();
		} catch (Exception e){
			log.error("Did not manage to check if deployed packages exist", e);
			throw new Exception("Did not manage to check if deployed packages exist", e);
		}
	}
	
	/**
	 * {@link Set} which contains the ids of the deployed packages.
	 */
	private HashSet<String> deployedPackages = new HashSet<String>();
	
	/**
	 * Keeps measures for the time needed to deploy a package.
	 */
	private static Metric deployPackageMetric = StatisticsManager.createMetric("DeployPackageMetric", "Time to deploy a package", MetricType.DTS);
	
	/**
	 * Deploys packages.
	 * 
	 * @param packages {@link List} with the packages that will be deployed.
	 * @throws Exception If an error occurred in deploying the packages.
	 */
	synchronized public void deployPackages(ArrayList<SoftwarePackage> packages) throws Exception {
		for(SoftwarePackage swrPackage: packages){
			if(isPackageDeployed(swrPackage.getId())){
				log.trace("Package "+swrPackage.getId()+" is already deployed");
				continue;
			}
			//Deploying a package...
			long startTime = System.currentTimeMillis();
			String packageID = swrPackage.getId();
			URL packageLocation = swrPackage.getLocation();
			log.debug("Deploying package "+packageID+"...");
			log.debug("This package will be fetched from "+packageLocation);
			try {//TODO: Better download package to tmp directory taken from file utils...
				org.apache.commons.io.FileUtils.copyURLToFile(packageLocation, 
						new File(DTS_LIBS_PATH+packageID+File.separator+packageID+".tar.gz"));
				log.debug("Package downloaded successfully to file "+DTS_LIBS_PATH+packageID);
			} catch (Exception e) {
				log.error("Did not manage to save package file into DTS_LIBS_PATH", e);
				throw new Exception("Did not manage to save package file into DTS_LIBS_PATH", e);
			}
			//Here executing the deployScript...
			Process process;
			try {
				process = Runtime.getRuntime().exec(DTS_LIBS_PATH+"deployScript.sh "+packageID+" "+DTS_LIBS_PATH);
			} catch (Exception e) {
				log.error("Execution of the deploy script failed", e);
				throw new Exception("Execution of the deploy script failed", e);
			}
			int returnCode = process.waitFor();
			if(returnCode!=0){
				log.error("Execution of the deploy script failed: Return code = " + returnCode);
				throw new Exception("Execution of the deploy script failed: Return code = " + returnCode); 
			}else{
				log.debug("Execution of the deploy script succeeded");
			}
			//Here adding the the package to the deployed ones...
			deployedPackages.add(packageID);
			org.apache.commons.io.FileUtils.writeLines(dtsDeployedPkgsFile, "UTF-8", deployedPackages);
			updateClassLoader();
			deployPackageMetric.addMeasure(System.currentTimeMillis()-startTime);
		}
	}
	
	/**
	 * Checks if a package is deployed.
	 * 
	 * @param packageID The id of the package that will be checked.
	 * @return true if package is already deployed.
	 */
	private boolean isPackageDeployed(String packageID){
		return deployedPackages.contains(packageID);
	}
	
	/**
	 * Filters out the files that are not jars.
	 */
	private FileFilter fileFilter = new org.apache.commons.io.filefilter.WildcardFilter("*.jar");
	
	/**
	 * <p>Updates the class loader.</p>
	 * <p>Creates a new {@link ClassLoader} which reads the contents of <tt>DTS_LIBS_PATH</tt></p>
	 * 
	 * @throws Exception If an error occurred in updating the class loader.
	 */
	private void updateClassLoader() throws Exception {

		log.debug("Updating the DTS Class loader...");
		ArrayList<URL> urls = new ArrayList<URL>();

		//Putting DTS_LIBS_PATH into the classpath of the dts class loader(for .class files)...
		urls.add(dtsLibsPathFile.toURL());
		
		//Putting into dts class loader only jar files...
		File[] files = dtsLibsPathFile.listFiles(fileFilter);
		for (File file : files) {
			log.trace("Putting "+file.getAbsolutePath()+" into class loaders urls");
			urls.add(file.toURL());
		}
		//Create a new class loader with the directory
		//Setting as parent the current 
		this.depClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
	}
	
	/**
	 * <p>Returns the class loader which also looks in the <tt>DTS_LIBS_PATH</tt> directory.</p>
	 * <p>Note: It is not safe to return the cl and keep references to it.</p>
	 * 
	 * @return The class loader.
	 */
	public ClassLoader getClassLoader(){
		return this.depClassLoader;
	}

}

