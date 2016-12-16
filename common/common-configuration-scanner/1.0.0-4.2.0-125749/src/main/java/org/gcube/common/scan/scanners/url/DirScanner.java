package org.gcube.common.scan.scanners.url;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scan.resources.FileResource;

/**
 * Implements {@link URLScanner} for <code>file:</code> URLs refer to directories.
 * @author Fabio Simeoni
 *
 */
public class DirScanner implements URLScanner {

	@Override
	public boolean handles(URL url) {
		return "file".equals(url.getProtocol()) && url.toExternalForm().endsWith("/");
	}
	
	@Override
	public Set<URL> additional(URL url) {
		return Collections.emptySet();
	}
	
	@Override
	public Set<ClasspathResource> scan(URL url) throws Exception {
		
		URI uri = url.toURI();
     	
    	File dir = new File(uri.getSchemeSpecificPart());
		
		if (!dir.exists() || !dir.isDirectory() || !dir.canRead())
			throw new IllegalArgumentException(dir+" is not readable or is not a directory");
		
		Set<ClasspathResource> resources = new HashSet<ClasspathResource>();
		
		buildClosure(dir,dir,resources);
		
		return resources;
	}

	private void buildClosure(File root,File dir,Set<ClasspathResource> resources) {
		
		for (File f : dir.listFiles())
			if (f.isDirectory())
				buildClosure(root,f,resources);
			else 
			{
				String rootPath = root.getPath();
				String path = f.getPath();
				String relativePath = path.substring(rootPath.length()+1);
				resources.add(new FileResource(rootPath,relativePath));
			}
	}

}
