package org.gcube.portal.wssynclibrary.shared;



/**
 * The Class WorkspaceFolderLocked.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 13, 2018
 */
public class WorkspaceFolderLocked extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -778037901117579435L;
	private String folderId;

	/**
	 * Instantiates a new item not synched.
	 */
	public WorkspaceFolderLocked() {
		super();
	}

	/**
	 * Instantiates a new item not synched.
	 *
	 * @param arg0 the arg 0
	 */
	public WorkspaceFolderLocked(String folderId, String arg0) {
		super(arg0);
		this.folderId = folderId;
	}


	/**
	 * @return the folderId
	 */
	public String getFolderId() {

		return folderId;
	}

}
