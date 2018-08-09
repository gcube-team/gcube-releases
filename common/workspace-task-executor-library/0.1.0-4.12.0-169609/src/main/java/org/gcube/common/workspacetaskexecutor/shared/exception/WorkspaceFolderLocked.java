package org.gcube.common.workspacetaskexecutor.shared.exception;



/**
 * The Class WorkspaceFolderLocked.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class WorkspaceFolderLocked extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 7375143436359627673L;
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
	 * @param folderId the folder id
	 * @param arg0 the arg 0
	 */
	public WorkspaceFolderLocked(String folderId, String arg0) {
		super(arg0);
		this.folderId = folderId;
	}


	/**
	 * Gets the folder id.
	 *
	 * @return the folderId
	 */
	public String getFolderId() {

		return folderId;
	}

}
