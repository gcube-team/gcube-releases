/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import org.gcube.portlets.user.homelibrary.home.HomeManagerFactory;
import org.gcube.portlets.user.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.WorkspaceNotFoundException;
import org.gcube.portlets.user.homelibrary.util.HomeLibraryVisitor;
import org.gcube.portlets.user.homelibrary.util.logging.LoggingUtil;


/**
 * Show how to visit the entire home library.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class VisitHomeLibrary {

	/**
	 * @param args not used.
	 * @throws InternalErrorException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 */
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException {

		LoggingUtil.reconfigureLogging();
		
		HomeManagerFactory factory = ExamplesUtil.getHomeManagerFactory();
		
		HomeLibraryVisitor hlv = new HomeLibraryVisitor(true);
		hlv.visitHomeLibrary(factory);

	}
}
