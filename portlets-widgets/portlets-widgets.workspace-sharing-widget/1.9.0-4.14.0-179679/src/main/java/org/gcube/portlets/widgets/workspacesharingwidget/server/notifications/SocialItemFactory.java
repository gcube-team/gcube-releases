package org.gcube.portlets.widgets.workspacesharingwidget.server.notifications;

import org.gcube.applicationsupportlayer.social.shared.SocialFileItem;
import org.gcube.applicationsupportlayer.social.shared.SocialSharedFolder;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class SocialItemFactory {

	public SocialFileItem createSocialFileItem(Item item, FolderItem parent) {
		SocialSharedFolder socialSharedFolder=createSocialFolder(parent);
		return new SocialFileItem(item.getId(), item.getName(), item.getTitle(), item.getPath(), socialSharedFolder);
	}

	public SocialSharedFolder createSocialFolder(FolderItem folder) {
		return new SocialSharedFolder(folder.getId(), folder.getName(), folder.getTitle(), folder.getName(),
				folder.getPath(), folder.getParentId(), false);

	}

}
