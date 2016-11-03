/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class Util {
	
	public static final ItemType[] FOLDERS = new ItemType[]{ItemType.ROOT, ItemType.FOLDER};
	
	public static boolean isFolder(ItemType type)
	{
		for (ItemType folder:FOLDERS) if (type==folder) return true;
		return false;
	}
	
	public static String ellipsis(String value, int lenght, boolean left)
	{
		if (lenght<3) throw new IllegalArgumentException("The lenght have to be more than 3");
        if(value.length() > lenght){
            if (left) return "..."+value.substring(value.length()-lenght+3);
            else return value.substring(0, lenght-3)+"...";
        }
        return value;
	}
}
