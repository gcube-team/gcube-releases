package org.gcube.portlets.admin.vredefinition.client.model;


import java.util.List;

import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.extjs.gxt.ui.client.data.ModelData;


public class VREDefinitionModel {

	private VREDescriptionBean VREDescription;
	private List<ModelData> VREFunctionalitiesSelected;
	private WizardStepModel steps;
	private WizardStepType stepSelected;
	
	public VREDefinitionModel() {
		steps = new WizardStepModel("root","info-icon",WizardStepType.VREDefinitionStart);
		steps.add(new WizardStepModel("Info","info-icon",WizardStepType.VREDescription));
		
		stepSelected = WizardStepType.VREDescription;
	}
	
	public void setEditMode() {
		steps.add(new WizardStepModel("Functionalities","functionality-icon",WizardStepType.VREFunctionalities));
		steps.add(new WizardStepModel("Summary","report-icon",WizardStepType.VREDefinitionEnd));
	}
	
	public void setVREDescription(VREDescriptionBean bean){
		this.VREDescription = bean;
	}
	
	public VREDescriptionBean getVREDescriptionBean() {
		return VREDescription;
	}
	
	public void setVREFunctionalities(List<ModelData> list) {
		this.VREFunctionalitiesSelected = list;
	}
	
	public List<ModelData> getVREFunctionalities() {
		return VREFunctionalitiesSelected;
	}
	
	public void setNextStep() {
		
		int step = stepSelected.ordinal();
		step++;
		addStep(step);
		stepSelected = WizardStepType.values()[step];
	}
	
	private void addStep(int step) {
		
		if (steps.getChildren().size() >= step)
			return;
		
		WizardStepType type =  WizardStepType.values()[step];
		switch(type){
		case VREDescription:
			steps.add(new WizardStepModel("Info","info-icon",WizardStepType.VREDescription));
			break;
		case VREFunctionalities:
			steps.add(new WizardStepModel("Functionalities","functionality-icon",WizardStepType.VREFunctionalities));
			break;
		case VREDefinitionEnd:
			steps.add(new WizardStepModel("Summary","report-icon",WizardStepType.VREDefinitionEnd));
			break;
		default:
			break;
		}
			
	}
	
	public void setBackStep() {
		stepSelected = WizardStepType.values()[stepSelected.ordinal() - 1];
	}
	
	public WizardStepModel getWizardStepModel() {
		return steps;
	}
	
	public WizardStepType getWizardStep() {
		return stepSelected;
	}
	
	public void setWizardStep(WizardStepType step) {
		this.stepSelected =  step;
	}
}
