package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class CheckMethod {
	
	private Logger logger;

	public CheckMethod() 
	{
		this.logger = LoggerFactory.getLogger(CheckMethod.class);
	}
	
	public boolean checkMethod(String machine, String token) throws Exception {
		try {
			this.logger.debug("Checking method for machine "+machine);
			this.logger.debug("By using tocken "+token);
			System.out.println("Machine: " + machine);
			String getCapabilitesRequest = new String();
			String getCapabilitesResponse = new String();
			System.out.println("   Token: " + token);
			String request = "http://" + machine
					+ "/wps/WebProcessingService?Request=GetCapabilities&Service=WPS&gcube-token=" + token;
			String response = machine + "___" + token + ".xml";
			getCapabilitesRequest = request;
			getCapabilitesResponse = response;
			String baseDescriptionRequest = "http://" + machine
					+ "/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0" + "&gcube-token="
					+ token + "&Identifier=";
			URL requestURL = new URL(request);
			this.logger.debug("Request url "+requestURL.toString());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestURL.openStream()));
			FileWriter fileWriter = new FileWriter(response);
			String line;
			boolean flag = true;
			this.logger.debug("Writing file");
			while (flag && (line = bufferedReader.readLine()) != null) {
				this.logger.debug(line);
				fileWriter.write(line);
				fileWriter.write(System.lineSeparator());
				
				if (line.contains("ows:Identifier")) 
				{
					this.logger.debug("Identifier found");
					String operatorName = line.substring(line.indexOf(">") + 1);
					operatorName = operatorName.substring(0, operatorName.indexOf("<"));
					this.logger.debug("Operator "+operatorName);
					System.out.println("      " + operatorName);
					URL innerRequestURL = new URL(baseDescriptionRequest + operatorName);
					BufferedReader innerBufferedReader = new BufferedReader(
							new InputStreamReader(innerRequestURL.openStream()));
					String innerLine = innerBufferedReader.readLine();
					this.logger.debug("Inner line "+innerLine);
					boolean innerFlag = true;
					while (innerFlag && (innerLine = innerBufferedReader.readLine()) != null) 
					{
						if (innerLine.contains("ows:Abstract")) 
						{
							this.logger.debug("Abstract found");
							String operatorDescription = innerLine.substring(innerLine.indexOf(">") + 1);
							operatorDescription = operatorDescription.substring(0, operatorDescription.indexOf("<"));
							this.logger.debug("Operator descriptor "+operatorDescription);
							System.out.println("         " + operatorDescription);
							innerFlag = false;
						} else if (innerLine.contains("ows:ExceptionText")) 
						{
							this.logger.debug("Exception found");
							System.out.println("         " + "error retrieving operator description");
							innerFlag = false;
							flag = false;
						} else
						{
							innerLine = innerBufferedReader.readLine();
							this.logger.debug("Inner line completed "+innerLine);
						}
					}
				}
			}
			
			this.logger.debug("Operation successful");
			fileWriter.close();
			return true;

		} catch (Exception e) {
			e.getMessage();
			this.logger.error("Error "+e.getMessage(),e);
			return false;
		}
	}
	
	
	
	public boolean algoExists(Algorithm algo/*, String env*/) throws Exception{

		this.logger.debug("Looking if algo "+algo.getName()+ " exists");
		Configuration stagingConfiguration = DMPMClientConfiguratorManager.getInstance().getStagingConfiguration(); 
		File file = new File(stagingConfiguration.getGhostAlgoDirectory()+"/"+algo.getName()+".jar");
		File file2 = new File(stagingConfiguration.getGhostAlgoDirectory()+"/"+algo.getName()+"_interface.jar");
		this.logger.debug("Looking for files "+file.getPath()+ " "+file.getPath());
		
		System.out.println("First file is located to: "+file.getPath());
		System.out.println("Second file is located to: "+file2.getPath());
		
		
		if ((this.doesExist(file.getPath()/*,env*/)) && (this.doesExist(file2.getPath()/*,env*/)))
		{
			this.logger.debug("Files found");
			this.copyFromDmToSVN(file/*,env*/);
			this.copyFromDmToSVN(file2/*,env*/);
			System.out.println("Files have been copied to SVN");


			return true;
		}
		else 
		{
			this.logger.debug("Files not found");
			System.out.println("Algorithm "+algo.getName()+".jar"+ " and "+algo.getName()+"_interface.jar files are not present at the expected path");
			return false;
		}
		
	}
	
	
	public boolean algoExistsProd(Algorithm a/*, String env*/) throws Exception{
		Configuration productionConfiguration = DMPMClientConfiguratorManager.getInstance().getProductionConfiguration(); 
		File file = new File(productionConfiguration.getGhostAlgoDirectory()+"/"+a.getName()+".jar");
		File file2 = new File(productionConfiguration.getGhostAlgoDirectory()+"/"+a.getName()+"_interface.jar");

		
		System.out.println("First file is located to: "+file.getPath());
		System.out.println("Second file is located to: "+file2.getPath());
		
		
		if ((this.doesExistProd(file.getPath()/*,env*/)) && (this.doesExistProd(file2.getPath()/*,env*/))){
			this.copyFromDmToSVNProd(file/*,env*/);
			this.copyFromDmToSVNProd(file2/*,env*/);


			return true;
		}
		else 
			System.out.println("Algorithm "+a.getName()+".jar"+ " and "+a.getName()+"_interface.jar files are not present at the expected path");
			return false;		
	}
	
	
	public void deleteFiles(Algorithm a/*,String env*/) throws Exception{
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp c = null;
		Configuration stagingConfiguration = DMPMClientConfiguratorManager.getInstance().getStagingConfiguration(); 
		System.out.println("checking existing in env: " + stagingConfiguration.getHost());
		
		File file = new File(stagingConfiguration.getGhostAlgoDirectory()+"/"+a.getName()+".jar");
		File file2 = new File(stagingConfiguration.getGhostAlgoDirectory()+"/"+a.getName()+"_interface.jar");
		
		
		System.out.println("First file is located to: "+file.getPath());
		System.out.println("Second file is located to: "+file2.getPath());
		
		
		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", stagingConfiguration.getHost());
		System.out.println("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();

		channel = session.openChannel("sftp");
		channel.connect();
		System.out.println("shell channel connected....");

		c = (ChannelSftp) channel;

		if(doesExist(file.getPath()/*,env*/)&&(doesExist(file2.getPath()/*,env*/))){
			
			c.rm(file.getPath());
			c.rm(file2.getPath());
	    	System.out.println("Both the files have been deleted");
		}
		else System.out.println("Files not found");
	 	channel.disconnect();
		c.disconnect();
		session.disconnect();

	}
	
	
	public void deleteFilesProd(Algorithm a/*,String env*/) throws Exception{
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp c = null;
		Configuration productionConfiguration = DMPMClientConfiguratorManager.getInstance().getProductionConfiguration(); 
		System.out.println("checking existing in env: " + productionConfiguration.getHost());
		
		File file = new File(productionConfiguration.getGhostAlgoDirectory()+"/"+a.getName()+".jar");
		File file2 = new File(productionConfiguration.getGhostAlgoDirectory()+"/"+a.getName()+"_interface.jar");
		
		
		System.out.println("First file is located to: "+file.getPath());
		System.out.println("Second file is located to: "+file2.getPath());
		
		
		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", productionConfiguration.getHost());
		System.out.println("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();

		channel = session.openChannel("sftp");
		channel.connect();
		System.out.println("shell channel connected....");

		c = (ChannelSftp) channel;

		if(doesExistProd(file.getPath()/*,env*/)&&(doesExistProd(file2.getPath()/*,env*/))){
			
			c.rm(file.getPath());
			c.rm(file2.getPath());
	    	System.out.println("Both the files have been deleted");
		}
		else System.out.println("Files not found");
	 	channel.disconnect();
		c.disconnect();
		session.disconnect();

	}
	
	
	
	public boolean doesExist(String path/*, String env*/) throws Exception {
		this.logger.debug("Looking if file "+path + " exists");
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp c = null;
		boolean success = false;
		Configuration stagingConfiguration = DMPMClientConfiguratorManager.getInstance().getStagingConfiguration(); 
		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		this.logger.debug("Staging configuration host "+stagingConfiguration.getHost());
		session = jsch.getSession("root",stagingConfiguration.getHost() );
		System.out.println("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();

		channel = session.openChannel("sftp");
		channel.connect();
		System.out.println("shell channel connected....");

		c = (ChannelSftp) channel;

		SftpATTRS is = null;
		System.out.println(path);

		try {			
			is = c.lstat(path);
			this.logger.debug("File found");
			success = true;
		} catch (SftpException e) 
		{
			this.logger.error("File not found",e);
			
			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) 
			{
				// file doesn't exist
				success = false;
			}
			//success = true; // something else went wrong
		}
		channel.disconnect();
		c.disconnect();
		session.disconnect();
		this.logger.debug("Operation result "+success);
		return success;

	}
	
	
	
	public boolean doesExistProd(String path/*, String env*/) throws Exception {
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp c = null;
		boolean success = false;
		Configuration productionConfiguration = DMPMClientConfiguratorManager.getInstance().getProductionConfiguration(); 


		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", productionConfiguration.getHost());
		System.out.println("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();

		channel = session.openChannel("sftp");
		channel.connect();
		System.out.println("shell channel connected....");

		c = (ChannelSftp) channel;

		SftpATTRS is = null;
		System.out.println(path);

		try {
			is = c.lstat(path);
			success = true;
		} catch (SftpException e) {
			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				// file doesn't exist
				success = false;
			}
			//success = true; // something else went wrong
		}
		channel.disconnect();
		c.disconnect();
		session.disconnect();
		return success;

	}
	
	
	public void copyFromDmToSVN(File a/*,String env*/) throws Exception {
		JSch jsch = new JSch();
		Session session = null;
	    SVNUpdater svnUpdater = new SVNUpdater();
		Configuration stagingConfiguration = DMPMClientConfiguratorManager.getInstance().getStagingConfiguration(); 


		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", stagingConfiguration.getHost());
		System.out.println("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();
		
		Channel channel = session.openChannel("sftp");
		channel.connect();

		ChannelSftp sftp = (ChannelSftp) channel;

		sftp.cd(stagingConfiguration.getGhostAlgoDirectory());
		
		System.out.println("REMOTE : "+stagingConfiguration.getGhostAlgoDirectory()+"/"+a.getName());
		System.out.println("LOCAL : /tmp/"+a.getName());
		
		sftp.get(stagingConfiguration.getGhostAlgoDirectory()+"/"+a.getName(),"/tmp/"+a.getName());

		channel.disconnect();
		session.disconnect();
	
	    File f = new File("/tmp/"+a.getName());
		svnUpdater.updateAlgorithmFiles(f);	
		f.delete();
	}
 
 
	
	public void copyFromDmToSVNProd(File a/*,String env*/) throws Exception {
		JSch jsch = new JSch();
		Session session = null;
		SVNUpdater svnUpdater = new SVNUpdater();
		Configuration productionConfiguration = DMPMClientConfiguratorManager.getInstance().getProductionConfiguration(); 

		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", productionConfiguration.getHost());
		System.out.println("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();
		
		Channel channel = session.openChannel("sftp");
		channel.connect();

		ChannelSftp sftp = (ChannelSftp) channel;

		sftp.cd(productionConfiguration.getGhostAlgoDirectory());
		
		System.out.println("REMOTE : "+productionConfiguration.getGhostAlgoDirectory()+"/"+a.getName());
		System.out.println("LOCAL : /tmp/"+a.getName());
		
		sftp.get(productionConfiguration.getGhostAlgoDirectory()+"/"+a.getName(),"/tmp/"+a.getName());

		channel.disconnect();
		session.disconnect();
	
	    File f = new File("/tmp/"+a.getName());
		svnUpdater.updateAlgorithmFilesProd(f);	
		f.delete();
	}
    
	public List<String> getFiles(String a){

		String[] array = a.split(",");
		ArrayList<String> list = new ArrayList<>(Arrays.asList(array));
		List<String> ls = new LinkedList<String>();
		
        for (String s: list){
        	ls.add(s.trim());
        }
      
     	return ls;	
	} 
	
	
	public static void main(String[] args) throws Exception {
//		ServiceConfiguration a = new ServiceConfiguration();
//		System.out.println(a.getStagingHost());
	
	CheckMethod a = new CheckMethod();
	
	//a.getFiles("/trunk/data-analysis/RConfiguration/RPackagesManagement/r_deb_pkgs.txt, /trunk/data-analysis/RConfiguration/RPackagesManagement/r_cran_pkgs.txt, /trunk/data-analysis/RConfiguration/RPackagesManagement/r_github_pkgs.txt");
	
//	File aa = new File("OCTAVEBLACKBOX.jar");
//	System.out.println(aa.getName());
//	System.out.println(aa.getPath());
	
	
	
	
	
	
	//a.copyFromDmToSVN(aa);
//	if (a.checkMethod("dataminer-ghost-d.dev.d4science.org", "708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548")){
//		System.out.println("AAA");	}
//		
//	if (a.doesExist("/home/gcube/wps_algorithms/algorithms/WINDOWS_BLACK_BOX_EXAMPLE.jar")){
//		System.out.println("BBBB");
//	
//	}
//	if (a.doesExist("/home/gcube/wps_algorithms/algorithms/WINDOWS_BLACK_BOX_EXAMPLE_interface.jar")){
//		System.out.println("CCCC");}
//	
//	File aa = new File("/home/gcube/wps_algorithms/algorithms/RBLACKBOX_interface.jar");
//	a.copyFromDmToSVN(aa, "Dev");
	
//
	
	
//System.out.println(a.checkMethod("dataminer-ghost-t.pre.d4science.org",
//		"2eceaf27-0e22-4dbe-8075-e09eff199bf9-98187548"));

//System.out.println(a.checkMethod("dataminer-proto-ghost.d4science.org",
	//	"3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462"));

System.out.println(a.checkMethod("dataminer-ghost-d.dev.d4science.org",
	"708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548"));


//Algorithm aa = new Algorithm();
//aa.setName("UDPIPE_WRAPPER");
//System.out.println(a.algoExists(aa));
////
//ServiceConfiguration bp = new ServiceConfiguration();
////
//SecurityTokenProvider.instance.set("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");
////
//if (a.checkMethod(bp.getStagingHost(), SecurityTokenProvider.instance.get())&&a.algoExists(aa)); {
//System.out.println("ciao");
//
//}

//
//Algorithm al = new Algorithm();
//	al.setName("UDPIPE_WRAPPER");
//	a.deleteFiles(al);
	
	
	
	
	
}
}
