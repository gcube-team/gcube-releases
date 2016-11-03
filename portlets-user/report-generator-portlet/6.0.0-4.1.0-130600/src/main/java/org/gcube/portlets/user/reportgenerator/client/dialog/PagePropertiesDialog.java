package org.gcube.portlets.user.reportgenerator.client.dialog;



import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <code> OpenTemplateDialog </code> class is is the Dialog for showing template properties
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class PagePropertiesDialog extends GCubeDialog {

	/**
	 * this layout panel
	 */
	private VerticalPanel dialogPanel = new VerticalPanel();
	
	private TextBox templateNameLabelBox = new TextBox();


	/**
	 * 
	 * @param templateModel .
	 * @param presenter .
	 */
	public PagePropertiesDialog(final TemplateModel templateModel, final Presenter presenter) {
		setText("Report Properties");
		dialogPanel.setSpacing(4);

		HTML spacer = new HTML("<hr width=\"100%\" />");
		spacer.setHeight("30");

		/*
		 * template name part
		 */
		HTML templateNameLabel = new HTML("Template Name: ");

				
		templateNameLabelBox.setText(templateModel.getTemplateName());
		templateNameLabelBox.setReadOnly(true);
				
		templateNameLabelBox.setWidth("200");
		CellPanel templateNamaPanel = new HorizontalPanel();
		templateNamaPanel.add(templateNameLabel);
		templateNamaPanel.add(templateNameLabelBox);
		templateNamaPanel.setCellWidth(templateNameLabel, "120");

		
		
		dialogPanel.add(templateNamaPanel);

		dialogPanel.add(new HTML("<hr color=\"#DDDDDD\" height=\"1px\" width=\"100%\" />"));

		List<Metadata> metadatas =  presenter.getModel().getMetadata();
		int nRows = metadatas.size();
		Grid metadataGrid = new Grid(nRows, 2);
		int i = 0;
		for (Metadata md : metadatas) {
			metadataGrid.setWidget(i, 0, new HTML("<b>" + md.getAttribute() + ": </b>"));
			metadataGrid.setWidget(i, 1, new HTML(md.getValue()));
			i++;
		}

		dialogPanel.add(templateNamaPanel);

		dialogPanel.add(new HTML("<hr color=\"#DDDDDD\" height=\"1px\" width=\"100%\" />"));

		dialogPanel.add(metadataGrid);
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		HorizontalPanel buttonsContainer = new HorizontalPanel();

		// Add a cancel button at the bottom of the dialog
		Button cancelButton = new Button("Close",new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsContainer.setSpacing(10);
		buttonsContainer.add(cancelButton);
		buttonsPanel.add(buttonsContainer);


		dialogPanel.add(buttonsPanel);
		dialogPanel.setPixelSize(350, 275);
		setWidget(dialogPanel);

	}
}
