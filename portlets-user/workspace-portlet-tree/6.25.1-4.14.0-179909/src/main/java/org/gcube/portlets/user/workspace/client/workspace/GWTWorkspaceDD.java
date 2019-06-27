/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;


/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTWorkspaceDD {
	
	public static boolean canDrop(GWTWorkspaceItem targetItem, GWTWorkspaceItem sourceItem)
	{
		if (sourceItem!=null && targetItem!=null){
			
			//we check if the node as moved in the same folder
			if (targetItem.getId().equals(sourceItem.getParent().getId())) return false;
			
			//we can't drop an item inside himself
			if (targetItem.getId().equals(sourceItem.getId())) return false;
			
			//the root can't be moved
			if (sourceItem.isRoot()) return false;
			
			//we can't move an item inside himself or a child
			if (targetItem.isAncestor(sourceItem)) return false;
			
			//siblings can't have same name
			for (GWTWorkspaceItem child:targetItem.getChildren()){
				if (child.getName().equals(sourceItem.getName())) return false;
			}
			
			/*GWT.log("is ancestor? "+sourceItem.isAncestor(targetItem), null);
			
			GWTWorkspaceItem parent = sourceItem;
			
			while(!parent.isRoot()){
				GWT.log("parent "+parent.getName(), null);
				parent = parent.getParent();
			}*/

			switch (targetItem.getType())
			{
				//WORKSPACE, FOLDER_ITEM -> WORKSPACE
				case FOLDER: return true;

			}

		}

		return false;
	}


}
