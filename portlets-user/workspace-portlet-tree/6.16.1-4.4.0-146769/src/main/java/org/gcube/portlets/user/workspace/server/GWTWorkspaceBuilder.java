package org.gcube.portlets.user.workspace.server;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.gcube.DocumentAlternativeLink;
import org.gcube.common.homelibary.model.items.gcube.DocumentMetadata;
import org.gcube.common.homelibary.model.items.gcube.DocumentPartLink;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibary.model.versioning.WorkspaceVersion;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryAdd;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryCreate;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryCut;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryDisabledPublicAccess;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryEnabledPublicAccess;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryPaste;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRead;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRemoval;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRenaming;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRestore;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryShare;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryUnshare;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryUpdate;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolderItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.model.FileDetailsModel;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.model.FolderGridModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.model.MessageModel;
import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.model.SmartFolderModel;
import org.gcube.portlets.user.workspace.client.util.ImageRequestType;
import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalImage;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalUrl;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTDocumentMetadata;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTImageDocument;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTUrlDocument;
import org.gcube.portlets.user.workspace.server.util.UserUtil;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL.USER_TYPE;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingEntryType;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.thoughtworks.xstream.XStream;


/**
 * The Class GWTWorkspaceBuilder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 30, 2016
 */
public class GWTWorkspaceBuilder {

	protected static final String IMAGE_SERVICE_URL = "ImageService";
	protected static Logger logger = Logger.getLogger(GWTWorkspaceBuilder.class);
	private InfoContactModel userLogged;
	protected static HashMap<String, InfoContactModel> hashTestUser = null;

	/**
	 * Instantiates a new GWT workspace builder.
	 */
	public GWTWorkspaceBuilder() {
	}


