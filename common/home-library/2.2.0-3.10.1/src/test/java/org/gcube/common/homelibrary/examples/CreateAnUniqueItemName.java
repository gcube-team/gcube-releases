/**
 * 
 */
package org.gcube.common.homelibrary.examples;

import java.io.FileNotFoundException;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.WorkspaceTreeVisitor;
import org.gcube.common.homelibrary.util.WorkspaceUtil;

/**
 * This example show how to clone some Workspace items.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CreateAnUniqueItemName {

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
		
		String nameCandidate = "My first folder";
		
		String name = WorkspaceUtil.getUniqueName(nameCandidate, root);
				
		root.createFolder(name, "This is my first folder created with the HomeLibrary");
				
		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor();
		
		wtv.visitVerbose(root);
	}

}
