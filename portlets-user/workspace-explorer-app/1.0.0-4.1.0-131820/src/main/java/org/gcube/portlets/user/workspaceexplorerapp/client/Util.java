/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client;

import org.gcube.portlets.user.workspaceexplorerapp.client.resources.WorkspaceExplorerResources;
import org.gcube.portlets.user.workspaceexplorerapp.client.resources.WorkspaceLightTreeResources;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemType;

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

	public static final ItemType[] FOLDERS = new ItemType[] {ItemType.FOLDER};

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
	 * Gets the image resource.
	 *
	 * @param item the item
	 * @return the image resource
	 */
	public static ImageResource getImageResource(Item item)
	{
		if(item==null || item.getType()==null)
			return WorkspaceLightTreeResources.INSTANCE.unknownType();

		switch (item.getType()) {
//			case ROOT: return WorkspaceLightTreeResources.INSTANCE.root();
			case FOLDER: {
				if (item.isSharedFolder()) return WorkspaceExplorerResources.INSTANCE.shared_folder();
				else return WorkspaceExplorerResources.INSTANCE.folder();
			}
			case EXTERNAL_IMAGE: return WorkspaceLightTreeResources.INSTANCE.external_image();
			case EXTERNAL_FILE: return WorkspaceLightTreeResources.INSTANCE.external_file();
			case EXTERNAL_PDF_FILE: return WorkspaceLightTreeResources.INSTANCE.external_pdf();
			case EXTERNAL_URL: return WorkspaceLightTreeResources.INSTANCE.external_url();
			case REPORT_TEMPLATE: return WorkspaceLightTreeResources.INSTANCE.report_template();
			case REPORT: return WorkspaceLightTreeResources.INSTANCE.report();
			case QUERY: return WorkspaceLightTreeResources.INSTANCE.query();
			case DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.document();
			case METADATA: return WorkspaceLightTreeResources.INSTANCE.metadata();
			case PDF_DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.pdf_document();
			case IMAGE_DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.image_document();
			case URL_DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.url_document();
			case GCUBE_ITEM: return WorkspaceLightTreeResources.INSTANCE.gucbeItem();
			case UNKNOWN_TYPE: return WorkspaceLightTreeResources.INSTANCE.unknown();
			default: {
				System.err.println("Unknown item type "+item.getType());
				return WorkspaceLightTreeResources.INSTANCE.unknownType();
			}
		}
	}

	/**
	 * Gets the formatted size.
	 *
	 * @param value the value
	 * @return the formatted size
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

	/**
	 * Adjust size.
	 *
	 * @param el the el
	 * @param panel the panel
	 * @param offset the offset
	 */
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

}
