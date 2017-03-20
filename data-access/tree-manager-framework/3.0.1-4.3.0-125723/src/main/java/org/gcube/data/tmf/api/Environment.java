/**
 * 
 */
package org.gcube.data.tmf.api;

import java.io.File;
import java.io.Serializable;

/**
 * An abstraction over the deployment environment of the plugin.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Environment extends Serializable {

	
	/**
	 * Returns a {@link File} in an area of the file system reserved to the service.
	 * @param path the file's path relative to the storage root of the service
	 * @return the file
	 */
	File file(String path);
}
