package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class CheckMethod {
	
	

	public CheckMethod() {

	}
	
	public boolean checkMethod(String machine, String token) throws Exception {
		try {
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
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestURL.openStream()));
			FileWriter fileWriter = new FileWriter(response);
			String line;
			boolean flag = true;
			while (flag && (line = bufferedReader.readLine()) != null) {
				fileWriter.write(line);
				fileWriter.write(System.lineSeparator());
				if (line.contains("ows:Identifier")) {
					String operatorName = line.substring(line.indexOf(">") + 1);
					operatorName = operatorName.substring(0, operatorName.indexOf("<"));
					System.out.println("      " + operatorName);
					URL innerRequestURL = new URL(baseDescriptionRequest + operatorName);
					BufferedReader innerBufferedReader = new BufferedReader(
							new InputStreamReader(innerRequestURL.openStream()));
					String innerLine = innerBufferedReader.readLine();
					boolean innerFlag = true;
					while (innerFlag && (innerLine = innerBufferedReader.readLine()) != null) {
						if (innerLine.contains("ows:Abstract")) {
							String operatorDescription = innerLine.substring(innerLine.indexOf(">") + 1);
							operatorDescription = operatorDescription.substring(0, operatorDescription.indexOf("<"));
							System.out.println("         " + operatorDescription);
							innerFlag = false;
						} else if (innerLine.contains("ows:ExceptionText")) {
							System.out.println("         " + "error retrieving operator description");
							innerFlag = false;
							flag = false;
						} else
							innerLine = innerBufferedReader.readLine();
					}
				}
			}
			fileWriter.close();
		} catch (Exception a) {
			a.getMessage();
			return false;
		}
		return true;
	}
	
	
	
	public boolean algoExists(Algorithm a/*, String env*/) throws Exception{
		ServiceConfiguration p = new ServiceConfiguration();

		File file = new File(p.getGhostAlgoDirectory()+"/"+a.getName()+".jar");
		File file2 = new File(p.getGhostAlgoDirectory()+"/"+a.getName()+"_interface.jar");

		
		System.out.println("First file is located to: "+file.getPath());
		System.out.println("Second file is located to: "+file2.getPath());
		
		
		if ((this.doesExist(file.getPath()/*,env*/)) && (this.doesExist(file2.getPath()/*,env*/))){
			this.copyFromDmToSVN(file/*,env*/);
			this.copyFromDmToSVN(file2/*,env*/);


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
		ServiceConfiguration p = new ServiceConfiguration();
		System.out.println("checking existing in env: " + p.getStagingHost());
		
		File file = new File(p.getGhostAlgoDirectory()+"/"+a.getName()+".jar");
		File file2 = new File(p.getGhostAlgoDirectory()+"/"+a.getName()+"_interface.jar");
		
		
		System.out.println("First file is located to: "+file.getPath());
		System.out.println("Second file is located to: "+file2.getPath());
		
		
		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", p.getStagingHost());
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
	
	
	
	
	
	
	public boolean doesExist(String path/*, String env*/) throws Exception {
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp c = null;
		boolean success = false;
		ServiceConfiguration p = new ServiceConfiguration();


		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", p.getStagingHost());
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
		ServiceConfiguration sc = new ServiceConfiguration();
	    SVNUpdater svnUpdater = new SVNUpdater(sc);

		ServiceConfiguration p = new ServiceConfiguration();

		jsch.setKnownHosts("~/.ssh/known_hosts");
		String privateKey = "~/.ssh/id_rsa";

		jsch.addIdentity(privateKey);
		System.out.println("Private Key Added.");

		session = jsch.getSession("root", p.getStagingHost());
		System.out.println("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();
		
		Channel channel = session.openChannel("sftp");
		channel.connect();

		ChannelSftp sftp = (ChannelSftp) channel;

		sftp.cd(p.getGhostAlgoDirectory());
		
		System.out.println("REMOTE : "+p.getGhostAlgoDirectory()+"/"+a.getName());
		System.out.println("LOCAL : /tmp/"+a.getName());
		
		sftp.get(p.getGhostAlgoDirectory()+"/"+a.getName(),"/tmp/"+a.getName());

		channel.disconnect();
		session.disconnect();
	
	    File f = new File("/tmp/"+a.getName());
		svnUpdater.updateAlgorithmFiles(f);	
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
	
	a.getFiles("/trunk/data-analysis/RConfiguration/RPackagesManagement/r_deb_pkgs.txt, /trunk/data-analysis/RConfiguration/RPackagesManagement/r_cran_pkgs.txt, /trunk/data-analysis/RConfiguration/RPackagesManagement/r_github_pkgs.txt");
	
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
	
	
//a.checkMethod("dataminer-ghost-d.dev.d4science.org",
//		"708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");



//	Algorithm al = new Algorithm();
//	al.setName("RBLACKBOX");
//	a.deleteFiles(al);
	
	
	
	
	
	
}
}
