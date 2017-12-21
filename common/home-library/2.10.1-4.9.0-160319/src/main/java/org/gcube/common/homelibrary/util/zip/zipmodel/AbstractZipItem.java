/**
 * 
 */
package org.gcube.common.homelibrary.util.zip.zipmodel;

import java.io.File;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public abstract class AbstractZipItem implements ZipItem {
	
	protected ZipFolder parent;
	protected String name;
	protected ZipItemType type;
	protected String comment;
	protected byte[] extra;

	/**
	 * @param parent the item parent.
	 * @param name the item name.
	 * @param type the item type.
	 * @param comment the item comment.
	 * @param extra the extra field.
	 */
	public AbstractZipItem(ZipFolder parent, String name, ZipItemType type, String comment,	byte[] extra) {
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.comment = comment;
		this.extra = extra;
	}
	
	/**
	 * @param name the item name.
	 * @param type the item type.
	 * @param comment the item comment.
	 */
	public AbstractZipItem(String name, ZipItemType type, String comment) {
		this(null,name,type,comment,new byte[0]);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ZipFolder getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParent(ZipFolder parent) {
		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getComment() {
		return comment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getExtra() {
		return extra;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ZipItemType getType() {
		return type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		if (parent == null) return File.separatorChar + name;
		return parent.getPath() + File.separatorChar + name;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(" ");
		sb.append(name);
		sb.append(" ");
		sb.append(comment);
		sb.append(" ");
		sb.append(extra);
		return sb.toString();
	}
}
