package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Popover;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Labels and texts for core ckan information
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class InfoIconsLabels {

	// TAGS
	public static final String TAGS_INFO_ID_POPUP = "tags-popup-panel-info";
	public static final String TAGS_INFO_CAPTION = "Tags";
	public static final String TAGS_INFO_TEXT = "Tags are meaningful information that can be associated to the "
			+ "product and by means of them it can be retrieved. A tag can contain only alphanumeric characters. "
			+ "If the tag is composed by a single word it must have a size of at least two characters."
			+ "Examples of good tags: \"This is a sample tag\", \"tagY\". Example of bad tag: \"c\".";

	// LICENSES
	public static final String LICENSES_INFO_ID_POPUP = "licenses-popup-panel-info";
	public static final String LICENSES_INFO_CAPTION = "Licenses";
	public static final String LICENSES_INFO_TEXT = "License definitions and additional information can be found at <a href=\"http://opendefinition.org/licenses/\" target=\"_blank\">opendefinition.org</a>";

	// VISIBILITY
	public static final String VISIBILITY_INFO_ID_POPUP = "visibility-popup-panel-info";
	public static final String VISIBILITY_INFO_CAPTION = "Visibility";
	public static final String VISIBILITY_INFO_TEXT = "Restricted products can only be accessed by certain users, while  Public products can be accessed by anyone.";

	// AUTHOR
	public static final String AUTHOR_INFO_ID_POPUP = "author-popup-panel-info";
	public static final String AUTHOR_INFO_CAPTION = "Author's fullname";
	public static final String AUTHOR_INFO_TEXT = "The author of this product. Example: Joe Bloggs.";

	// AUTHOR EMAIL
	public static final String AUTHOR_EMAIL_INFO_ID_POPUP = "author-email-popup-panel-info";
	public static final String AUTHOR_EMAIL_INFO_CAPTION = "Author's email";
	public static final String AUTHOR_EMAIL_INFO_TEXT = "The author's email. Example: joe.bloggs@catalogue.com";

	// MAINTAINER
	public static final String MAINTAINER_INFO_ID_POPUP = "maintainer-popup-panel-info";
	public static final String MAINTAINER_INFO_CAPTION = "Maintainer";
	public static final String MAINTAINER_INFO_TEXT = "The maintainer of this product (a person or an organization). Examples: Joe Bloggs, D4Science";

	// MAINTAINER EMAIL
	public static final String MAINTAINER_EMAIL_INFO_ID_POPUP = "maintainer-email-popup-panel-info";
	public static final String MAINTAINER_EMAIL_INFO_CAPTION = "Maintainer's email";
	public static final String MAINTAINER_EMAIL_INFO_TEXT = "The maintainer's email. Example: joe.bloggs@catalogue.com";

	// PROFILES
	public static final String PROFILES_INFO_ID_POPUP = "product-profiles-popup-panel-info";
	public static final String PROFILES_INFO_CAPTION = "Product Profiles";
	public static final String PROFILES_INFO_TEXT = "Select a profile, different from none, for your product among the ones available";

	// RESOURCES
	public static final String RESOURCES_INFO_ID_POPUP = "resouces-popup-panel-info";
	public static final String RESOURCES_INFO_CAPTION = "Manage resource products";
	public static final String RESOURCES_INFO_TEXT = "Move the files you want to attach to the product on the right panel below. Double click on the item for changing resource's name or description."
			+ " Please consider that any complex hierarchy structure you may have will be flatten.";

	// CUSTOM FIELDS
	public static final String CUSTOM_FIELDS_INFO_ID_POPUP = "custom-fields-popup-panel-info";
	public static final String CUSTOM_FIELDS_INFO_CAPTION = "Product Custom Fields";
	public static final String CUSTOM_FIELDS_INFO_TEXT = "Custom fields are customable metadata that will be added to the product. You have to choose a unique key for the field and a value for this. You can remove them at any time until you create the product.";

	// TITLE
	public static final String TITLE_INFO_ID_POPUP = "title-popup-panel-info";
	public static final String TITLE_INFO_TEXT = "Product Title must contain only alphanumer characters, dots, underscore or hyphen minus. No others symbols are allowed. Please note that this field will be always visible, despite the product's visibility.";
	public static final String TITLE_INFO_CAPTION = "Product Title";
	
	// DESCRIPTION
	public static final String DESCRIPTION_INFO_ID_POPUP = "description-popup-panel-info";
	public static final String DESCRIPTION_INFO_TEXT = "Description of a few sentences, written in plain language. Should provide a sufficiently comprehensive overview of the resource for anyone, "
			+ "to understand its content, origins, and any continuing work on it. The description can be written at the end, since it summarizes key, information from the other metadata fields. Please note that this field will be always visible, despite the product's visibility.";
	public static final String DESCRIPTION_INFO_CAPTION = "Product Description";
	
	// GROUPS
	public static final String GROUPS_INFO_ID_POPUP = "groups-popup-panel-info";
	public static final String GROUPS_INFO_TEXT = "Associate this product to groups. A group is a view of products belonging to one or more organization.";
	public static final String GROUPS_INFO_CAPTION = "Product Groups";


	/**
	 * Prepare the popover and the gcube popup panel for information.
	 * @param text
	 * @param captionText
	 * @param iconElement
	 * @param popover
	 * @param focusPanel
	 */
	public static void preparePopupPanelAndPopover(
			final String popupId, 
			final String text, 
			final String captionText, 
			Icon iconElement, 
			Popover popover, 
			FocusPanel focusPanel,
			final List<String> popupOpenedIds
			){

		// prepare the popover
		popover.setText(new HTML("<p style='color:initial'>" + text +"</p>").getHTML());
		popover.setHeading(new HTML("<b>" + captionText +"</b>").getHTML());

		// set icon cursor
		iconElement.getElement().getStyle().setCursor(Cursor.HELP);

		// prepare the gcube dialog
		focusPanel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// Retrieve elemnt that should have this id
				GcubeDialogExtended popup = null;
				try{
					Element element = DOM.getElementById(popupId);
					popup = (GcubeDialogExtended) Widget.asWidgetOrNull(getWidget(element));
				}catch(Exception e){
					GWT.log("ERROR", e);
				}

				// if it doesn't exist, create it
				if(popup == null){

					popup = new GcubeDialogExtended(captionText, text);
					popup.getElement().setId(popupId);
					popup.setModal(false);

					// add its id
					popupOpenedIds.add(popupId);

				}

				// then center and show
				popup.center();
				popup.show();

			}
		});

	}

	/**
	 * Check if an element of such type is actually a widget
	 * @param element
	 * @return
	 */
	public static IsWidget getWidget(Element element) {
		EventListener listener = DOM
				.getEventListener(element);
		// No listener attached to the element, so no widget exist for this
		// element
		if (listener == null) {
			GWT.log("Widget is NULL");
			return null;
		}
		if (listener instanceof Widget) {
			// GWT uses the widget as event listener
			GWT.log("Widget is " + listener);
			return (Widget) listener;
		}
		return null;
	}

	/**
	 * Close any dialog box opened
	 */
	public static void closeDialogBox(List<String> popupOpenedIds) {

		for (String popupid : popupOpenedIds) {
			GcubeDialogExtended popup = null;
			try{
				Element element = DOM.getElementById(popupid);
				popup = (GcubeDialogExtended) Widget.asWidgetOrNull(getWidget(element));
				popup.hide();
			}catch(Exception e){
				GWT.log("ERROR", e);
			}	
		}
	}

}
