/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

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
import org.gcube.portlets.user.homelibrary.util.WorkspaceTreeVisitor;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CreateAndFillWorkspace {


	/**
	 * Create and fill a basket.
	 * @param args not used.
	 * @throws WorkspaceNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 * @throws MalformedScopeExpressionException if an error occurs.
	 * @throws ItemAlreadyExistException if an error occurs.
	 * @throws InsufficientPrivilegesException if an error occurs.
	 */
	public static void main(String[] args) throws MalformedScopeExpressionException, InternalErrorException, HomeNotFoundException, WorkspaceNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException {
			
		WorkspaceArea workspaceArea = ExamplesUtil.createWorkspaceArea();
		
		Workspace root = workspaceArea.getRoot();
		
		//first we create a test basket (also the default basket can be used)
		Basket basket = root.createBasket("Test Basket", "");
		
		//we use some utility methods to fill the basket
		TestDataFactory.getInstance().fillAllPDFDocuments(basket);
		TestDataFactory.getInstance().fillAllImageDocuments(basket);
		
		//then we show the content.
		WorkspaceTreeVisitor visitor = new WorkspaceTreeVisitor();
		visitor.visitVerbose(root);
		
	}
	


}
