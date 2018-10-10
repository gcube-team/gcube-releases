package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.forms;

import java.util.HashMap;
import java.util.logging.Logger;

import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerController;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.GenericParameterEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowMessageEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.AdvancedGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Wizard extends Composite {

	private static Logger logger = Logger.getLogger(Wizard.class+"");
	
	private static WizardUiBinder uiBinder = GWT
			.create(WizardUiBinder.class);

	interface WizardUiBinder extends UiBinder<Widget, Wizard> {
	}

	@UiField DialogBox dialog;
//	@UiField DockLayoutPanel dock;
	@UiField SimpleLayoutPanel centerContainer;
	
	// Button Bar
	@UiField FocusPanel back;
	@UiField FocusPanel createNew;
	@UiField FocusPanel next;
	
	private WizardConfiguration configuration;
	
	private int currentStepIndex=0;
	
	private HashMap<String,String> parameters=new HashMap<String, String>();
	
	
	public Wizard(WizardConfiguration config) {
		initWidget(uiBinder.createAndBindUi(this));
		this.configuration=config;
	
		initStep(0);
		dialog.center();
		dialog.show();
		
	}

	
	private void initStep(int index){
		logger.fine("Init step index "+index);
//		if(index>0){
//			dock.remove(configuration.getSteps()[currentStepIndex].getWidget());
//		}
		StepDefinition step=configuration.getSteps()[index];
		centerContainer.setWidget(step.getWidget());
		dialog.getCaption().setText(configuration.getTitle()+":"+step.getTitle());
		back.setVisible(index>0);
		next.setVisible(index<configuration.getSteps().length-1);
		createNew.setVisible(index==configuration.getSteps().length-1);		
		currentStepIndex=index;
		step.onShowStep();
//		dock.forceLayout();
	}
	
	
	@UiHandler("back")
	void handleBack(ClickEvent e){
		currentStepIndex--;
		initStep(currentStepIndex);
	}
	
	@UiHandler("next")
	void handleNext(ClickEvent e){
		StepDefinition current=configuration.getSteps()[currentStepIndex];		
		if(current.isStepValid()){
			parameters.putAll(current.getDefinedFields());
			currentStepIndex++;
			initStep(currentStepIndex);
		}
		else{
			showMessage(current.getMessage());
		}
	}
	
	@UiHandler("createNew")
	void handleCreate(ClickEvent e){
		StepDefinition current=configuration.getSteps()[currentStepIndex];
		if(current.isStepValid()){
			parameters.putAll(current.getDefinedFields());
			GenericParameterEvent event=configuration.getTheEvent();
			event.setParameters(parameters);
			FhnManagerController.eventBus.fireEvent((GwtEvent<?>) event);
			dialog.hide();
		}else{
			showMessage(current.getMessage());
		}
	}
	
	
	@UiHandler("close")
	void handleClose(ClickEvent e){
		dialog.hide(true);
	}
	
	private void showMessage(String message){
		StepDefinition current=configuration.getSteps()[currentStepIndex];
		FhnManagerController.eventBus.fireEvent(new ShowMessageEvent(current.getTitle(), current.getMessage()));
	}
}
