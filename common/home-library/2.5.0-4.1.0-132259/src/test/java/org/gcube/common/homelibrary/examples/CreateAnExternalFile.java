/**
 * 
 */
package org.gcube.common.homelibrary.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.WorkspaceTreeVisitor;

/**
 * This example show how to clone some Workspace items.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CreateAnExternalFile {

	/**
	 * @param args not used.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 * @throws InsufficientPrivilegesException if an error occurs.
	 * @throws ItemAlreadyExistException if an error occurs.
	 * @throws FileNotFoundException if an error occurs.
	 */
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, FileNotFoundException {
		Workspace workspace = ExamplesUtil.createWorkspace();
		
		WorkspaceFolder root = workspace.getRoot();
	
		File myFile = new File("build.xml");
		InputStream fileData = new FileInputStream(myFile);
			
		root.createExternalFileItem("My External File", "This is an external file", "text/xml", fileData);
				
		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor();
		
		wtv.visitVerbose(root);
		
		
	}

}
