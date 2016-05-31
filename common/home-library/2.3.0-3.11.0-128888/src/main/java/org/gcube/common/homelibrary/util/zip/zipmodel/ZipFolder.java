/**
 * 
 */
package org.gcube.common.homelibrary.util.zip.zipmodel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipFolder extends AbstractZipItem {
	
	protected List<ZipItem> children;

	/**
	 * Create an instance of Zip Folder.
	 * @param parent the folder parent.
	 * @param name the folder name.
	 * @param comment the folder comment.
	 * @param extra the folder extra field.
	 */
	public ZipFolder(ZipFolder parent, String name, String comment, byte[] extra) {
		super(parent, name, ZipItemType.FOLDER, comment, extra);
		this.children = new LinkedList<ZipItem>();
	}
	
	/**
	 * Create an instance of Zip Folder.
	 * @param name the folder name.
	 * @param comment the folder comment.
	 */
	public ZipFolder(String name, String comment) {
		super(name, ZipItemType.FOLDER, comment);
		this.children = new LinkedList<ZipItem>();
	}
	
	/**
	 * @param child the child to add.
	 */
	public void addChild(ZipItem child)
	{
		children.add(child);
	}
	
	/**
	 * @return the children list.
	 */
	public List<ZipItem> getChildren()
	{
		return children;
	}


}
