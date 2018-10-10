/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.io.InputStream;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ExternalResourceBrokenLinkException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ExternalResourcePluginNotFoundException;

/**
 * @author gioia
 *
 */
public interface ExternalResourceLinkPlugin {
	
	/**
	 * @return
	 */
	String getPluginName();

}
