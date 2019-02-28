package org.gcube.portlets.user.shareupdates.client;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.ClientFeed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portlets.user.shareupdates.shared.LinkPreview;
import org.gcube.portlets.user.shareupdates.shared.MentionedDTO;
import org.gcube.portlets.user.shareupdates.shared.UploadedFile;
import org.gcube.portlets.user.shareupdates.shared.UserSettings;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("shareupdateServlet")
public interface ShareUpdateService extends RemoteService {
	
	ClientFeed sharePostWithLinkPreview
	(String feedText, FeedType type, PrivacyLevel pLevel, Long vreOrgId, LinkPreview preview, 
			String urlThumbnail, ArrayList<MentionedDTO> mentionedUsers, boolean notifyGroup);
	
	ClientFeed sharePostWithAttachments
	(String feedText, FeedType type, PrivacyLevel pLevel, Long vreOrgId,ArrayList<UploadedFile> uploadedFiles, 
			ArrayList<MentionedDTO> mentionedUsers, boolean notifyGroup, boolean saveCopyWokspace);
	
	UserSettings getUserSettings();
	
	LinkPreview checkLink(String linkToCheck);
	
	LinkPreview checkUploadedFile(String fileName, String fileabsolutePathOnServer);
	
	ArrayList<ItemBean> getPortalItemBeans();
	
	ArrayList<ItemBean> getHashtags();
}
