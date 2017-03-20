/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gcube.portlets.user.workspaceexplorerapp.client.grid.DisplayField;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;


/**
 * The Class ItemComparator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 21, 2016
 * @param <T> the generic type
 */
public class ItemComparatorUtility {

	/**
 * Instantiates a new item comparator utility.
 *
 * @param field the field
 * @param ascending the ascending
 * @param list the list
	 * @return
 */
	public static ArrayList<Item> sortItems(DisplayField field, boolean ascending, List<Item> list) {

		if(DisplayField.NAME.equals(field)){
			Collections.sort(list, new NameCompare());
			if(ascending)
				Collections.reverse(list);
		}else if(DisplayField.OWNER.equals(field)){

		}

		return new ArrayList<Item>(list);
	}

	/**
	 * The Class NameCompare.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Mar 21, 2016
	 */
	public static class NameCompare implements Comparator<Item> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(Item item1, Item item2) {

			//if one of the item is folder and the other one not, we move up the folder
			boolean isItem1Folder = item1.isFolder();
			boolean isItem2Folder = item2.isFolder();

			boolean isSpecialFolder1 = isSpecialFolder(item1);
			boolean isSpecialFolder2 = isSpecialFolder(item2);

			//XOR
			if (isSpecialFolder1 ^ isSpecialFolder2) return isSpecialFolder1 ? -1 : 1;

			//XOR
			if (isItem1Folder ^ isItem2Folder) return isItem1Folder ? -1 : 1;

			//otherwise we compare the names
			return String.CASE_INSENSITIVE_ORDER.compare(item1.getName(), item2.getName());
		}

		/**
		 * Checks if is special folder.
		 *
		 * @param item the item
		 * @return true, if is special folder
		 */
		private boolean isSpecialFolder(Item item) {
//			return item.getName().equals(WorkspaceExplorerConstants.VRE_FOLDERS_LABEL) && item.getParent()!=null && item.getParent().isRoot();
			return item.getName().equals(WorkspaceExplorerAppConstants.VRE_FOLDERS_LABEL) && item.isSpecialFolder();
		}
	}


}
