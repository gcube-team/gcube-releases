package org.gcube.portlets.user.shareupdates.client.view;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;

import org.gcube.portal.databook.shared.ClientFeed;
import org.gcube.portal.databook.shared.ClientFeed.ClientFeedJsonizer;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.shareupdates.client.ShareUpdateService;
import org.gcube.portlets.user.shareupdates.client.ShareUpdateServiceAsync;
import org.gcube.portlets.user.shareupdates.shared.LinkPreview;
import org.gcube.portlets.user.shareupdates.shared.UploadedFile;
import org.gcube.portlets.user.shareupdates.shared.UserSettings;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadCompleteEvent;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadCompleteEventHandler;
import org.gcube.portlets.widgets.fileupload.client.view.FileSubmit;
import org.gcube.portlets.widgets.fileupload.client.view.UploadProgressPanel;
import org.jsonmaker.gwt.client.Jsonizer;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;
/**
 * 	The main share update class.
 * @author Massimiliano Assante at ISTI CNR
 * @author Costantino Perciante at ISTI CNR
 *
 */
public class ShareUpdateForm extends Composite {

	//Create a remote service proxy to talk to the server-side Greeting service.
	private final ShareUpdateServiceAsync shareupdateService = GWT
			.create(ShareUpdateService.class);

	final PageBusAdapter pageBusAdapter = new PageBusAdapter();

	// the label for all Vres/channels
	private final static String ALL_VRES = "Share with: your Virtual Research Environments";

	// maximum number of files that can be attached
	private static final int MAX_NUMBER_ATTACHMENTS = 10;

	// Labels
	protected final static String SHARE_UPDATE_TEXT = "Share an update or a link, use “@” to mention and “#” to add a topic";
	protected final static String ERROR_UPDATE_TEXT = "Looks like empty to me!";
	public final static String NO_TEXT_FILE_SHARE = "_N0_73X7_SH4R3_";
	private final static String LISTBOX_LEVEL = " - ";
	public static final String DROP_FILE_HERE_TEXT  = "Drop your file(s) here!";
	public static final String ATTACHMENT_LOADED = "Attachment loaded!";
	public static final String ATTACHMENT_NOT_LOADED = "Attachment not loaded!";
	private static final String DELETE_LINK_PREVIEW = "The link preview will be removed. Would you like to continue?";
	private static final String DELETE_ATTACHMENTS = "The attachment(s) will be removed. Would you like to continue?";
	private static final String TOO_MUCH_ATTACHMENT_ALERT = "Sorry, but you cannot upload more than " + MAX_NUMBER_ATTACHMENTS + " attachments!";
	private static final String WAIT_CURRENT_UPLOAD_FINISHING = "Please, wait the current upload to finish";
	// image urls
	public static final String loading = GWT.getModuleBaseURL() + "../images/avatarLoader.gif";
	public static final String avatar_default = GWT.getModuleBaseURL() + "../images/Avatar_default.png";
	public static final String attachImageUrl = GWT.getModuleBaseURL() + "../images/attach.png";
	public static final String attachedDefaultImageUrl = GWT.getModuleBaseURL() + "../images/attachment_default.png";
	public static final String loadedAttachmentImageUrl = GWT.getModuleBaseURL() + "../images/load.png";
	public static final String notLoadedAttachmentImageUrl = GWT.getModuleBaseURL() + "../images/not_load.png";

	// remember the previous text in the textarea (while handling drag and drop)
	private static String previousText;

	// list of attachedFiles (both correctly or not correctly uploaded)
	private List<AttachedFile> listOfAttachedFiles = new ArrayList<>();

	private HandlerManager eventBus = new HandlerManager(null);

	private static ShareUpdateFormUiBinder uiBinder = GWT
			.create(ShareUpdateFormUiBinder.class);

	// The link previewer
	private LinkPreviewer linkPreviewer;

	// panel that show the in progress upload of an attachment
	private UploadProgressPanel uploadProgress;

	interface ShareUpdateFormUiBinder extends UiBinder<Widget, ShareUpdateForm> {
	}

	// this instance
	private static ShareUpdateForm singleton;

	/**
	 * Get this ShareUpdateForm object
	 * @return
	 */
	public static ShareUpdateForm get() {
		return singleton;
	}

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Placeholder preview;

	@UiField
	Button submitButton;

	@UiField
	Button attachButton;

