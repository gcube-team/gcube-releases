package org.gcube.portlets.user.messages.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolderItem;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.FolderModel;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.messages.shared.InfoContactModel;
import org.gcube.portlets.user.messages.shared.MessageModel;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;


/**
 * The Class GWTMessagesBuilder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 13, 2017
 */
public class GWTMessagesBuilder {

	protected static final String IMAGE_SERVICE_URL = "ImageService";
	protected Logger logger = Logger.getLogger(MessagesServiceImpl.class);
	//	private final String UNKNOWN = "unknown";
	//	private final String FOLDER = "Folder";

	/**
	 * Instantiates a new GWT messages builder.
	 */
	public GWTMessagesBuilder() {
	}


	/**
	 * To date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	protected Date toDate(Calendar calendar)
	{
		if (calendar == null) return new Date(0);
		return calendar.getTime();

	}

	/**
	 * To date format to string.
	 *
	 * @param calendar the calendar
	 * @return the string
	 */
	protected String toDateFormatToString(Calendar calendar){

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM, yyyy HH:mm:ss z");

		Date resultdate = getDate(calendar);

		return dateFormat.format(resultdate);
	}


	/**
	 * To date format.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	protected Date toDateFormat(Calendar calendar){

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM, yyyy HH:mm:ss z");

		Date resultdate = getDate(calendar);

		try {

			resultdate = dateFormat.parse(dateFormat.format(resultdate));

		} catch (ParseException e) {
			e.printStackTrace();
			resultdate = new Date(0);
		}

		return resultdate;
	}


	/**
	 * Gets the date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	private Date getDate(Calendar calendar) {

		Date resultdate = null;

		if (calendar == null)
			resultdate = new Date(0);
		else
			resultdate = new Date(calendar.getTimeInMillis());

		return resultdate;

	}

	/**
	 * Sets the folder item type.
	 *
	 * @param fileModel the file model
	 * @param worspaceFolderItem the worspace folder item
	 * @return the file model
	 */
	protected FileModel setFolderItemType(FileModel fileModel, FolderItem worspaceFolderItem){

		switch(worspaceFolderItem.getFolderItemType())
		{
		case EXTERNAL_IMAGE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_IMAGE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_IMAGES);
			ExternalImage extImage = (ExternalImage) worspaceFolderItem;
			fileModel.setType(extImage.getMimeType());
			break;
		case EXTERNAL_FILE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_FILE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			ExternalFile extFile = (ExternalFile) worspaceFolderItem;
			fileModel.setType(extFile.getMimeType());
			break;
		case EXTERNAL_PDF_FILE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			ExternalPDFFile pdfExt = (ExternalPDFFile) worspaceFolderItem;
			fileModel.setType(pdfExt.getMimeType());
			break;
		case EXTERNAL_URL:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_LINKS);
			break;
		case REPORT_TEMPLATE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT_TEMPLATE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_REPORTS);
			break;
		case REPORT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_REPORTS);
			break;
		case QUERY:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.QUERY);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case TIME_SERIES:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.TIME_SERIES);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_TIMESERIES);
			break;
			//			case AQUAMAPS_ITEM:
				//				fileModel.setFolderItemType(GXTFolderItemTypeEnum.AQUAMAPS_ITEM);
			//				fileModel.setShortcutCategory(GXTCategoryItemInterface.BIODIVERSITY);
			//				break;
		case PDF_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.PDF_DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case IMAGE_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.IMAGE_DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_IMAGES);
			GCubeItem imgDoc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
			try {
				fileModel.setType(imgDoc.getMimeType());
			} catch (InternalErrorException e) {
				logger.error("IMAGE_DOCUMENT InternalErrorException when getting MimeType on "+fileModel.getIdentifier());
			}
			break;
		case DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			GCubeItem doc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
			try {
				fileModel.setType(doc.getMimeType());
			} catch (InternalErrorException e) {
				logger.error("DOCUMENT InternalErrorException when getting MimeType on "+fileModel.getIdentifier());
			}
			break;
		case URL_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.URL_DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case METADATA:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.METADATA);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case GCUBE_ITEM:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.GCUBE_ITEM);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_GCUBE_ITEMS);
			break;
		default:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.UNKNOWN_TYPE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_UNKNOWN);
			fileModel.setType(GXTFolderItemTypeEnum.UNKNOWN_TYPE.toString());
			//				logger.trace("**************************************UNKNOWN******* filemodel "+ fileModel.getName());
			break;
		}

		return fileModel;
	}

	/**
	 * Builds the gxt list file model item for attachs.
	 *
	 * @param listWorspaceItems the list worspace items
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	protected List<FileModel> buildGXTListFileModelItemForAttachs(List<WorkspaceItem> listWorspaceItems) throws InternalErrorException
	{

		List<FileModel> listFileModel = new ArrayList<FileModel>();

		for (WorkspaceItem item : listWorspaceItems){

			FileModel fileModel = null;

			switch (item.getType()) {

			case FOLDER:
				fileModel = new FolderModel(item.getId(), item.getName(), true);
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
				break;
			case FOLDER_ITEM:
				fileModel = new FileModel(item.getId(), item.getName(), false);
				FolderItem folderItem = (FolderItem) item;
				fileModel = setFolderItemType(fileModel, folderItem);
				break;
			case SHARED_FOLDER:
				fileModel = new FolderModel(item.getId(), item.getName(), true);
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
				break;
			default:
				logger.error("gxt conversion return null for item "+item.getName());
				break;
			}

			listFileModel.add(fileModel);
		}
		return listFileModel;
	}



	/**
	 * Builds the gxt file model item.
	 *
	 * @param item the item
	 * @param parentFolderModel the parent folder model
	 * @return the file model
	 * @throws InternalErrorException the internal error exception
	 */
	protected FileModel buildGXTFileModelItem(WorkspaceItem item, FileModel parentFolderModel) throws InternalErrorException
	{

		FileModel fileModel = null;

		switch (item.getType()) {

			case FOLDER:

				fileModel = new FolderModel(item.getId(), item.getName(), parentFolderModel, true);
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
				break;

			case FOLDER_ITEM:
				//				fileModel = new FileModel(item.getId(), item.getName(), item.getPath(), (FolderModel) parentFolderModel, false);
				fileModel = new FileModel(item.getId(), item.getName(), parentFolderModel, false);
				FolderItem folderItem = (FolderItem) item;
				fileModel = setFolderItemType(fileModel, folderItem);
				break;

			case SHARED_FOLDER:

				fileModel = new FolderModel(item.getId(), item.getName(), parentFolderModel, true);
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
				break;

			default:
				logger.error("gxt conversion return null for item "+item.getName());
				break;
		}

		return fileModel;

	}

	/**
	 * Sets the folder item type for search.
	 *
	 * @param fileModel the file model
	 * @param searchFolderItem the search folder item
	 * @return the file model
	 */
	protected FileModel setFolderItemTypeForSearch(FileModel fileModel, SearchFolderItem searchFolderItem){

		switch(searchFolderItem.getFolderItemType())
		{
		case EXTERNAL_IMAGE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_IMAGE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_IMAGES);
			fileModel.setType(searchFolderItem.getMimeType());
			break;
		case EXTERNAL_FILE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_FILE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			fileModel.setType(searchFolderItem.getMimeType());
			break;
		case EXTERNAL_PDF_FILE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			fileModel.setType(searchFolderItem.getMimeType());
			break;
		case EXTERNAL_URL:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_LINKS);
			break;
		case REPORT_TEMPLATE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT_TEMPLATE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_REPORTS);
			break;
		case REPORT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_REPORTS);
			break;
		case QUERY:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.QUERY);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case TIME_SERIES:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.TIME_SERIES);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_TIMESERIES);
			break;
			//			case AQUAMAPS_ITEM:
				//				fileModel.setFolderItemType(GXTFolderItemTypeEnum.AQUAMAPS_ITEM);
			//				fileModel.setShortcutCategory(GXTCategoryItemInterface.BIODIVERSITY);
			//				break;
		case PDF_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.PDF_DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case IMAGE_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.IMAGE_DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_IMAGES);
			fileModel.setType(searchFolderItem.getMimeType());
			break;
		case DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			fileModel.setType(searchFolderItem.getMimeType());
			break;
		case URL_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.URL_DOCUMENT);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case METADATA:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.METADATA);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_DOCUMENTS);
			break;
		case GCUBE_ITEM:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.GCUBE_ITEM);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_GCUBE_ITEMS);
			break;
		default:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.UNKNOWN_TYPE);
			fileModel.setShortcutCategory(GXTCategoryItemInterface.SMF_UNKNOWN);
			fileModel.setType(GXTFolderItemTypeEnum.UNKNOWN_TYPE.toString());
			//				logger.trace("**************************************UNKNOWN******* filemodel "+ fileModel.getName());
			break;
		}

		return fileModel;

	}

	/**
	 * Builds the gxt list contacts model.
	 *
	 * @param listUsers the list users
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<InfoContactModel> buildGXTListContactsModel(List<User> listUsers) throws Exception {
		UserManager um = new LiferayUserManager();
		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		for(User user: listUsers){

			String fullName = um.getUserByUsername(user.getPortalLogin()).getFullname();
			listContactsModel.add(new InfoContactModel(user.getId(), user.getPortalLogin(), fullName));

		}
		return listContactsModel;
	}

	/**
	 * Builds the gxt list message model for grid.
	 *
	 * @param listMessages the list messages
	 * @param typeMessages the type messages
	 * @param isPortalMode the is portal mode
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<MessageModel> buildGXTListMessageModelForGrid(List<WorkspaceMessage> listMessages, String typeMessages, boolean isPortalMode) throws Exception {

		List<MessageModel> listMessageModel = new ArrayList<MessageModel>();
		logger.trace("converting messages");

		for(WorkspaceMessage mess: listMessages){
			List<WorkspaceItem> listAttachs = mess.getAttachments();
			List<String> listAttachsNames = new ArrayList<String>();

			logger.debug("MESSAGE "+typeMessages+": [id: " +  mess.getId()+ ", subject: "+mess.getSubject()+"]");

			if(listAttachs!=null){
				for(WorkspaceItem attach: listAttachs){
					listAttachsNames.add(attach.getName());
					logger.trace("Received attach: "+ attach.getName() + " " +attach.getId());
				}
			}

			listMessageModel.add(new MessageModel(mess.getId(), mess.getSubject(), buildGXTInfoContactModel(mess.getSender(), isPortalMode), toDate(mess.getSendTime()), listAttachsNames, typeMessages, mess.isRead()));
		}
		logger.trace("returning "+listMessageModel.size()+ " messages");
		return listMessageModel;
	}

	/**
	 * Builds the gxt message model.
	 *
	 * @param mess the mess
	 * @param listWorkspaceItems the list workspace items
	 * @param messageType the message type
	 * @param isPortalMode the is portal mode
	 * @return the message model
	 * @throws Exception the exception
	 */
	public MessageModel buildGXTMessageModel(WorkspaceMessage mess, List<WorkspaceItem> listWorkspaceItems, String messageType, boolean isPortalMode) throws Exception {

		List<FileModel> listAttachs = buildGXTListFileModelItemForAttachs(listWorkspaceItems);
		Map<String, String> contacts = getFullNameListFromMessage(mess, isPortalMode);
		return new MessageModel(mess.getId(), mess.getSubject(), buildGXTInfoContactModel(mess.getSender(), isPortalMode), toDate(mess.getSendTime()),mess.getBody(), listAttachs, contacts, messageType, mess.isRead());
	}


	/**
	 * Gets the full name list from message.
	 *
	 * @param mess the mess
	 * @param isPortalMode the is portal mode
	 * @return a Map <String, String> username - fullname (in dev mode: username)
	 */
	protected Map<String, String> getFullNameListFromMessage(WorkspaceMessage mess, boolean isPortalMode){
		UserManager um = new LiferayUserManager();
		if(mess.getAddresses()==null || mess.getAddresses().isEmpty())
			return new HashMap<String, String>(1);

		Map<String, String> hashAddresses = new HashMap<String, String>(mess.getAddresses().size());

		if(isPortalMode){
			for (String login : mess.getAddresses()) {

				String fullName = null;
				try {
					fullName = um.getUserByUsername(login).getFullname();
				} catch (UserManagementSystemException | UserRetrievalFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(fullName!=null && !fullName.isEmpty())
					hashAddresses.put(login, fullName);
				else
					hashAddresses.put(login, login);
			}
		}
		else{ //IS NOT PORTAL
			for (String login : mess.getAddresses())
				hashAddresses.put(login, login);
		}

		return hashAddresses;
	}


	/**
	 * Builds the gxt info contact model.
	 *
	 * @param user the user
	 * @param isPortalMode the is portal mode
	 * @return the info contact model
	 * @throws Exception the exception
	 */
	private InfoContactModel buildGXTInfoContactModel(User user, boolean isPortalMode) throws Exception{
		if (isPortalMode) {
			try{
				UserManager um = new LiferayUserManager();
				String fullName = um.getUserByUsername(user.getPortalLogin()).getFullname();
				return new InfoContactModel(user.getId(), user.getPortalLogin(), fullName);
			}catch(Exception e){
				logger.warn("An error occurred during getting full name from LF returning user login: "+user.getPortalLogin());
				return new InfoContactModel(user.getId(), user.getPortalLogin(), user.getPortalLogin());
			}
		} else
			return new InfoContactModel(user.getId(), user.getPortalLogin(), user.getPortalLogin());
	}
}
