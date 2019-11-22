package org.gcube.common.vremanagement.deployer.impl.operators.ant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.*;
import java.io.*;
import java.util.*;

/**
 * <PRE>
 * 
 * This class is designed to programmatically invoke Ant targets   
 * 1. initialize a new Project by calling "init" 
 * 2. invoke the deployment by running the deploy* Ant Target and feeding Ant with the following properties:
 * 	- gar.name/jar.name
 *  - package.source.dir
 *  - package.name
 *  - package.file
 * 
 * You'll need ant-launcher.jar ant.jar to compile.
 * 
 * 
 * Example :
 * <code>
 * try {
 *  	//init 
 *  	init("/home/me/build.xml","/home/me/"); 
 *  	//properties 
 *  	HashMap m = new HashMap(); 
 *  	m.put("event", "test"); 
 *  	m.put("subject", "sujet java 3");
 *  	m.put("message", "message java 3"); 
 *  	setProperties(m, false); 
 *  	//run
 * 		runTarget("test"); 
 * } catch (Exception e) { e.printStackTrace(); }
 * </code>
 * </PRE>
 * 
 * 
 */

public class AntRunner {

	private Project project;

	protected Log logger = LogFactory.getLog(this.getClass().getName());
	
	/**
	 * Creates a new Ant runner
	 *
	 */
	public AntRunner () {
		
		project = new Project(); 
	}
	
	/**
	 * Initializes a new Ant Project.
	 * 
	 * @param _buildFile The build File to use. If none is provided, it
	 *              will be defaulted to "build.xml".
	 * @param _baseDir The project's base directory. If none is provided,
	 *              will be defaulted to "." (the current directory).
	 * @throws 	AntInterfaceException if the initialization fails 
	 */
	public void init(String _buildFile, String _baseDir) throws AntInterfaceException {
		// Create a new project, and perform some default initialization
		
		try {
			project.init();
		} catch (BuildException e) {
			throw new AntInterfaceException("The default task list could not be loaded.");
		}
		logger.trace("initializing the Ant project... ");
		logger.trace("base dir = " + _baseDir);
		// Set the base directory. If none is given, "." is used.
		if (_baseDir == null)
			_baseDir = new String(".");
		try {
			project.setBasedir(_baseDir);
		} catch (BuildException e) {
			throw new AntInterfaceException(
					"The given basedir doesn't exist, or isn't a directory.");
		}
		logger.trace("build file = " + _buildFile);
		// Parse the given buildfile. If none is given, "build.xml" is used.
		if (_buildFile == null)
			_buildFile = new String("build.xml");
		try {
			ProjectHelper.getProjectHelper().parse(project,
					new File(_buildFile));
		} catch (BuildException e) {
			e.printStackTrace(System.err);
			throw new AntInterfaceException("Configuration file " + _buildFile
					+ " is invalid, or cannot be read.");
		}
	}

	/**
	 * Sets the project's properties. May be called to set project-wide
	 * properties, or just before a target call to set target-related properties
	 * only.
	 * 
	 * @param _properties A map containing the properties' name/value
	 *              couples
	 * @param _overridable If set, the provided properties values may be
	 *              overriden by the config file's values
	 * @throws AntInterfaceException if the project is null or the properties are null 
	 */
	public void setProperties(@SuppressWarnings("rawtypes") Map _properties, boolean _overridable)
			throws AntInterfaceException {
		// Test if the project exists
		if (this.project == null)
			throw new AntInterfaceException(
					"Properties cannot be set because the project has not been initialized. Please call the 'init' method first !");

		// Property hashmap is null
		if (_properties == null)
			throw new AntInterfaceException("The provided property map is null.");

		// Loop through the property map
		@SuppressWarnings("rawtypes")
		Set propertyNames = _properties.keySet();
		@SuppressWarnings("rawtypes")
		Iterator iter = propertyNames.iterator();
		while (iter.hasNext()) {
			// Get the property's name and value
			String propertyName = (String) iter.next();
			String propertyValue = (String) _properties.get(propertyName);
			if (propertyValue == null)
				continue;
			logger.trace("property " + propertyName + " = " + propertyValue);
			// Set the properties
			if (_overridable)
				this.project.setProperty(propertyName, propertyValue);
			else
				this.project.setUserProperty(propertyName, propertyValue);
		}
	}

	/**
	 * Runs the given Target.
	 * 
	 * @param _target The name of the target to run. If null, the
	 *              project's default target will be used.
	 * @throws AntInterfaceException if an error occurs during the execution
	 */
	public void runTarget(String _target) throws AntInterfaceException {
		// Test if the project exists
		if (this.project == null)
			throw new AntInterfaceException(
					"No target can be launched because the project has not been initialized. Please call the 'init' method first !");

		// If no target is specified, run the default one.
		if (_target == null)
			_target = project.getDefaultTarget();

		// Run the target
		try {
			project.executeTarget(_target);

		} catch (BuildException e) {
			throw new AntInterfaceException(e.getMessage());
		}
	}

	

	/**
	 * Executes an external scripts
	 * 
	 * @throws AntInterfaceException if an error occurs during the execution
	 */
	public void runScript() throws AntInterfaceException {
		this.runTarget("runScript");
	}

}
