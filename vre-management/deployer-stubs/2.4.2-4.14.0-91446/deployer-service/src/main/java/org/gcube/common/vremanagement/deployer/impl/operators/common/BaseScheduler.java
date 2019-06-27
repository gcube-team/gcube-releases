package org.gcube.common.vremanagement.deployer.impl.operators.common;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;

/**
 * Base scheduler implementation
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class BaseScheduler implements Scheduler {

	protected final GCUBELog  logger = new GCUBELog(this.getClass());
	
	protected Map<KeyData, Set<File>> scripts = Collections.synchronizedMap(new HashMap<KeyData, Set<File>>());			

	/** {@inheritDoc} */
	public void add(BasePackage target) throws DeployException {
		Set<File> tempScripts = new HashSet<File>();
		for (String script : this.getScriptsToSchedule(target))
			tempScripts.add(new File(script));
		
		if (!scripts.containsKey(target.getKey())) 
			scripts.put(target.getKey(), tempScripts);
		else 
			scripts.get(target.getKey()).addAll(tempScripts);	
	}
	
	/** {@inheritDoc} */
	public void remove(BasePackage target) throws DeployException {
		scripts.remove(target.getKey());
	}

	/** {@inheritDoc} */
	public void run(KeyData key) throws DeployException {
		if (!scripts.containsKey(key)) 
			return;//nothing to run
		
		for (File script : scripts.get(key)) {							
			logger.debug(this.getClass().getName() + " is going to execute " + script.getAbsolutePath() + " as script for " + key.getPackageName() +"...");						
			//only for Unix-based systems, we need to change the file permissions
			try {							
				Process p = Runtime.getRuntime().exec("chmod 777 " + script.getAbsolutePath(), new String[]{}, script.getParentFile());
				p.waitFor();
				p.destroy();
			} catch (Exception e) {/*nothing to log, the service is running on a Windows system*/}			
			//executes the script
			try {							
				Process p = Runtime.getRuntime().exec(script.getPath(), null, script.getParentFile());
				p.waitFor();
				p.destroy();
			} catch (Exception ioe) {
					logger.error(ioe); 
					throw new DeployException("InstallScheduler is unable to execute the script " + script.getAbsolutePath() + " for package " + key.getPackageName());					
			}
		}
	}
	
	/**
	 * Extracts the scripts to schedule from the given package
	 * 
	 * @param target the package
	 * @return the scripts to schedule
	 */
	abstract protected List<String> getScriptsToSchedule(BasePackage target);
	
	/**
	 * Overrides the Object.clone() method to avoid the Scheduler cloning
	 * 
	 * @return the cloned object
	 * @throws CloneNotSupportedException always thrown
	 */
	@Override
	public Object clone() throws CloneNotSupportedException  {
		//no cloning available for this singleton class
		throw new CloneNotSupportedException();     
	}
	
}
