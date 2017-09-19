/**
 * 
 */
package org.gcube.portlets.widgets.wsexplorer.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.widgets.wsexplorer.server.ItemBuilder;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.WorkspaceNavigatorServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public class TestGetBreadcrumbs {
	public static final Logger _log = LoggerFactory.getLogger(TestGetBreadcrumbs.class);
	static Workspace workspace;
	
	public static void main(String[] args) throws WorkspaceNavigatorServiceException {

		ScopeBean scope = new ScopeBean("/gcube/devsec");
		ScopeProvider.instance.set(scope.toString());
		
		try {
			workspace = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("francesco.mangiacrapa")
					.getWorkspace();

		System.out.println("Start get breadcrumb");
		
		String parentLimit = "bef9b6b6-8479-4077-9f30-8dda8ed99180";
		
//		47494ea7-a095-41a0-9037-2a62611c410b
		List<Item> parents = getBreadcrumbsByItemIdentifierToParentLimit("47494ea7-a095-41a0-9037-2a62611c410b", parentLimit, true);
		
		for (Item item : parents) {
//			System.out.println(item);
			System.out.println(item.getId() +" "+item.getName());
		}
		
		
		} catch (WorkspaceFolderNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HomeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public static List<Item> getBreadcrumbsByItemIdentifier(String itemIdentifier, boolean includeItemAsParent) throws Exception {
//		List<Item> listParents = new ArrayList<Item>();
		_log.trace("ListParents By Item Identifier "+ itemIdentifier);
		try {
			
			WorkspaceItem wsItem = workspace.getItem(itemIdentifier);
			_log.trace("workspace retrieve item name: "+wsItem.getName());
			List<WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);	
			_log.trace("parents size: "+parents.size());
			Item[] arrayParents;

			if(includeItemAsParent==true && wsItem.isFolder()){
				arrayParents = new Item[parents.size()];
				arrayParents[parents.size()-1] = ItemBuilder.buildFolderForBreadcrumbs((WorkspaceFolder) wsItem, null);
			}else
				arrayParents = new Item[parents.size()-1];
			
			/** HANDLE MY_SPECIAL_FOLDER TO AVOID COMPLETE PATH WORKSPACE/MY_SPECIAL_FOLDER 
			 * BUT RETURNING ONLY /MY_SPECIAL_FOLDER
			 */
			if(wsItem.isFolder()){
				if(ItemBuilder.isSpecialFolder((WorkspaceFolder) wsItem))
					return new ArrayList<Item>(Arrays.asList(arrayParents));
//					return listParents;
			}
			
			//CONVERTING PATH
			_log.trace("converting path from second-last..");
			for (int i =  parents.size()-2; i >= 0; i--) {
				WorkspaceFolder wsParentFolder = (WorkspaceFolder) parents.get(i);
				arrayParents[i] = ItemBuilder.buildFolderForBreadcrumbs((WorkspaceFolder) wsParentFolder, null);
				if(arrayParents[i].isSpecialFolder()){ //SKIP HOME PARENT FOR MY_SPECIAL_FOLDER
					_log.info("arrayParents i-mo is special folder, exit");
					break;
				}
//				wsItem = wsParentFolder;
			}
			
			//SET PARENTS
			_log.trace("setting parents..");
			for(int i=0; i<arrayParents.length-1; i++){
				
				Item parent = arrayParents[i];
				Item fileModel = arrayParents[i+1];
				fileModel.setParent(parent);
			}
			
			_log.trace("ListParents return size: "+arrayParents.length);
			
			if(arrayParents[0]==null){
				List<Item> breadcrumbs = new ArrayList<Item>(arrayParents.length-1);
				for (int i=1; i<arrayParents.length; i++) {
					breadcrumbs.add(arrayParents[i]);
				}
				return breadcrumbs;
			}else
				return new ArrayList<Item>(Arrays.asList(arrayParents));
			
		} catch (Exception e) {
			_log.error("Error in get List Parents By Item Identifier ", e);
			throw new Exception("Sorry, an error occurred during path retrieving!");
		}
	}
	
	
	/**
	 * Gets the parents by item identifier to limit.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @param includeItemAsParent
	 *            the include item as parent
	 * @return the parents by item identifier to limit
	 * @throws Exception
	 *             the exception
	 */
	public static List<Item> getBreadcrumbsByItemIdentifierToParentLimit(String itemIdentifier, String parentLimit, boolean includeItemAsParent) throws Exception {
		_log.trace("ListParents By Item Identifier " + itemIdentifier);
		try {

			WorkspaceItem wsItem = workspace.getItem(itemIdentifier);
			_log.trace("workspace retrieve item name: "+wsItem.getName());
			List<WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);	
			_log.trace("parents size: "+parents.size());
			Item[] arrayParents;

			if(includeItemAsParent==true && wsItem.isFolder()){
				arrayParents = new Item[parents.size()];
				arrayParents[parents.size()-1] = ItemBuilder.buildFolderForBreadcrumbs((WorkspaceFolder) wsItem, null);
			}else
				arrayParents = new Item[parents.size()-1];
			
			/** HANDLE MY_SPECIAL_FOLDER TO AVOID COMPLETE PATH WORKSPACE/MY_SPECIAL_FOLDER 
			 * BUT RETURNING ONLY /MY_SPECIAL_FOLDER
			 */
			if(wsItem.isFolder()){
				if(ItemBuilder.isSpecialFolder((WorkspaceFolder) wsItem))
					return new ArrayList<Item>(Arrays.asList(arrayParents));
//					return listParents;
			}
			
			parentLimit = parentLimit!=null?parentLimit:"";
			
			//CONVERTING PATH
			_log.trace("converting path from second-last..");
			for (int i =  parents.size()-2; i >= 0; i--) {
				WorkspaceFolder wsParentFolder = (WorkspaceFolder) parents.get(i);
				arrayParents[i] = ItemBuilder.buildFolderForBreadcrumbs((WorkspaceFolder) wsParentFolder, null);
				if(arrayParents[i].isSpecialFolder()){ //SKIP HOME PARENT FOR MY_SPECIAL_FOLDER
					_log.info("arrayParents index "+i+" is special folder, break");
					break;
				}else if(parentLimit.compareTo(arrayParents[i].getId())==0){
					_log.info("reached parent limit "+parentLimit+", break");
					break;
				}
			}
			
			//SET PARENTS
			_log.trace("setting parents..");
			for(int i=0; i<arrayParents.length-1; i++){
				
				Item parent = arrayParents[i];
				Item fileModel = arrayParents[i+1];
				
				if(fileModel!=null)
					fileModel.setParent(parent);
			}
			
			_log.trace("ListParents return size: "+arrayParents.length);
			if(arrayParents[0]==null){ //EXIT BY BREAK IN CASE OF SPECIAL FOLDER OR REACHED PARENT LIMIT
				List<Item> breadcrumbs = new ArrayList<Item>();
				for (int i=1; i<arrayParents.length; i++) {
					if(arrayParents[i]!=null)
						breadcrumbs.add(arrayParents[i]);
				}
				return breadcrumbs;
			}else
				return new ArrayList<Item>(Arrays.asList(arrayParents));
			
		} catch (Exception e) {
			_log.error("Error in get List Parents By Item Identifier ", e);
			throw new Exception("Sorry, an error occurred during path retrieving!");
		}
	}
	
	
	

}
