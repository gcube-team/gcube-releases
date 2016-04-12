package org.gcube.portlets.admin.vredefinition.client.view;

import org.gcube.portlets.admin.vredefinition.client.presenter.WizardActionsPresenter;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;


public class WizardActionsView extends Composite implements WizardActionsPresenter.Display{

	private Button nextButton;
	private Button backButton;
	private HorizontalPanel panel;
	private Label label;
	
	public WizardActionsView(ToolBar container) {
		
		panel = new HorizontalPanel();
		panel.setWidth("100%");
		panel.setTableWidth(""+container.getOffsetWidth()+"px");
		panel.setTableHeight("30px");
		
		TableData backData = new TableData("45%","100%");
		backData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		backData.setVerticalAlign(VerticalAlignment.MIDDLE);
		
		TableData nextData = new TableData("45%","100%");
		nextData.setHorizontalAlign(HorizontalAlignment.LEFT);
		nextData.setVerticalAlign(VerticalAlignment.MIDDLE);
		
		TableData labelData = new TableData("10%","100%");
		labelData.setHorizontalAlign(HorizontalAlignment.CENTER);
		labelData.setVerticalAlign(VerticalAlignment.MIDDLE);
		
		
		backButton = new Button("Back");
		backButton.setSize("100px", "25px");
		
		label = new Label("1 of 3");
		label.setStyleName("label-wizard");
		label.getElement().getStyle().setFontSize(30, Unit.PX);
		
		nextButton = new Button("Next");
		nextButton.setSize("100px", "25px");
		
		panel.add(backButton,backData);
		panel.add(label, labelData);
		panel.add(nextButton,nextData);
		
		
	
		initComponent(panel);
		
	}
	
	

	public Composite asComponent() {
	    return this;
	  }

	public Button getNextButton() {
		return nextButton;
	}


	public Button getBackButton() {
		return backButton;
	}



	public Label getLabel() {
		// TODO Auto-generated method stub
		return label;
	}



	public HorizontalPanel getContainer() {
		// TODO Auto-generated method stub
		return panel;
	}
	

}
