package org.gcube.common.scan.resources;

import java.io.File;
import java.io.InputStream;

/**
 * Models a {@link ClasspathResource} contained in some archive thereof included in a classpath.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ClasspathResource {

	/**
	 * Returns the name of the resource.
	 * @return
	 */
	String name();
	
	/**
	 * Returns the path of the resource, relatively to the containing archive.
	 * @return
	 */
	String path();
	
	/**
	 * Returns the resource as a stream.
	 * @return the stream
	 * @throws Exception if the stream cannot be returned
	 */
	InputStream stream() throws Exception;
	
	/**
	 * Returns the resource as a file
	 * @return the file
	 * @throws Exception if the file cannot be returned
	 */
	File file() throws Exception;
}
