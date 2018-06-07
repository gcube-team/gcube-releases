/**
 * 
 */
package org.gcube.common.homelibrary.examples;

import java.io.File;
import java.io.IOException;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.data.ApplicationsArea;
import org.gcube.common.homelibrary.home.data.exceptions.FileAlreadyExistException;
import org.gcube.common.homelibrary.home.data.exceptions.FolderAlreadyExistException;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.HomeFolderVisitor;

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
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 */
	public static void main(String[] args) throws InternalErrorException, FolderAlreadyExistException, FileAlreadyExistException, IOException, HomeNotFoundException, WorkspaceFolderNotFoundException {
		
/*		Home home = ExamplesUtil.createHome("/test", "test.user");
		ApplicationsArea homeManager = home.getDataArea();
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
		*/
		
		//homeManager.close();

	}

}
