package org.gcube.rest.commons.resourcefile;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.inject.ResourcesFoldername;
import org.gcube.rest.commons.inject.StatefulResourceClass;
import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class ResourceFileUtilsJSON<T extends StatefulResource> extends IResourceFileUtils<T> {

	private static final Logger logger = LoggerFactory.getLogger(ResourceFileUtilsJSON.class);
	private final Class<T> clazz;
	private final String resourcesFoldername;
	
	@Inject
	public ResourceFileUtilsJSON(@StatefulResourceClass Class<T> clazz, @ResourcesFoldername String resourcesFoldername){
		this.clazz = clazz;
		this.resourcesFoldername = resourcesFoldername;
	}
	
	public String getResourcesFoldername() {
		return this.resourcesFoldername;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.rest.commons.helpers.IResourceFileUtils#createResourceDirectory(java.lang.String)
	 */
	@Override
	public void  createResourceDirectory() throws IOException {
		File resourcesDir = new File(resourcesFoldername);
		Files.createParentDirs(new File(resourcesFoldername));
		if (resourcesDir.mkdir()){
			logger.info("creating new resources folder");
		} else {
			logger.info("resources folder already exists");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.rest.commons.helpers.IResourceFileUtils#writeResourceToFile(java.lang.String, java.lang.String, T)
	 */
	@Override
	public void writeResourceToFile(String resourceID, T resource) throws IOException{
		String filename = resourceID;
		
		File resourceFile = new File(resourcesFoldername + "/" + filename);
		if (resourceFile.createNewFile()){
			logger.info("creating new resources folder");
		} else {
			logger.info("resources folder already exists");
		}
		
		String json = JSONConverter.convertToJSON(resource, true);
		Files.write(json.getBytes(), resourceFile);
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.rest.commons.helpers.IResourceFileUtils#readResourceFromFile(java.lang.String, java.lang.String)
	 */
	@Override
	public T readResourceFromFile(String filename) throws IOException, ClassNotFoundException
	{
		return readResourceFromFile(new File(filename));
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.rest.commons.helpers.IResourceFileUtils#readResourceFromFile(java.io.File)
	 */
	@Override
	public T readResourceFromFile(File file) throws IOException, ClassNotFoundException{
		byte[] jsonResource = Files.toByteArray(file);

		T resource = JSONConverter.fromJSON(new String(jsonResource), this.clazz);
		
		logger.info("read resource from json file with class : " + resource.getClass() + " of " + this.clazz + " and id : " + resource.getResourceID());
		
		return resource;
	}

}
