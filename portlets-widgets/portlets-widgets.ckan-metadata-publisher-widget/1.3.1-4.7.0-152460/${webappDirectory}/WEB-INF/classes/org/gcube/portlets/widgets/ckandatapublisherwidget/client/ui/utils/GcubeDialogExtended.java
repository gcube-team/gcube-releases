package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.TwinColumnSelection.ResourceInfoForm;

import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * Extended version of the GcubeDialog with close symbol on the caption
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GcubeDialogExtended extends GCubeDialog {

	/**
	 * For simple information
	 * @param captionText
	 * @param text
	 */
	public GcubeDialogExtended(String captionText, String text){

		// add custom style
		addStyleName("metadata-popup-panel");

		// create an anchor to close the dialogbox
		final Anchor closeAnchor = new Anchor("x");
		closeAnchor.setTitle("Close");

		// create a panel that will be put into the caption
		FlexTable captionLayoutTable = new FlexTable();
		captionLayoutTable.setText(0, 0, captionText);
		captionLayoutTable.setWidget(0, 3, closeAnchor);
		captionLayoutTable.getCellFormatter().setHorizontalAlignment(0, 3, HasHorizontalAlignment.ALIGN_RIGHT);
		captionLayoutTable.setWidth("100%");
		HTML caption = (HTML) getCaption();
		caption.getElement().getStyle().setCursor(Cursor.MOVE);
		caption.getElement().appendChild(captionLayoutTable.getElement());
		caption.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// get the event
				EventTarget target = event.getNativeEvent().getEventTarget();
				Element targetElement = (Element) target.cast();

				// fire the event to the anchor
				if (targetElement == closeAnchor.getElement()) {
					closeAnchor.fireEvent(event);
				}
			}
		});
		closeAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		add(new Paragraph(text));
	}

	/**
	 * For simple information
	 * @param captionText
	 * @param text
	 */
	public GcubeDialogExtended(final ResourceInfoForm form){

		// add custom style
		addStyleName("metadata-popup-panel");

		// create an anchor to close the dialogbox
		final Anchor closeAnchor = new Anchor("x");
		closeAnchor.setTitle("Close");
		closeAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		// create a panel that will be put into the caption
		FlexTable captionLayoutTable = new FlexTable();
		captionLayoutTable.setText(0, 0, "About Resource");
		captionLayoutTable.setWidget(0, 3, closeAnchor);
		captionLayoutTable.getCellFormatter().setHorizontalAlignment(0, 3, HasHorizontalAlignment.ALIGN_RIGHT);
		captionLayoutTable.setWidth("100%");
		HTML caption = (HTML) getCaption();
		caption.getElement().getStyle().setCursor(Cursor.MOVE);
		caption.getElement().appendChild(captionLayoutTable.getElement());
		caption.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventTarget target = event.getNativeEvent().getEventTarget();
				Element targetElement = (Element) target.cast();
				if (targetElement == closeAnchor.getElement()) {
					closeAnchor.fireEvent(event);
				}
			}
		});

		setWidget(form);
		Widget widget = getWidget();

		ClickHandler click = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventTarget target = event.getNativeEvent().getEventTarget();
				Element targetElement = (Element) target.cast();
				GWT.log("Target elem is " + targetElement);
				if (targetElement == form.resourceDescription.getElement()) {
					GWT.log("Fired click");
					form.resourceDescription.fireEvent(event);
				}else if (targetElement == form.resourceName.getElement()) {
					GWT.log("Fired click");
					form.resourceName.fireEvent(event);
				}else if(targetElement == form.getElement()){
					form.fireEvent(event);
				}
			}
		};
		widget.addDomHandler(click, ClickEvent.getType());

		widget.addDomHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				EventTarget target = event.getNativeEvent().getEventTarget();
				Element targetElement = (Element) target.cast();
				GWT.log("Target elem is " + targetElement);
				if (targetElement == form.resourceDescription.getElement()) {
					GWT.log("Fired click");
					form.resourceDescription.fireEvent(event);
				}else if (targetElement == form.resourceName.getElement()) {
					GWT.log("Fired click");
					form.resourceName.fireEvent(event);
				}else if(targetElement == form.getElement()){
					form.fireEvent(event);
				}
			}
		}, DoubleClickEvent.getType());
	}

}
