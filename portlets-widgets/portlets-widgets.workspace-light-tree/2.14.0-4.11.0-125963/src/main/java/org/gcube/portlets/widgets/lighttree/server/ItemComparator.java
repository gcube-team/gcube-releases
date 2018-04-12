/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.server;

import java.util.Comparator;

import org.gcube.portlets.widgets.lighttree.client.Item;
import org.gcube.portlets.widgets.lighttree.client.Util;
import org.gcube.portlets.widgets.lighttree.shared.LightTreeConstants;

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
		
		if (isSpecialFolder(item1) ^ isSpecialFolder(item2)) return isSpecialFolder(item1) ? -1 : 1;		
		
		if (isItem1Folder ^ isItem2Folder) return isItem1Folder ? -1 : 1;		

		//otherwise we compare the names
		return String.CASE_INSENSITIVE_ORDER.compare(item1.getName(), item2.getName());		
	}

	private boolean isSpecialFolder(Item item) {
		return item.getName().equals(LightTreeConstants.VRE_FOLDERS_LABEL);
	}
}
