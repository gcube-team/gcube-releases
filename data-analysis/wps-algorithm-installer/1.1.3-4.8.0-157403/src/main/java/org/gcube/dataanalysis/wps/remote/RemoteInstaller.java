package org.gcube.dataanalysis.wps.remote;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;

public class RemoteInstaller {

	static String sshConnection = "plink -ssh -i privatekeyss2.ppk gcube@";

	static String print(InputStream std,OutputStream out,InputStream err) throws Exception{
		 int value = 0;
		 StringBuffer sb = new StringBuffer();
		    if (std.available () > 0) {
		        System.out.println ("STD:");
		        value = std.read ();
		        System.out.print ((char) value);
		        sb.append(""+(char) value);
		        while (std.available () > 0) {
		            value = std.read ();
		            System.out.print ((char) value);
		            sb.append(""+(char) value);
		        }
		    }

		    if (err.available () > 0) {
		        System.out.println ("ERR:");
		        value = err.read ();
		        System.out.print ((char) value);
		        sb.append(""+(char) value);
		        while (err.available () > 0) {
		            value = err.read ();
		            System.out.print ((char) value);
		            sb.append(""+(char) value);
		        }
		    }
		    
		    System.out.println();
		    
		    return sb.toString();
	}
	
	static void cmd1(String[] commands) throws Exception {
	Runtime r = Runtime.getRuntime ();
    Process p = r.exec (commands[0]);
    InputStream std = p.getInputStream ();
    OutputStream out = p.getOutputStream ();
    InputStream err = p.getErrorStream ();
    Thread.sleep (1000);
    print(std,out,err);
    int commandTries = 1;
    for (int i=2;i<commands.length;i=i+2){
    	String command = commands[i];
    	System.out.println("Executing "+command);
    	out.write ((command+"\n").getBytes ());
    	out.flush ();
    	Thread.sleep (1000);
    	String value = print(std,out,err);
    	System.out.println("N. LINES: ************************ "+value.length());
    	int k = 1;
    	int steps = Integer.parseInt(commands[i+1]);
    	StringBuffer lastline = new StringBuffer();
    	lastline.append(value);
    	while (k<steps){
    		Thread.sleep(1000);
    		System.out.println("flushing...."+k);
    		value = print(std,out,err);
    		System.out.println("length...."+value.length());
    		k++;
    		if (value.length()>0){
    			k=1;
    			lastline.append(value);
    		}
        	
    	}
    	
    	if (command.contains("./addAlgorithm")){
    		//if (!lastline.toString().contains("All done!")){
    		if (lastline.toString().contains("Exception:")){
    			if (commandTries<2){
    				commandTries++;
    				i = i-2; //retry the command
    			}
    			else{
    				System.err.println("Error at installing the algorithm!!!");
    				System.err.println("last line "+lastline);
    				System.exit(-1);
    			}
    		}
    	}
    	
    }

    p.destroy ();
    
    System.out.println("Ready!");
	}
	
	static void cmd2(String[] commands) throws Exception {
		Runtime r = Runtime.getRuntime ();
	    Process p = r.exec (commands[0]);
	    InputStream std = p.getInputStream ();
	    OutputStream out = p.getOutputStream ();
	    InputStream err = p.getErrorStream ();
	    Thread.sleep (1000);
	    print(std,out,err);
	    int commandTries = 1;
	    for (int i=2;i<commands.length;i=i+2){
	    	String command = commands[i];
	    	System.out.println("Executing "+command);
	    	out.write ((command+"\n").getBytes ());
	    	out.flush ();
	    	Thread.sleep (1000);
	    	String value = print(std,out,err);
	    	System.out.println("N. LINES: ************************ "+value.length());
	    	int k = 1;
	    	int steps = Integer.parseInt(commands[i+1]);
	    	StringBuffer lastline = new StringBuffer();
	    	lastline.append(value);
	    	while (k<steps){
	    		Thread.sleep(1000);
	    		System.out.println("flushing...."+k);
	    		value = print(std,out,err);
	    		System.out.println("length...."+value.length());
	    		k++;
	    		if (value.length()>0){
	    			k=1;
	    			lastline.append(value);
	    			System.out.println("lastline: "+value.substring(0, Math.min(200,value.length())));
	    			if (value.startsWith("gcube@dataminer")){
	    				System.out.println("Prompt READY!");
	    				break;
	    			}
	    		}
	        	
	    	}
	    	
	    	if (command.contains("./addAlgorithm")){
	    		//if (!lastline.toString().contains("All done!")){
	    		if (lastline.toString().contains("Exception:")){
	    			if (commandTries<2){
	    				commandTries++;
	    				i = i-2; //retry the command
	    			}
	    			else{
	    				System.err.println("Error at installing the algorithm!!!");
	    				System.err.println("last line "+lastline);
	    				System.exit(-1);
	    			}
	    		}
	    	}
	    	
	    }

	    p.destroy ();
	    
	    System.out.println("Ready!");
		}
	
	public static void startRobot(String dataminer,String password,String scope, boolean skipinstallerdownload) throws Exception{
		String filepath = "DataMinerAlgorithms.txt";
		startRobot(dataminer, password, scope, filepath, skipinstallerdownload);
	}
	
