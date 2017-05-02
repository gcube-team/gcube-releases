/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import org.gcube.portlets.user.homelibrary.home.data.DataArea;
import org.gcube.portlets.user.homelibrary.home.data.exceptions.FolderAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.data.fs.DataFolder;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CreateDataFolder {


	/**
	 * @param args not used.
	 * @throws InternalErrorException if an error occurs.
	 * @throws FolderAlreadyExistException if an error occurs.
	 */
	public static void main(String[] args) throws InternalErrorException, FolderAlreadyExistException
	{
		DataArea homeManager = null;//JCRHomeManager.getInstance().getHome("","pippo");
		DataFolder root = homeManager.getDataFolderRoot();
		
		root.createFolder("test");

	}

}
