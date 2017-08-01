///**
// * 
// */
//package org.gcube.portlets.user.workspace.client.uploader;
//
//import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
//import org.gcube.portlets.user.workspace.client.model.FileModel;
//import org.gcube.portlets.widgets.fileupload.client.view.UploadProgressDialog;
//
//import com.extjs.gxt.ui.client.widget.Info;
//import com.google.gwt.event.shared.HandlerManager;
//
//
///**
// * The Class FileUploader.
// *
// * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
// * Jul 15, 2015
// */
//public class FileUploader {
//	
//	
//	private final UploadProgressDialog dlg;
//	private FileModel folderParent;
//	private UpdateServiceUploader updateServiceUploader;
//	private String uploadType;
//	
//	/**
//	 * Instantiates a new file uploader.
//	 *
//	 * @param eventBus the event bus
//	 * @param folderParent the folder parent
//	 * @param uploadType the upload type
//	 */
//	public FileUploader(HandlerManager eventBus, FileModel folderParent, String uploadType) {
//		this.folderParent = folderParent;
//		this.uploadType = uploadType;
//
//		/**
//		 * Prepare new servlet uploader
//		 */
//		updateServiceUploader = new UpdateServiceUploader(this, folderParent, uploadType);
//		
//		String caption = "Upload ";
//		if(uploadType.compareTo(ConstantsExplorer.ARCHIVE)==0)
//			caption+= " a zip Archive";
//		else if(uploadType.compareTo(ConstantsExplorer.FILE)==0)
//			caption+= "File";
//			
//		caption+= " in: "+folderParent.getName();
//		
//		dlg = new UploadProgressDialog(caption,  eventBus, true);
//		dlg.center();
//		dlg.show();
//	}
//	
//
//	/**
//	 * Submit form.
//	 */
//	public void submitForm() {
//		
//		try {
//			dlg.submitForm();
//		} catch (Exception e) {
//			Info.display("Error", "Sorry an error occurred on the server "+e.getLocalizedMessage() + ". Please try again later");
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Overwrite.
//	 */
//	public void overwrite(){
//		updateServiceUploader.setOverwrite();
//	}
//	
//	/**
//	 * Submit servlet form.
//	 *
//	 * @param absolutePathOnServer the absolute path of the file uploaded on the server
//	 */
//	public void submitServletForm(String absolutePathOnServer){
//		
//		updateServiceUploader.setFileName(absolutePathOnServer);
//		try {
//			updateServiceUploader.submitForm();
//		} catch (Exception e) {
//			dlg.showRegisteringResult(false);
//		}
//	}
//
//	/**
//	 * Gets the folder parent.
//	 *
//	 * @return the folder parent
//	 */
//	public FileModel getFolderParent() {
//		return folderParent;
//	}
//
//	/**
//	 * Gets the upload type.
//	 *
//	 * @return the upload type
//	 */
//	public String getUploadType() {
//		return uploadType;
//	}
//
//	/**
//	 * Show registering result.
//	 *
//	 * @param b the b
//	 * @param message the message
//	 */
//	public void showRegisteringResult(boolean b, String message) {
//		this.dlg.showRegisteringResult(b, message);
//	}
//
//
//	/**
//	 * Show registering result.
//	 *
//	 * @param b the b
//	 */
//	public void showRegisteringResult(boolean b) {
//		this.dlg.showRegisteringResult(b);
//	}
//
//}
