//package org.gcube.portlets.user.workspace.client.uploader;
//
//
//import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
//import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
//import org.gcube.portlets.user.workspace.client.event.CompletedFileUploadEvent;
//import org.gcube.portlets.user.workspace.client.model.FileModel;
//import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;
//
//import com.extjs.gxt.ui.client.widget.Window;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.http.client.Request;
//import com.google.gwt.http.client.RequestBuilder;
//import com.google.gwt.http.client.RequestCallback;
//import com.google.gwt.http.client.RequestException;
//import com.google.gwt.http.client.Response;
//import com.google.gwt.http.client.URL;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.ui.HTML;
//
//
///**
// * 
// * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
// * @Jan 20, 2014
// *
// */
//public class UpdateServiceUploader extends Window{  
//
//	private FileUploader fileUploaderDlg = null;
//	private String fileName = "";
//	private boolean overwrite = false;
//	private FileModel parent;
//	private String uploadType;
//
//	
//	/**
//	 * 
//	 * @param fileUploader
//	 * @param parent
//	 * @param uploadType
//	 * 
//	 * By default overwrite paramameter is false
//	 */
//	public UpdateServiceUploader(final FileUploader fileUploader, final FileModel parent, String uploadType){
//		
//		this.fileUploaderDlg = fileUploader;
//		this.parent = parent;
//		this.uploadType = uploadType;
//	}
//	
//	public static String encodeUrlDelimiters(String s) {
//	    if (s == null) {
//	      return null;
//	    }
//	    s = s.replaceAll(";", "%2F");
//	    s = s.replaceAll("/", "%2F");
//	    s = s.replaceAll(":", "%3A");
//	    s = s.replaceAll("\\?", "%3F");
//	    s = s.replaceAll("&", "%26");
//	    s = s.replaceAll("\\=", "%3D");
//	    s = s.replaceAll("\\+", "%2B");
//	    s = s.replaceAll("\\$", "%24");
//	    s = s.replaceAll(",", "%2C");
//	    s = s.replaceAll("#", "%23");
//	    return s;
//	}
//	
//
//	public void submitForm() throws Exception{
//		
//		if(fileName == null || fileName.isEmpty())
//			throw new Exception("File absolute path on server is null");
//		
//		String parameters = "";
//
//		String fileNameEscaped = URL.encodeQueryString(fileName);
//		parameters+=ConstantsExplorer.UPLOAD_FORM_ELEMENT+"="+fileNameEscaped+"&";
//		
//		if(parent.getIdentifier()!=null && !parent.getIdentifier().isEmpty())
//			parameters+=ConstantsExplorer.ID_FOLDER+"="+parent.getIdentifier()+"&";
//		else
//			throw new Exception("Parent Folder ID parameter is null or empty");
//		
//		if(uploadType!=null && !uploadType.isEmpty())
//			parameters+=ConstantsExplorer.UPLOAD_TYPE+"="+uploadType+"&";
//		else
//			throw new Exception("UploadType parameter is null or empty");
//		
//		parameters+=ConstantsExplorer.IS_OVERWRITE+"="+overwrite;
//
//		GWT.log("Encoded parameters are: "+parameters);
//		
////		String urlRequest = ConstantsExplorer.LOCAL_UPLOAD_WORKSPACE_SERVICE+"?"+parameters;
//		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, ConstantsExplorer.LOCAL_UPLOAD_WORKSPACE_SERVICE);
//		requestBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded");
//		
//		try {
//			
//			requestBuilder.sendRequest(parameters, new RequestCallback() {
//
//			    @Override
//			    public void onResponseReceived(Request request,  Response response) {
//			    	
////			    	int status = response.getStatusCode();
//
//					//expected <pre>200:Upload complete</pre>
//					/*we strip tags added by webserver, 
//					 * 
//					 * Massi fix because webkit returns 
//					 * <pre style="word-wrap: break-word; white-space: pre-wrap;">OK:File france_flag.png(0) imported correctly in /Workspace</pre>
//					 * 
//					 * TODO: recall it next time
//					 */
//					//String strippedResult = result.replace("<pre>", "").replace("</pre>", ""); //this won't work for webkit
//					//replaced by new HTML(result).getText()
//					String strippedResult = new HTML(response.getText()).getText();
//
//					final HandlerResultMessage resultMessage = HandlerResultMessage.parseResult(strippedResult);
//
//					switch (resultMessage.getStatus()) {
//						case ERROR: 
//							GWT.log("Error during upload: "+resultMessage.getMessage());
//							fileUploaderDlg.showRegisteringResult(false, resultMessage.getMessage());
//							break;
//						case UNKNOWN: 
//							GWT.log("Error during upload: "+resultMessage.getMessage());
//							fileUploaderDlg.showRegisteringResult(false, "Error during upload: "+resultMessage.getMessage());
//							break;
//						case WARN: {
//							GWT.log("Upload completed with warnings: "+resultMessage.getMessage());
//							fileUploaderDlg.showRegisteringResult(false, "Upload completed with warnings: "+resultMessage.getMessage());
//							break;
//						}
//						case OK: {
//							Timer t = new Timer() {
//								public void run() {
//									AppControllerExplorer.getEventBus().fireEvent(new CompletedFileUploadEvent(parent, null));
//									fileUploaderDlg.showRegisteringResult(true);
//								}
//							};
//	
//							t.schedule(250);
//						}
//					}
//			    }
//
//			    @Override
//			    public void onError(Request request, Throwable exception) {
//			    	fileUploaderDlg.showRegisteringResult(false);
//					return;
//			    }
//			});
//			
//		} catch (RequestException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void setOverwrite(){
//		overwrite = true;
//	}
//
//	public String getFileName() {
//		return fileName;
//	}
//
//	public void setFileName(String fileName) {
//		this.fileName = fileName;
//	}
//}  
//
