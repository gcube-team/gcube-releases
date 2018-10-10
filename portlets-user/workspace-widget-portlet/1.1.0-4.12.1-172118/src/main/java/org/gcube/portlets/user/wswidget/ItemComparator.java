/**
 * 
 */
package org.gcube.portlets.user.wswidget;

import java.util.Comparator;

import org.gcube.portlets.user.wswidget.shared.WSItem;



/**
 * A comparator that sorts the items first by type, folder up others down, the each group alphabetically on the item name. 
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class ItemComparator implements Comparator<WSItem> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(WSItem item1, WSItem item2) {

		//if one of the item is folder and the other one not, we move up the folder
		boolean isItem1Folder = Utils.isFolder(item1.getType());
		boolean isItem2Folder = Utils.isFolder(item2.getType());
		
		//XOR
		if (isSpecialFolder(item1) ^ isSpecialFolder(item2)) return isSpecialFolder(item1) ? -1 : 1;	 	
		
		//XOR
		if (isItem1Folder ^ isItem2Folder) return isItem1Folder ? -1 : 1;		

		//otherwise we compare the names
		return String.CASE_INSENSITIVE_ORDER.compare(item1.getName(), item2.getName());		
	}

	private boolean isSpecialFolder(WSItem item) {
		return item.getName().equals(Utils.VRE_FOLDERS_LABEL) && item.isSpecialFolder();
	}
}
