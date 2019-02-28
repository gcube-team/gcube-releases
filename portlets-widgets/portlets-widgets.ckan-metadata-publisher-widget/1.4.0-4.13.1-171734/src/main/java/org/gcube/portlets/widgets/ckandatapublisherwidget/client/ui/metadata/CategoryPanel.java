package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.metadata;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.PageHeader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CategoryPanel extends Composite{

	private static CategoryPanelUiBinder uiBinder = GWT
			.create(CategoryPanelUiBinder.class);

	interface CategoryPanelUiBinder extends UiBinder<Widget, CategoryPanel> {
	}

	@UiField VerticalPanel fieldsPanel;
	@UiField PageHeader categoryHeader;
	private List<MetaDataFieldSkeleton> fieldsForThisCategory;
	
	public CategoryPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * 
	 * @param fieldsForThisCategory
	 * @param title
	 * @param description
	 */
	public CategoryPanel(String title, String description) {
		initWidget(uiBinder.createAndBindUi(this));
		title = title == null ? "" : title;
		description = description == null ? "" : description;
		categoryHeader.setTitle(title);
		categoryHeader.setText(title);
		categoryHeader.setSubtext(description);
		categoryHeader.getElement().getStyle().setFloat(Float.LEFT);
	}

	/**
	 * Add a field to this widget
	 * @param fieldWidget
	 */
	public void addField(MetaDataFieldSkeleton fieldWidget) {
		if(fieldsForThisCategory == null){
			fieldsForThisCategory = new ArrayList<MetaDataFieldSkeleton>();
			fieldWidget.setVisible(true);
			fieldsPanel.setVisible(true);
		}
		fieldsForThisCategory.add(fieldWidget);
		fieldsPanel.add(fieldWidget);
	}

}
