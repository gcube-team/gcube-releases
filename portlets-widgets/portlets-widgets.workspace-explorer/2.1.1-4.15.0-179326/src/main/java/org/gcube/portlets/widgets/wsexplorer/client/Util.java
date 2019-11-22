/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client;

import org.gcube.portal.stohubicons.shared.resources.GWTIconsManager;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The Class Util.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jun 18, 2015
 */
public class Util {

//	public static final NumberFormat numberFormatterKB = NumberFormat.getFormat("#,##0 KB;(#,##0 KB)");

	public static final ItemType[] FOLDERS = new ItemType[] {ItemType.FOLDER, ItemType.PRIVATE_FOLDER, ItemType.SHARED_FOLDER, ItemType.VRE_FOLDER};

	/**
	 * Checks if is folder.
	 *
	 * @param type
	 *            the type
	 * @return true, if is folder
	 */
	public static boolean isFolder(ItemType type) {
		for (ItemType folder : FOLDERS)
			if (type == folder)
				return true;
		return false;
	}

	/**
	 * Ellipsis.
	 *
	 * @param value
	 *            the value
	 * @param lenght
	 *            the lenght
	 * @param left
	 *            the left
	 * @return the string
	 */
	public static String ellipsis(String value, int lenght, boolean left) {
		if (lenght < 3)
			throw new IllegalArgumentException(
					"The lenght have to be more than 3");
		if (value.length() > lenght) {
			if (left)
				return "..." + value.substring(value.length() - lenght + 3);
			else
				return value.substring(0, lenght - 3) + "...";
		}
		return value;
	}


	/**
	 * Returns the image based on the item type.
	 * @param type the item type.
	 * @return the image.
	 */
	public static ImageResource getImage(Item item) {
		if(item==null)
			return null;
		if (item.isFolder())
			return GWTIconsManager.getIconFolder(item.isSharedFolder());
		if (item.getType() == ItemType.EXTERNAL_URL)
			return GWTIconsManager.getIconTypeLink();
		if (item.getType() == ItemType.XML)
			return GWTIconsManager.getXMLTypeLink();
		return GWTIconsManager.getIconFile(item.getName());
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static String getFormattedSize(long value){

		if(value>0){
			double kb = value/1024;
			if(kb<1)
				kb=1;
//			return numberFormatterKB.format(kb);
			return kb+"KB";
		}else if(value==0){
			return "EMPTY";
		}else
			return "";
	}

	public static void adjustSize(Element el, SimplePanel panel, int offset){
		if(el!=null){
			int heigth = el.getClientHeight();
			GWT.log("getClientHeight adjustSize el: "+heigth);
			heigth = heigth-offset>100?heigth-offset:0;
			if(heigth>0){
				GWT.log("set new Explorer size: "+heigth);
				panel.setHeight(heigth+"px");
			}
		}
	}

	public static native void console(String text)
	/*-{
	    //console.log(text);
	}-*/;

}
