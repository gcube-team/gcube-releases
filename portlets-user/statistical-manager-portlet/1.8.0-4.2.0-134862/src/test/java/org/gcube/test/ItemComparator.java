/**
 * 
 */
package org.gcube.test;

import java.util.Comparator;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;


/**
 * A comparator that sorts the items first by type, folder up others down, the each group alphabetically on the item name. 
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class ItemComparator implements Comparator<Item> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Item item1, Item item2) {
		
		//if one of the item is folder and the other one not, we move up the folder
		boolean isItem1Folder = Util.isFolder(item1.getType());
		boolean isItem2Folder = Util.isFolder(item2.getType());
		
		if (isItem1Folder ^ isItem2Folder) return isItem1Folder?0:1;
		
		//otherwise we compare the names
		return String.CASE_INSENSITIVE_ORDER.compare(item1.getName(), item2.getName());
	}

}
