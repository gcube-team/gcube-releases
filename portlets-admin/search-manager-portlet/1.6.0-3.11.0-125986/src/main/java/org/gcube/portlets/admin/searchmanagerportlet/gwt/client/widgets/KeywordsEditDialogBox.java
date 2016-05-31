package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.FieldsAnnotationPanel;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.SearchManager;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class KeywordsEditDialogBox extends DialogBox {

	private Label groupLabel = new Label("Select group");
	private VerticalPanel hostPanel = new VerticalPanel();
	private VerticalPanel annsPanel = new VerticalPanel();
	private ListBox groupsList = new ListBox();

	private Button removeBtn = new Button("Remove");

	private HashMap<String,ArrayList<String>> groupsAndKeywords = new HashMap<String, ArrayList<String>>();

	public KeywordsEditDialogBox(final FieldsAnnotationPanel parent) {

		setAnimationEnabled(true);
		setModal(true);
		setAutoHideEnabled(true);
		setText("Remove Keyword");

		hostPanel.setSpacing(30);
		hostPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hostPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		annsPanel.setSpacing(30);
		annsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		annsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		HorizontalPanel groupsPanel = new HorizontalPanel();
		groupsPanel.setSpacing(20);
		groupsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		groupsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		groupsPanel.add(groupLabel);
		groupsPanel.add(groupsList);



		hostPanel.add(groupsPanel);
		hostPanel.add(annsPanel);
		hostPanel.add(removeBtn);
		hostPanel.setCellHorizontalAlignment(removeBtn, HasHorizontalAlignment.ALIGN_RIGHT);

		AsyncCallback<HashMap<String,ArrayList<String>>> getKeywordsAndGroupsCallback = new AsyncCallback<HashMap<String,ArrayList<String>>>() {

			public void onFailure(Throwable caught) {


			}

			public void onSuccess(HashMap<String,ArrayList<String>> result) {
				groupsAndKeywords = result;

				Set<String> groups = groupsAndKeywords.keySet();
				for (String g : groups) {
					groupsList.addItem(g);
				}
				groupsList.setSelectedIndex(0);
				annsPanel.clear();
				for (String ann : groupsAndKeywords.get(groupsList.getItemText(groupsList.getSelectedIndex()))) {
					CheckBox c = new CheckBox(ann);
					c.setFormValue(ann);
					annsPanel.add(c);
				}

			}
		};SearchManager.smService.getGroupsAndKeywords(getKeywordsAndGroupsCallback);


		groupsList.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				annsPanel.clear();
				for (String ann : groupsAndKeywords.get(groupsList.getItemText(groupsList.getSelectedIndex()))) {
					CheckBox c = new CheckBox(ann);
					c.setFormValue(ann);
					annsPanel.add(c);
				}

			}
		});

		removeBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				ArrayList<String> keyWordsToDelete = new ArrayList<String>();
				for (int i=0; i<annsPanel.getWidgetCount(); i++) {
					Widget w = annsPanel.getWidget(i);
					if (((CheckBox)w).getValue())
						keyWordsToDelete.add(((CheckBox)w).getFormValue());
				}
				if (keyWordsToDelete.isEmpty())
					SearchManager.showInfoPopup("There are no keywords selected to remove");
				else {
					AsyncCallback<Void> keywordsToRemoveCallback = new AsyncCallback<Void>() {

						public void onFailure(Throwable caught) {

						}

						public void onSuccess(Void result) {
							parent.loadAnnotations();
							parent.refreshTreeInfo();
							hide();

						}
					};SearchManager.smService.removeKeywordsFromPresentationGroup(groupsList.getValue(groupsList.getSelectedIndex()), keyWordsToDelete, keywordsToRemoveCallback);
				}
			}
		});

		setWidget(hostPanel);

	}

}
