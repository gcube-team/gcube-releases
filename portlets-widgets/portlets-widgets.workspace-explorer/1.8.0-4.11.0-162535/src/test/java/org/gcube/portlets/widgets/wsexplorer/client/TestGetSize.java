/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.widgets.wsexplorer.server.ItemBuilder;
import org.gcube.portlets.widgets.wsexplorer.server.ItemComparator;
import org.gcube.portlets.widgets.wsexplorer.server.StringUtil;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;
import org.gcube.portlets.widgets.wsexplorer.shared.WorkspaceNavigatorServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public class TestGetSize {
	/**
	 *
	 */
	private static final String TEST_SCOPE = "/gcube/devsec";
	/**
	 *
	 */
	public static final String TEST_USER = "francesco.mangiacrapa";
	public static final Logger _log = LoggerFactory.getLogger(TestGetSize.class);
	private static Workspace workspace;



	public static void main(String[] args) throws WorkspaceNavigatorServiceException {
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));
		boolean purgeEmpyFolders = false;
		List<String> allowedMimeTypes = new ArrayList<String>();
		Map<String, String> requiredProperties = new HashMap<String, String>();
		FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,null,null);


		try {

			ScopeBean scope = new ScopeBean(TEST_SCOPE);
			ScopeProvider.instance.set(scope.toString());
			 workspace = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(TEST_USER)
					.getWorkspace();

			 for (WorkspaceItem child: workspace.getRoot().getChildren()){
					_log.debug("Child item: "+child);
//					Item itemChild = getItem(item, child, showableTypes, filterCriteria, false);
//					_log.debug("Item: "+itemName +" converted!!!");
//					if (itemChild!=null){
//						item.addChild(itemChild);
//					}
				}

			 /*
			 Item mySpecial = getMySpecialFolder(showableTypes, false, filterCriteria);

			 for (Item spf : mySpecial.getChildren()) {
				 String size = getReadableSizeByItemId(spf.getId());
				 System.out.println(spf.getName() + ", size: "+size);
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getRoot(showableTypes, purgeEmpyFolders, filterCriteria);

	}


	public static Item getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		_log.trace("getRoot showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+ filterCriteria);

		try {

			_log.trace("Start getRoot...");

			WorkspaceItem root = workspace.getRoot();

			_log.trace("GetRoot  - Replyiing root");
			long startTime = System.currentTimeMillis();
			_log.trace("start time - " + startTime);

			Item rootItem = ItemBuilder.getItem(null, root, root.getPath(), showableTypes, filterCriteria, true, false);
			rootItem.setName(WorkspaceExplorerConstants.HOME_LABEL);

			/* SPECIAL FOLDERS
			Item specialFolders = ItemBuilder.getItem(null, specials, showableTypes, filterCriteria, 2);
			specialFolders.setShared(true);
			rootItem.addChild(specialFolders);
			 */
			if (purgeEmpyFolders) rootItem = ItemBuilder.purgeEmptyFolders(rootItem);

			_log.trace("Returning:");
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.info("end time - " + time);

			Collections.sort(rootItem.getChildren(), new ItemComparator());
			_log.info("Returning children size: "+rootItem.getChildren().size());

			return rootItem;

		} catch (Exception e) {
			_log.error("Error during root retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get root");
		}
	}

	/**
	 * {@inheritDoc}
	 */

	public static Item getMySpecialFolder(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		_log.trace("GetMySpecialFolder showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			WorkspaceItem folder = workspace.getMySpecialFolders();

			long startTime = System.currentTimeMillis();
			_log.trace("start time - " + startTime);

			Item itemFolder = ItemBuilder.getItem(null, folder, folder.getPath(), showableTypes, filterCriteria, true, false);
			itemFolder.setName(WorkspaceExplorerConstants.VRE_FOLDERS_LABEL);
			itemFolder.setSpecialFolder(true);

			_log.trace("Builded MySpecialFolder: "+itemFolder);

			_log.trace("Only showable types:");
			//printName("", folderItem);

			if (purgeEmpyFolders) itemFolder = ItemBuilder.purgeEmptyFolders(itemFolder);

			_log.trace("Returning:");

			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.trace("end time - " + time);

			//printName("", folderItem);

			Collections.sort(itemFolder.getChildren(), new ItemComparator());

			return itemFolder;

		} catch (Exception e) {
			_log.error("Error during special folders retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get My Special Folder");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#loadSizeByItemId(java.lang.String)
	 */

	public static Long getSizeByItemId(String itemId) throws Exception {

		_log.info("get Size By ItemId "+ itemId);
		try {

			WorkspaceItem wsItem = workspace.getItem(itemId);
			Long size = new Long(-1);

			if(wsItem instanceof FolderItem){ //ITEM
				FolderItem folderItem = (FolderItem) wsItem;
				size = new Long(folderItem.getLength());
			} else if (wsItem instanceof WorkspaceFolder ){ //FOLDER
				WorkspaceFolder theFolder = (WorkspaceFolder) wsItem;
				size = theFolder.getSize();
			} else if (wsItem instanceof WorkspaceSharedFolder){ //SHARED FOLDER
				WorkspaceSharedFolder theFolder = (WorkspaceSharedFolder) wsItem;
				size = theFolder.getSize();
			}
			_log.info("returning size: " +size);
			return size;

		} catch (Exception e) {
			_log.error("get Size By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}


	public static String getReadableSizeByItemId(String itemId) throws Exception {

		try{
		_log.info("getFormattedSize ByItemId "+ itemId);
				long size = getSizeByItemId(itemId);
				return StringUtil.readableFileSize(size);
		} catch (Exception e) {
			_log.error("getFormattedSize By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

}
