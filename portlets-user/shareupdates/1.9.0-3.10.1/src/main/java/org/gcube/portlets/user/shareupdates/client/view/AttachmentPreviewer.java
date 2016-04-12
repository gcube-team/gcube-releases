package org.gcube.portlets.user.shareupdates.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to show an attached file.
 * @author Costantino Perciante at ISTI-CNR
 *
 */
public class AttachmentPreviewer extends Composite {	

	private static AttachmentPreviewerUiBinder uiBinder = GWT
			.create(AttachmentPreviewerUiBinder.class);

	interface AttachmentPreviewerUiBinder extends
	UiBinder<Widget, AttachmentPreviewer> {
	}

	public AttachmentPreviewer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	//private static final String DELETE_ATTACHMENT = "Delete this attachment?";
	private static final String RETRY_TO_UPLOAD = "Retry to upload this attachment on the server.";

	@UiField
	HTMLPanel attachmentContainer;

	@UiField 
	HTML deleteAttachment;

	@UiField
	Image imagePreview;

	@UiField
	Label fileNameLabel;

	@UiField
	Label resultLabel;

	@UiField
	Image resultImage;

	@UiField
	HorizontalPanel attachmentResult;

	// Parent of this AttachmentPreviewer object
	private Placeholder parent;

	// the ShareUpdateForm 
	private ShareUpdateForm shareUpdateForm;

	// retry upload button reference
	private HTML retryButton;

	public AttachmentPreviewer(String fileName, String urlImagePreview, Placeholder parent, ShareUpdateForm shareUpdateForm) {

		initWidget(uiBinder.createAndBindUi(this));

		// set filename and temp attachment url
		this.imagePreview.setUrl(urlImagePreview);
		this.fileNameLabel.setTitle(fileName);
		this.imagePreview.setTitle(fileName);

		fileName = fileName.length() > 26 ? fileName.substring(0, 22) + " ..." : fileName;		
		this.fileNameLabel.setText(fileName);
		// style the delete button
		this.deleteAttachment.setStyleName("su-deleteAttachment");
		this.deleteAttachment.setTitle("Cancel");


		// save parent
		this.parent = parent;

		// save the shareUpdateForm object, since it maintains the list of attached files
		this.shareUpdateForm = shareUpdateForm;
	}

	@UiHandler("deleteAttachment")
	void onClick(ClickEvent e) {

		// alert the user (In some firefox versions, this may cause the bug 
		//uncaught exception: java.lang.AssertionError: Negative entryDepth value at exit -1)
		// due to the fact that a window.alert or window.confirm is invoked within an handler
		//		boolean confirm = Window.confirm(DELETE_ATTACHMENT);
		//
		//		if(!confirm)
		//			return;

		// we have to remove the AttachmentPreview object (that is, this object) and
		// remove the file from the List of AttachedFiles
		parent.remove(this);
		shareUpdateForm.removeAttachedFile(this);
		parent.resizeLastElementWidth();

	}

	/**
	 * set the label and the icon that shows if the file has been saved or not 
	 * @param result
	 * @param urlImageResult
	 */
	public void setResultAttachment(String result, String urlImageResult){
		this.resultLabel.setText(result);
		this.resultImage.setUrl(urlImageResult);
	}

	/**
	 * Change the image preview of the attachment from the default one
	 * @param urlImagePreview
	 */
	public void setImagePreview(String urlImagePreview){
		this.imagePreview.setUrl(urlImagePreview);
	}

	/**
	 * 
	 * Change style of part of this object to allow the user to retry to upload the file.
	 * 
	 */
	public void retryToUpload(final AttachmentPreviewer thisPreviewer) {

		// add the button to retry to upload such file
		retryButton = new HTML("<a>"+ "<span>Try Again</span></a>");
		retryButton.getElement().getStyle().setMarginLeft(5, Unit.PX);
		retryButton.getElement().getStyle().setCursor(Cursor.POINTER);
		retryButton.setTitle(RETRY_TO_UPLOAD);

		retryButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// we have to remove the AttachmentPreview object (that is, this object) and
				// remove the file from the List of AttachedFiles
				parent.remove(thisPreviewer);
				parent.resizeLastElementWidth();
				shareUpdateForm.removeAttachedFile(thisPreviewer);

			}
		});

		attachmentResult.add(retryButton);
	}

	public void changeAttachmentWidth(int newWidth, Unit unit){

		attachmentContainer.getElement().getStyle().setWidth(newWidth, unit);

	}

}
