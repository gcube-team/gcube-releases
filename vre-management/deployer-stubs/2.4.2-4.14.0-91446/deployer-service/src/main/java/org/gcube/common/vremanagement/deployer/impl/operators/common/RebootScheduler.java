package org.gcube.common.vremanagement.deployer.impl.operators.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.vremanagement.deployer.impl.contexts.ServiceContext;
import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;



/**
 * Scheduler to execute the packages' reboot scripts. <br> 
 * These script are meant to prepare the environment for the package that brings each of them.  
 * Therefore, they must be executed from an external process, immediately before the gContainer process is going to start.  
 * The scheduler serializes the list of packages on this file: <code>ServiceContext.getContext().getFile("reboot_scripts")</code>
 * It is expected that the gcore-start-container-* scripts take care of this list by executing them before to start the gContainer itself.
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @see Scheduler
 */
public class RebootScheduler extends BaseScheduler {
	
	
	/**
	 * the singleton instance of the scheduler
	 */
	private static RebootScheduler ref;
	
	/**
	 * Creates and returns a valid scheduler
	 * @return a reboot scheduler instance
	 * @throws DeployException  if the scheduler initialization fails
	 */
	public static synchronized Scheduler getScheduler() throws DeployException {
      if (ref == null)     	
         ref = new RebootScheduler();      
      return ref;
    }	

	/** {@inheritDoc} */
	@Override
	public void add(BasePackage target) throws DeployException {
		super.add(target);
		try {
			this.serialize();
		} catch (IOException e) {
			throw new DeployException(e.getMessage());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void remove(BasePackage target) throws DeployException {
		super.remove(target);
		try {
			this.serialize();
		} catch (IOException e) {
			throw new DeployException(e.getMessage());
		}
	}


	/** {@inheritDoc} */
	@Override
	public void run(KeyData key) throws DeployException {
		/* reboot scripts are not meant to be run from inside the container, this empty block will invalidate any attempt */
	}
	
	/** 
	 * Serializes the scheduler 
	 */
	private synchronized void serialize() throws IOException {		
		
		File serializationFile = ServiceContext.getContext().getFile("reboot_scripts", true);
		
		//load the serialization file content
		FileReader fr;
		Set<String> serializedScripts = new HashSet<String>();
		try {
			fr = new FileReader(serializationFile);
			BufferedReader br = new BufferedReader(fr);
			String s;
			try {
				while((s = br.readLine()) != null) 	serializedScripts.add(s);
				fr.close();
				logger.info("Appeding the scripts to the reboot scheduler serialization");
			} catch (IOException e) {
				logger.error("Unable to read the reboot scheduler serialization", e);
				logger.info("Creating the reboot scheduler serialization");
			}			
			
		} catch (FileNotFoundException e) {
			logger.info("Creating the reboot scheduler serialization");
		}						 
		
		//append to the serialization file the new scripts, a "cd" command is prefixed in order to allow the execution in their own folder
		for (Set<File> fileslist : scripts.values()) {
			for (File file : fileslist) {
				try {							
					Process p = Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath(), new String[]{}, file.getParentFile());
					p.waitFor();
					p.destroy();
				} catch (Exception e) {/*nothing to log, the service is running on a Windows system*/}
				serializedScripts.add("cd "+ file.getParent() +"; . " +file.getAbsolutePath());
			}
		}
		//serialize again
	    try {
	    	StringBuilder content = new StringBuilder();
	        BufferedWriter out = new BufferedWriter(new FileWriter(serializationFile, false));
	        for (String script : serializedScripts)  content.append(script).append("\n");
	        out.write(content.toString());
	        out.close();
	    } catch (IOException e) {
	    	logger.error("Unable to write the reboot scheduler serialization", e);
	    	throw new IOException("Unable to write the reboot scheduler serialization");
	    }

	}

	/** {@inheritDoc} */
	@Override
	protected List<String> getScriptsToSchedule(BasePackage target) {
		return target.getRebootScripts();
	}
	
}
