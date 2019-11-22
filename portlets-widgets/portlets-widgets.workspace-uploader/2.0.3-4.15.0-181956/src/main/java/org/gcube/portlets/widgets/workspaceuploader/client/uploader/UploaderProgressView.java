/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import org.gcube.portlets.widgets.workspaceuploader.client.StringUtil;
import org.gcube.portlets.widgets.workspaceuploader.client.events.CancelUploadEvent;
import org.gcube.portlets.widgets.workspaceuploader.client.resource.WorkspaceUploaderResources;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.bar.ProgressBar;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class UploaderProgressView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 12, 2015
 */
public class UploaderProgressView {

	private VerticalPanel vp = new VerticalPanel();
	private static final int MAX_CHARS = 50;
	private HorizontalPanel hp = new HorizontalPanel();
	private HorizontalPanel hpBar = new HorizontalPanel();
	private HTML html = new HTML();
	private ProgressBar bar = new ProgressBar();
	private HandlerManager eventBus;
	private boolean cancel = false;
	private Image cancelImg = null;

	/**
	 * Instantiates a new uploader progress view.
	 *
	 * @param uploader the uploader
	 * @param fileName the file name
	 * @param eventBus the event bus
	 */
	public UploaderProgressView(WorkspaceUploaderItem uploader, final String fileName, HandlerManager eventBus) {
		this.eventBus = eventBus;
		cancelImg = WorkspaceUploaderResources.getImageCancel();
		cancelImg.setTitle("Cancel upload");
		cancelImg.addStyleName("cancel-upload");
		hpBar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		String text = "<div><img src='"+WorkspaceUploaderResources.getImageLoading().getUrl()+"'>";
		String msg = StringUtil.ellipsize("Uploading "+fileName, MAX_CHARS, 0);
		text+="<span style='margin-left:5px; vertical-align: top;'>"+msg+"</span>";
		text+="</div>";

		html.setHTML(text);
		html.setTitle("Uploading "+fileName);
		hp.add(html);

		bar.update(0);
		setVisibleBar(false);
		setVisibleCancel(false);
		vp.add(hp);

		hpBar.add(cancelImg);
		hpBar.add(bar);
		vp.add(hpBar);
//		vp.add(bar);
	}

	/**
	 * @param b
	 */
	private void setVisibleCancel(boolean b) {
		cancelImg.setVisible(b);
	}

	public void setVisibleBar(boolean bool){
		bar.setVisible(bool);
	}

