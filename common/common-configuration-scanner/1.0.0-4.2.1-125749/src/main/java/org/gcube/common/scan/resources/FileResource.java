package org.gcube.common.scan.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A {@link ClasspathResource} that lives in a directory archive.
 * @author Fabio Simeoni
 *
 */
public class FileResource implements ClasspathResource {

	private final String relativePath;
	private final String rootPath;
	
	/**
	 * Creates an instance from its absolute path on the file system, and from the its path relative to archive
	 * that contains it.
	 * 
	 * @param absolutePath the absolute path
	 * @param relativePath the relative path
	 */
	public FileResource(String absolutePath, String relativePath) {
		this.relativePath=relativePath;
		this.rootPath=absolutePath;
	}
	
	@Override
	public String name() {
		return relativePath.substring(relativePath.lastIndexOf(File.separatorChar)+1);
		
		
	}
	
	public String path() {
		String path = relativePath;
		if (relativePath.endsWith(".class"))
			path = relativePath.replace("/",".");
		return path;
	}
	
	@Override
	public InputStream stream() {
		try {
			return new FileInputStream(file());
		}
		catch(FileNotFoundException e) {
			throw new RuntimeException("cannot read resource after existence check",e);
		}
	}
	
	@Override
	public File file() {
		return new File(rootPath,relativePath);
	}
	
	@Override
	public String toString() {
		return "file-entry:"+path();
	}

}