	public static void startRobot(String dataminer,String password,String scope, String filepath, boolean skipinstallerdownload) throws Exception{
		long t0 = System.currentTimeMillis();
		String installStrings = FileTools.loadString(filepath,"UTF-8");
		//String[] install = installStrings.split("\n");
		String[] install = installStrings.split("\n");
		System.out.println("Algorithms to install "+install.length);
		ArrayList<String> installArray = new ArrayList<String>(Arrays.asList(install));
		
		String rmlogging = "rm ./tomcat/webapps/wps/WEB-INF/lib/log4j-over-slf4j-1.7.5.jar";
		String rmlib1 = "rm ./tomcat/webapps/wps/WEB-INF/lib/STEP1VPAICCATBFTERetros-1.0.0.jar";
//		String rmlib1 = "rm ./tomcat/webapps/wps/WEB-INF/lib/ECOPATH*";
		String rmlib2 = "rm ./tomcat/webapps/wps/WEB-INF/lib/TunaAtlasDataAccess-1.0.0.jar";
		String rmlib3 = "rm ./tomcat/webapps/wps/WEB-INF/lib/dataminer-algorithms.jar";
		
		
		String rmInstaller = "rm algorithmInstaller.zip";
		String rmInstallerFolder = "rm -r ./algorithmInstaller";
		
		String chmod = "chmod 777 tomcat/webapps/wps/config/*";
		String rmSMState = "rm -r SmartGears/state/";
		
		//String commands [] = {sshConnection+dataminer,"0",password,"0","ls -l","0",rmlogging,"0",chmod,"0","cd algorithmInstaller","0",install,"5",install,"5","cd ..","0",rmSMState,"0","./stopContainer.sh","3","./startContainer.sh","30"};
		String forecommands [] = null;
		if (!skipinstallerdownload){
		String getInstaller = "wget --no-check-certificate https://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataminerAlgorithmsInstaller/package/algorithmInstaller.zip";
		String unzipInstaller = "unzip algorithmInstaller.zip";
		String choice= "N";
		String mod= "chmod 777 -R algorithmInstaller/*";
		
		String iforecommands [] = {sshConnection+dataminer,"2","y","0",password,"0","ls -l","0",rmlogging,"0",rmlib1,"0",rmlib2,"0",rmlib3,"0",rmInstaller,"0",rmInstallerFolder,"0",
				chmod,"0",getInstaller,"2",unzipInstaller,"2",choice,"0",mod,"0","./stopContainer.sh","3",
				"cd algorithmInstaller","0"};
			forecommands = iforecommands;
		}
		else{
			String iforecommands [] = {sshConnection+dataminer,"2","y","0",password,"0","ls -l","0",rmlogging,"0",rmlib1,"0",rmlib2,"0",rmlib3,"0",rmInstaller,"0",
					chmod,"0","./stopContainer.sh","10","cd algorithmInstaller","0"};
			forecommands = iforecommands;
		}
		
		
		String postcommands [] = {"cd ..","0","./startContainer.sh","30"};
		
		
		ArrayList<String> commandsArray = new ArrayList<String>(Arrays.asList(forecommands));
		String [] installers = installStrings.split("\n");
		StringBuffer sb = new StringBuffer(); 
		int max = 10;
		int i =0;
		for (String installer: installers){
			int limit = 1000;
			if (installer.length()>limit)
				installer = installer.substring(0, limit)+"...\"";
			sb.append(installer+"\n");
			if (i == max)
			{
				i = 0;
				String commands = sb.toString();
				commandsArray.add(commands);
				commandsArray.add("10");
				sb = new StringBuffer();
			}
			else	
				i++;
		}
		
		if (sb.toString().length()>0)
		{
			commandsArray.add(sb.toString());
			commandsArray.add("10");
		}
		/*
		for (String installer:installArray) {
			installer = installer.trim().replace("/gcube/devsec", scope);
			if (installer.length()>0){
				commandsArray.add(installer);
				commandsArray.add("3");
			}
		}
		*/
		
		commandsArray.addAll(new ArrayList<String>(Arrays.asList(postcommands)));
		
		String[] commands = new String[commandsArray.size()];
		commands = commandsArray.toArray(commands);
		
		cmd1(commands);
		System.out.println("Elapsed Time: "+(System.currentTimeMillis()-t0));
	}
	
	public enum Environment {
		PROD,
		DEV,
		PROTO,
		PRE
	}
	
	public static String dumpInstallerFile(Environment env){
		
		String url = "";
		String file = "";
		
		switch(env){
		case PROD:
				url = "http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/algorithms/prod/algorithms";
				file ="ProdInstaller.txt";
				break;
		case DEV:
				url = "http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/algorithms/dev/algorithms";
				file ="DevInstaller.txt";
				break;
		
		case PROTO:
			url = "http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/algorithms/proto/algorithms";
			file ="ProtoInstaller.txt";
			break;
		
		case PRE:
			url = "http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/algorithms/preprod/algorithms";
			file ="PreInstaller.txt";
			break;
		
		}
		
		String answer = HttpRequest.sendGetRequest(url, "");
		
		List<String> installationStringsList = new ArrayList<String>();
		String answerbuffer = answer;
		String install = "";
		while (answerbuffer.length()>0){
			
			for (int i=0;i<7;i++){
				
				int pipe = answerbuffer.indexOf("|");
				String token = answerbuffer.substring(0,pipe);
				install+=token+"|";
				answerbuffer = answerbuffer.substring(pipe+1);
			}
			install = install.trim();
			if (!install.startsWith("|"))
				install = "|"+install;
			installationStringsList.add(install);
			install = "";
		}
		
		StringBuffer sb = new StringBuffer();
		for (String installer:installationStringsList){
			if (installer.contains("deprecated"))
				continue;
			String [] tablerow = installer.split("\\|");
			String row = tablerow[5];
			if (row.contains("<notextile>")){
				row = row .replace("<notextile>","").replace("</notextile>", "");
				row = row.trim();
				sb.append(row+"\n");
			}
		}
		
		try {
		FileWriter fw = new FileWriter(new File(file),false) ;
		fw.write(sb.toString());
		fw.close();
		return file;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	}
	
}
