/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.portlets.user.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.home.workspace.Workspace;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceArea;
import org.gcube.portlets.user.homelibrary.home.workspace.basket.Basket;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.WorkspaceNotFoundException;
import org.gcube.portlets.user.homelibrary.util.WorkspaceTreeVisitor;

/**
 * This example show how to clone some WorkspaceArea items.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CreateAnExternalFile {

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
		
		Basket basket = root.createBasket("My first basket", "This is my first basket created with the HomeLibrary");

		File myFile = new File("build.xml");
		InputStream fileData = new FileInputStream(myFile);
			
		basket.createExternalFileItem("My External File", "This is an external file", "text/xml", fileData);
				
		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor();
		
		wtv.visitVerbose(root);
		
		
	}

}
