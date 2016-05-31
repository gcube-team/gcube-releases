package org.gcube.portlets.user.shareupdates.client;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.ClientFeed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portlets.user.shareupdates.shared.LinkPreview;
import org.gcube.portlets.user.shareupdates.shared.UploadedFile;
import org.gcube.portlets.user.shareupdates.shared.UserSettings;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ShareUpdateService</code>.
 */
public interface ShareUpdateServiceAsync {

	void checkLink(String linkToCheck, AsyncCallback<LinkPreview> callback);

	void getUserSettings(AsyncCallback<UserSettings> callback);

	void getPortalUsers(AsyncCallback<ArrayList<ItemBean>> callback);

	void checkUploadedFile(String fileName, String fileabsolutePathOnServer,
			AsyncCallback<LinkPreview> callback);

	void getHashtags(AsyncCallback<ArrayList<ItemBean>> callback);

	void sharePostWithLinkPreview(String feedText, FeedType type,
			PrivacyLevel pLevel, Long vreOrgId, LinkPreview preview,
			String urlThumbnail, ArrayList<String> mentionedUsers, boolean notifyGroup,
			AsyncCallback<ClientFeed> callback);

	void sharePostWithAttachments(String feedText, FeedType type,
			PrivacyLevel pLevel, Long vreOrgId,
			ArrayList<UploadedFile> uploadedFiles,
			ArrayList<String> mentionedUsers, boolean notifyGroup,
			boolean saveCopyWokspace, AsyncCallback<ClientFeed> callback);

}