	/**
	 * Update.
	 *
	 * @param uploader the uploader
	 */
	public void update(WorkspaceUploaderItem uploader){
		String text;
		try{
			switch(uploader.getUploadStatus()){
			case COMPLETED:
				setVisibleBar(true);
				text = "<div><img src='"+WorkspaceUploaderResources.getImageCompleted().getUrl()+"'>";
				String msgClt = null;
				if(uploader.getFile().getVersionName()!=null)
					//msgClt = "v. "+ uploader.getFile().getVersionName() +" of "+StringUtil.ellipsize(uploader.getFile().getFileName()+" uploaded successfully!", MAX_CHARS-10, 0);
					msgClt = StringUtil.ellipsize(uploader.getFile().getFileName(), MAX_CHARS-23, 0) +" v."+ uploader.getFile().getVersionName() +" uploaded successfully!";
				else
					msgClt = StringUtil.ellipsize(uploader.getFile().getFileName()+" uploaded successfully!", MAX_CHARS, 0);

				text+="<span style='margin-left:5px; vertical-align: top;'>"+msgClt+"</span></div>";
	//			GWT.log(text);
				html.setHTML(text);
				html.setTitle(uploader.getStatusDescription());
				bar.update(uploader.getUploadProgress().getLastEvent().getReadPercentage());
				try{
					hpBar.clear();
				}catch (Exception e) {}
				break;
			case FAILED:
				setVisibleBar(true);
				text = "<div><img src='"+WorkspaceUploaderResources.getImageFailed().getUrl()+"'>";
				text+="<span style='margin-left:5px; vertical-align: top;'>"+StringUtil.ellipsize(uploader.getStatusDescription(), MAX_CHARS, 0)+"</span></div>";
	//			GWT.log(text);
				html.setHTML(text);
				html.setTitle(uploader.getStatusDescription());
	//			bar.update(uploader.getUploadProgress().getLastEvent().getReadPercentage());
				try{
					hpBar.clear();
				}catch (Exception e) {}
				break;
			case ABORTED:
				setVisibleBar(true);
				text = "<div><img src='"+WorkspaceUploaderResources.getImageAbort().getUrl()+"'>";
				text+="<span style='margin-left:5px; vertical-align: top;'>"+StringUtil.ellipsize(uploader.getStatusDescription(), MAX_CHARS, 0)+"</span></div>";
	//			GWT.log(text);
				html.setHTML(text);
				html.setTitle(uploader.getStatusDescription());
	//			bar.update(uploader.getUploadProgress().getLastEvent().getReadPercentage());
				try{
					hpBar.clear();
				}catch (Exception e) {}
				break;
			case IN_PROGRESS:
				setVisibleBar(true);
	//			text = "<div><img src='"+WorkspaceUploaderResources.getImageUpload().getUrl()+"'>";
				text = "<div><img src='"+WorkspaceUploaderResources.getImageLoading().getUrl()+"'>";

				String msg = "";
				if(uploader.getUploadProgress().getLastEvent().getReadPercentage()<100){
					msg = StringUtil.ellipsize(uploader.getFile().getFileName(), MAX_CHARS, 0);
				}else{ //is 100%
					String message = UploadingMessageBeforeCompleted.getMessage(uploader.getClientUploadKey(), uploader.getFile().getFileName());
					msg = StringUtil.ellipsize(message, MAX_CHARS, 0);
				}

				text+="<span style='margin-left:5px; vertical-align: top;'>"+msg+"</span>";
				text+="</div>";
	//			GWT.log(text);
				html.setHTML(text);
				html.setTitle(uploader.getStatusDescription());

				//TODO CANCEL OPERATION MUST BE ENHANCED IN ORDER TO CANCELL ALL UPLOADS
				/*
				if(uploader.getUploadProgress().getLastEvent().getReadPercentage()!=100 && !cancel){
					cancel = true;
					handleCancelUpload(uploader);
					setVisibleCancel(true);
				}else if(uploader.getUploadProgress().getLastEvent().getReadPercentage()==100 && cancel){
					try{
						setVisibleCancel(false);
	//					hp.remove(cancelImg);
					}catch (Exception e) {}
				}*/

				bar.update(uploader.getUploadProgress().getLastEvent().getReadPercentage());
				break;
			case WAIT:
				setVisibleBar(false);
				text = "<div><img src='"+WorkspaceUploaderResources.getImageLoading().getUrl()+"'>";
				String descr = "";
				if(uploader.getFile()!=null && uploader.getFile().getFileName()!=null)
					descr = uploader.getFile().getFileName();
				else
					descr = uploader.getStatusDescription();

				text+="<span style='margin-left:5px; vertical-align: top;'>"+StringUtil.ellipsize(descr, MAX_CHARS, 0)+"</span></div>";
				text+="</div>";
	//			GWT.log(text);
				html.setHTML(text);

				if(uploader.getUploadProgress()!=null && uploader.getUploadProgress().getLastEvent()!=null)
					bar.update(uploader.getUploadProgress().getLastEvent().getReadPercentage());
				else
					bar.update(0);

				break;
			default:
				break;
			}
		}catch(Exception e) {
			GWT.log("error during update");
		}
	}

	/**
	 * Handle cancel upload.
	 *
	 * @param uploader the uploader
	 * @return the image
	 */
	private Image handleCancelUpload(final WorkspaceUploaderItem uploader){

		cancelImg.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GWT.log("Click cancel");
				String fileName = uploader.getFile()!=null? uploader.getFile().getFileName(): "";
				if(Window.confirm("Confirm cancel uploading "+fileName+"?")){
					hp.clear();
					HTML html = new HTML();
					hp.add(html);
					String text = "<div><img src='"+WorkspaceUploaderResources.getImageCancel().getUrl()+"'>";
					String msg = StringUtil.ellipsize("Aborting upload: "+fileName, MAX_CHARS, 0);
					text+="<span style='margin-left:5px; vertical-align: top;'>"+msg+"</span></div>";
					html.setHTML(text);
					eventBus.fireEvent(new CancelUploadEvent(uploader, UploaderProgressView.this, fileName));
				}
			}
		});
		return cancelImg;
	}


	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public VerticalPanel getPanel() {
		return vp;
	}

}
