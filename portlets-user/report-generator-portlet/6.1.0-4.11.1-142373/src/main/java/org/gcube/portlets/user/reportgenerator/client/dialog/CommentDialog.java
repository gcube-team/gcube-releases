package org.gcube.portlets.user.reportgenerator.client.dialog;

import org.gcube.portlets.d4sreporting.common.client.ImageConstants;
import org.gcube.portlets.d4sreporting.common.client.uicomponents.resources.Images;
import org.gcube.portlets.user.reportgenerator.client.events.AddCommentEvent;
import org.gcube.portlets.user.reportgenerator.client.events.RemovedUserCommentEvent;
import org.gcube.portlets.user.reportgenerator.client.targets.ReportTextArea;
import org.gcube.portlets.user.reportgenerator.shared.UserBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentDialog extends DialogBox {
	private static final int PANEL_WIDTH = 200;

	VerticalPanel mainPanel = new VerticalPanel();
	AbsolutePanel header = new AbsolutePanel();
	TextArea area;
	Images images = GWT.create(Images.class);
	public CommentDialog(final HandlerManager eventBus, final ReportTextArea source, final UserBean user, final String previousComment, int areaHeight) {
		super(true);
		setStyleName("comment-popup");
		area = new TextArea();
		if (previousComment != null && !(previousComment.compareTo("") == 0)) {
			area.setText(previousComment);
		} else {
			area.setText(" - " + user.getFullName()+":\n");
		}
		if (areaHeight > 0) {
			area.setPixelSize(PANEL_WIDTH, areaHeight);
		} else {
			area.setPixelSize(PANEL_WIDTH, 85);
		}
		setSize(PANEL_WIDTH+"px", "100px");
		header.setPixelSize(PANEL_WIDTH, 15);
		final Image enterImage = new Image(ImageConstants.IMAGE_ARROW_ENTER);
		final Image closeImage = new Image(ImageConstants.IMAGE_CLOSE);
		final Image binImage = new Image(ImageConstants.IMAGE_BIN);
		closeImage.setStyleName("selectable");
		binImage.setStyleName("selectable");
		enterImage.setStyleName("selectable");
		header.add(enterImage, PANEL_WIDTH-46, -2);
		header.add(binImage, PANEL_WIDTH-27, 0);
		header.add(closeImage, PANEL_WIDTH-12, 3);
		binImage.setTitle("Discard this comment");
		closeImage.setTitle("Close and save");
		enterImage.setTitle("Add another comment");

		area.setStyleName("comment-popup-textarea");

		header.setStyleName("comment-popup-header");
		mainPanel.add(header);
		mainPanel.add(area);
		add(mainPanel);

		enterImage.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				area.setText(area.getText() + "\n______\n"+user.getFullName()+":\n");
				area.setPixelSize(PANEL_WIDTH, area.getOffsetHeight()+70);
			}
		});

		closeImage.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new AddCommentEvent(source, area.getText(), area.getOffsetHeight()));
				source.addCommentView();
				hide();
			}
		});

		binImage.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new RemovedUserCommentEvent(source));
				source.removeCommentView();
				hide();
			}
		});

	}

}
