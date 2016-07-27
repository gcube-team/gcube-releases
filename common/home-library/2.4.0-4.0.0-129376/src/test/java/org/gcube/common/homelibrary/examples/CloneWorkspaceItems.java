/**
 * 
 */
package org.gcube.common.homelibrary.examples;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.WorkspaceTreeVisitor;

/**
 * This example show how to clone some Workspace items.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CloneWorkspaceItems {

	/**
	 * @param args not used.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 * @throws InsufficientPrivilegesException if an error occurs.
	 * @throws ItemAlreadyExistException if an error occurs.
	 */
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException {
		Workspace workspace = ExamplesUtil.createWorkspace();
		
		WorkspaceFolder root = workspace.getRoot();

		//we create a test folder
		WorkspaceFolder folder = root.createFolder("TestFolder", "This is a test folder");
		
		//we clone the workspace
		@SuppressWarnings("unused")
		WorkspaceItem cloneWorkspace = folder.cloneItem("Clone folder");
		
		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor();
		
		wtv.visitSimple(root);
		
		wtv.visitVerbose(root);
	}

}
