package org.gcube.portlets.user.lastupdatedfiles.client.panel.ui;

import org.gcube.portlets.user.lastupdatedfiles.client.FileServiceAsync;
import org.gcube.portlets.user.lastupdatedfiles.client.bundle.IconImages;
import org.gcube.portlets.user.lastupdatedfiles.shared.ImageType;
import org.gcube.portlets.user.lastupdatedfiles.shared.LufFileItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author massi
 *
 */
/**
 * 
 * @author massi
 *
 */
public class FileItem extends Composite {

	private static FileItemUiBinder uiBinder = GWT
			.create(FileItemUiBinder.class);

	interface FileItemUiBinder extends UiBinder<Widget, FileItem> {
	}

	@UiField Image iconImage;
	@UiField HTML fileItem;
	
	private IconImages images = IconImages.INSTANCE;
	
	public FileItem(final FileServiceAsync fileService, final LufFileItem toDisplay) {
		initWidget(uiBinder.createAndBindUi(this));
		String nameLabel = toDisplay.getFilename(); 
		if (nameLabel.length() > 30)
			nameLabel = nameLabel.substring(0, 27) + "...";
		fileItem.setHTML("<a class=\"luflink\" href=\""+toDisplay.getFileDownLoadURL()+"\">"+nameLabel+"</a>");
		String theDate = DateTimeFormat.getFormat(PredefinedFormat.RFC_2822).format(toDisplay.getLastUpdated());
		fileItem.setTitle(toDisplay.getFilename() + " was last edited " + theDate + " by " + toDisplay.getOwnerUserName());
		iconImage.setUrl(getIconImage(toDisplay.getType()).getSafeUri());
		
		fileItem.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				fileService.setRead(toDisplay.getWorkspaceItemId(), new AsyncCallback<Void>() {					
					@Override
					public void onSuccess(Void result) { }
					
					@Override
					public void onFailure(Throwable caught) { }
				});
			}
		});
	}

	
	private ImageResource getIconImage(ImageType type) {
		switch (type) {
		case DOC:
			return images.docx();
		case XLS:
			return images.xls();
		case PPT:
			return images.pptx();
		case 
			PDF:
				return images.pdf();
		case IMAGE:
			return images.jpeg();
		case MOVIE:
			return images.avi();
		case HTML:
			return images.html();
		case RAR:
			return images.rar();
		case ZIP:
			return images.zip();
		default:
			return images.noType();
		}
	}
	
}
