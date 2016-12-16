package org.gcube.portlets.admin.vredefinition.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SummaryPanel extends Composite{

	private static SummaryPanelUiBinder uiBinder = GWT
			.create(SummaryPanelUiBinder.class);

	interface SummaryPanelUiBinder extends UiBinder<Widget, SummaryPanel> {
	}

	@UiField 
	InlineLabel vreNameSummary;
	@UiField 
	InlineLabel vreDesignerSummary;
	@UiField 
	InlineLabel vreManagerSummary;
	@UiField 
	InlineLabel vreFromSummary;
	@UiField 
	InlineLabel vreToSummary;
	@UiField 
	InlineLabel vreDescriptionSummary;
	@UiField 
	VerticalPanel functionalities;

	public SummaryPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Set vre's main information
	 * @param name
	 * @param manager
	 * @param designer
	 * @param description
	 * @param from
	 * @param to
	 */
	public void setVreMainInformation(String name, String manager, String designer, String description, Date from, Date to){

		vreNameSummary.setText(name);
		vreDesignerSummary.setText(designer);
		vreManagerSummary.setText(manager);
		vreDescriptionSummary.setText(description);

		// format date in dd/mm/yy
		DateTimeFormat formatter =  DateTimeFormat.getFormat("dd.MM.yyyy");// new DateTimeFormat("dd.MM.yyyy");
		vreFromSummary.setText(formatter.format(from));
		vreToSummary.setText(formatter.format(to));

	}

	/**
	 * Add the functionalities tree
	 * @param w
	 */
	public void addFunctionality(Widget w){
		functionalities.add(w);
	}

	/**
	 * Remove the functionalities tree
	 * @param w
	 */
	public void clearFunctionalitiesPanel() {
		functionalities.clear();
	}
}