	/**
	 * Used in test mode.
	 *
	 * @return the hash test users
	 */
	public static HashMap<String, InfoContactModel> getHashTestUsers(){

		if(hashTestUser==null){
			hashTestUser = new HashMap<String, InfoContactModel>();

			//USERS
			hashTestUser.put("federico.defaveri", new InfoContactModel("federico.defaveri", "federico.defaveri", "Federico de Faveri",false));
			hashTestUser.put("antonio.gioia", new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia",false));
			hashTestUser.put("fabio.sinibaldi", new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi",false));
			hashTestUser.put("pasquale.pagano", new InfoContactModel("pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",false));
			hashTestUser.put("valentina.marioli", new InfoContactModel("valentina.marioli", "valentina.marioli", "Valentina Marioli",false));
			hashTestUser.put("roberto.cirillo", new InfoContactModel("roberto.cirillo", "roberto.cirillo", "Roberto Cirillo",false));
			hashTestUser.put("francesco.mangiacrapa", new InfoContactModel("francesco.mangiacrapa", "francesco.mangiacrapa", "Francesco Mangiacrapa",false));
			hashTestUser.put("massimiliano.assante", new InfoContactModel("massimiliano.assante", "massimiliano.assante", "Massimiliano Assante",false));

//			try{
//				logger.info("Sleeping for testing...");
//				Thread.sleep(30000);
//				logger.info("Alive again");
//			}catch(Exception e){
//
//			}
			//GROUPS
//			hashTestUser.put("/gcube/devsec/devVRE", new InfoContactModel("/gcube/devsec/devVRE", "/gcube/devsec/devVRE", "",true));
//			hashTestUser.put("/gcube/devsec/gcube-test-test", new InfoContactModel("/gcube/devsec/gcube-test-test", "/gcube/gcube-test-test", "",true));
		}

		return hashTestUser;
	}


	/**
	 * To date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	public static Date toDate(Calendar calendar){
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
	 * @param calendar
	 *            the calendar
	 * @return the date
	 */
	protected Date toDateFormat(Calendar calendar) {

		SimpleDateFormat dateFormat =
			new SimpleDateFormat("dd-MM, yyyy HH:mm:ss z");
		Date resultdate = getDate(calendar);
		try {
			resultdate = dateFormat.parse(dateFormat.format(resultdate));
		}
		catch (ParseException e) {
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
	 * Builds the gwt properties.
	 *
	 * @param metadata the metadata
	 * @return the GWT properties
	 * @throws InternalErrorException the internal error exception
	 */
	protected GWTProperties buildGWTProperties(Properties metadata) throws InternalErrorException
	{
		//FIXME temp solution
//		GWTProperties gwtProperties = new GWTProperties(metadata.getId(), metadata.getProperties());
		return new GWTProperties();
	}


	/**
	 * Builds the image url.
	 *
	 * @param id the id
	 * @param currentGroupId the current group id
	 * @param currUserId the curr user id
	 * @return the string
	 */
	protected String buildImageUrl(String id, String currentGroupId, String currUserId)
	{
		return buildImageServiceUrl(id, ImageRequestType.IMAGE, currentGroupId, currUserId);
	}



	/**
	 * Builds the thumbnail url.
	 *
	 * @param id the id
	 * @param currentGroupId the current group id
	 * @param currUserId the curr user id
	 * @return the string
	 */
	protected String buildThumbnailUrl(String id, String currentGroupId, String currUserId)
	{
		return buildImageServiceUrl(id, ImageRequestType.THUMBNAIL, currentGroupId, currUserId);
	}


	/**
	 * Builds the image service url.
	 *
	 * @param id the id
	 * @param requestType the request type
	 * @param currentGroupId the current group id read from PortalContext
	 * @param currUserId the curr user id
	 * @return the string
	 */
	protected String buildImageServiceUrl(String id, ImageRequestType requestType, String currentGroupId, String currUserId){
		StringBuilder sb = new StringBuilder();
		sb.append(IMAGE_SERVICE_URL);
		sb.append("?id=");
		sb.append(id);
		sb.append("&type=");
		sb.append(requestType.toString());
		sb.append("&"+ConstantsExplorer.CURRENT_CONTEXT_ID+"=");
		sb.append(currentGroupId);
//		sb.append("&"+ConstantsExplorer.CURRENT_USER_ID+"=");
//		sb.append(currUserId);
		sb.append("&random=");
		sb.append(UUID.randomUUID().toString());
		return sb.toString();
	}

	/**
	 * Builds the gwt workspace image.
	 *
	 * @param item the item
	 * @param isInteralImage the is interal image
	 * @param fullDetails the full details
	 * @param currentGroupId the current group id
	 * @param currentUserId the current user id
	 * @return the GWT workspace item
	 * @throws InternalErrorException the internal error exception
	 */
	@SuppressWarnings("unchecked")
	protected GWTWorkspaceItem buildGWTWorkspaceImage(WorkspaceItem item, boolean isInteralImage, boolean fullDetails, String currentGroupId, String currentUserId) throws InternalErrorException
	{

		GWTWorkspaceItem gwtImage;

		GWTProperties gwtProperties = buildGWTProperties(item.getProperties());
//
		if(isInteralImage){

			GCubeItem image = (GCubeItem) item; //Cast OLD Image Document

			List<DocumentAlternativeLink> links = (List<DocumentAlternativeLink>) new XStream().fromXML(image.getProperties().getPropertyValue(NodeProperty.ALTERNATIVES.toString()));
			List<DocumentPartLink> parts = (List<DocumentPartLink>) new XStream().fromXML(image.getProperties().getPropertyValue(NodeProperty.PARTS.toString()));
			Map<String, DocumentMetadata> metadatas = (Map<String, DocumentMetadata>) new XStream().fromXML(image.getProperties().getPropertyValue(NodeProperty.METADATA.toString()));
			Map<String, GWTDocumentMetadata> gwtmetadatas = getMetadatas(metadatas, image.getId());

			gwtImage = new GWTImageDocument(
					toDate(image.getCreationTime()),
					image.getId(),
					gwtProperties,
					image.getName(),
					image.getOwner().getPortalLogin(),
					image.getDescription(),
					toDate(image.getLastModificationTime()),
					GWTWorkspaceItemAction.valueOf(image.getLastAction().toString()),
					null,
					buildImageUrl(image.getId(), currentGroupId, currentUserId),
					buildThumbnailUrl(image.getId(), currentGroupId, currentUserId),
					Integer.parseInt(image.getProperties().getPropertyValue(NodeProperty.IMAGE_WIDTH.toString())),
					Integer.parseInt(image.getProperties().getPropertyValue(NodeProperty.IMAGE_HEIGHT.toString())),
					Integer.parseInt(image.getProperties().getPropertyValue(NodeProperty.THUMBNAIL_WIDTH.toString())),
					Integer.parseInt(image.getProperties().getPropertyValue(NodeProperty.THUMBNAIL_HEIGHT.toString())),
					-1,
					image.getLength(),
					image.getMimeType(),
					image.getProperties().getPropertyValue(NodeProperty.OID.toString()),
					gwtmetadatas,
					new LinkedHashMap<String, String>(),
					image.getProperties().getPropertyValue(NodeProperty.COLLECTION_NAME.toString()),
					links.size(),
					parts.size());
		}

		else{

			ExternalImage image = (ExternalImage) item; //Cast External Document

			if(fullDetails){

				gwtImage = new GWTExternalImage(
						toDate(image.getCreationTime()),
						image.getId(),
						gwtProperties,
						image.getName(),
						image.getOwner().getPortalLogin(),
						image.getDescription(),
						toDate(image.getLastModificationTime()),
						GWTWorkspaceItemAction.valueOf(image.getLastAction().toString()),
						null, //parent
						buildImageUrl(image.getId(), currentGroupId, currentUserId),
						buildThumbnailUrl(image.getId(), currentGroupId, currentUserId),
						image.getWidth(),
						image.getHeight(),
						image.getLength(),
						image.getThumbnailWidth(),
						image.getThumbnailHeight(),
						-1,
						image.getMimeType());
			}else{

				gwtImage = new GWTExternalImage(
						buildImageUrl(image.getId(), currentGroupId, currentUserId),
						buildThumbnailUrl(image.getId(), currentGroupId, currentUserId),
						image.getWidth(),
						image.getHeight(),
						image.getLength(),
						image.getThumbnailWidth(),
						image.getThumbnailHeight(),
						-1,
						image.getMimeType());
			}
		}
		return gwtImage;
	}

	/**
	 * Builds the gwt external url.
	 *
	 * @param url the url
	 * @param parent the parent
	 * @return the GWT external url
	 * @throws InternalErrorException the internal error exception
	 */
	protected GWTExternalUrl buildGWTExternalUrl(ExternalUrl url, GWTWorkspaceFolder parent) throws InternalErrorException
	{
		GWTProperties gwtProperties = buildGWTProperties(url.getProperties());
		GWTExternalUrl gwtUrl = new GWTExternalUrl(
				toDate(url.getCreationTime()),
				url.getId(),
				gwtProperties,
				url.getName(),
				url.getOwner().getPortalLogin(),
				url.getDescription(),
				toDate(url.getLastModificationTime()),
				GWTWorkspaceItemAction.valueOf(url.getLastAction().toString()),
				parent,
				url.getLength(),
				url.getUrl());

		return gwtUrl;
	}


	/**
	 * Builds the gwt worspace url.
	 *
	 * @param item the item
	 * @param isInternalUrl the is internal url
	 * @param fullDetails the full details
	 * @return the GWT workspace item
	 * @throws InternalErrorException the internal error exception
	 */
	@SuppressWarnings("unchecked")
	protected GWTWorkspaceItem buildGWTWorspaceUrl(WorkspaceItem item, boolean isInternalUrl, boolean fullDetails) throws InternalErrorException
	{

		GWTWorkspaceItem gwtUrl = null;
		GWTProperties gwtProperties = buildGWTProperties(item.getProperties());

		if(isInternalUrl){

			GCubeItem document = (GCubeItem) item; //Cast OLD UrlDocument
//			UrlDocument document = (UrlDocument) item; //Cast
			List<DocumentAlternativeLink> links = (List<DocumentAlternativeLink>) new XStream().fromXML(document.getProperties().getPropertyValue(NodeProperty.ALTERNATIVES.toString()));
			List<DocumentPartLink> parts = (List<DocumentPartLink>) new XStream().fromXML(document.getProperties().getPropertyValue(NodeProperty.PARTS.toString()));
			Map<String, DocumentMetadata> metadatas = (Map<String, DocumentMetadata>) new XStream().fromXML(document.getProperties().getPropertyValue(NodeProperty.METADATA.toString()));
			Map<String, GWTDocumentMetadata> gwtmetadatas = getMetadatas(metadatas, document.getId());

			gwtUrl = new GWTUrlDocument(
				toDate(document.getCreationTime()),
				document.getId(),
				gwtProperties,
				document.getName(),
				document.getOwner().getPortalLogin(),
				document.getDescription(),
				toDate(document.getLastModificationTime()),
				GWTWorkspaceItemAction.valueOf(document.getLastAction().toString()),
				null,
				document.getLength(),
				document.getProperties().getPropertyValue(NodeProperty.OID.toString()),
				document.getMimeType(),
				gwtmetadatas,
				new LinkedHashMap<String, String>(),
				document.getProperties().getPropertyValue(NodeProperty.COLLECTION_NAME.toString()),
				links.size(),
				parts.size(),
				document.getProperties().getPropertyValue(NodeProperty.URL.toString()));
		}
		else{

			ExternalUrl document = (ExternalUrl) item; //Cast

			if(fullDetails){
				gwtUrl = new GWTExternalUrl(
				toDate(document.getCreationTime()),
				document.getId(),
				gwtProperties,
				document.getName(),
				document.getOwner().getPortalLogin(),
				document.getDescription(),
				toDate(document.getLastModificationTime()),
				GWTWorkspaceItemAction.valueOf(document.getLastAction().toString()),
				null,
				document.getLength(),
				document.getUrl());
			}
			else
				gwtUrl = new GWTExternalUrl(document.getUrl());
		}
		return gwtUrl;
	}


	/**
	 * Gets the metadatas.
	 *
	 * @param metadatas the metadatas
	 * @param documentId the document id
	 * @return the metadatas
	 */
	protected Map<String, GWTDocumentMetadata> getMetadatas(Map<String, DocumentMetadata> metadatas, String documentId)
	{
		Map<String, GWTDocumentMetadata> gwtmetadatas = new LinkedHashMap<String, GWTDocumentMetadata>();

		for (Entry<String, DocumentMetadata> metadata : metadatas.entrySet()) gwtmetadatas.put(metadata.getKey(), getMetadata(metadata.getValue(), documentId));

		return gwtmetadatas;
	}

	/**
	 * Gets the metadata.
	 *
	 * @param metadata the metadata
	 * @param documentId the document id
	 * @return the metadata
	 */
	protected GWTDocumentMetadata getMetadata(DocumentMetadata metadata, String documentId)
	{
		return new GWTDocumentMetadata(metadata.getSchemaName(),
				"MetadataService?id="+documentId+"&schema="+metadata.getSchemaName()+"&type="+MetadataFormat.HTML,
				"MetadataService?id="+documentId+"&schema="+metadata.getSchemaName()+"&type="+MetadataFormat.RAW_XML_AS_HTML,
				"MetadataService?id="+documentId+"&schema="+metadata.getSchemaName()+"&type="+MetadataFormat.FORMATTED_XML);
	}

	/**
	 * Builds the workspace file model root.
	 *
	 * @param workspaceRoot the workspace root
	 * @return the folder model
	 * @throws InternalErrorException the internal error exception
	 */
	public FolderModel buildWorkspaceFileModelRoot(WorkspaceFolder workspaceRoot) throws InternalErrorException {

		logger.trace("workspace id: "+ workspaceRoot.getId());
		logger.trace("workspace name:  "+ workspaceRoot.getName());
		logger.trace("workspace path "+ workspaceRoot.getPath());

		FolderModel root = new FolderModel(workspaceRoot.getId(),workspaceRoot.getName(),null, true, workspaceRoot.isShared(), false, workspaceRoot.isPublic());
		root.setIsRoot(true);

		return root;
	}


	/**
	 * Builds the gxt list file model item.
	 *
	 * @param workspaceFolder the workspace folder
	 * @param parentFolderModel the parent folder model
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	protected List<FileModel> buildGXTListFileModelItem(WorkspaceItem workspaceFolder, FileModel parentFolderModel) throws InternalErrorException
	{

		List<FileModel> listFileModel = new ArrayList<FileModel>();

		List<WorkspaceItem> listItems = (List<WorkspaceItem>) workspaceFolder.getChildren();

		if(listItems!=null)
			logger.trace("HL return "+listItems.size()+  " items, converting...");

		//TEST TIME
//		Long startTime =  System.currentTimeMillis();
//		Long endTime = System.currentTimeMillis() - startTime;
//		String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
//		logger.trace("tree getChildren() returning "+listItems.size()+" elements in " + time);
//		startTime =  System.currentTimeMillis();

		for (WorkspaceItem item : listItems){
			logger.debug("item: "+item.getName()+  "is root? " +item.isRoot());
			listFileModel.add(buildGXTFileModelItem(item,parentFolderModel));
		}

		//TEST TIME
//		endTime = System.currentTimeMillis() - startTime;
//		time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
//		logger.trace("tree gxt objects getChildren() returning "+listItems.size()+" elements in " + time);

		return listFileModel;

//		return buildGXTListFileModelItem((WorkspaceItem) workspaceFolder.getChildren(), parentFolderModel);
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

				case FOLDER: fileModel = new FolderModel(item.getId(), item.getName(), true, false);
					fileModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel());
					break;

				case FOLDER_ITEM:
					fileModel = new FileModel(item.getId(), item.getName(), false);
					FolderItem folderItem = (FolderItem) item;
					fileModel = setFolderItemType(fileModel, folderItem);
					break;

				 case SHARED_FOLDER:

					WorkspaceSharedFolder shared = (WorkspaceSharedFolder) item;
				    String name = shared.isVreFolder()?shared.getDisplayName():item.getName();

			    	fileModel = new FolderModel(item.getId(), name, true, shared.isVreFolder());
			    	fileModel.setShared(true);
					fileModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.getLabel());

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
	 * Builds the gxt info contacts from portal logins.
	 *
	 * @param listPortalLogin the list portal login
	 * @return the list
	 */
	public List<InfoContactModel> buildGxtInfoContactsFromPortalLogins(List<String> listPortalLogin){

		List<InfoContactModel> listContact = new ArrayList<InfoContactModel>();

		if(listPortalLogin!=null && listPortalLogin.size()>0){
			for (String portalLogin : listPortalLogin)
				listContact.add(buildGxtInfoContactFromPortalLogin(portalLogin));
		}

		logger.debug("buildGxtInfoContactsFromPortalLogins return: "+ listContact.size());
		return listContact;
	}

	/**
	 * Builds the gxt info contact from portal login.
	 *
	 * @param portalLogin the portal login
	 * @return the info contact model
	 */
	protected InfoContactModel buildGxtInfoContactFromPortalLogin(String portalLogin){

		if(portalLogin==null){
			logger.warn("portal login is null, return empty");
			portalLogin = "";
		}

		return new InfoContactModel(portalLogin, portalLogin, UserUtil.getUserFullName(portalLogin), false);
	}


	/**
	 * Used in test mode.
	 *
	 * @param listPortalLogin the list portal login
	 * @return the list
	 */
	protected List<InfoContactModel> buildGxtInfoContactFromPortalLoginTestMode(List<String> listPortalLogin){

		List<InfoContactModel> listContact = new ArrayList<InfoContactModel>();

		for (String portalLogin : listPortalLogin)
			listContact.add(getHashTestUsers().get(portalLogin));

		return listContact;
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
//		logger.debug("buildGXTFileModelItem: "+item.getName());

		FileModel fileModel = null;

		/*
		if(parentFolderModel!=null)
			parentFolderModel = parentFolderModel;
		 */

		switch (item.getType()) {

			case FOLDER:
				boolean isPublic = ((WorkspaceFolder) item).isPublic();
				fileModel = new FolderModel(item.getId(), item.getName(), parentFolderModel, true, false, false, isPublic);
				if(isPublic)
					fileModel.setType(GXTFolderItemTypeEnum.FOLDER_PUBLIC.getLabel());
				else
					fileModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel());

				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel());
				fileModel.setShareable(true);
				fileModel.setDescription(item.getDescription());
				break;

			case FOLDER_ITEM:

				fileModel = new FileModel(item.getId(), item.getName(), parentFolderModel, false, false);
				FolderItem folderItem = (FolderItem) item;
				fileModel = setFolderItemType(fileModel, folderItem);
				fileModel.setShareable(true);
				break;

		    case SHARED_FOLDER:
		    	WorkspaceSharedFolder shared = (WorkspaceSharedFolder) item;
		    	isPublic = ((WorkspaceFolder) shared).isPublic();
		    	String name = shared.isVreFolder()?shared.getDisplayName():item.getName();
		    	fileModel = new FolderModel(item.getId(), name, parentFolderModel, true, true, shared.isVreFolder(), isPublic);
		    	if(isPublic)
					fileModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED_PUBLIC.toString());
				else
					fileModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.toString());

				fileModel.setShareable(true);
				fileModel.setDescription(item.getDescription());
				break;

			default:
				logger.error("gxt conversion return null for item "+item.getName());
				break;

		}

		//SET SHARE POLICY
		if(parentFolderModel!=null && parentFolderModel.isShared()){
			fileModel.setShared(true);
			fileModel.setShareable(false); //UPDATED TO CHANGE PERMISSIONS TO SHARED SUBFOLDERS
		}else if(parentFolderModel==null && item.isShared()){  //ADDED TO FIX #1808
			fileModel.setShared(true);
			if(item.getParent()!=null && item.getParent().isShared())
				fileModel.setShareable(false);
		}

		return fileModel;
	}


