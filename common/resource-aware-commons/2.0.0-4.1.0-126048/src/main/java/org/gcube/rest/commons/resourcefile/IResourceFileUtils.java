package org.gcube.rest.commons.resourcefile;

import java.io.File;
import java.io.IOException;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;

import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;

public abstract class IResourceFileUtils<T extends StatefulResource> {

	public abstract void createResourceDirectory()
			throws IOException;

	public abstract void writeResourceToFile(
			String resourceID, T resource) throws IOException;
	
	public abstract T readResourceFromFile(
			String filename) throws IOException, ClassNotFoundException;

	public abstract T readResourceFromFile(File file) throws IOException,
			ClassNotFoundException;
	
	public abstract String getResourcesFoldername();
	
	public Iterable<File> getResourcesFiles() {
		File dir = new File(this.getResourcesFoldername());

		TreeTraverser<File> tr = Files.fileTreeTraverser();

		return tr.children(dir);
	}
	
	public boolean deleteResourceFromFile(String resourceID) {
		String filename = resourceID;

		File resourceFile = new File(this.getResourcesFoldername() + "/" + filename);
		
		return resourceFile.delete();
	}
	
}