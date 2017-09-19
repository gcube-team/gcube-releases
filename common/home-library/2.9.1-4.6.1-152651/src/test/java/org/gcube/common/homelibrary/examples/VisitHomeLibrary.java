/**
 * 
 */
package org.gcube.common.homelibrary.examples;

import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.HomeLibraryVisitor;


/**
 * Show how to visit the entire home library.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class VisitHomeLibrary {

	/**
	 * @param args not used.
	 * @throws InternalErrorException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 */
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException {

		
		HomeManagerFactory factory = ExamplesUtil.getHomeManagerFactory();
		
		HomeLibraryVisitor hlv = new HomeLibraryVisitor(true);
		hlv.visitHomeLibrary(factory);

	}
}
