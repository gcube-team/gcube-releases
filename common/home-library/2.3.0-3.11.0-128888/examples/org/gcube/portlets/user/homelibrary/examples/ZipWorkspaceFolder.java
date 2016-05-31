/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import java.io.File;
import java.io.IOException;

import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.portlets.user.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.home.workspace.Workspace;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceArea;
import org.gcube.portlets.user.homelibrary.home.workspace.basket.Basket;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.WorkspaceNotFoundException;
import org.gcube.portlets.user.homelibrary.testdata.TestDataFactory;
import org.gcube.portlets.user.homelibrary.util.zip.ZipUtil;

/**
 * Show how to zip a folder content.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipWorkspaceFolder {

	/**
	 * @param args not used.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws InsufficientPrivilegesException if an error occurs.
	 * @throws ItemAlreadyExistException if an error occurs.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 * @throws IOException if an error occurs.
	 */
	public static void main(String[] args) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, WorkspaceNotFoundException, IOException {
		WorkspaceArea workspaceArea = ExamplesUtil.createWorkspaceArea();
		
		Workspace root = workspaceArea.getRoot();
		
		Basket basket = root.createBasket("Test Basket", "");
		
		//filling some test data
		TestDataFactory.getInstance().fillAllPDFDocuments(basket);
		TestDataFactory.getInstance().fillAllImageDocuments(basket);
		
		//zipping the root content
		File zip = ZipUtil.zipFolder(root);
		
		//System.out.println(zip.getAbsolutePath());

	}

}
