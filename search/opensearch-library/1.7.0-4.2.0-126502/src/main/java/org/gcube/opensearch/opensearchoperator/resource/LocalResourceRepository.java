package org.gcube.opensearch.opensearchoperator.resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the ResourceRepository interface that is used to store and retrieve OpenSearch resources
 * in the absence of an InformationSystem
 * 
 * @author gerasimos.farantatos
 *
 */
public class LocalResourceRepository implements ResourceRepository {

	private Logger logger = LoggerFactory.getLogger(LocalResourceRepository.class.getName());
	
	private Map<String,File> fileResources = new HashMap<String,File>();
	private Map<String, String> URLResources = new HashMap<String, String>();
	private Map<String, OpenSearchResource> otherResources = new HashMap<String, OpenSearchResource>();
	private File schemaFile = null;
	
	/**
	 * Creates a new LocalResourceRepository
	 * 
	 * @param schemaFile The XSD of the OpenSearch resource that will be used to validate the XML
	 * representation of the OpenSearch resources retrieved. This is an optional parameter and can
	 * be left null if no validation is to be performed.
	 */
	public LocalResourceRepository(File schemaFile) {
		this.schemaFile = schemaFile;
	}
	
	/**
	 * Retrieves all OpenSearch resources, whose XML representations are contained in a directory
	 * with a given path and adds them to the repository
	 * 
	 * @param dirPath The path of the directory to look for OpenSearch resources
	 * @throws Exception In case of error
	 */
	public void add(String dirPath) throws Exception {
		FilenameFilter filter = new FilenameFilter() { 
			public boolean accept(File dir, String name) { 
				return name.endsWith(".xml"); 
				} 
		}; 
		
		File dir = new File(dirPath);
		File resourceFiles[] = dir.listFiles(filter);
		
		if(resourceFiles == null)
			throw new Exception("Error while listing directory files");
		
		for(File res: resourceFiles) {
			OpenSearchResource resource;
			try {
				resource = new LocalOpenSearchResource(res, schemaFile);
			}
			catch(Exception e) {
				logger.warn("Error while processing resource file " + res.getName() + ". Ignoring.", e);
				continue;
			}
			fileResources.put(resource.getDescriptionDocURL().toString(), res);
		}
		
		//System.out.println("fileResources: " + fileResources);
		//System.out.println("URLResources: " + URLResources);
		//System.out.println("otherResources: " + otherResources);
	}
	
	/**
	 * Adds an OpenSearch resource whose XML representation is retrieved through a URL to the repository
	 * 
	 * @param resourceURL The URL of the OpenSearch resource
	 * @throws Exception In case of error
	 */
	public void addURL(String resourceURL) throws Exception {
		OpenSearchResource resource = new LocalOpenSearchResource(resourceURL, schemaFile);
		URLResources.put(resource.getDescriptionDocURL(), resourceURL);
	}
	
	/**
	 * Adds a pre-processed OpenSearchResource to the repository
	 * 
	 * @param resource The OpenSearch resource to be added to the repository
	 */
	public void add(OpenSearchResource resource) {
		otherResources.put(resource.getDescriptionDocURL(), resource);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.ResourceRepository#get(String)
	 */
	public OpenSearchResource get(String descriptionDocURL) throws Exception {
		if(fileResources.containsKey(descriptionDocURL))
			return new LocalOpenSearchResource(fileResources.get(descriptionDocURL), schemaFile);
		if(otherResources.containsKey(descriptionDocURL))
			return otherResources.get(descriptionDocURL);
		if(URLResources.containsKey(descriptionDocURL))
			return new LocalOpenSearchResource(URLResources.get(descriptionDocURL), schemaFile);
		return null;
	}
}
