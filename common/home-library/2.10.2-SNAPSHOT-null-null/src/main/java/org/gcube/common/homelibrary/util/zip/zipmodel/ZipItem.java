/**
 * 
 */
package org.gcube.common.homelibrary.util.zip.zipmodel;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface ZipItem {
	
	/**
	 * Return this item parent.
	 * @return the parent, null if is the root.
	 */
	public ZipFolder getParent();
	
	/**
	 * Set this item parent.
	 * @param parent the parent.
	 */
	public void setParent(ZipFolder parent);
	
	/**
	 * Return this item name.
	 * @return the item name.
	 */
	public String getName();
	
	/**
	 * Sets the item name.
	 * @param name the item name.
	 */
	public void setName(String name);
	
	/**
	 * Return this item type.
	 * @return the item type.
	 */
	public ZipItemType getType();
	
	/**
	 * Return this item comment.
	 * @return the item comment.
	 */
	public String getComment();
	
	/**
	 * Return this item extra field.
	 * @return the extra field.
	 */
	public byte[] getExtra();

	/**
	 * return this item path.
	 * @return the item path.
	 */
	public String getPath();
}
