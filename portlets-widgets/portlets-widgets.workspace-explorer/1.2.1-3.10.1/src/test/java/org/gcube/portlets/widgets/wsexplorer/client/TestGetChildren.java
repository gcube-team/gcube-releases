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
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.widgets.wsexplorer.server.ItemBuilder;
import org.gcube.portlets.widgets.wsexplorer.server.ItemComparator;
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
public class TestGetChildren {
	public static final Logger _log = LoggerFactory.getLogger(TestGetChildren.class);

	public static void main(String[] args) throws WorkspaceNavigatorServiceException {
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));
		boolean purgeEmpyFolders = false;
		List<String> allowedMimeTypes = new ArrayList<String>();
		Map<String, String> requiredProperties = new HashMap<String, String>();
		FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,null,null);
		Item item = getRoot(showableTypes, purgeEmpyFolders, filterCriteria);

		for (Item child : item.getChildren()) {
			System.out.println(child);
		}

	}

	public static Item getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		_log.trace("getRoot showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+ filterCriteria);

		try {

			ScopeBean scope = new ScopeBean("/gcube/devsec");
			ScopeProvider.instance.set(scope.toString());

			Workspace workspace = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("francesco.mangiacrapa")
					.getWorkspace();
			//
			_log.debug("Start getRoot...");

			WorkspaceItem root = workspace.getRoot();

			_log.debug("GetRoot  - Replyiing root");
			long startTime = System.currentTimeMillis();
			_log.debug("start time - " + startTime);

			Item rootItem = ItemBuilder.getItem(null, root, root.getPath(),showableTypes, filterCriteria, true);

			if(rootItem==null)
				throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get root");

			rootItem.setName(WorkspaceExplorerConstants.HOME_LABEL);

			if (purgeEmpyFolders) rootItem = ItemBuilder.purgeEmptyFolders(rootItem);

			_log.debug("Returning:");
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.debug("end time - " + time);

			Collections.sort(rootItem.getChildren(), new ItemComparator());
			_log.debug("Returning children size: "+rootItem.getChildren().size());

			return rootItem;

		} catch (Exception e) {
			_log.error("Error during root retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get root");
		}
	}

}
