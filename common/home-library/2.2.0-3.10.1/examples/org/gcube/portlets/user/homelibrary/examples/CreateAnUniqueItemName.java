/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import java.io.FileNotFoundException;

import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.portlets.user.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.home.workspace.Workspace;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceArea;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.WorkspaceNotFoundException;
import org.gcube.portlets.user.homelibrary.util.WorkspaceAreaUtil;
import org.gcube.portlets.user.homelibrary.util.WorkspaceTreeVisitor;

/**
 * This example show how to clone some WorkspaceArea items.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CreateAnUniqueItemName {

	/**
	 * @param args not used.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 * @throws InsufficientPrivilegesException if an error occurs.
	 * @throws ItemAlreadyExistException if an error occurs.
	 * @throws FileNotFoundException if an error occurs.
	 */
	public static void main(String[] args) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, FileNotFoundException {
		WorkspaceArea workspaceArea = ExamplesUtil.createWorkspaceArea();
		
		Workspace root = workspaceArea.getRoot();
		
		String nameCandidate = "My first basket";
		
		String name = WorkspaceAreaUtil.getUniqueName(nameCandidate, root);
				
		root.createBasket(name, "This is my first basket created with the HomeLibrary");
				
		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor();
		
		wtv.visitVerbose(root);
		
		
	}

}
