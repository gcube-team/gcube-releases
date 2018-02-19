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
import java.util.Properties;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.AlgorithmException;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.GenericException;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.SVNCommitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public abstract class CheckMethod {
	
	private Logger logger;
	private Configuration configuration;
	
	private final String 	KNOWN_HOSTS= "~/.ssh/known_hosts",
							PRIVATE_KEY = "~/.ssh/id_rsa",
							SSH_USER = "root",
							SFTP_PROTOCOL = "sftp",
							TEMP_DIRECTORY = "tmp";
	private final Properties sshConfig;
	
	public CheckMethod(Configuration configuration) 
	{
		this.logger = LoggerFactory.getLogger(CheckMethod.class);
		this.configuration = configuration;
		sshConfig = new java.util.Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
	}
	
	public void checkMethod(String machine, String token) throws AlgorithmException {
		try {
			this.logger.debug("Checking method for machine "+machine);
			this.logger.debug("By using tocken "+token);
			this.logger.debug("Machine: " + machine);
//			String getCapabilitesRequest = new String();
//			String getCapabilitesResponse = new String();
			this.logger.debug("   Token: " + token);
			String request = "http://" + machine
					+ "/wps/WebProcessingService?Request=GetCapabilities&Service=WPS&gcube-token=" + token;
			String response = machine + "___" + token + ".xml";
//			getCapabilitesRequest = request;
//			getCapabilitesResponse = response;
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
							this.logger.debug("         " + operatorDescription);
							innerFlag = false;
						} else if (innerLine.contains("ows:ExceptionText")) 
						{
							this.logger.debug("Exception found");
							this.logger.debug("         " + "error retrieving operator description");
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


		} catch (Exception e) {
			
			throw new AlgorithmException("Error "+e.getMessage(),e);
			
		}
	}
	
	

	public void copyAlgorithms(Algorithm algo/*, String env*/) throws SVNCommitException, GenericException, AlgorithmException
	{

		this.logger.debug("Looking if algo "+algo.getName()+ " exists");
		File file = new File(this.configuration.getGhostAlgoDirectory()+"/"+algo.getName()+".jar");
		File file2 = new File(this.configuration.getGhostAlgoDirectory()+"/"+algo.getName()+"_interface.jar");
		this.logger.debug("Looking for files "+file.getPath()+ " "+file.getPath());
		boolean fileExists = false;
		
		try
		{
			fileExists = (this.doesExist(file.getPath()/*,env*/)) && (this.doesExist(file2.getPath()/*,env*/));
			
		} catch (Exception e)
		{
			throw new GenericException(e);
		}
		
		
		if (fileExists)
		{
			
			try
			{
				this.logger.debug("Files found");
				this.copyFromDmToSVN(file/*,env*/);
				this.copyFromDmToSVN(file2/*,env*/);
				this.logger.debug("Files have been copied to SVN");
			} catch (Exception e)
			{
				throw new GenericException(e);
			}

		}
		else 
		{
			this.logger.debug("Files not found");
			this.logger.debug("Algorithm "+algo.getName()+".jar"+ " and "+algo.getName()+"_interface.jar files are not present at the expected path");
			throw new AlgorithmException("Algorithm "+algo.getName()+".jar"+ " and "+algo.getName()+"_interface.jar files are not present at the expected path");
		}
		
	}


	
	public void deleteFiles(Algorithm a/*,String env*/) throws GenericException
	{
		try
		{
			Session session = generateSession();
			this.logger.debug("checking existing in env: " + this.configuration.getHost());
			
			File file = new File(this.configuration.getGhostAlgoDirectory()+"/"+a.getName()+".jar");
			File file2 = new File(this.configuration.getGhostAlgoDirectory()+"/"+a.getName()+"_interface.jar");
			
			
			this.logger.debug("First file is located to: "+file.getPath());
			this.logger.debug("Second file is located to: "+file2.getPath());
			
			
			this.logger.debug("session created.");
			session.setConfig(this.sshConfig);
			session.connect();

			Channel channel = session.openChannel(SFTP_PROTOCOL);
			channel.connect();
			this.logger.debug("shell channel connected....");

			ChannelSftp c = (ChannelSftp) channel;

			if(doesExist(file.getPath()/*,env*/)&&(doesExist(file2.getPath()/*,env*/))){
				
				c.rm(file.getPath());
				c.rm(file2.getPath());
				this.logger.debug("Both the files have been deleted");
			}
			else this.logger.debug("Files not found");
		 	channel.disconnect();
			c.disconnect();
			session.disconnect();
		} catch (Exception e)
		{
			throw new GenericException(e);
		}
		


	}
	
	


	public boolean doesExist(String path/*, String env*/) throws Exception {

		Session session = generateSession();
		boolean success = false;
		session.connect();
		Channel channel = session.openChannel(SFTP_PROTOCOL);
		channel.connect();
		this.logger.debug("shell channel connected....");
		ChannelSftp c = (ChannelSftp) channel;
		this.logger.debug(path);

		try {
			c.lstat(path);
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
		this.logger.debug("Operation result "+success);
		return success;

	}

	
	protected abstract void copyFromDmToSVN(File a) throws SVNCommitException, Exception;

	
	protected void copyFromDmToSVN(File algorithmsFile/*,String env*/,SVNUpdater svnUpdater) throws SVNException, SVNCommitException, JSchException, SftpException {

		this.logger.debug("Copying algorithm file from Data Miner to SVN");
		String fileName = algorithmsFile.getName();
		this.logger.debug("File name "+fileName);
		Session session = generateSession();
		session.connect();
		Channel channel = session.openChannel(SFTP_PROTOCOL);
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		sftp.cd(this.configuration.getGhostAlgoDirectory());
		String remoteFile = new StringBuilder(this.configuration.getGhostAlgoDirectory()).append(File.separator).append(fileName).toString();
		this.logger.debug("Remote file "+remoteFile);
		String localFile = new StringBuilder(File.separator).append(TEMP_DIRECTORY).append(File.separator).append(fileName).toString();
		this.logger.debug("Local file "+localFile);
		sftp.get(remoteFile,localFile);
		channel.disconnect();
		session.disconnect();
	    File f = new File(localFile);
		svnUpdater.updateAlgorithmFiles(f);	
		f.delete();
	}
 
	private Session generateSession () throws JSchException
	{
		JSch jsch = new JSch();
		jsch.setKnownHosts(KNOWN_HOSTS);
		jsch.addIdentity(PRIVATE_KEY);
		this.logger.debug("Private Key Added.");
		Session session = jsch.getSession(SSH_USER, this.configuration.getHost());
		this.logger.debug("session created.");
		session.setConfig(this.sshConfig);
		return session;
	}
 

    
	public static List<String> getFiles(String a){

		String[] array = a.split(",");
		ArrayList<String> list = new ArrayList<>(Arrays.asList(array));
		List<String> ls = new LinkedList<String>();
		
        for (String s: list){
        	ls.add(s.trim());
        }
      
     	return ls;	
	} 
	
	

}
