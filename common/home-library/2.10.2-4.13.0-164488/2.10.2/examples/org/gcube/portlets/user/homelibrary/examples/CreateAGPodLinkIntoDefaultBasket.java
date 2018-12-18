/**
 * 
 */
package org.gcube.portlets.user.homelibrary.examples;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.portlets.user.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.portlets.user.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.portlets.user.homelibrary.home.workspace.Workspace;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceArea;
import org.gcube.portlets.user.homelibrary.home.workspace.basket.Basket;
import org.gcube.portlets.user.homelibrary.home.workspace.basket.items.gpod.GeospatialCoordinate;
import org.gcube.portlets.user.homelibrary.home.workspace.basket.items.gpod.TaskInfo;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.portlets.user.homelibrary.home.workspace.exceptions.WorkspaceNotFoundException;
import org.gcube.portlets.user.homelibrary.util.WorkspaceTreeVisitor;

/**
 * This example show how to clone some WorkspaceArea items.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CreateAGPodLinkIntoDefaultBasket {

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
		
		Basket defaultBasket = workspaceArea.getDefaultBasket();
		
		GeospatialCoordinate gc = new GeospatialCoordinate(new Date(), new Date(), "test-minLongitude", "test-minLatitude", "test-maxLongitude", "test-maxLatitude", "test-geospatialZone");
		TaskInfo taskInfo = new TaskInfo("test-task-id", "test-token", "test-task-name", "test-username", gc);
		
		List<String> hdfUrls = new LinkedList<String>();
		hdfUrls.add("ftp://test.org/test.hdf");
		
		List<String> imgUrls = new LinkedList<String>();
		imgUrls.add("ftp://test.org/test.png");
		
		String thumbnailUrl = "ftp://test.org/thumb.png";
		String metadataUrl = "ftp://test.org/metadata.xml";
		
		defaultBasket.createGPODLink("My First GpodLink", "A very usefull gpod link", "test-username", "test-psw", hdfUrls, imgUrls, thumbnailUrl, metadataUrl, taskInfo);

		WorkspaceTreeVisitor wtv = new WorkspaceTreeVisitor();
		
		wtv.visitVerbose(root);
					
		
	}

}
