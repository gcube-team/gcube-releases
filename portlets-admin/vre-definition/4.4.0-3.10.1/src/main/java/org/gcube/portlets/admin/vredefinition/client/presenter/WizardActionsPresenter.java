package org.gcube.portlets.admin.vredefinition.client.presenter;

import org.gcube.portlets.admin.vredefinition.client.event.BackButtonEvent;
import org.gcube.portlets.admin.vredefinition.client.event.NextButtonEvent;
import org.gcube.portlets.admin.vredefinition.client.model.WizardStepType;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;




public class WizardActionsPresenter {

	public interface Display {
		Button getNextButton();
		Button getBackButton();
		Label getLabel();
		HorizontalPanel getContainer();
		void setVisible(boolean visible);
		Composite asComponent();
	}

	private final HandlerManager eventBus;
	public final Display display;

	public WizardActionsPresenter(HandlerManager eventBus, Display view) {

		this.eventBus = eventBus;
		this.display = view;
		bind();


	}

	private void bind() {

		display.getNextButton().addClickHandler(new ClickHandler() {


			public void onClick(ClickEvent arg0) {
				eventBus.fireEvent(new NextButtonEvent());
			}
		});


		display.getBackButton().addClickHandler(new ClickHandler() {


			public void onClick(ClickEvent arg0) {
				eventBus.fireEvent(new BackButtonEvent());
			}
		});
	}



	public void go(final ToolBar container) {	

		container.removeAll();
		display.asComponent().setSize(container.getAbsoluteLeft(), 44);
		container.add(display.asComponent());
		container.layout();
	}	

	public void setStepWizard(WizardStepType type) {

		display.getBackButton().setVisible(true);
//		display.getNextButton().changeStyle("forward-icon");
//		display.getNextButton().setSize(48, 19);

		// display.getBackButton().setEnabled(true);
		String string = "" + type.ordinal() + " of " + (WizardStepType.values().length - 1);
		display.getLabel().setText(string);
		switch(type){
		case VREDefinitionStart:	 
			break;
		case VREDescription:
			display.getBackButton().setVisible(false);
			break;
		case VREDefinitionEnd:
			display.getNextButton().setText("Upload");
	
			break;
		default:
			display.getBackButton().setEnabled(true);
			display.getNextButton().setEnabled(true);
			break;	 
		}
	}
}
