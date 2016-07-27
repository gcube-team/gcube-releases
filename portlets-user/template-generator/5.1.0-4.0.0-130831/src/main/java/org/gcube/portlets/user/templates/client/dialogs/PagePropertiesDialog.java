package org.gcube.portlets.user.templates.client.dialogs;

import java.util.List;

import org.gcube.portlets.d4sreporting.common.client.CommonConstants;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code> OpenTemplateDialog </code> class is is the Dialog for changing template properties
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class PagePropertiesDialog extends DialogBox {

	/**
	 * this layout panel
	 */
	private VerticalPanel dialogPanel = new VerticalPanel();

	private TextBox templateNameLabelBox = new TextBox();


	/**
	 * 
	 * @param templateModel .
	 * @param controller .
	 */
	public PagePropertiesDialog(final TemplateModel templateModel, final Presenter controller) {

		dialogPanel.setSpacing(4);

		HTML spacer = new HTML("<hr width=\"100%\" />");
		spacer.setHeight("30");

		/*
		 * template name part
		 */
		HTML templateNameLabel = new HTML("Template Name: ");


		templateNameLabelBox.setText(templateModel.getTemplateName());

		//add the listener
		templateNameLabelBox.addChangeListener(new ChangeListener() {

			public void onChange(Widget sender) {
				TextBox tmp = ((TextBox) sender);
				tmp.setText(tmp.getText().replaceAll(CommonConstants.ACCEPTED_CHARS_ALPHANUM, ""));
				//				checking value
				if (tmp.getText().compareTo("") == 0) {
					Window.alert("Template name cannot be blank");
					tmp.setText(templateModel.getTemplateName());
				}
			}

		});


		templateNameLabelBox.setWidth("200");
		CellPanel templateNamaPanel = new HorizontalPanel();
		templateNamaPanel.add(templateNameLabel);
		templateNamaPanel.add(templateNameLabelBox);
		templateNamaPanel.setCellWidth(templateNameLabel, "120");





		List<Metadata> metadatas =  controller.getModel().getMetadata();
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
		Button cancelButton = new Button("Cancel",new ClickListener() {
			public void onClick(Widget sender) {

				hide();
			}
		});

		Button applyButton = new Button("Apply",new ClickListener() {
			public void onClick(Widget sender) {
				if (templateModel.getTemplateName().compareTo(templateNameLabelBox.getText()) != 0) {
					controller.changeTemplateName(templateNameLabelBox.getText());			
				}
				hide();
			}
		});

		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsContainer.setSpacing(10);
		buttonsContainer.add(cancelButton);
		buttonsContainer.add(applyButton);
		buttonsPanel.add(buttonsContainer);


		dialogPanel.add(buttonsPanel);
		dialogPanel.setPixelSize(350, 275);
		setWidget(dialogPanel);

	}

	/**
	 * 
	 * @return .
	 */
	public TextBox getTemplateNameLabelBox() {
		return templateNameLabelBox;
	}

}
