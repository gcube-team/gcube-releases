/**
 *
 */

package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The Class WorkspaceItemImpl.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jun 15, 2018
 */

/* (non-Javadoc)
 * @see org.gcube.portal.storagehubwrapper.shared.tohl.WorkspaceItem#isHidden()
 */
@Getter

/**
 * Sets the folder.
 *
 * @param isFolder the new folder
 */
@Setter

/**
 * Instantiates a new workspace item.
 */
@NoArgsConstructor

/**
 * Instantiates a new workspace item.
 *
 * @param id the id
 * @param name the name
 * @param path the path
 * @param parentId the parent id
 * @param trashed the trashed
 * @param shared the shared
 * @param locked the locked
 * @param title the title
 * @param description the description
 * @param lastModifiedBy the last modified by
 * @param lastModificationTime the last modification time
 * @param creationTime the creation time
 * @param owner the owner
 * @param hidden the hidden
 * @param accounting the accounting
 * @param type the type
 * @param isFolder the is folder
 * @param isRoot the is root
 */
@AllArgsConstructor
@ToString
public class WorkspaceItem implements org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5444534091783292538L;
	private String id;
	private String name;
	private String path;
	private String parentId;
	//private String primaryType;
	private boolean trashed;
	private boolean shared;
	private boolean locked;
	private String title;
	private String description;
	private String lastModifiedBy;
	private Calendar lastModificationTime;
	private Calendar creationTime;
	private String owner;
	//private ItemAction lastAction;
	private boolean hidden;
	private List<AccountEntry> accounting;
	private WorkspaceItemType type;
	private boolean isFolder;
	boolean isRoot;
	private PropertyMap propertyMap;

}
