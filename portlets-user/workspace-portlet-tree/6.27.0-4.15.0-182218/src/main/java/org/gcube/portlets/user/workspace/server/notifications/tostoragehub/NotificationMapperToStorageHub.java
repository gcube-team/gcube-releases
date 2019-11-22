/**
 *
 */
package org.gcube.portlets.user.workspace.server.notifications.tostoragehub;

import org.gcube.applicationsupportlayer.social.shared.SocialFileItem;
import org.gcube.applicationsupportlayer.social.shared.SocialSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class NotificationMapper.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *         Oct 2, 2018
 */
public class NotificationMapperToStorageHub {

	protected static Logger logger = LoggerFactory.getLogger(NotificationMapperToStorageHub.class);

	/**
	 * To social shared folder.
	 *
	 * @param sharedFolder the shared folder
	 * @return the social shared folder
	 */
	public static SocialSharedFolder toSocialSharedFolder(WorkspaceSharedFolder sharedFolder) {

		return new SocialSharedFolder(sharedFolder.getId(), sharedFolder.getName(), sharedFolder.getTitle(),
				sharedFolder.getName(), sharedFolder.getPath(), sharedFolder.getParentId(), sharedFolder.isVreFolder());
	}

	/**
	 * To social item.
	 *
	 * @param workspace the workspace
	 * @param item      the item
	 * @return the social file item
	 */
	public static SocialFileItem toSocialItem(org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace,
			WorkspaceItem item) {

		WorkspaceItem theParentFolder = null;
		try {
			theParentFolder = workspace.getItem(item.getParentId());
		} catch (Exception e) {
			logger.warn("Impossible the item using the parent id: " + item.getParentId());
		}
//
		SocialSharedFolder parent = null;
		if (theParentFolder != null && theParentFolder instanceof WorkspaceSharedFolder)
			parent = toSocialSharedFolder((WorkspaceSharedFolder) theParentFolder);
//
		return new SocialFileItem(item.getId(), item.getName(), item.getTitle(), item.getPath(), parent);
	}
}
