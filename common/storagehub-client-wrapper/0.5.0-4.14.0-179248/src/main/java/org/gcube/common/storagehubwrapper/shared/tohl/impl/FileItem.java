/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.items.WorkspaceVersion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * The Class FileItem.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 21, 2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileItem extends WorkspaceItem implements org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem {

	/**
	 *
	 */
	private static final long serialVersionUID = -4149282274165182444L;


	private FileItemType fileItemType;
	private Long size;
	private String mimeType;
	private WorkspaceVersion currentVersion;

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.File#getFolderItemType()
	 */
	@Override
	public FileItemType getFileItemType() {

		return fileItemType;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.File#getSize()
	 */
	@Override
	public Long getSize() {
		return size;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.File#getMimeType()
	 */
	@Override
	public String getMimeType() {
		return mimeType;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.FileItem#getCurrentVersion()
	 */
	@Override
	public WorkspaceVersion getCurrentVersion() {

		return currentVersion;
	}



}