	/**
	 * Builds the gxt list file grid model item for search.
	 *
	 * @param listSearchItems the list search items
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<FileGridModel> buildGXTListFileGridModelItemForSearch(List<SearchItem> listSearchItems) throws Exception {
		List<FileGridModel> listFileGridModel = new ArrayList<FileGridModel>();
		FileModel parentFileModel = null;
		for (SearchItem item : listSearchItems)
				listFileGridModel.add(buildGXTFileGridModelItemForSearch(item,parentFileModel));
		return listFileGridModel;
	}

	/**
	 * Builds the gxt file grid model item for search.
	 *
	 * @param item the item
	 * @param parentFileModel the parent file model
	 * @return the file grid model
	 * @throws Exception the exception
	 */
	private FileGridModel buildGXTFileGridModelItemForSearch(SearchItem item, FileModel parentFileModel) throws Exception{

		FileGridModel fileGridModel = null;
		try{

			switch (item.getType()) {

			case FOLDER:
				fileGridModel = new FolderGridModel(item.getId(), item.getName(), toDate(item.getLastModified()), parentFileModel, -1, true, false,false, false);
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel());
				fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_FOLDERS);
				fileGridModel.setShareable(true);
				break;

			case FOLDER_ITEM:
				SearchFolderItem folderItem = (SearchFolderItem) item;
				fileGridModel = new FileGridModel(item.getId(), item.getName(), toDate(item.getLastModified()), parentFileModel, folderItem.getSize(), false, false);
				fileGridModel = (FileGridModel) setFolderItemTypeForSearch(fileGridModel, folderItem);
				break;

			case SHARED_FOLDER:
				//ATTENTION: SEARCH ITEM IS NOT CASTABLE AT WorkspaceSharedFolder
				fileGridModel = new FolderGridModel(item.getId(), item.getName(), toDate(item.getLastModified()), parentFileModel, -1, true, true, item.isVreFolder(), false);
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.getLabel());
				fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_SHARED_FOLDERS);
				fileGridModel.setShareable(true);
				break;

			default:
				logger.error("gxt conversion return null for item "+item.getName());
				break;

			}

			if(parentFileModel!=null && parentFileModel.isShared()){
				fileGridModel.setShared(true);
				fileGridModel.setShareable(false); //UPDATED TO CHANGE PERMISSIONS TO SHARED SUBFOLDERS
			}

			//OWNER
			if(item.isShared()){ //IS READ FROM HL ONLY IF THE ITEM IS SHARED
				fileGridModel.setShared(true); //TEMPORARY SOLUTION: ADDED TO FIX WRONG TYPE SearchFolderItem
				String portalLogin = item.getOwner(); //IS PORTAL LOGIN
				if(portalLogin!=null){
					fileGridModel.setOwnerFullName(UserUtil.getUserFullName(portalLogin));
				}
			}
			else{
				if(userLogged!=null)
					fileGridModel.setOwnerFullName(userLogged.getName());
			}

			return fileGridModel;

		}catch (Exception e) {
			logger.error("An error occurred in buildGXTFileGridModelItemForSearch:", e);
			throw new Exception(e);
		}
	}

	/**
	 * Builds the gxt list file grid model item.
	 *
	 * @param listWorkspaceItems the list workspace items
	 * @param parentFileModel the parent file model
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	public List<FileGridModel> buildGXTListFileGridModelItem(List<WorkspaceItem> listWorkspaceItems, FileModel parentFileModel) throws InternalErrorException{

		Long startTime =  System.currentTimeMillis();
		List<FileGridModel> listFileGridModel = new ArrayList<FileGridModel>();

		for (WorkspaceItem item : listWorkspaceItems)
				listFileGridModel.add(buildGXTFileGridModelItem(item,parentFileModel));

		Long endTime = System.currentTimeMillis() - startTime;
		String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
		logger.trace("##GRID FILLING: gxt grid objects getChildren() returning "+listWorkspaceItems.size()+" elements in " + time);

		return listFileGridModel;
	}

	/**
	 * Builds the gxt file grid model item.
	 *
	 * @param item the item
	 * @param parentFileModel the parent file model
	 * @return the file grid model
	 * @throws InternalErrorException the internal error exception
	 */
	public FileGridModel buildGXTFileGridModelItem(WorkspaceItem item, FileModel parentFileModel) throws InternalErrorException{


		FileGridModel fileGridModel = null;

			switch (item.getType()) {

			case FOLDER:
				boolean isPublic = ((WorkspaceFolder)item).isPublic();
				fileGridModel = new FolderGridModel(item.getId(), item.getName(), toDate(item.getLastModificationTime()), parentFileModel, -1, true, false,false, isPublic);
				if(isPublic)
					fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_PUBLIC.getLabel().toString());
				else
					fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel().toString());

				fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_FOLDERS);
				fileGridModel.setShareable(true);
				fileGridModel.setDescription(item.getDescription());
				break;

			case FOLDER_ITEM:
				FolderItem folderItem = (FolderItem) item;
				fileGridModel = new FileGridModel(item.getId(), item.getName(), toDate(item.getLastModificationTime()), parentFileModel, folderItem.getLength(), false, false);
				fileGridModel = (FileGridModel) setFolderItemType(fileGridModel, folderItem);
				break;

			case SHARED_FOLDER:
		    	WorkspaceSharedFolder shared = (WorkspaceSharedFolder) item;
		    	isPublic = ((WorkspaceFolder)shared).isPublic();
		    	String name = shared.isVreFolder()?shared.getDisplayName():item.getName();
				fileGridModel = new FolderGridModel(item.getId(), name, toDate(item.getLastModificationTime()), parentFileModel, -1, true, true, shared.isVreFolder(), isPublic);

				if(isPublic)
					fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED_PUBLIC.getLabel().toString());
				else
					fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.getLabel().toString());

				fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_SHARED_FOLDERS);
				fileGridModel.setShareable(true);
				fileGridModel.setDescription(item.getDescription());
				break;

			default:
				logger.error("gxt conversion return null for item "+item.getName());
				break;

			}

			if(parentFileModel!=null && parentFileModel.isShared()){
				fileGridModel.setShared(true);
				fileGridModel.setShareable(false); //UPDATED TO CHANGE PERMISSIONS TO SHARED SUBFOLDERS
			}

			//OWNER
			if(item.isShared()){ //IS READ FROM HL ONLY IF THE ITEM IS SHARED
				fileGridModel.setShared(true); //NOT REMOVE IT IS IMPORTAT SEE #1459
				User owner = item.getOwner();
				if(owner!=null){
//					System.out.println("++++reading owner");
					String portalLogin = owner.getPortalLogin();
					fileGridModel.setOwnerFullName(UserUtil.getUserFullName(portalLogin));
				}
			}
			else{
				if(userLogged!=null)
//					fileGridModel.setOwner(new InfoContactModel(userLogged.getId(), userLogged.getLogin(), userLogged.getName()));
					fileGridModel.setOwnerFullName(userLogged.getName());
			}

		return fileGridModel;
	}

	/**
	 * Sets the folder item type for search.
	 *
	 * @param fileModel the file model
	 * @param searchFolderItem the search folder item
	 * @return the file model
	 */
	protected FileModel setFolderItemTypeForSearch(FileModel fileModel, SearchFolderItem searchFolderItem){

		if(searchFolderItem.getFolderItemType()==null){
			logger.trace("Search folder item type is null for "+searchFolderItem.getId() + " name: "+searchFolderItem.getName());
			//FOR DEBUG
//			System.out.println("Search folder item type is null for "+searchFolderItem.getId() + " name: "+searchFolderItem.getName());
			return fileModel;
		}

		switch(searchFolderItem.getFolderItemType())
		{
			case EXTERNAL_IMAGE:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_IMAGE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_IMAGES);
				fileModel.setType(searchFolderItem.getMimeType());
				break;
			case EXTERNAL_FILE:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_FILE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				fileModel.setType(searchFolderItem.getMimeType());
				break;
			case EXTERNAL_PDF_FILE:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				fileModel.setType(searchFolderItem.getMimeType());
				break;
			case EXTERNAL_URL:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_LINKS);
				break;
			case REPORT_TEMPLATE:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT_TEMPLATE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_REPORTS);
				break;
			case REPORT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_REPORTS);
				break;
			case QUERY:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.QUERY);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case TIME_SERIES:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.TIME_SERIES);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_TIMESERIES);
				break;
			case PDF_DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.PDF_DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case IMAGE_DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.IMAGE_DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_IMAGES);
				fileModel.setType(searchFolderItem.getMimeType());
				break;
			case DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				fileModel.setType(searchFolderItem.getMimeType());
				break;
			case URL_DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.URL_DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case METADATA:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.METADATA);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case GCUBE_ITEM:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.GCUBE_ITEM);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_GCUBE_ITEMS);
				break;
			default:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.UNKNOWN_TYPE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_UNKNOWN);
				fileModel.setType(GXTFolderItemTypeEnum.UNKNOWN_TYPE.toString());
				break;
		}

		return fileModel;
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
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_IMAGES);
				ExternalImage extImage = (ExternalImage) worspaceFolderItem;
				fileModel.setType(extImage.getMimeType());
				break;
			case EXTERNAL_FILE:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_FILE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				ExternalFile extFile = (ExternalFile) worspaceFolderItem;
				fileModel.setType(extFile.getMimeType());
				break;
			case EXTERNAL_PDF_FILE:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				ExternalPDFFile pdfExt = (ExternalPDFFile) worspaceFolderItem;
				fileModel.setType(pdfExt.getMimeType());
				break;
			case EXTERNAL_URL:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_LINKS);
				break;
			case REPORT_TEMPLATE:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT_TEMPLATE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_REPORTS);
				break;
			case REPORT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_REPORTS);
				break;
			case QUERY:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.QUERY);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case TIME_SERIES:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.TIME_SERIES);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_TIMESERIES);
				break;
			case PDF_DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.PDF_DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case IMAGE_DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.IMAGE_DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_IMAGES);