	@UiField
	Image avatarImage;

	@UiField 
	SuperPosedTextArea shareTextArea; 

	@UiField 
	ListBox privacyLevel = new ListBox();

	@UiField
	ListBox notifyListbox = new ListBox();

	@UiField
	SaveInWorkspaceBox saveInWorkspaceCheckbox;

	// requested user's information
	private UserInfo myUserInfo;

	/**
	 * Constructor
	 */
	public ShareUpdateForm() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;
		bind();
		avatarImage.setUrl(loading);
		shareTextArea.setText(SHARE_UPDATE_TEXT);

		attachButton.getElement().getStyle().setDisplay(Display.INLINE);
		attachButton.addStyleName("upload-btn-m");

		shareupdateService.getUserSettings(new AsyncCallback<UserSettings>() {
			public void onFailure(Throwable caught) {
				avatarImage.setUrl(avatar_default);
			}

			public void onSuccess(UserSettings userSettings) {
				myUserInfo = userSettings.getUserInfo();				
				avatarImage.getElement().getParentElement().setAttribute("href", myUserInfo.getAccountURL());
				avatarImage.setUrl(myUserInfo.getAvatarId());
				String singleVREName = "";

				boolean notificationEmail = userSettings.isNotificationViaEmailEnabled();

				if (myUserInfo.getOwnVREs().size() > 1) {
					privacyLevel.addItem(ALL_VRES, PrivacyLevel.VRES.toString());
					for (String vreId : myUserInfo.getOwnVREs().keySet()) 
						privacyLevel.addItem(LISTBOX_LEVEL +  "Share with: " + myUserInfo.getOwnVREs().get(vreId), vreId);
				}				
				else if (myUserInfo.getOwnVREs().size() == 1)
					for (String vreId : myUserInfo.getOwnVREs().keySet()) {
						singleVREName =  myUserInfo.getOwnVREs().get(vreId);
						privacyLevel.addItem(LISTBOX_LEVEL +  "Share with: " + singleVREName, vreId);						
					}

				if (myUserInfo.isAdmin())
					privacyLevel.addItem("Share with: Everyone", PrivacyLevel.PORTAL.toString());

				//change css if deployed in VRE scope
				if (!userSettings.isInfrastructure()) {
					mainPanel.addStyleName("framed");	
					notifyListbox.addItem("Share with: " + singleVREName);
					notifyListbox.addItem("Share with: " + singleVREName+" + Notification to members");
					if (notificationEmail) {
						notifyListbox.setSelectedIndex(1);
					}
					notifyListbox.setVisible(true);
				}
				else
					privacyLevel.setVisible(true);
				attachButton.setVisible(true);
				submitButton.setVisible(true);

				// check if DND can be activated and enable it if it's possible
				if(checkDNDAvailability()){

					// add drag over handler on shareTextArea
					shareTextArea.addDragOverHandler(new DragOverHandler() {

						@Override
						public void onDragOver(DragOverEvent event) {

							GWT.log("Drag over handler");

							// add style change
							addDNDStyleEffects(); 

						}
					});

					// clear drag over effect
					shareTextArea.addDragLeaveHandler(new DragLeaveHandler() {

						@Override
						public void onDragLeave(DragLeaveEvent event) {

							GWT.log("Drag leave handler");

							// remove style changes
							resetDNDStyleEffects();

						}

					});

					// enable shareTextArea as drop target (using native javascript)
					addNativeDropHandler(singleton, FileSubmit.URL);

				}else{

					GWT.log("Drag and drop not supported.");

				}
			}
		});
	}

	/**
	 * Bind events to manage
	 */
	private void bind() {

		//get the uploaded file result
		eventBus.addHandler(FileUploadCompleteEvent.TYPE, new FileUploadCompleteEventHandler() {

			@Override
			public void onUploadComplete(FileUploadCompleteEvent event) {
				String absolutePathOnServer = event.getUploadedFileInfo().getAbsolutePath();
				GWT.log("uploaded on Server here: " + absolutePathOnServer);
				checkFile(event.getUploadedFileInfo().getFilename(), absolutePathOnServer);
			}
		});
	}

	/** Used by UiBinder to instantiate UploadProgressView */
	@UiFactory 
	UploadProgressPanel instatiateProgressView() {
		uploadProgress = new UploadProgressPanel(eventBus);
		uploadProgress.setVisible(false);
		return uploadProgress;
	}

	@UiHandler("shareTextArea")
	void onShareUpdateClick(ClickEvent e) {
		shareTextArea.removeSampleText();
		if (shareTextArea.getText().compareTo("") == 0) {
			Document.get().getElementById("highlighterContainer").getStyle().setHeight(52, Unit.PX);
			Document.get().getElementById("highlighter").getStyle().setHeight(52, Unit.PX);
			Document.get().getElementById("postTextArea").getStyle().setHeight(52, Unit.PX);
		}
	}

	@UiHandler("attachButton")
	void onAttachClick(ClickEvent e) {	

		// check if there is a link preview
		if(linkPreviewer != null){

			// ask the user
			boolean delete = wantToDeleteLinkPreview();

			if(!delete)
				return;

			// remove link preview
			cancelLinkPreview();
		}

		// check the number of already attached files
		if(numberOfAttachments() >= MAX_NUMBER_ATTACHMENTS){

			Window.alert(TOO_MUCH_ATTACHMENT_ALERT);
			return;

		}

		// proceed with the upload
		FileUpload up = uploadProgress.initialize();
		up.click();
		up.setVisible(false);

		// disable attach button when users select a file
		up.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				attachButton.setEnabled(false);
			}
		});

		uploadProgress.setVisible(true);

	}

	@UiHandler("submitButton")
	void onClick(ClickEvent e) {	

		//because otherwise it looses the other properties setting
		attachButton.getElement().getStyle().setVisibility(Visibility.VISIBLE); 

		shareupdateService.getUserSettings(new AsyncCallback<UserSettings>() {
			public void onFailure(Throwable caught) {

				Window.alert("Ops! we encountered some problems delivering your message, server is not responding, please try again in a short while.");

			}
			public void onSuccess(UserSettings result) {

				if (result.getUserInfo().getUsername().equals("test.user")) {
					Window.alert("Your session has expired, please log out and login again");
					return;
				}
				myUserInfo = result.getUserInfo();
				String toShare = shareTextArea.getText().trim();

				// Establish the content of this post
				PostContent postContent = PostContent.ONLY_TEXT;

				// check if we are going to send a link preview (The shared text cannot be empty nor it can be an error message)
				if(linkPreviewer != null && (toShare.equals(SHARE_UPDATE_TEXT) || toShare.equals(ERROR_UPDATE_TEXT) || toShare.equals(""))){

					shareTextArea.addStyleName("error");
					shareTextArea.setText(ERROR_UPDATE_TEXT);
					return;

				}else{

					if(linkPreviewer != null)
						postContent = PostContent.TEXT_AND_LINK;

					if(numberOfAttachmentsUploaded() > 0){
						postContent = PostContent.TEXT_AND_ATTACHMENTS;

						if(toShare.equals("") || toShare.equals(SHARE_UPDATE_TEXT) || toShare.equals(ERROR_UPDATE_TEXT))
							toShare = NO_TEXT_FILE_SHARE;
					}

				}

				// check the text (attachment can be sent without shared text)
				if (toShare.equals(SHARE_UPDATE_TEXT) || toShare.equals(ERROR_UPDATE_TEXT) || toShare.equals("")) {
					shareTextArea.addStyleName("error");
					shareTextArea.setText(ERROR_UPDATE_TEXT);
					return;
				}

				//then you can post but you have to pass html checks now
				String toPost = toShare;
				postTweet(toPost, shareTextArea.getMentionedUsers(), postContent);
			}
		});
	}


	/**
	 * Publish a post.
	 * @param textToPost the text of this port
	 * @param mentionedUsers list of users mentioned in the text (if any)
	 * @param postContent the type of post
	 */
	private void postTweet(String textToPost, ArrayList<String> mentionedUsers, PostContent postContent) {

		// escape html text
		String toShareText = escapeHtml(textToPost);
		if (! checkTextLength(toShareText)) {
			Window.alert("We found a single word containing more than 50 chars and it's not a link, is it meaningful?");
			return;
		}

		// disable text edit and submission button
		submitButton.setEnabled(false);
		shareTextArea.setEnabled(false);

		// retrieve the vre id
		String vreId = "";
		if (getPrivacyLevel() == PrivacyLevel.SINGLE_VRE) {
			vreId = privacyLevel.getValue(privacyLevel.getSelectedIndex());  
		}

		// notify group information
		boolean notifyGroup = notifyListbox.getSelectedIndex() > 0; 

		// case in which there are no attachments but there could be a link preview
		if(postContent == PostContent.ONLY_TEXT || postContent == PostContent.TEXT_AND_LINK){

			//preparing to send stuff
			String linkTitle = "", linkDescription = "" , linkUrl = "", linkUrlThumbnail = "", linkHost = "";

			if (linkPreviewer != null) {
				linkTitle = linkPreviewer.getLinkTitle();
				linkDescription = linkPreviewer.getLinkDescription();
				linkUrl = linkPreviewer.getUrl();
				linkUrlThumbnail = linkPreviewer.getUrlThumbnail();
				linkHost = linkPreviewer.getHost();
			}

			LinkPreview preview2Share = new LinkPreview(linkTitle, linkDescription, linkUrl, linkHost, null); 

			Long vreOrgId = Long.parseLong(vreId);
			// share post (it could contain a link preview)
			shareupdateService.sharePostWithLinkPreview(toShareText, FeedType.TWEET, getPrivacyLevel(), vreOrgId, preview2Share, linkUrlThumbnail, mentionedUsers, notifyGroup, new AsyncCallback<ClientFeed>() {

				public void onFailure(Throwable caught) {
					submitButton.setEnabled(true);
					shareTextArea.setEnabled(true);	
					shareTextArea.setText(SHARE_UPDATE_TEXT);
					shareTextArea.cleanHighlighterDiv();
					preview.clear();
					linkPreviewer = null;
				}

				public void onSuccess(ClientFeed feed) {

					submitButton.setEnabled(true);
					shareTextArea.setEnabled(true);
					shareTextArea.setText(SHARE_UPDATE_TEXT);
					shareTextArea.cleanHighlighterDiv();
					preview.clear();
					linkPreviewer = null;

					if (feed == null)
						Window.alert("Ops! we encountered some problems delivering your message, please try again in a short while.");
					else {
						// publish a message with the refresh notification
						try {

							pageBusAdapter.PageBusPublish("org.gcube.portal.databook.shared", feed, (Jsonizer)GWT.create(ClientFeedJsonizer.class));

						} catch (PageBusAdapterException ex) {
							GWT.log(ex.getMessage());
						}
					}

					//needed when posting long texts otherwise it stays with the current height 
					shareTextArea.getElement().getStyle().setHeight(54, Unit.PX);
				}
			});
		}
		else{

			// case with at least one attachment available
			ArrayList<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();

			// consider only correctly uploaded file(s)
			for(AttachedFile file: listOfAttachedFiles){

				if(file.isCorrectlyUploaded())
					uploadedFiles.add(
							new UploadedFile(
									file.getFileName(), 
									file.getFileAbsolutePathOnServer(), 
									file.getDescription(), 
									file.getDownloadUrl(), 
									file.getThumbnailUrl(), 
									file.getFormat()));

			}
			Long vreOrgId = Long.parseLong(vreId);
			// share the post
			shareupdateService.sharePostWithAttachments(toShareText, FeedType.TWEET, getPrivacyLevel(), vreOrgId, uploadedFiles, mentionedUsers, notifyGroup, saveInWorkspaceCheckbox.getValue(), new AsyncCallback<ClientFeed>() {

				@Override
				public void onSuccess(ClientFeed feed) {

					//GWT.log("Saved feed looks like " + feed.toString());

					submitButton.setEnabled(true);
					shareTextArea.setEnabled(true);
					shareTextArea.setText(SHARE_UPDATE_TEXT);
					shareTextArea.cleanHighlighterDiv();
					saveInWorkspaceCheckbox.setVisible(false);
					preview.clear();
					listOfAttachedFiles.clear();

					if (feed == null)
						Window.alert("Ops! we encountered some problems delivering your message, please try again in a short while.");
					else {
						// publish a message with the refresh notification
						try {
							pageBusAdapter.PageBusPublish("org.gcube.portal.databook.shared", feed, (Jsonizer)GWT.create(ClientFeedJsonizer.class));
							GWT.log("SENT");
						} catch (PageBusAdapterException ex) {
							GWT.log(ex.getMessage());
						}
					}

					//needed when posting long texts otherwise it stays with the current height 
					shareTextArea.getElement().getStyle().setHeight(54, Unit.PX);

				}

				@Override
				public void onFailure(Throwable caught) {

					GWT.log(caught.toString());

					submitButton.setEnabled(true);
					shareTextArea.setEnabled(true);	
					shareTextArea.setText(SHARE_UPDATE_TEXT);
					shareTextArea.cleanHighlighterDiv();
					saveInWorkspaceCheckbox.setVisible(false);
					preview.clear();
					listOfAttachedFiles.clear();

				}
			});

		}
	}

	/**
	 * Determines the privacy level of the post to be shared.
	 * @return
	 */
	private PrivacyLevel getPrivacyLevel() {
		String selected = privacyLevel.getValue(privacyLevel.getSelectedIndex());
		if (selected.compareTo(PrivacyLevel.CONNECTION.toString()) == 0)
			return PrivacyLevel.CONNECTION;
		else if (selected.compareTo(PrivacyLevel.VRES.toString()) == 0)
			return PrivacyLevel.VRES;
		else if (selected.compareTo(PrivacyLevel.PRIVATE.toString()) == 0)
			return PrivacyLevel.PRIVATE;
		else if (selected.compareTo(PrivacyLevel.PORTAL.toString()) == 0)
			return PrivacyLevel.PORTAL;
		else
			return PrivacyLevel.SINGLE_VRE;

	}
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	/**
	 * called when pasting a possible link
	 * @param linkToCheck
	 */
	protected void checkLink(String textToCheck) {
		if (linkPreviewer == null) {
			String [] parts = textToCheck.split("\\s");
			// Attempt to convert each item into an URL.   
			for( String item : parts ) {
				if (item.startsWith("http") || item.startsWith("www")) {

					// check if there are attachments and inform the user that they will be lost
					if(!listOfAttachedFiles.isEmpty()){

						// in this case let the user to choose what to do
						boolean confirm = Window.confirm(DELETE_ATTACHMENTS);

						if(!confirm)
							return;

						// else... remove attachments and continue
						listOfAttachedFiles.clear();
						preview.clear();
						saveInWorkspaceCheckbox.setVisible(false);

					}

					preview.add(new LinkLoader());
					submitButton.setEnabled(false);

					//GWT.log("It's http link:" + linkToCheck);
					shareupdateService.checkLink(textToCheck, new AsyncCallback<LinkPreview>() {
						public void onFailure(Throwable caught) {
							preview.clear();
							submitButton.setEnabled(true);
						}

						public void onSuccess(LinkPreview result) {

							// For a link, the LinkPreview object is like this
							// LinkPreview [title=ANSA.it - Homepage, description=ANSA.it: Il sito Internet dell'Agenzia ANSA. Ultime notizie, foto, video e approfondimenti su: cronaca, politica, economia, regioni, mondo, sport, calcio, cultura e tecnologia, 
							// url=http://www.ansa.it/, host=ansa.it, imageUrls=[http://www.ansa.it/sito/img/ico/ansa-57-precomposed.png]]
							// GWT.log(result.toString());
							preview.clear();
							if (result != null) 
								addPreviewLink(result);
							submitButton.setEnabled(true);
						}
					});
					break;
				}
			}

		} else {
			Window.alert("You cannot post two links, please remove the previous one first.");
		}
	}

	/**
	 * called when the file was correctly uploaded on server
	 * @param fileNameLabel the name of the file
	 * @param absolutePathOnServer the path of the file ending with its name on the server temp
	 */
	protected void checkFile(final String fileName, final String absolutePathOnServer) {		

		// create temp view of the attached file and add to the previewer
		final AttachmentPreviewer atPrev = new AttachmentPreviewer(fileName, attachedDefaultImageUrl, preview, this);

		shareupdateService.checkUploadedFile(fileName, absolutePathOnServer, new AsyncCallback<LinkPreview>() {
			public void onFailure(Throwable caught) {

				GWT.log("Unable to check uploaded file!");

				// hide progress bar
				uploadProgress.setVisible(false);

				// attach the file with error..
				listOfAttachedFiles.add(
						new AttachedFile(
								fileName, 
								absolutePathOnServer, 
								atPrev, 
								null)
						);

				// there is no a linkPreview...
				addPreviewAttachment(null, atPrev);

				// enable anyway the button
				submitButton.setEnabled(true);

				// enable attach button
				attachButton.setEnabled(true);
			}

			// it returns a LinkPreview (for compatibility with old code)
			public void onSuccess(LinkPreview result) {	

				if(result == null)
					return;

				listOfAttachedFiles.add(
						new AttachedFile(
								result.getTitle(), 
								absolutePathOnServer, 
								result.getDescription(), 
								result.getUrl(), 
								result.getImageUrls().get(0),
								result.getHost(), 
								atPrev, 
								true)
						);

				addPreviewAttachment(result, atPrev);	
				submitButton.setEnabled(true);

				// enable attach button
				attachButton.setEnabled(true);
			}
		});
	}

	/**
	 * called when pasting. it tries to avoid pasting long non spaced strings
	 * @param linkToCheck
	 */
	private boolean checkTextLength(String textToCheck) {

		String [] parts = textToCheck.split("\\s");
		// check the length of tokens   
		for( String item : parts ) {
			if (! item.startsWith("http")) { //url are accepted as they can be trunked
				if (item.length() > 50) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * add the link preview in the view
	 * @param result
	 */
	private void addPreviewLink(LinkPreview result) {
		//GWT.log(result.toString());
		preview.clear();
		uploadProgress.setVisible(false);
		linkPreviewer = new LinkPreviewer(this, result);
		preview.add(linkPreviewer);
	}

	/**
	 * Call it to show attachment(s)
	 */
	private void addPreviewAttachment(LinkPreview result, AttachmentPreviewer atPrev){

		// GWT.log(result.toString());

		// disable progress bar
		uploadProgress.setVisible(false);		

		// check the result
		if(result == null){

			// failed upload
			atPrev.setResultAttachment(ATTACHMENT_NOT_LOADED, notLoadedAttachmentImageUrl);

			// change the atPrev object and let the user retry the upload
			atPrev.retryToUpload(atPrev);

		}
		else{

			// set the preview information (the first image is the one related to the type of file)
			atPrev.setResultAttachment(ATTACHMENT_LOADED, loadedAttachmentImageUrl);
			atPrev.setImagePreview(result.getImageUrls().get(0));

		}

		preview.add(atPrev);

		// enable checkbox to save in workspace if it's the case
		if(numberOfAttachments() > 0 && !saveInWorkspaceCheckbox.isVisible())
			saveInWorkspaceCheckbox.setVisible(true);
	}
	/**
	 * Delete the only link previewer allowed.
	 */
	protected void cancelLinkPreview() {
		preview.clear();
		linkPreviewer = null;
		attachButton.getElement().getStyle().setVisibility(Visibility.VISIBLE); //beacuse otherwise it looses the other properties setting	
	}

	/**
	 * Handle drop of files within shareTextArea (native javascript code)
	 * @param instance
	 */
	private static native void addNativeDropHandler(ShareUpdateForm instance,
			String servletUrl)/*-{

		// retrieve textArea by id
		var drop = $wnd.$('#postTextArea')[0];		

		// check if this file is a folder
		function isFolder(file) {

			if (file != null && !file.type && file.size % 4096 == 0) {
				return true;
			}
			return false;
		}

		// function used to add the handler
		function addEventHandler(obj, evt, handler) {
			if (obj.addEventListener) {
				// W3C method
				obj.addEventListener(evt, handler, false);
			} else if (obj.attachEvent) {
				// IE method.
				obj.attachEvent('on' + evt, handler);
			} else {
				// Old school method.
				obj['on' + evt] = handler;
			}
		}

		// The real drop handler
		addEventHandler(
				drop,
				'drop',
				function(e) {

					// get window.event if e argument missing (in IE)
					e = e || window.event; 

					 // stops the browser from redirecting off to the image.
					if (e.preventDefault) {

						e.preventDefault();

					}

					// opts for the remote call
					var opts = {

						url : servletUrl,
						type : "POST",
						processData : false

					};

					// get the file(s)
					var dt = e.dataTransfer;
					var files = dt.files;

					// chek if a link preview is already there
					var linkPreviewPresent = instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::isLinkPreviewPresent()();

					if(linkPreviewPresent){

						var hasToBeDelete = instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::wantToDeleteLinkPreview()();

						if(!hasToBeDelete){
							instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::resetDNDStyleEffects()();
							return;
						}
						// else delete the link preview and proceeed
						instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::cancelLinkPreview()();

					}

					// check limit for number of files 
					var numberOfAlreadyAttachedFiles = instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::numberOfAttachments()();
					numberOfAlreadyAttachedFiles += files.length;
					var limitExceeded = (numberOfAlreadyAttachedFiles > @org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::MAX_NUMBER_ATTACHMENTS);

					if(limitExceeded){

						var msg = "Sorry, you are trying to attach more than " + 
						 + @org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::MAX_NUMBER_ATTACHMENTS " files!"
						instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::showAlert(Ljava/lang/String;)(msg);
						console.log(msg);

						// reset text area
						instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::resetDNDStyleEffects()();
						return;
					}

					// reset if no file was dropped (??)
					if (files.length == 0) {

						// reset text area
						instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::resetDNDStyleEffects()();
						return;

					}

					console.log("Number of dropped file(s): " + files.length);

					var numFolder = 0;

					// save maximum allowed size
					var maximumSize = @org.gcube.portlets.widgets.fileupload.client.view.FileSubmit::MAX_SIZE_ATTACHED_FILE_MB;

					// msg for ignored (too big files)
					var ignoredFilesAlert = " file(s) ignored because larger than " + maximumSize + "MB"; 

					// number of ignored files
					var numberIgnoredFiles = 0;

				    // disable attach button
					instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::enableAttachButton(Z)(false);

					// for each dropped file
					for (var i = 0; i < files.length; i++) {

						var file = files[i];
						var fileSelected = file.name + ";";

						// be sure it is not a folder
						if (!isFolder(file)) {
							console.log("filesSelected: " + fileSelected);
							console.log("files: " + files);

							// check its size
							var fileSize = file.size / 1024 / 1024;

							console.log("File size is " + fileSize + "MB");

							if(fileSize > maximumSize){
								numberIgnoredFiles ++;
								continue;
							}

							// create new progress bar
							instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::showProgressDND()();

							// create request
							var xhr = new XMLHttpRequest();
							xhr.open(opts.type, opts.url, true);
							var formdata = new FormData(); 

							// append the file
							formdata.append("fileUpload", file);

							// send data
							xhr.send(formdata);

							console.log("File " + file.name + " sent at " + servletUrl);

						}else{ 

							// increment the number of skipped folders
							numFolder++;

						}
					}

					// enable attach button (the checkFile method will do this...)

					// alert the user that folder(s) can't be uploaded
					if(numFolder > 0){
						var msg;

						if(numFolder == files.length){

							msg = "Sorry but it's not possible to upload a folder!";
							instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::showAlert(Ljava/lang/String;)(msg);

							// reset text area
							instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::resetDNDStyleEffects()();
							return;

						}

                        // print ignored folders, if any
						var msg = "Ignored ";
						msg += numFolder > 1? numFolder+" folders": numFolder+" folder";
						msg+= " during upload.";
						console.log(msg);
						instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::showAlert(Ljava/lang/String;)(msg);
					}

					// alert for too large files
					if(numberIgnoredFiles > 0){
						var msg = numberIgnoredFiles + ignoredFilesAlert;

						if(numberIgnoredFiles == files.length){

							msg = file.name + " can't be uploaded because it is too large!";
							instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::showAlert(Ljava/lang/String;)(msg);

							// reset text area
							instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::resetDNDStyleEffects()();
							return;

						}

						var msg = numberIgnoredFiles + ignoredFilesAlert;
						console.log(msg);
						instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::showAlert(Ljava/lang/String;)(msg);
					}

					// reset text area
					instance.@org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm::resetDNDStyleEffects()();
				});

	}-*/;


	/**
	 * Check if DND could be enabled (i.e, it's supported by the browser)
	 * @return
	 */
	public static native boolean checkDNDAvailability()/*-{

		return window.FileReader;

	}-*/;

	/**
	 * Add DND style effect on drag over.
	 */
	private void addDNDStyleEffects() {
		// save current text (note that the DragOverEvent event can be fired several times)
		boolean conditionToSave = !shareTextArea.getText().equals(DROP_FILE_HERE_TEXT) &&  !shareTextArea.getText().equals(SHARE_UPDATE_TEXT);
		previousText =  conditionToSave ? shareTextArea.getText() : previousText;

		// change border properties
		shareTextArea.getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
		shareTextArea.getElement().getStyle().setBorderColor("rgba(82, 168, 236, 0.6)");
		shareTextArea.getElement().getStyle().setBorderWidth(2.5, Unit.PX);

		// change background color
		shareTextArea.getElement().getStyle().setBackgroundColor("rgba(82, 168, 236, 0.2)");

		// enlarge the window
		Document.get().getElementById("highlighterContainer").getStyle().setHeight(52, Unit.PX);
		Document.get().getElementById("highlighter").getStyle().setHeight(52, Unit.PX);
		Document.get().getElementById("postTextArea").getStyle().setHeight(52, Unit.PX);

		// add "Drop file here" text 
		shareTextArea.setText(DROP_FILE_HERE_TEXT);
		shareTextArea.setAlignment(TextAlignment.CENTER);
		shareTextArea.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		shareTextArea.getElement().getStyle().setPaddingTop(
				(Double.parseDouble(shareTextArea.getElement().getStyle().getHeight().replace("px", "")) + 20)/2.0, Unit.PX);

		// set the color of the text if needed to gray
		if(!previousText.equals(SHARE_UPDATE_TEXT))
			shareTextArea.getElement().getStyle().setColor("#999");

	}

	/**
	 * On dragLeave reset changes on the text area
	 */
	private void resetDNDStyleEffects() {

		// remove border properties
		shareTextArea.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		shareTextArea.getElement().getStyle().setBorderColor("#333");
		shareTextArea.getElement().getStyle().setBorderWidth(1, Unit.PX);

		// change back background color
		shareTextArea.getElement().getStyle().setBackgroundColor("transparent");

		// remove text "Drop file here" and reput the old text
		shareTextArea.setText(previousText);
		shareTextArea.setAlignment(TextAlignment.LEFT);

		// change text color if needed
		if(!previousText.equals(DROP_FILE_HERE_TEXT) && !previousText.equals(SHARE_UPDATE_TEXT))
			shareTextArea.getElement().getStyle().setColor("#333"); 

		// reset padding top
		shareTextArea.getElement().getStyle().setPaddingTop(4, Unit.PX);

		// reset font weight
		shareTextArea.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
	}

	/**
	 * Alert the user about something.
	 *
	 * @param msg the msg to show
	 */
	private void showAlert(String msg){

		Window.alert(msg);

	}

	/**
	 * Show progress bar and start the ProgressController
	 * @param e
	 */
	private void showProgressDND() {	
		uploadProgress.initializeDND();
		uploadProgress.setVisible(true);
	}

	/**
	 * Remove an attached file from the listOfAttachedFiles
	 * @param attachmentPreviewer
	 */
	public void removeAttachedFile(AttachmentPreviewer attachmentPreviewer) {

		Iterator<AttachedFile> iterator = listOfAttachedFiles.iterator();

		while (iterator.hasNext()) {
			AttachedFile attachedFile = (AttachedFile) iterator.next();
			if(attachedFile.getAtPrev().equals(attachmentPreviewer)){
				iterator.remove();
				break;
			}
		}

		// check the final number of attachments and if it's less than one, set to false
		// the save in workspace checkbox visibility
		if(numberOfAttachments() == 0)
			saveInWorkspaceCheckbox.setVisible(false);

	}

	/**
	 * Get the number of attached files (both uploaded and not).
	 * @return number of attached files
	 */
	private int numberOfAttachments(){

		return listOfAttachedFiles.size();

	}

	/**
	 * Retrieve the number of correctly uploaded attached files.
	 * @return number of attached files correctly uploaded.
	 */
	private int numberOfAttachmentsUploaded(){

		int counter = 0;
		for (AttachedFile attachedFile : listOfAttachedFiles) {

			if(attachedFile.isCorrectlyUploaded())
				counter ++;

		}
		return counter;
	}

	/**
	 * Is there any link preview?
	 * @return <true> if a link preview is already there
	 */
	private boolean isLinkPreviewPresent(){

		return linkPreviewer != null;

	}

	/**
	 * Asks the user if he/she wants to delete the link preview
	 * @return
	 */
	private boolean wantToDeleteLinkPreview(){

		return Window.confirm(DELETE_LINK_PREVIEW);
	}

	/**
	 * Enables or disable the attach button
	 * @param enable
	 */
	private void enableAttachButton(boolean enable){

		GWT.log("Enable attach button? " + enable);
		attachButton.setEnabled(enable);

	}
}
