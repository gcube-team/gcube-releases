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
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
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
import org.gcube.portlets.user.messages.server.util.UserUtil;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.FolderModel;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.messages.shared.InfoContactModel;
import org.gcube.portlets.user.messages.shared.MessageModel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GWTMessagesBuilder {

	protected static final String IMAGE_SERVICE_URL = "ImageService";
	protected Logger logger = Logger.getLogger(MessagesServiceImpl.class);
	//	private final String UNKNOWN = "unknown";
	//	private final String FOLDER = "Folder";

	public GWTMessagesBuilder() {
	}


	protected Date toDate(Calendar calendar)
	{
		if (calendar == null) return new Date(0);
		return calendar.getTime();

	}

	protected String toDateFormatToString(Calendar calendar){

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM, yyyy HH:mm:ss z");

		Date resultdate = getDate(calendar);

		return dateFormat.format(resultdate);
	}


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


	private Date getDate(Calendar calendar) {

		Date resultdate = null;

		if (calendar == null)
			resultdate = new Date(0);
		else
			resultdate = new Date(calendar.getTimeInMillis());

		return resultdate;

	}

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



	protected FileModel buildGXTFileModelItem(WorkspaceItem item, FileModel parentFolderModel) throws InternalErrorException
	{

		FileModel fileModel = null;

		switch (item.getType()) {

			case FOLDER: 
				
				fileModel = new FolderModel(item.getId(), item.getName(), (FolderModel) parentFolderModel, true);
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
				break;
	
			case FOLDER_ITEM: 
				//				fileModel = new FileModel(item.getId(), item.getName(), item.getPath(), (FolderModel) parentFolderModel, false);
				fileModel = new FileModel(item.getId(), item.getName(), (FolderModel) parentFolderModel, false);
				FolderItem folderItem = (FolderItem) item;
				fileModel = setFolderItemType(fileModel, folderItem);
				break;
	
			case SHARED_FOLDER: 
				
				fileModel = new FolderModel(item.getId(), item.getName(), (FolderModel) parentFolderModel, true);
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
				break;
			
			default:
				logger.error("gxt conversion return null for item "+item.getName());
				break;
		}

		return fileModel;

	}

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

	public List<InfoContactModel> buildGXTListContactsModel(List<User> listUsers) throws InternalErrorException {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		for(User user: listUsers){
			String fullName = UserUtil.getUserFullName(user);
			listContactsModel.add(new InfoContactModel(user.getId(), user.getPortalLogin(), fullName));

		}
		//		listContactsModel.add(new InfoContactModel("1", "Federico"));
		//		listContactsModel.add(new InfoContactModel("2", "Antonio"));
		//		listContactsModel.add(new InfoContactModel("3", "Francesco"));

		return listContactsModel;
	}

	public List<MessageModel> buildGXTListMessageModelForGrid(List<WorkspaceMessage> listMessages, String typeMessages, boolean isPortalMode) throws InternalErrorException {

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

	public MessageModel buildGXTMessageModel(WorkspaceMessage mess, List<WorkspaceItem> listWorkspaceItems, String messageType, boolean isPortalMode) throws InternalErrorException {
		
		List<FileModel> listAttachs = buildGXTListFileModelItemForAttachs(listWorkspaceItems);
		Map<String, String> contacts = getFullNameListFromMessage(mess, isPortalMode);
		
		/*String publicLinks = getPublicLinksForAttachs(mess);
		String body = mess.getBody();
		if(publicLinks!=null && !publicLinks.isEmpty())
			body+=publicLinks;
		*/
		return new MessageModel(mess.getId(), mess.getSubject(), buildGXTInfoContactModel(mess.getSender(), isPortalMode), toDate(mess.getSendTime()),mess.getBody(), listAttachs, contacts, messageType, mess.isRead());
	}
	
	/*
	protected String getPublicLinksForAttachs(WorkspaceMessage mess){
		
		try {
			StringBuilder builder = new StringBuilder();
			List<WorkspaceItem> listAttachs = mess.getAttachments();
			
			if(listAttachs!=null && listAttachs.size()>0){
				builder.append("\n\n\nThe following ");
				String msg = listAttachs.size()>1?"files were attached:":"file was attached:";
				builder.append(msg+"\n");
				for (WorkspaceItem workspaceItem : listAttachs) {
					
					if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
						FolderItem folderItem = (FolderItem) workspaceItem;
						String publicLink = folderItem.getPublicLink(true);
						builder.append(workspaceItem.getName() + " ("+publicLink+")");
						builder.append("\n");
					}
				}
			}
			logger.info("returning public links: "+builder.toString());
			return builder.toString();
		} catch (InternalErrorException e) {
			logger.warn("An error occurred when creating public links for attachs, skipping", e);
			return null;
		}
	}*/
	
	/**
	 * 
	 * @param mess
	 * @param isPortalMode
	 * @return a Map <String, String> username - fullname (in dev mode: username)
	 */
	protected Map<String, String> getFullNameListFromMessage(WorkspaceMessage mess, boolean isPortalMode){
		
		if(mess.getAddresses()==null || mess.getAddresses().isEmpty())
			return new HashMap<String, String>(1);
		
		Map<String, String> hashAddresses = new HashMap<String, String>(mess.getAddresses().size());
		
		if(isPortalMode){
			for (String login : mess.getAddresses()) {
				
				String fullName = UserUtil.getUserFullName(login);
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


	private InfoContactModel buildGXTInfoContactModel(User user, boolean isPortalMode) throws InternalErrorException{
		if (isPortalMode) {
			String fullName = UserUtil.getUserFullName(user);
			return new InfoContactModel(user.getId(), user.getPortalLogin(), fullName);
		} else 
			return new InfoContactModel(user.getId(), user.getPortalLogin(), user.getPortalLogin());
	}
}
