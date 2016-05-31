package org.gcube.portlets.user.messages.client.resources;

import org.gcube.portlets.user.messages.shared.GXTFolderItemTypeEnum;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class Resources {
	
	public static final Icons ICONS = GWT.create(Icons.class);
	
	private static final String XML = "xml";
	private static final String JAVA = "java";
	private static final String HTML = "html";
	private static final String GIF = "gif";
	private static final String PNG = "png";
	private static final String JPEG = "jpeg";
	private static final String JPG = "jpg";
	private static final String PDF = "pdf";
	private static final String TIFF = "tiff";
	private static final String SVG = "svg";
	private static final String MSWORD = "msword";
//	private static final String DOCX = "msword";
	private static final String EXCEL = "excel";
	private static final String TXT = "plain";
	private static final String MPEG = "mpeg";
	private static final String SWF = "swf";
	private static final String FLV = "flv";
	private static final String AVI = "avi"; 

	public static AbstractImagePrototype getIconTable(){
		
		return AbstractImagePrototype.create(ICONS.table());  
	}
	

	public static AbstractImagePrototype getCloseIcon(){
		
		return AbstractImagePrototype.create(ICONS.close());  
	}
	
	public static AbstractImagePrototype getIconGif(){
		
		return AbstractImagePrototype.create(ICONS.gif());  
	} 
	
	public static AbstractImagePrototype getIconJpeg(){
		
		return AbstractImagePrototype.create(ICONS.jpeg());  
	} 
	
	public static AbstractImagePrototype getIconSvg(){
		
		return AbstractImagePrototype.create(ICONS.svg());  
	} 
	
	public static AbstractImagePrototype getIconPng(){
		
		return AbstractImagePrototype.create(ICONS.png());  
	} 
	
	public static AbstractImagePrototype getIconTiff(){
		
		return AbstractImagePrototype.create(ICONS.tiff());  
	} 
	
	public static AbstractImagePrototype getIconPdf(){
		
		return AbstractImagePrototype.create(ICONS.pdf());  
	}
	
	public static AbstractImagePrototype getIconXml(){
		
		return AbstractImagePrototype.create(ICONS.xml());  
	} 
	
	public static AbstractImagePrototype getIconHtml(){
		
		return AbstractImagePrototype.create(ICONS.html());  
	} 
	
	public static AbstractImagePrototype getIconJava(){
		
		return AbstractImagePrototype.create(ICONS.java());  
	} 
	
	public static AbstractImagePrototype getIconDoc(){
		
		return AbstractImagePrototype.create(ICONS.doc());  
	} 
	
	public static AbstractImagePrototype getIconTxt(){
		
		return AbstractImagePrototype.create(ICONS.txt());  
	} 
	
	public static AbstractImagePrototype getIconMovie(){
		
		return AbstractImagePrototype.create(ICONS.movie());  
	} 
	
	public static AbstractImagePrototype getIconAddFolder(){
		
		return AbstractImagePrototype.create(ICONS.addFolder());  
	} 
	
	public static AbstractImagePrototype getIconAddFolder32(){
		
		return AbstractImagePrototype.create(ICONS.addFolder32());  
	} 
	
	public static AbstractImagePrototype getIconRenameItem(){
		
		return AbstractImagePrototype.create(ICONS.renameItem());  
	} 
	
	public static AbstractImagePrototype getIconRenameItem32(){
		
		return AbstractImagePrototype.create(ICONS.renameItem32());  
	} 
	
	public static AbstractImagePrototype getIconFileUpload(){
		
		return AbstractImagePrototype.create(ICONS.uploadFile());  
	} 
	
	public static AbstractImagePrototype getIconFileUpload32(){
		
		return AbstractImagePrototype.create(ICONS.uploadFile32());  
	} 
	
	public static AbstractImagePrototype getIconDeleteFolder(){
		
		return AbstractImagePrototype.create(ICONS.deleteFolder());  
	} 
	
	public static AbstractImagePrototype getIconDeleteItem(){
		
		return AbstractImagePrototype.create(ICONS.deleteItem());  
	} 
	
	public static AbstractImagePrototype getIconDeleteItem32(){
		
		return AbstractImagePrototype.create(ICONS.deleteItem32());  
	} 
	
	public static AbstractImagePrototype getIconFolder(){
		
		return AbstractImagePrototype.create(ICONS.folder());  
	} 
	
	public static AbstractImagePrototype getIconAudio(){
		
		return AbstractImagePrototype.create(ICONS.audio());  
	} 
	
	public static AbstractImagePrototype getIconArchiveUpload(){
		
		return AbstractImagePrototype.create(ICONS.archiveUpload());  
	} 
	
	public static AbstractImagePrototype getIconBiodiversity(){
		
		return AbstractImagePrototype.create(ICONS.biodiversity());  
	} 
	
	public static AbstractImagePrototype getIconImages(){
		
		return AbstractImagePrototype.create(ICONS.images());  
	} 
	
	public static AbstractImagePrototype getIconDocuments(){
		
		return AbstractImagePrototype.create(ICONS.documents());  
	} 
	
	public static AbstractImagePrototype getIconSearch() {
		
		return AbstractImagePrototype.create(ICONS.search());  
	}
	
	public static AbstractImagePrototype getIconLinks(){
		
		return AbstractImagePrototype.create(ICONS.links());  
	} 
	
	public static AbstractImagePrototype getIconReport(){
		return AbstractImagePrototype.create(ICONS.report());  
	} 
	
	public static AbstractImagePrototype getIconReportTemplate() {
		return AbstractImagePrototype.create(ICONS.reportTemplate());  
	}

	public static AbstractImagePrototype getIconTimeSeries(){
		return AbstractImagePrototype.create(ICONS.timeSeries());  
	} 
	
	public static AbstractImagePrototype getIconDownload(){
		return AbstractImagePrototype.create(ICONS.download());  
	} 
	
	public static AbstractImagePrototype getIconCancel(){
		return AbstractImagePrototype.create(ICONS.cancel());  
	} 
	
	public static AbstractImagePrototype getIconToggleList() {
		return AbstractImagePrototype.create(ICONS.toggleList());  
	}
	
	public static AbstractImagePrototype getIconToggleGroup() {
		return AbstractImagePrototype.create(ICONS.toggleGroup());  
	}
	
	public static AbstractImagePrototype getIconToggleIcon() {
		return AbstractImagePrototype.create(ICONS.toggleIcon());  
	}
	
	public static AbstractImagePrototype getIconSave() {
		return AbstractImagePrototype.create(ICONS.save());  
	}
	
	public static AbstractImagePrototype getIconStar() {
		return AbstractImagePrototype.create(ICONS.star());  
	}
	
	public static AbstractImagePrototype getIconPreview() {
		return AbstractImagePrototype.create(ICONS.preview());  
	}
	
	public static AbstractImagePrototype getIconShow() {
		return AbstractImagePrototype.create(ICONS.show());  
	}
	
	public static AbstractImagePrototype getIconOpenUrl() {
		return AbstractImagePrototype.create(ICONS.openUrl());  
	}
	
	public static AbstractImagePrototype getIconAddUrl() {
		return AbstractImagePrototype.create(ICONS.addUrl());  
	}
	
	public static AbstractImagePrototype getIconSendTo() {
		return AbstractImagePrototype.create(ICONS.sendTo());  
	}
	
	public static AbstractImagePrototype getIconCheckUser() {
		return AbstractImagePrototype.create(ICONS.checkUser());  
	}
	
	public static AbstractImagePrototype getIconMessagesReceived() {
		return AbstractImagePrototype.create(ICONS.inboxReceived());  
	}

	public static AbstractImagePrototype getIconMessagesSent() {
		return AbstractImagePrototype.create(ICONS.inboxSent());  
	}
	
	public static AbstractImagePrototype getIconEmail() {
		return AbstractImagePrototype.create(ICONS.email());  
	}
	
	public static AbstractImagePrototype getIconOpenEmail() {
		return AbstractImagePrototype.create(ICONS.openEmail());  
	}
	
	public static AbstractImagePrototype getIconSaveAttachments() {
		return AbstractImagePrototype.create(ICONS.saveAttachs());  
	}
	
	public static AbstractImagePrototype getIconDownloadEmails16x16() {
		return AbstractImagePrototype.create(ICONS.downloadEmail16x16());  
	}
	
	public static AbstractImagePrototype getIconDownloadEmails() {
		return AbstractImagePrototype.create(ICONS.downloadEmail());  
	}

	public static AbstractImagePrototype getIconEmailRead() {
		return AbstractImagePrototype.create(ICONS.emailRead()); 
	}

	public static AbstractImagePrototype getIconEmailNotRead() {
		return AbstractImagePrototype.create(ICONS.emailNotRead()); 
	}
	
	public static AbstractImagePrototype getIconDeleteMessage16x16() {
		return AbstractImagePrototype.create(ICONS.emailDelete16x16()); 
	}
	
	public static AbstractImagePrototype getIconDeleteMessage() {
		return AbstractImagePrototype.create(ICONS.emailDelete()); 
	}
	
	public static AbstractImagePrototype getIconEmailForward16x16() {
		return AbstractImagePrototype.create(ICONS.emailForward16x16());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype getIconEmailForward() {
		return AbstractImagePrototype.create(ICONS.emailForward());
	}

	
	public static AbstractImagePrototype getIconCopy() {
		return AbstractImagePrototype.create(ICONS.copy());
	}

	public static AbstractImagePrototype getIconPaste() {
		return AbstractImagePrototype.create(ICONS.paste());
	}
	
	public static AbstractImagePrototype getIconRefresh() {
		return AbstractImagePrototype.create(ICONS.refresh());
	}
	
//	public static AbstractImagePrototype getIconBulkUpdate() {
//		return AbstractImagePrototype.create(ICONS.loading2());
//	}
	
	public static AbstractImagePrototype getIconLoading() {
		return AbstractImagePrototype.create(ICONS.loading());
	}

	public static AbstractImagePrototype getIconLoadingOff() {
		return AbstractImagePrototype.create(ICONS.loadingOff());
	}
	
	public static AbstractImagePrototype getIconLoading2() {
		return AbstractImagePrototype.create(ICONS.loading2());
	}
	
	public static AbstractImagePrototype getIconDelete2() {
		return AbstractImagePrototype.create(ICONS.delete2());
	}

	public static AbstractImagePrototype getIconUrlWebDav() {
		return AbstractImagePrototype.create(ICONS.urlWebDav());
	}
	
	public static AbstractImagePrototype getIconRemoveFilter() {
		return AbstractImagePrototype.create(ICONS.removeFilter());
	}
	
	public static AbstractImagePrototype getIconNewMail() {
		return AbstractImagePrototype.create(ICONS.createNewMail());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype getIconNewMail16x16() {
		return AbstractImagePrototype.create(ICONS.createNewMail16x16());
	}
	
	public static AbstractImagePrototype getIconReplyMail16x16() {
		return AbstractImagePrototype.create(ICONS.replyMail16x16());
	}
	
	public static AbstractImagePrototype getIconReplyMail() {
		return AbstractImagePrototype.create(ICONS.replyMail());
	}
	
	public static AbstractImagePrototype getIconReplyAllMail16x16() {
		return AbstractImagePrototype.create(ICONS.replyAllMail16x16());
	}
	
	public static AbstractImagePrototype getIconReplyAllMail() {
		return AbstractImagePrototype.create(ICONS.replyAllMail());
	}
	
	public static AbstractImagePrototype getIconWorkflowReport() {
		return AbstractImagePrototype.create(ICONS.workflowReport());
	}

	public static AbstractImagePrototype getIconWorkflowTemplate() {
		return AbstractImagePrototype.create(ICONS.workflowTemplate());
	}

	public static AbstractImagePrototype getIconWebDav() {
		return AbstractImagePrototype.create(ICONS.webDav());
	}
	
	public static AbstractImagePrototype getIconResourceLink() {
		return AbstractImagePrototype.create(ICONS.resourceLink());
	}
	/**
	 * @return
	 */
	public static AbstractImagePrototype getIconGcubeItem() {
		return AbstractImagePrototype.create(ICONS.gcubeItem());
	}


	//ImageResources
	public static ImageResource getImagePathSeparator(){
		return ICONS.separatorPath();
	}
	
	public static ImageResource getImageLoading() {
		return ICONS.loading();
	}

	public static ImageResource getImageHardDisk(){
		return ICONS.hardDisk();
	}
	
	public static ImageResource getImageSearch(){
		return ICONS.search();
	}
	
	public static ImageResource getImageFolder(){
		return ICONS.folder();
	}
	
	public static ImageResource getImageCancel() {
		return ICONS.cancel();
	}
	
	public static ImageResource getImageDelete() {
		return ICONS.delete2();
	}
	
	public static ImageResource getImageAttachs() {
		return ICONS.attach();  
	}
	

	
	public static AbstractImagePrototype getIconByExtension(String fileExtension) {

		if (MPEG.equals(fileExtension) || SWF.equals(fileExtension) || FLV.equals(fileExtension) || AVI.equals(fileExtension)) {
			return Resources.getIconMovie();
		} else if (JPEG.equals(fileExtension) || JPG.equals(fileExtension)) {
			return Resources.getIconJpeg();
		} else if (MSWORD.equals(fileExtension) || MSWORD.equals(fileExtension)) {
			return Resources.getIconDoc();
		} else if (XML.equals(fileExtension)) {
			return Resources.getIconXml();
		} else if (JAVA.equals(fileExtension)) {
			return Resources.getIconJava();
		} else if (HTML.equals(fileExtension)) {
			return Resources.getIconHtml();
		} else if (PNG.equals(fileExtension)) {
			return Resources.getIconPng();
		} else if (PDF.equals(fileExtension)) {
			return Resources.getIconPdf();
		} else if (TIFF.equals(fileExtension)) {
			return Resources.getIconTiff();
		} else if (SVG.equals(fileExtension)) {
			return Resources.getIconSvg();
		} else if (GIF.equals(fileExtension)) {
			return Resources.getIconGif();
		} else if (TXT.equals(fileExtension)) {
			return Resources.getIconTxt();
		}
		return Resources.getIconTable();
	}

	
	public static AbstractImagePrototype getIconByFolderItemType(GXTFolderItemTypeEnum itemType){
		
		if(itemType!=null){

			if(itemType.equals(GXTFolderItemTypeEnum.ANNOTATION)){
				return Resources.getIconTxt();
			}else if(itemType.equals(GXTFolderItemTypeEnum.DOCUMENT)){
				return Resources.getIconTxt();
			}else if(itemType.equals(GXTFolderItemTypeEnum.EXTERNAL_FILE)){
				return Resources.getIconTable();
			}else if(itemType.equals(GXTFolderItemTypeEnum.EXTERNAL_IMAGE)){
				return Resources.getIconJpeg();
			}else if(itemType.equals(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE)){
				return Resources.getIconPdf();
			}else if(itemType.equals(GXTFolderItemTypeEnum.EXTERNAL_URL)){
				return Resources.getIconHtml();
			}else if(itemType.equals(GXTFolderItemTypeEnum.IMAGE_DOCUMENT)){
				return Resources.getIconPng();
			}else if(itemType.equals(GXTFolderItemTypeEnum.METADATA)){
				return Resources.getIconSvg();
			}else if(itemType.equals(GXTFolderItemTypeEnum.PDF_DOCUMENT)){
				return Resources.getIconPdf();
	//		}else if(itemType.equals(GXTFolderItemTypeEnum.QUERY)){
	//			return Resources.getIconTable();
			}else if(itemType.equals(GXTFolderItemTypeEnum.GCUBE_ITEM)){
				return Resources.getIconGcubeItem();
			}else if(itemType.equals(GXTFolderItemTypeEnum.TIME_SERIES)){
				return Resources.getIconTimeSeries();
			}else if(itemType.equals(GXTFolderItemTypeEnum.REPORT)){
				return Resources.getIconReport();
			}else if(itemType.equals(GXTFolderItemTypeEnum.REPORT_TEMPLATE)){
				return Resources.getIconReportTemplate();
			}else if(itemType.equals(GXTFolderItemTypeEnum.URL_DOCUMENT)){
				return Resources.getIconHtml();
			}else if(itemType.equals(GXTFolderItemTypeEnum.WORKFLOW_REPORT)){
				return Resources.getIconWorkflowReport();
			}else if(itemType.equals(GXTFolderItemTypeEnum.WORKFLOW_TEMPLATE)){
				return Resources.getIconWorkflowTemplate();
			}else if(itemType.equals(GXTFolderItemTypeEnum.FOLDER)){
				return Resources.getIconFolder();
			}else if(itemType.equals(GXTFolderItemTypeEnum.EXTERNAL_RESOURCE_LINK)){
				return Resources.getIconResourceLink();
			}
		}
		return Resources.getIconTable();
	}


	public static AbstractImagePrototype getIconByType(String type){
		
		if(type.equals(GXTFolderItemTypeEnum.FOLDER.toString()))
			return Resources.getIconFolder();
		
		int sl = type.indexOf("/");
		String extension = type.substring(sl+1, type.length());

		return Resources.getIconByExtension(extension);
	}





	
}
