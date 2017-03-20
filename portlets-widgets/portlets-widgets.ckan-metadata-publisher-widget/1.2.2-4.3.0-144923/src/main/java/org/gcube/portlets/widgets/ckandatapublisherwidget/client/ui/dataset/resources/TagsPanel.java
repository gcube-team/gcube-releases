package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.gcubewidgets.client.elements.Span;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils.InfoIconsLabels;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class TagsPanel extends Composite{

	private static TagsPanelUiBinder uiBinder = GWT
			.create(TagsPanelUiBinder.class);

	interface TagsPanelUiBinder extends UiBinder<Widget, TagsPanel> {
	}

	@UiField TextBox tagsEnterTextBox;
	@UiField FlowPanel tagsPanel;
	@UiField Icon infoIconTags;
	@UiField FocusPanel focusPanelTags;
	@UiField Popover popoverTags;
	@UiField ControlGroup tagsInsertGroup;
	

	// regular expression for tags
	private static final String REGEX_TAG = "^[a-zA-Z0-9]*$";

	// tags list
	private List<String> tagsList = new ArrayList<String>();

	public TagsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Prepare icons
	 * @param popupOpenedIds
	 */
	public void prepareIcon(List<String> popupOpenedIds) {
		InfoIconsLabels.preparePopupPanelAndPopover(
				InfoIconsLabels.TAGS_INFO_ID_POPUP,
				InfoIconsLabels.TAGS_INFO_TEXT,
				InfoIconsLabels.TAGS_INFO_CAPTION,
				infoIconTags,
				popoverTags,
				focusPanelTags,
				popupOpenedIds
				);
	}

	@UiHandler("tagsEnterTextBox")
	void onAddTag(KeyDownEvent event){

		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if (!tagsEnterTextBox.getValue().trim().isEmpty()) {

				addTagElement(tagsEnterTextBox);

			}
		}
	}

	/**
	 * Add the tag as an element (inserted by the user)
	 */
	private void addTagElement(TextBox itemBox){

		if (itemBox.getValue() != null && !itemBox.getValue().trim().isEmpty()) {

			if(tagsList.contains(itemBox.getValue().trim())){
				itemBox.setValue("");
				return;
			}

			// ckan accepts only alphanumeric values
			String[] subTags = itemBox.getValue().trim().split(" ");
			if(subTags.length == 1){
				if(!subTags[0].matches(REGEX_TAG))
					return;
				if(subTags[0].length() <= 1)
					return;
			}else{
				for (int i = 0; i < subTags.length; i++) {
					String subTag = subTags[i];
					if(!subTag.matches(REGEX_TAG))
						return;
				}
			}

			final String value = itemBox.getValue().trim();
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("tag-style");
			Span tagText = new Span(itemBox.getValue());

			Span tagRemove = new Span("x");
			tagRemove.setTitle("Remove this tag");
			tagRemove.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					removeTag(displayItem, value);
				}
			});

			tagRemove.setStyleName("tag-style-x");
			displayItem.add(tagText);
			displayItem.add(tagRemove);
			itemBox.setValue("");
			itemBox.setFocus(true);
			tagsPanel.add(displayItem);
			tagsList.add(value);
		}
	}

	/**
	 * Add the tag as an element (when publishing from workspace)
	 */
	public void addTagElement(final String tag){

		if(tagsList.contains(tag))
			return;

		// ckan accepts only alphanumeric values
		String[] subTags = tag.trim().split(" ");
		if(subTags.length == 1){
			if(!subTags[0].matches(REGEX_TAG))
				return;
			if(subTags[0].length() <= 1)
				return;
		}else{
			for (int i = 0; i < subTags.length; i++) {
				String subTag = subTags[i];
				if(!subTag.matches(REGEX_TAG))
					return;
			}
		}

		final ListItem displayItem = new ListItem();
		displayItem.setStyleName("tag-style");
		Span p = new Span(tag);

		Span span = new Span("x");
		span.setTitle("Remove this tag");
		span.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent clickEvent) {
				removeTag(displayItem, tag);
			}
		});

		span.setStyleName("tag-style-x");
		displayItem.add(p);
		displayItem.add(span);
		tagsPanel.add(displayItem);
		tagsList.add(tag);
	}

	/**
	 * Remove a tag from the list
	 * @param displayItem
	 */
	private void removeTag(ListItem displayItem, String value) {

		tagsList.remove(value.trim());
		tagsPanel.remove(displayItem);

	}

	/**
	 * Remove all inserted tags
	 */
	public void removeTags(){

		tagsList.clear();
		tagsPanel.clear();

	}

	/**
	 * Return the tag list
	 * @return
	 */
	public List<String> getTags() {
		return tagsList;
	}

	/**
	 * Freeze tags
	 */
	public void freeze() {

		tagsEnterTextBox.setEnabled(false);
		for(int i = 0; i < tagsList.size(); i++){

			// get tag widget
			ListItem tagWidget = (ListItem)tagsPanel.getWidget(i);

			// get the "x" span
			tagWidget.getWidget(1).removeFromParent();

		}

	}

	/**
	 * Set the tag group panel type
	 * @param none
	 */
	public void setGroupPanelType(ControlGroupType type) {
	
		tagsInsertGroup.setType(type);
		
	}
}
