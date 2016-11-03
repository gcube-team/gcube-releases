package org.gcube.test;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;

public class testws {


	public static void main(String[] args) {
		 try {
			 ScopeProvider.instance.set("/gcube/devsec/devVRE");
//				
				Workspace workspace = HomeLibrary.getUserWorkspace("fabio.fiorellato");
				
				
				WorkspaceItem root = workspace.getRoot();
//				System.out.println("before sort");
//				 List<String> allowedMimeTypes = new ArrayList<String>();
//				 Map<String, String> requiredProperties = new HashMap<String, String>();
//				FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes, requiredProperties);
//				List<ItemType> showableTypesParam = new ArrayList<ItemType>();
//
//				showableTypesParam.add(ItemType.ROOT);
//				showableTypesParam.add(ItemType.FOLDER);
//				showableTypesParam.add(ItemType.EXTERNAL_IMAGE);
//				showableTypesParam.add(ItemType.EXTERNAL_FILE);
//				showableTypesParam.add(ItemType.EXTERNAL_PDF_FILE);
//				showableTypesParam.add(ItemType.EXTERNAL_URL);
//				showableTypesParam.add(ItemType.QUERY);
//				showableTypesParam.add(ItemType.REPORT_TEMPLATE);
//				showableTypesParam.add(ItemType.REPORT);
//				showableTypesParam.add(ItemType.DOCUMENT);
//				showableTypesParam.add(ItemType.METADATA);
//				showableTypesParam.add(ItemType.PDF_DOCUMENT);
//				showableTypesParam.add(ItemType.IMAGE_DOCUMENT);
//				showableTypesParam.add(ItemType.URL_DOCUMENT);
//				showableTypesParam.add(ItemType.TIME_SERIES);
//				showableTypesParam.add(ItemType.AQUAMAPS_ITEM);
//				showableTypesParam.add(ItemType.WORKFLOW_REPORT);
//				showableTypesParam.add(ItemType.WORKFLOW_TEMPLATE);
//				showableTypesParam.add(ItemType.EXTERNAL_RESOURCE_LINK);
//				showableTypesParam.add(ItemType.UNKNOWN_TYPE);
//				Item rootItem = ItemBuilder.getItem(null, root, showableTypesParam, filterCriteria, 2);
//
//				System.out.println("############ rootItem null");
//
//			boolean purgeEmpyFolders=false;
//
//				if (purgeEmpyFolders) rootItem = ItemBuilder.purgeEmptyFolders(rootItem);
//
//				System.out.println("Returning:");
//
//				
//				Collections.sort(rootItem.getChildren(), new ItemComparator());
////				Collections.sort(root.getChildren(), new ItemComparator());
//				
//				System.out.println("after sort");

				
				
				
				
				
			System.out.print("end");
		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	

}
}
