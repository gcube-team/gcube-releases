package org.gcube.portlets.user.workspace.client.model;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;

/**
 * The Class FileGridModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Nov 17, 2015
 */
public class FileGridModel extends FileModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file grid model.
	 */
	protected FileGridModel() {
	}

	/**
	 * Instantiates a new file grid model.
	 *
	 * @param identifier
	 *            the identifier
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 * @param lastUpdate
	 *            the last update
	 * @param parent
	 *            the parent
	 * @param size
	 *            the size
	 * @param isDirectory
	 *            the is directory
	 * @param isShared
	 *            the is shared
	 */
	public FileGridModel(String identifier, String name, String path,
			Date lastUpdate, FileModel parent, long size, boolean isDirectory,
			boolean isShared) {
		super(identifier, name, parent, isDirectory, isShared);

		setLastModification(lastUpdate);
		setSize(size);
	}

	/**
	 * Instantiates a new file grid model.
	 *
	 * @param identifier
	 *            the identifier
	 * @param name
	 *            the name
	 * @param lastUpdate
	 *            the last update
	 * @param parent
	 *            the parent
	 * @param size
	 *            the size
	 * @param isDirectory
	 *            the is directory
	 * @param isShared
	 *            the is shared
	 */
	public FileGridModel(String identifier, String name, Date lastUpdate,
			FileModel parent, long size, boolean isDirectory, boolean isShared) {
		super(identifier, name, parent, isDirectory, isShared);

		setLastModification(lastUpdate);
		setSize(size);
	}

	/**
	 * Sets the size.
	 *
	 * @param size
	 *            the new size
	 */
	private void setSize(long size) {
		set(ConstantsExplorer.SIZE, size);
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public long getSize() {
		return (Long) get(ConstantsExplorer.SIZE);
	}

	/**
	 * Sets the last modification.
	 *
	 * @param lastUpdate
	 *            the new last modification
	 */
	private void setLastModification(Date lastUpdate) {
		set(ConstantsExplorer.LASTMODIFIED, lastUpdate);

	}

	/**
	 * Gets the last modification.
	 *
	 * @return the last modification
	 */
	public Date getLastModification() {
		return (Date) get(ConstantsExplorer.LASTMODIFIED);

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.user.workspace.client.model.FileModel#equals(java.
	 * lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileGridModel) {
			FileGridModel mobj = (FileGridModel) obj;
			return getIdentifier().equals(mobj.getIdentifier());
		}
		return super.equals(obj);
	}
}
