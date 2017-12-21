package org.gcube.dataanalysis.dataminer.poolmanager.util.impl;

import java.io.File;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.util.CheckMethod;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class CheckMethodStaging extends CheckMethod{
	
	private Logger logger;

	public CheckMethodStaging() 
	{
		this.logger = LoggerFactory.getLogger(CheckMethodStaging.class);
	}
	

	
	@Override
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
	

	
	@Override
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

	
	@Override
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

		//SftpATTRS is = null;
		System.out.println(path);

		try {			
			c.lstat(path);
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
	

	@Override
	public void copyFromDmToSVN(File a/*,String env*/) throws Exception {
		JSch jsch = new JSch();
		Session session = null;
	    SVNUpdater svnUpdater = new SVNUpdaterStaging();
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
 
 

	
	
	public static void main(String[] args) throws Exception {
//		ServiceConfiguration a = new ServiceConfiguration();
//		System.out.println(a.getStagingHost());
	
	CheckMethodStaging a = new CheckMethodStaging();
	
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
