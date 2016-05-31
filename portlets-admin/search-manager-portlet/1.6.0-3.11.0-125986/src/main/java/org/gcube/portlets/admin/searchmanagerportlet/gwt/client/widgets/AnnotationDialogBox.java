package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import java.util.ArrayList;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.FieldsAnnotationPanel;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.SearchManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AnnotationDialogBox extends DialogBox {
	
	private Label groupLabel = new Label("Select group");
	private Label annLabel = new Label("Add annotation name");
	private VerticalPanel hostPanel = new VerticalPanel();
	private TextBox annotationTextBox = new TextBox();
	private ListBox groupsList = new ListBox();
	
	private Button saveBtn = new Button("Save");
	
	public AnnotationDialogBox(final FieldsAnnotationPanel parent, ArrayList<String> groups) {
		
		setAnimationEnabled(true);
		setModal(true);
		setAutoHideEnabled(true);
		setText("Create Keyword");
				
		hostPanel.setSpacing(30);
		hostPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hostPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		
		
		HorizontalPanel groupsPanel = new HorizontalPanel();
		groupsPanel.setSpacing(20);
		groupsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		groupsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		groupsPanel.add(groupLabel);
		groupsPanel.add(groupsList);

		HorizontalPanel annPanel = new HorizontalPanel();
		annPanel.setSpacing(20);
		annPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		annPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		annPanel.add(annLabel);
		annPanel.add(annotationTextBox);
		
		hostPanel.add(groupsPanel);
		hostPanel.add(annPanel);
		hostPanel.add(saveBtn);
		hostPanel.setCellHorizontalAlignment(saveBtn, HasHorizontalAlignment.ALIGN_RIGHT);
		
		for (String g : groups)
			groupsList.addItem(g);
		
		
		saveBtn.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				AsyncCallback<Void> addKeywordCallback = new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						
					}

					public void onSuccess(Void result) {
						parent.loadAnnotations();
						parent.refreshTreeInfo();
						hide();
						
					}
				};SearchManager.smService.addKeywordToPresentationGroup(groupsList.getValue(groupsList.getSelectedIndex()), annotationTextBox.getText().trim(), addKeywordCallback);
				
			}
		});
		
		setWidget(hostPanel);
		
	}

}
