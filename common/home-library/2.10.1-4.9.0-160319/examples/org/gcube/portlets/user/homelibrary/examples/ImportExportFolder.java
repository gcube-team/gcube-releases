/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import java.io.File;
import java.io.IOException;

import org.gcube.portlets.user.homelibrary.home.data.DataArea;
import org.gcube.portlets.user.homelibrary.home.data.exceptions.FileAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.data.exceptions.FolderAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.data.fs.DataFolder;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.util.HomeFolderVisitor;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ImportExportFolder {


	/**
	 * @param args not used.
	 * @throws InternalErrorException if an error occurs.
	 * @throws FolderAlreadyExistException if an error occurs.
	 * @throws FileAlreadyExistException if an error occurs.
	 * @throws IOException if an error occurs.
	 */
	public static void main(String[] args) throws InternalErrorException, FolderAlreadyExistException, FileAlreadyExistException, IOException {
		
		//FIXME to complete
		DataArea homeManager = null;//JCRHomeManager.getInstance().getHome("","pippo");
		DataFolder root = homeManager.getDataFolderRoot();
		
		// A/
		DataFolder folderA = root.createFolder("A");
		
		// A/B/
		DataFolder folderB = folderA.createFolder("B");
		
		// A/B/testfile2.txt
		folderB.importFile(new File("test-data/testfile2.txt"));
		
		// A/C/
		DataFolder folderC = folderA.createFolder("C");
		
		// A/C/testDataFolder ***
		folderC.importFolder(new File("test-data/testFolder"));
		
		HomeFolderVisitor hfv = new HomeFolderVisitor();
		
		hfv.visit(root);
		
		File tmp = new File("tmp");
		tmp.mkdir();
		
		root.exportFolder(tmp);
		
		
		//homeManager.close();

	}

}