//				ImageDocument imgDoc = (ImageDocument) worspaceFolderItem;
				GCubeItem imgDoc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
				try {
					fileModel.setType(imgDoc.getMimeType());
				} catch (InternalErrorException e) {
					logger.error("IMAGE_DOCUMENT InternalErrorException when getting MimeType on "+fileModel.getIdentifier());
				}
				break;
			case DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
//				Document doc = (Document) worspaceFolderItem;
				GCubeItem doc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
				try {
					fileModel.setType(doc.getMimeType());
				} catch (InternalErrorException e) {
					logger.error("DOCUMENT InternalErrorException when getting MimeType on "+fileModel.getIdentifier());
				}
				break;
			case URL_DOCUMENT:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.URL_DOCUMENT);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case METADATA:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.METADATA);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
				break;
			case GCUBE_ITEM:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.GCUBE_ITEM);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_GCUBE_ITEMS);
				break;
			default:
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.UNKNOWN_TYPE);
				fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_UNKNOWN);
				fileModel.setType(GXTFolderItemTypeEnum.UNKNOWN_TYPE.toString());
				break;
		}

		return fileModel;
	}

	/**
	 * Gets the public link for folder item.
	 *
	 * @param worspaceFolderItem the worspace folder item
	 * @return the publi link for folder item
	 * @throws InternalErrorException the internal error exception
	 */
	public String getPublicLinkForFolderItem(FolderItem worspaceFolderItem) throws InternalErrorException{

		if(worspaceFolderItem==null)
			return "";

		try{
			switch(worspaceFolderItem.getFolderItemType()){
				case EXTERNAL_IMAGE:
					return ((ExternalImage) worspaceFolderItem).getPublicLink();
				case EXTERNAL_FILE:
					return ((ExternalFile) worspaceFolderItem).getPublicLink();
				case EXTERNAL_PDF_FILE:
					return ((ExternalPDFFile) worspaceFolderItem).getPublicLink();
				case EXTERNAL_URL:
					break;
				case REPORT_TEMPLATE:
					break;
				case REPORT:
					break;
				case QUERY:
					break;
				case TIME_SERIES:
					break;
				case PDF_DOCUMENT:
					break;
				case IMAGE_DOCUMENT:
					GCubeItem imgDoc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
					return imgDoc.getPublicLink(false);
				case DOCUMENT:
					break;
				case URL_DOCUMENT:
					break;
				case METADATA:
					break;
				default:
					return "";
			}

		}catch (Exception e) {
			logger.error("an error occurred when get public link for item: "+worspaceFolderItem.getName());
			return "";
		}

		return "";
	}



	/**
	 * Gets the storage id for folder item.
	 *
	 * @param worspaceFolderItem the worspace folder item
	 * @return the storage id for folder item
	 * @throws InternalErrorException the internal error exception
	 */
	public String getStorageIDForFolderItem(FolderItem worspaceFolderItem) throws InternalErrorException{

		if(worspaceFolderItem==null)
			return "";

		try{
			switch(worspaceFolderItem.getFolderItemType()){
				case EXTERNAL_IMAGE:
					return ((ExternalImage) worspaceFolderItem).getStorageID();
				case EXTERNAL_FILE:
					return ((ExternalFile) worspaceFolderItem).getStorageID();
				case EXTERNAL_PDF_FILE:
					return ((ExternalPDFFile) worspaceFolderItem).getStorageID();
				case EXTERNAL_URL:
					break;
				case REPORT_TEMPLATE:
					break;
				case REPORT:
					break;
				case QUERY:
					break;
				case TIME_SERIES:
					break;
				case PDF_DOCUMENT:
					break;
				case IMAGE_DOCUMENT:
					GCubeItem imgDoc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
					return imgDoc.getStorageID();
				case DOCUMENT:
					break;
				case URL_DOCUMENT:
					break;
				case METADATA:
					break;
				default:
					return "";
			}

		}catch (Exception e) {
			logger.error("an error occurred when get public link for item: "+worspaceFolderItem.getName());
			return "";
		}

		return "";
	}

	/**
	 * Builds the gxt folder model item.
	 *
	 * @param wsFolder the ws folder
	 * @param parent the parent
	 * @return the folder model
	 * @throws InternalErrorException the internal error exception
	 */
	public FolderModel buildGXTFolderModelItem(WorkspaceFolder wsFolder, FileModel parent) throws InternalErrorException {
		logger.debug("buildGXTFolderModelItem...");

		String name = "";

		//MANAGEMENT SHARED FOLDER NAME
		if(wsFolder.isShared() && wsFolder.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
	    	WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wsFolder;
	    	name = shared.isVreFolder()?shared.getDisplayName():wsFolder.getName();
		}else
			name = wsFolder.getName();

		//MANAGEMENT SPECIAL FOLDER
//		if(wsFolder.getName().compareTo("MySpecialFolders")==0 && wsFolder.getParent()!=null && wsFolder.getParent().isRoot()){
//	    	WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wsFolder;
//	    	name = shared.isVreFolder()?shared.getDisplayName():wsFolder.getName();
//		}else
//			name = wsFolder.getName();

		FolderModel folder = new FolderModel(wsFolder.getId(), name, parent, true, wsFolder.isShared(), false, wsFolder.isPublic());
		folder.setShareable(true);
		folder.setDescription(wsFolder.getDescription());
//		folder.setOwner(wsFolder.getOwner());

		if(parent != null && parent.isShared()){
			folder.setShared(true);
			folder.setShareable(false);
		}

		return folder;
	}

	/**
	 * TODO ********TEMPORARY SOLUTION HL MUST MANAGE SPECIAL FOLDER AS WORKSPACESPECIALFOLDER****
	 * REMOVE THIS METHOD AND ADDING INSTANCE OF AT buildGXTFolderModelItem.
	 *
	 * @param wsFolder the ws folder
	 * @param parent the parent
	 * @param specialFolderName the special folder name
	 * @return the folder model
	 * @throws InternalErrorException the internal error exception
	 */
	public FolderModel buildGXTFolderModelItemHandleSpecialFolder(WorkspaceFolder wsFolder, FileModel parent, String specialFolderName) throws InternalErrorException {

		String name = "";

		logger.debug("buildGXTFolderModelItemHandleSpecialFolder to folder: "+wsFolder.getName());
		//MANAGEMENT SHARED FOLDER NAME
		if(wsFolder.isShared() && wsFolder.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
			logger.debug("MANAGEMENT SHARED Folder name..");
	    	WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wsFolder;
	    	logger.debug("shared.isVreFolder(): "+shared.isVreFolder());
	    	name = shared.isVreFolder()?shared.getDisplayName():wsFolder.getName();

	    	/*
	    	if(shared.isVreFolder())
	    		logger.debug("shared.getDisplayName(): "+shared.getDisplayName());
	    	*/
	    	//MANAGEMENT SPECIAL FOLDER
		}else if(wsFolder.getName().compareTo(ConstantsExplorer.MY_SPECIAL_FOLDERS)==0 && wsFolder.getParent()!=null && wsFolder.getParent().isRoot()){
			//MANAGEMENT SPECIAL FOLDER
			logger.debug("MANAGEMENT SPECIAL FOLDER NAME REWRITING AS: "+specialFolderName);
			if(specialFolderName!=null && !specialFolderName.isEmpty())
				name = specialFolderName;
			else
				name = wsFolder.getName();
		}else{
			logger.debug("MANAGEMENT Base Folder name..");
			name = wsFolder.getName();
		}

		logger.debug("Name is: "+name);

		FolderModel folder = new FolderModel(wsFolder.getId(), name, parent, true, wsFolder.isShared(), false, wsFolder.isPublic());
		folder.setShareable(true);
		folder.setIsRoot(wsFolder.isRoot());
		folder.setDescription(wsFolder.getDescription());
//		folder.setOwner(wsFolder.getOwner());

		if(parent != null && parent.isShared()){
			folder.setShared(true);
			folder.setShareable(false);
		}
		return folder;
	}

	/**
	 * Builds the gwt workspace file details.
	 *
	 * @param wsItem the ws item
	 * @param item the item
	 * @return the file details model
	 * @throws InternalErrorException the internal error exception
	 */
	public FileDetailsModel buildGWTWorkspaceFileDetails(WorkspaceItem wsItem, FileModel item) throws InternalErrorException {

		FileDetailsModel fileDetailsModel = new FileDetailsModel(
				wsItem.getId(),
				wsItem.getName(),
				wsItem.getPath(),
				toDate(wsItem.getCreationTime()),
				item.getParentFileModel(),
				0,//size
				item.isDirectory(),
				wsItem.getDescription(),
				toDate(wsItem.getLastModificationTime()),
				buildGXTInfoContactModel(wsItem.getOwner()),
				wsItem.isShared());

		return fileDetailsModel;
	}

	/**
	 * Gets the folder item type category.
	 *
	 * @param item the item
	 * @return the folder item type category
	 * @throws InternalErrorException the internal error exception
	 */
	protected GXTCategorySmartFolder getFolderItemTypeCategory(SearchItem item) throws InternalErrorException{

		switch (item.getType()) {

		case FOLDER:

			return GXTCategorySmartFolder.SMF_DOCUMENTS;

		case FOLDER_ITEM:

			SearchFolderItem folderItem = (SearchFolderItem) item;
			switch(folderItem.getFolderItemType())
			{
				case EXTERNAL_IMAGE:
					return GXTCategorySmartFolder.SMF_IMAGES;
				case EXTERNAL_FILE:
					return GXTCategorySmartFolder.SMF_DOCUMENTS;
				case EXTERNAL_PDF_FILE:
					return GXTCategorySmartFolder.SMF_DOCUMENTS;
				case EXTERNAL_URL:
					return GXTCategorySmartFolder.SMF_LINKS;
				case REPORT_TEMPLATE:
					return GXTCategorySmartFolder.SMF_REPORTS;
				case REPORT:
					return GXTCategorySmartFolder.SMF_REPORTS;
				case QUERY:
					return GXTCategorySmartFolder.SMF_DOCUMENTS;
				case TIME_SERIES:
					return GXTCategorySmartFolder.SMF_TIMESERIES;
				case PDF_DOCUMENT:
					return GXTCategorySmartFolder.SMF_DOCUMENTS;
				case IMAGE_DOCUMENT:
					return GXTCategorySmartFolder.SMF_IMAGES;
				case DOCUMENT:
					return GXTCategorySmartFolder.SMF_DOCUMENTS;
				case URL_DOCUMENT:
					return GXTCategorySmartFolder.SMF_DOCUMENTS;
				case METADATA:
					return GXTCategorySmartFolder.SMF_DOCUMENTS;
				case GCUBE_ITEM:
					return GXTCategorySmartFolder.SMF_GCUBE_ITEMS;
				default:
					return GXTCategorySmartFolder.SMF_UNKNOWN;
			}

		default:
			logger.error("gxt conversion return null for item "+item.getName());
			return GXTCategorySmartFolder.SMF_UNKNOWN;
		}

	}

	/**
	 * Filter list file grid model item by category.
	 *
	 * @param listSearchItem the list search item
	 * @param category the category
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<FileGridModel> filterListFileGridModelItemByCategory(List<SearchItem> listSearchItem, GXTCategorySmartFolder category) throws Exception {

		List<FileGridModel> filteredList = new ArrayList<FileGridModel>();
		logger.trace("filterListFileGridModelItemByCategory - Category:" + category + "listSearchItem size " + listSearchItem.size());
		for(SearchItem searchItem: listSearchItem){
			logger.trace("wsItem: " + searchItem.getName());
			if(category.equals(getFolderItemTypeCategory(searchItem)))
				filteredList.add(buildGXTFileGridModelItemForSearch(searchItem,null));
		}
		return filteredList;
	}


	/**
	 * Builds the gxt list smart folder model.
	 *
	 * @param listWorkspaceSmartFolder the list workspace smart folder
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<SmartFolderModel> buildGXTListSmartFolderModel(List<WorkspaceSmartFolder> listWorkspaceSmartFolder) throws Exception {
		List<SmartFolderModel> listSmartFolder = new ArrayList<SmartFolderModel>();

		for(WorkspaceSmartFolder workspaceFolder : listWorkspaceSmartFolder)
			listSmartFolder.add(buildGXTSmartFolderModel(workspaceFolder, ""));

		return listSmartFolder;
	}


	/**
	 * Builds the gxt smart folder model.
	 *
	 * @param wsFolder the ws folder
	 * @param query the query
	 * @return the smart folder model
	 * @throws Exception the exception
	 */
	public SmartFolderModel buildGXTSmartFolderModel(WorkspaceSmartFolder wsFolder, String query) throws Exception {

		SmartFolderModel smartFolderModel = new SmartFolderModel(
				wsFolder.getId(),
				wsFolder.getName(),
				query);

		logger.trace("in return SmartFolder: " + smartFolderModel.getIdentifier() + " " + smartFolderModel.getName()  + " " + smartFolderModel.getQuery());
		return smartFolderModel;
	}

	/**
	 * Builds the gxt list scope model.
	 *
	 * @param listFilteredScopes the list filtered scopes
	 * @param mapPortalScopes the map portal scopes
	 * @return the list
	 */
	public List<ScopeModel> buildGXTListScopeModel(List<String> listFilteredScopes, Map<String, String> mapPortalScopes) {

		List<ScopeModel> listScopeModel = new ArrayList<ScopeModel>();
		for(String scope: listFilteredScopes)
			listScopeModel.add(new ScopeModel(mapPortalScopes.get(scope),scope));

		return listScopeModel;

	}

	/**
	 * Builds the gxt list contacts model from user model.
	 *
	 * @param listUsers the list users
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromUserModel(List<GCubeUser> listUsers) throws InternalErrorException {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		if(listUsers==null)
			return listContactsModel;

		logger.trace("List<UserModel> size returned from Portal VO is: "+ listUsers.size());

		logger.trace("Building list contact model list user model");
		for (GCubeUser userModel : listUsers) {
			String fullName = userModel.getFullname();

			if(fullName!=null && !fullName.isEmpty())
				listContactsModel.add(new InfoContactModel(userModel.getUserId()+"", userModel.getScreenName(), fullName, false));
			else
				logger.trace("buildGXTListContactsModel is not returning user: "+userModel.getScreenName()+ "because name is null or empty");
		}
		logger.trace("List contact model completed, return " +listContactsModel.size()+" contacts");

		return listContactsModel;
	}

	/**
	 * Builds the gxt list contacts model from gcube group.
	 *
	 * @param list the list
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromGcubeGroup(List<GCubeGroup> list) throws InternalErrorException {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		if(list==null)
			return listContactsModel;

		logger.trace("List<GCubeGroup> size returned from GcubeGroup is: "+ list.size());

		logger.trace("Building list contact model...");

		for (GCubeGroup group : list){
			try{
				String groupDN = group.getDisplayName();

				if(groupDN==null || groupDN.isEmpty())
					groupDN = group.getName();

				if(group.getName()== null || group.getName().isEmpty())
					logger.warn("Skipping group with null or empty name "+group);
				else{
					InfoContactModel contact = new InfoContactModel(group.getName(), group.getName(), groupDN, true);
					logger.trace("Adding group "+contact);
					listContactsModel.add(contact);
				}
			}catch (InternalErrorException e) {
				logger.warn("Dispaly name is not available to group "+group);
				logger.warn("Adding get name property "+group.getName());

				if(group.getName()== null || group.getName().isEmpty())
					logger.warn("Skipping group with null or empty name "+group);
				else
					listContactsModel.add(new InfoContactModel(group.getName(), group.getName(), group.getName(), true));
			}
		}

		logger.trace("List GCubeGroup contact model completed, return " +listContactsModel.size()+" contacts");

		return listContactsModel;
	}


	/**
	 * Builds the list login from contanct model.
	 *
	 * @param listContactsModel the list contacts model
	 * @return list of portal logins
	 * @throws InternalErrorException the internal error exception
	 */
	public List<String> buildListLoginFromContanctModel(List<InfoContactModel> listContactsModel) throws InternalErrorException {

		List<String> listPortalLogin = new ArrayList<String>();

		for(InfoContactModel contact: listContactsModel){
			listPortalLogin.add(contact.getLogin());

		}
		return listPortalLogin;
	}


	/**
	 * Builds the gxt list message model for grid.
	 *
	 * @param listMessages the list messages
	 * @param typeMessages the type messages
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	public List<MessageModel> buildGXTListMessageModelForGrid(List<WorkspaceMessage> listMessages, String typeMessages) throws InternalErrorException {

		List<MessageModel> listMessageModel = new ArrayList<MessageModel>();


		for(WorkspaceMessage mess: listMessages){
			List<WorkspaceItem> listAttachs = mess.getAttachments();
			List<String> listAttachsNames = new ArrayList<String>();

			logger.trace("IN SERVER MESSAGE TYPE: " +typeMessages);
			logger.trace("subject " +  mess.getSubject());

			if(listAttachs!=null){
				for(WorkspaceItem attach: listAttachs){
					listAttachsNames.add(attach.getName());
					logger.trace("Received attach: "+ attach.getName() + " " +attach.getId());
				}
			}

			listMessageModel.add(new MessageModel(mess.getId(), mess.getSubject(), buildGXTInfoContactModel(mess.getSender()), toDate(mess.getSendTime()), listAttachsNames, typeMessages, mess.isRead()));

		}
		return listMessageModel;
	}

	/**
	 * Builds the gxt message model.
	 *
	 * @param mess the mess
	 * @param listWorkspaceItems the list workspace items
	 * @param messageType the message type
	 * @return the message model
	 * @throws InternalErrorException the internal error exception
	 */
	public MessageModel buildGXTMessageModel(WorkspaceMessage mess, List<WorkspaceItem> listWorkspaceItems, String messageType) throws InternalErrorException {

		List<FileModel> listAttachs = buildGXTListFileModelItemForAttachs(listWorkspaceItems);
		return new MessageModel(mess.getId(), mess.getSubject(), buildGXTInfoContactModel(mess.getSender()), toDate(mess.getSendTime()), mess.getBody(), listAttachs, mess.getAddresses(), messageType, mess.isRead());

	}

	/**
	 * Builds the gxt info contact model.
	 *
	 * @param user the user
	 * @return InfoContactModel
	 * @throws InternalErrorException the internal error exception
	 */
	public InfoContactModel buildGXTInfoContactModel(User user) throws InternalErrorException{

			if(user!=null)
				return new InfoContactModel(user.getId(), user.getPortalLogin(), UserUtil.getUserFullName(user.getPortalLogin()), false);

			return new InfoContactModel();
	}

	/**
	 * Builds the gxt accounting item.
	 *
	 * @param accoutings the accoutings
	 * @param gxtEntryType the gxt entry type
	 * @return the list
	 */
	public List<GxtAccountingField> buildGXTAccountingItem(List<AccountingEntry> accoutings, GxtAccountingEntryType gxtEntryType) {

		List<GxtAccountingField> listAccFields = new ArrayList<GxtAccountingField>();

		if(accoutings!=null){
			logger.trace("accoutings size "+accoutings.size()+ ", converting...");

			for (AccountingEntry accountingEntry : accoutings) {

				GxtAccountingField af = new GxtAccountingField();

				InfoContactModel user = buildGxtInfoContactFromPortalLogin(accountingEntry.getUser());

				af.setUser(user);
				af.setDate(toDate(accountingEntry.getDate()));

				switch (accountingEntry.getEntryType()) {

					case CREATE:

						if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.CREATE)){
							AccountingEntryCreate create = (AccountingEntryCreate) accountingEntry;
							af.setOperation(GxtAccountingEntryType.CREATE);

//							af.setDescription(GxtAccountingEntryType.CREATE.getName() + " by "+user.getName());
							String msg = "";
							if(create.getItemName()==null || create.getItemName().isEmpty())
								msg = GxtAccountingEntryType.CREATE.getId() + " by "+user.getName();
							else{

								if(create.getVersion()==null)
									msg = create.getItemName() + " " + GxtAccountingEntryType.CREATE.getName() + " by "+user.getName();
								else
									msg = create.getItemName() + " v. "+create.getVersion()+" "+ GxtAccountingEntryType.CREATE.getName() + " by "+user.getName();
							}

							af.setDescription(msg);
						}

						break;

					case READ:

						if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.READ)){

							AccountingEntryRead read = (AccountingEntryRead) accountingEntry;
							af.setOperation(GxtAccountingEntryType.READ);
							af.setDescription(read.getItemName() + " " + GxtAccountingEntryType.READ.getName() + " by "+user.getName());

							String msg = "";
							if(read.getItemName()==null || read.getItemName().isEmpty())
								msg = GxtAccountingEntryType.READ.getId() + " by "+user.getName();
							else{

								if(read.getVersion()==null)
									msg = read.getItemName() + " " + GxtAccountingEntryType.READ.getName() + " by "+user.getName();
								else
									msg = read.getItemName() + " v."+read.getVersion() +" "+ GxtAccountingEntryType.READ.getName() + " by "+user.getName();
							}

							af.setDescription(msg);
						}

						break;

					case CUT:

						if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.CUT)){

							af.setOperation(GxtAccountingEntryType.CUT);
							AccountingEntryCut cut = (AccountingEntryCut) accountingEntry;

							String msg = "";
							if(cut.getItemName()==null || cut.getItemName().isEmpty())
								msg = GxtAccountingEntryType.CUT.getName() +" by "+user.getName();
							else{
								if(cut.getVersion()==null)
									msg = cut.getItemName()+" "+GxtAccountingEntryType.CUT.getName() +" by "+user.getName();
								else
									msg = cut.getItemName()+" v."+cut.getVersion()+" "+GxtAccountingEntryType.CUT.getName() +" by "+user.getName();
							}

							af.setDescription(msg);
						}

						break;

					case PASTE:

						if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.PASTE)){

							af.setOperation(GxtAccountingEntryType.PASTE);
							AccountingEntryPaste paste = (AccountingEntryPaste) accountingEntry;

							if(paste.getVersion()==null)
								af.setDescription(GxtAccountingEntryType.PASTE.getName() + " from "+paste.getFromPath()+" by "+user.getName());
							else
								af.setDescription(GxtAccountingEntryType.PASTE.getName() + " v. "+paste.getVersion()+" from "+paste.getFromPath()+" by "+user.getName());
						}

						break;

					case REMOVAL:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) ||  gxtEntryType.equals(GxtAccountingEntryType.REMOVE)){

							af.setOperation(GxtAccountingEntryType.REMOVE);
							AccountingEntryRemoval rem = (AccountingEntryRemoval) accountingEntry;
							String msg = rem.getItemName()==null || rem.getItemName().isEmpty()?"":rem.getItemName()+" ";

							if(rem.getVersion()==null)
								msg+= GxtAccountingEntryType.REMOVE.getName() +" by "+user.getName();
							else
								msg+= GxtAccountingEntryType.REMOVE.getName() +" v."+rem.getVersion()+" by "+user.getName();

							af.setDescription(msg);
						}
						break;

					case RENAMING:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.RENAME)){

							af.setOperation(GxtAccountingEntryType.RENAME);
							AccountingEntryRenaming ren = (AccountingEntryRenaming) accountingEntry;
							String msg = ren.getOldItemName()==null || ren.getOldItemName().isEmpty()?"":ren.getOldItemName()+" ";
							if(ren.getVersion()==null)
								msg+= GxtAccountingEntryType.RENAME.getName() +" to "+ ren.getNewItemName()+ " by "+user.getName();
							else
								msg+= " v."+ren.getVersion() +" "+GxtAccountingEntryType.RENAME.getName() +" to "+ ren.getNewItemName()+ " by "+user.getName();

							af.setDescription(msg);
						}
						break;

					case ADD:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.ADD)){

							af.setOperation(GxtAccountingEntryType.ADD);
							AccountingEntryAdd acc = (AccountingEntryAdd) accountingEntry;
							String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";
							if(acc.getVersion()==null)
								msg+=GxtAccountingEntryType.ADD.getName()+ " by "+user.getName();
							else
								msg+=" v."+acc.getVersion()+ " "+GxtAccountingEntryType.ADD.getName()+ " by "+user.getName();

							af.setDescription(msg);
						}
						break;

					case UPDATE:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.UPDATE)){

							af.setOperation(GxtAccountingEntryType.UPDATE);
							AccountingEntryUpdate upd = (AccountingEntryUpdate) accountingEntry;
							String msg = upd.getItemName()==null || upd.getItemName().isEmpty()?"":upd.getItemName()+" ";
							if(upd.getVersion()==null)
								msg+=GxtAccountingEntryType.UPDATE.getName()+" by "+user.getName();
							else
								msg+=" v."+upd.getVersion()+" "+GxtAccountingEntryType.UPDATE.getName()+" by "+user.getName();

							af.setDescription(msg);
						}
						break;

					case SHARE:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.SHARE)){

							af.setOperation(GxtAccountingEntryType.SHARE);

							AccountingEntryShare acc = (AccountingEntryShare) accountingEntry;

							String msg = "";
							if(acc.getItemName()==null || acc.getItemName().isEmpty())
								msg = user.getName() + " "+GxtAccountingEntryType.SHARE.getName()+ " workspace folder";
							else
								msg = user.getName() + " "+GxtAccountingEntryType.SHARE.getName()+ " workspace folder "+acc.getItemName();

							if(acc.getMembers()!=null && acc.getMembers().size()>0)
								msg+=" with "+UserUtil.separateFullNameToCommaForPortalLogin(acc.getMembers());

							af.setDescription(msg);
						}
						break;

					case UNSHARE:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.UNSHARE)){

							af.setOperation(GxtAccountingEntryType.UNSHARE);
							AccountingEntryUnshare uns = (AccountingEntryUnshare) accountingEntry;
							String msg = uns.getItemName()==null || uns.getItemName().isEmpty()?"":uns.getItemName()+" ";
							msg+=GxtAccountingEntryType.UNSHARE.getName()+" by "+user.getName();
							af.setDescription(msg);
						}
						break;

					case RESTORE:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.RESTORE)){

							af.setOperation(GxtAccountingEntryType.RESTORE);
							AccountingEntryRestore acc = (AccountingEntryRestore) accountingEntry;
							String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";

							if(acc.getVersion()==null)
								msg+=GxtAccountingEntryType.RESTORE.getName()+" by "+user.getName();
							else
								msg+=" v."+acc.getVersion()+" "+GxtAccountingEntryType.RESTORE.getName() +" by "+user.getName();

							af.setDescription(msg);
						}
						break;

					case DISABLED_PUBLIC_ACCESS:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.DISABLED_PUBLIC_ACCESS)){

							af.setOperation(GxtAccountingEntryType.DISABLED_PUBLIC_ACCESS);
							AccountingEntryDisabledPublicAccess acc = (AccountingEntryDisabledPublicAccess) accountingEntry;
							String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";
							msg+=GxtAccountingEntryType.DISABLED_PUBLIC_ACCESS.getName()+" by "+user.getName();
							af.setDescription(msg);
						}

						break;

					case ENABLED_PUBLIC_ACCESS:

						if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.ENABLED_PUBLIC_ACCESS)){

							af.setOperation(GxtAccountingEntryType.ENABLED_PUBLIC_ACCESS);
							AccountingEntryEnabledPublicAccess acc = (AccountingEntryEnabledPublicAccess) accountingEntry;
							String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";
							msg+=GxtAccountingEntryType.ENABLED_PUBLIC_ACCESS.getName()+" by "+user.getName();
							af.setDescription(msg);
						}

						break;

					default:

						break;

				}
				listAccFields.add(af);
			}
		}
		logger.debug("get accounting readers converting completed - returning size "+listAccFields.size());

		return listAccFields;

	}


	/**
	 * Builds the gxt accounting item from readers.
	 *
	 * @param readers the readers
	 * @return the list
	 */
	public List<GxtAccountingField> buildGXTAccountingItemFromReaders(List<AccountingEntryRead> readers) {

		List<GxtAccountingField> listAccFields = new ArrayList<GxtAccountingField>();

		if(readers!=null){

			for (AccountingEntryRead accReader : readers) {

				GxtAccountingField af = new GxtAccountingField();
				InfoContactModel user = buildGxtInfoContactFromPortalLogin(accReader.getUser());

				af.setUser(user);
				af.setDate(toDate(accReader.getDate()));
				af.setOperation(GxtAccountingEntryType.READ);

				String msg = "";
				if(accReader.getItemName()==null || accReader.getItemName().isEmpty())
					msg = GxtAccountingEntryType.READ.getName() + " by "+user.getName();
				else
					msg = accReader.getItemName() + " " + GxtAccountingEntryType.READ.getName() + " by "+user.getName();

				af.setDescription(msg);
				listAccFields.add(af);
			}

		}

		return listAccFields;
	}


	/**
	 * Sets the user logged.
	 *
	 * @param infoContactModel the new user logged
	 */
	public void setUserLogged(InfoContactModel infoContactModel) {
		this.userLogged = infoContactModel;
	}


	/**
	 * Gets the workspace acl from ac ls.
	 *
	 * @param types the types
	 * @return the workspace acl from ac ls
	 * @throws Exception the exception
	 */
	public List<WorkspaceACL> getWorkspaceACLFromACLs(List<ACLType> types) throws Exception{

		List<WorkspaceACL> acls = new ArrayList<WorkspaceACL>();

		for (ACLType acl : types) {

			switch (acl) {
			case ADMINISTRATOR:
				acls.add(new WorkspaceACL(acl.toString(), "Admin", false, USER_TYPE.ADMINISTRATOR, ""));
				break;
			case READ_ONLY:
				acls.add(new WorkspaceACL(acl.toString(), "Read Only", false, USER_TYPE.OTHER, "Users can read any file but cannot update/delete"));
				break;
			case WRITE_OWNER:
				acls.add(new WorkspaceACL(acl.toString(), "Write Own", true, USER_TYPE.OTHER, "Users can update/delete only their files"));
				break;

			case WRITE_ALL:
				acls.add(new WorkspaceACL(acl.toString(), "Write Any", false, USER_TYPE.OTHER,"Any user can update/delete any file"));
				break;

			default:
//				acls.add(new WorkspaceACL(acl.toString(), acl.toString(), false, USER_TYPE.OTHER, ""));
//				break;
			}
		}

		if(acls.size()==0)
			throw new Exception("No ACLs rules found!");

		logger.trace("returning acls: "+acls);

		return acls;
	}

	/**
	 * Builds the gxt list trash content.
	 *
	 * @param trash the trash
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<FileTrashedModel> buildGXTListTrashContent(WorkspaceTrashFolder trash) throws Exception
	{
		List<WorkspaceTrashItem> trashContent = trash.listTrashItems();

		logger.info("Converting trash content, size is: "+ trashContent.size());

		List<FileTrashedModel> listFileModel = new ArrayList<FileTrashedModel>();

		try {

			for (WorkspaceTrashItem trashedItem : trashContent)
				try{
					FileTrashedModel cti = buildGXTTrashModelItem(trashedItem);
					listFileModel.add(cti);
				}catch (Exception e) {
					logger.warn("Error in server buildGXTListTrashContent, skipping conversion of trashedItem", e);
				}
		} catch (Exception e) {
			logger.error("Error in server buildGXTListTrashContent", e);
			String error = ConstantsExplorer.SERVER_ERROR +" get Trash content. "+e.getMessage();
			throw new Exception(error);
		}

		logger.info("Returning trash content as FileTrashedModel, size is: "+ trashContent.size());

		return listFileModel;

	}

	/**
	 * Builds the gxt trash model item.
	 *
	 * @param trashedItem the trashed item
	 * @return the file trashed model
	 * @throws InternalErrorException the internal error exception
	 */
	public FileTrashedModel buildGXTTrashModelItem(WorkspaceTrashItem trashedItem) throws InternalErrorException{

		FileTrashedModel fileTrashModel = new FileTrashedModel();
		fileTrashModel.setName(trashedItem.getName());

		fileTrashModel.setIdentifier(trashedItem.getId()); //TODO

		//SETTING PARENT
		FileModel oldParent = new FileModel(trashedItem.getOriginalParentId(), "", true);

		fileTrashModel.setOrginalPath(trashedItem.getDeletedFrom());
		fileTrashModel.setParentFileModel(oldParent);

		//SETTING DELETED BY
		InfoContactModel deleteUser = buildGxtInfoContactFromPortalLogin(trashedItem.getDeletedBy());
		fileTrashModel.setDeleteUser(deleteUser);

		//SETTING MIME TYPE
		fileTrashModel.setType(trashedItem.getMimeType());

		//SETTING IS DIRECTORY
		fileTrashModel.setIsDirectory(trashedItem.isFolder());

		//SETTING DELETE DATE
		fileTrashModel.setDeleteDate(toDate(trashedItem.getDeletedTime()));

		fileTrashModel.setShared(false);

		logger.debug("Converting return trash item: "+fileTrashModel.getName() +" id: "+fileTrashModel.getIdentifier());

		return fileTrashModel;

	}

	/**
	 * Builds the gxt trash model item by id.
	 *
	 * @param itemId the item id
	 * @param trash the trash
	 * @return the file trashed model
	 * @throws InternalErrorException the internal error exception
	 */
	public FileTrashedModel buildGXTTrashModelItemById(String itemId, WorkspaceTrashFolder trash) throws InternalErrorException{
		return null;

	}


	/**
	 * Gets the formatted html acl from ac ls.
	 *
	 * @param aclOwner the acl owner
	 * @return the formatted html of the ACLs
	 */
	public String getFormatHtmlACLFromACLs(Map<ACLType, List<String>> aclOwner) {

		String html = "<div style=\"width: 100%; text-align:left; font-size: 10px;\">";

		logger.trace("Formatting "+aclOwner.size() +" ACL/s");

		for (ACLType type : aclOwner.keySet()) {
			List<String> listLogins = aclOwner.get(type);

			html+="<span style=\"font-weight:bold; padding-top: 5px;\">"+type+": </span>";
			html+="<span style=\"font-weight:normal;\">";
			for (String login : listLogins) {
				logger.trace("Adding login "+login);
				 String fullName = UserUtil.getUserFullName(login);
				 if(fullName!=null && !fullName.isEmpty())
					 html+=fullName+"; ";
				 else
					 html+=login+"; ";
			}
			html+="</span><br/>";
		}
		html+="</div>";


		return html;
	}

	/**
	 * returns dynamically the formated size.
	 *
	 * @param size the size
	 * @return the string
	 */
	public static String formatFileSize(long size) {
	    String formattedSize = null;

	    double b = size;
	    double k = size/1024.0;
	    double m = size/1024.0/1024.0;
	    double g = size/1024.0/1024.0/1024.0;
	    double t = size/1024.0/1024.0/1024.0/1024.0;

	    DecimalFormat dec = new DecimalFormat("0.00");

	    if ( t>1 ) {
	    	formattedSize = dec.format(t).concat(" TB");
	    } else if ( g>1 ) {
	    	formattedSize = dec.format(g).concat(" GB");
	    } else if ( m>1 ) {
	    	formattedSize = dec.format(m).concat(" MB");
	    } else if ( k>1 ) {
	    	formattedSize = dec.format(k).concat(" KB");
	    } else {
	    	formattedSize = dec.format(b).concat(" Bytes");
	    }

	    return formattedSize;
	}

	/**
	 * Gets the item description for type by id.
	 *
	 * @param item the item
	 * @return the item description for type by id
	 * @throws Exception the exception
	 */
	public String getItemDescriptionForTypeById(WorkspaceItem item) throws Exception {

		if(item==null)
			throw new Exception("The item is null");

		logger.info("Getting ItemDescriptionById: "+item.getId());

		try {

			switch (item.getType()) {

				case FOLDER:{
					WorkspaceFolder theFolder = (WorkspaceFolder) item;
					return theFolder.getDescription();
				}
				case SHARED_FOLDER:{
					WorkspaceSharedFolder theFolder = (WorkspaceSharedFolder) item;
					return theFolder.getDescription();
				}

				case SMART_FOLDER:{
					WorkspaceSmartFolder theFolder = (WorkspaceSmartFolder) item;
					return theFolder.getDescription();
				}

				case TRASH_FOLDER:{
					WorkspaceTrashFolder  theFolder = (WorkspaceTrashFolder) item;
					return "";
				}

				case TRASH_ITEM:{
					WorkspaceTrashItem tItem = (WorkspaceTrashItem) item;
					return tItem.getDescription();
				}

				default:{ //IS AN ITEM
					return item.getDescription();
				}
			}

		} catch (Exception e) {
			logger.error("Error in server ItemDescriptionForTypeById: ", e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting description for item id: "+item.getId();
			throw new Exception(error);
		}
	}

	/**
	 * Gets the format html gcube item properties.
	 *
	 * @param item the item
	 * @return Format HTML. A DIV HTML containing gcube item properties. If item is a GcubeItem and contains properties return HTML, null otherwise
	 */
	public String getFormatHtmlGcubeItemProperties(WorkspaceItem item) {

		Map<String, String> properties = getGcubeItemProperties(item);

		if(properties!=null){

			if(properties.size()==0){
				try {
					logger.warn("Gcube Item Properties not found for item: "+item.getId());
				} catch (InternalErrorException e) {
					//SILENT
				}
				return null;
			}

			String html = "<div style=\"width: 100%; text-align:left; font-size: 10px;\">";

			for (String key : properties.keySet()) {
				String value = properties.get(key);
				logger.trace("Getting property: ["+key+","+properties.get(key)+"]");
				html+="<span style=\"font-weight:bold; padding-top: 5px;\">"+key+": </span>";
				html+="<span style=\"font-weight:normal;\">";
				html+=value;
				html+="</span><br/>";
			}
			html+="</div>";

			return html;
		}
		return null;
	}

	/**
	 * Gets the gcube item properties.
	 *
	 * @param item the item
	 * @return the gcube item properties
	 */
	public Map<String, String> getGcubeItemProperties(WorkspaceItem item) {

		if(item instanceof GCubeItem){
			GCubeItem gItem = (GCubeItem) item;
			try {
				if(gItem.getProperties()!=null){
					Map<String, String> map = gItem.getProperties().getProperties();
					HashMap<String, String> properties = new HashMap<String, String>(map.size()); //TO PREVENT GWT SERIALIZATION ERROR
					for (String key : map.keySet())
						properties.put(key, map.get(key));

					return properties;
				}
			} catch (InternalErrorException e) {
				logger.error("Error in server getItemProperties: ", e);
				return null;
			}
		}
		return null;
	}


	/**
	 * To version history.
	 *
	 * @param versions the versions
	 * @return the list
	 */
	public List<FileVersionModel> toVersionHistory(List<WorkspaceVersion> versions){

		if(versions==null){
			logger.warn("Version history is null!!!");
			return new ArrayList<FileVersionModel>();
		}

		List<FileVersionModel> listVersions = new ArrayList<FileVersionModel>(versions.size());
		for (WorkspaceVersion wsVersion : versions) {
			String user = UserUtil.getUserFullName(wsVersion.getUser());
			FileVersionModel file = new FileVersionModel(wsVersion.getName(), wsVersion.getName(), wsVersion.getRemotePath(), user, toDate(wsVersion.getCreated()), wsVersion.isCurrentVersion());
			listVersions.add(file);
		}
		return listVersions;
	}

}
