/**
 * 
 */
package org.gcube.common.mycontainer;

import static org.gcube.common.mycontainer.Utils.checkDir;
import static org.gcube.common.mycontainer.Utils.checkFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A set of configuration and binary resources for deployment in an instance of {@link MyContainer}.
 * 
 * @author Fabio Simeoni
 *
 */
public class Gar {

	File file;
	
	String id;
	List<File> libraries = new ArrayList<File>();
	List<File> interfaces = new ArrayList<File>();
	List<File> configurations = new ArrayList<File>();
	
	long lastModified;
	
	
	/**
	 * Creates an instance from an existing Gar file.
	 */
	public Gar(File file) {
		this.file=addResource(file.getAbsolutePath(),false);
		this.id=file.getName().substring(0,file.getName().lastIndexOf(".")); //remove file extension (typically .gar)
	}
	
	/**
	 * Creates an instance with a given identifier.
	 * @param id
	 */
	public Gar(String id) {
		this.id=id;	
	}
	
	/**
	 * Return the most recent modification to the resources in the Gar. 
	 * @return the lastModified
	 */
	public long lastModified() {
		return lastModified;
	}
	
	/**
	 * Adds a library resource to the Gar.
	 * @param resourcePath the path to the resource.
	 * @return this Gar
	 * @throws IllegalArgumentException if the resource does not exist or is not readable.
	 */
	public Gar addLibrary(String resourcePath) throws IllegalArgumentException {
		libraries.add(addResource(resourcePath,false));
		return this;
	}
	
	/**
	 * Adds all the library resources in a given directory to the Gar.
	 * @param resourcePath the path to the directory
	 * @return this Gar
	 * @throws IllegalArgumentException if the directory does not exist or is not readable or writable, 
	 * or if the files in it are not readable;
	 */
	public Gar addLibraries(String resourcePath) throws IllegalArgumentException {
		return addResources(libraries,resourcePath);
	}

	/**
	 * Adds an interface resource to the Gar.
	 * @param resourcePath the path to the resource.
	 * @return this Gar
	 * @throws IllegalArgumentException if the resource does not exist or is not readable.
	 */
	public Gar addInterface(String resourcePath) throws IllegalArgumentException {
		libraries.add(addResource(resourcePath,false));
		return this;
	}
	
	/**
	 * Adds all the interfaces in a given directory to the Gar.
	 * @param resourcePath the path to the directory
	 * @return this Gar
	 * @throws IllegalArgumentException if the directory does not exist or is not readable or writable, 
	 * or if the files in it are not readable;
	 */
	public Gar addInterfaces(String resourcePath) throws IllegalArgumentException {
		return addResources(interfaces,resourcePath);
	}
	
	/**
	 * Adds a configuration resource to the Gar.
	 * @param resourcePath the path to the resource.
	 * @return this Gar
	 * @throws IllegalArgumentException if the resource does not exist or is not readable.
	 */
	public Gar addConfiguration(String resourcePath) throws IllegalArgumentException {
		configurations.add(addResource(resourcePath,false));
		return this;
	}
	
	/**
	 * Adds all the configuration files in a given directory to the Gar.
	 * @param resourcePath the path to the directory
	 * @return this Gar
	 * @throws IllegalArgumentException if the directory does not exist or is not readable or writable, 
	 * or if the files in it are not readable;
	 */
	public Gar addConfigurations(String resourcePath) throws IllegalArgumentException {
		return addResources(configurations,resourcePath); 
	}
	
	/**
	 * @return the name
	 */
	public String id() {
		return id;
	}
	
	/**
	 * Returns the library resources in this Gar.
	 * @return the libraries
	 */
	public List<File> libs() {
		return libraries;
	}
	
	/**
	 * Returns the interface resources in this Gar.
	 * @return the interfaces
	 */
	public List<File> interfaces() {
		return interfaces;
	}
	
	/**
	 * @return the configDir
	 */
	public List<File> configuration() {
		return configurations;
	}
	
	/**
	 * @return the file
	 */
	public File file() {
		return file;
	}
	
	
	//helper: validates and records last modification time
	private File addResource(String name,boolean dir) {
		
		if (file!=null)
			throw new IllegalStateException("Gar is already bound to "+file+" and cannot be further configured");
		
		File file = new File(name);
		
		if (dir)
			checkDir(file);
		else 
			checkFile(file);
	
		lastModified = Math.max(lastModified,Utils.lastModified(file));
	
		return file;
	}
	

	private Gar addResources(List<File> resources,String resourcePath) {
		
		for (File resource : addResource(resourcePath,true).listFiles())
			if (!resource.isHidden()) 
				resources.add(addResource(resource.getAbsolutePath(), resource.isDirectory()? true: false));
		return this;

	}
	
}
