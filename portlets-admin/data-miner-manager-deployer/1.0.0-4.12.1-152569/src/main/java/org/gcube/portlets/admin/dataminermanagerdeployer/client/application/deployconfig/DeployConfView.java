package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.deployconfig;

import javax.inject.Inject;

import org.gcube.portlets.admin.dataminermanagerdeployer.shared.config.DMDeployConfig;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.config.DeployType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.animate.MaterialAnimation;
import gwt.material.design.client.ui.animate.Transition;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
class DeployConfView extends ViewWithUiHandlers<DeployConfUiHandlers> implements DeployConfPresenter.PresenterView {
	interface Binder extends UiBinder<Widget, DeployConfView> {
	}

	@UiField
	MaterialTextBox targetVRE;


	@UiField
	MaterialTextBox algorithmPackageURL;

	@UiField
	MaterialTextBox algorithmCategory;
	
	@UiField
	MaterialListBox deployType;

	@UiField
	MaterialTextArea result;

	@UiField
	MaterialButton resultIcon;

	@UiField
	MaterialButton run;

	@Inject
	DeployConfView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
		deployType.clear();
		for (DeployType tType : DeployType.values()) {
			deployType.add(tType.getLabel());
		}
	}

	@UiHandler("run")
	void onClick(ClickEvent e) {
		String targetVREValue = targetVRE.getValue();
		String algorithmPackageURLValue = algorithmPackageURL.getValue();
		String algorithmCategoryValue = algorithmCategory.getValue();
		String deployTypeValue = deployType.getValue();

		resultIcon.setVisible(false);
		result.setVisible(false);

		DMDeployConfig dmConfig = new DMDeployConfig(targetVREValue,algorithmPackageURLValue, algorithmCategoryValue, deployTypeValue);
		getUiHandlers().executeDeploy(dmConfig);
	}

	@Override
	public void setResult(String resultValue, boolean success) {
		resultIcon.setVisible(true);
		result.setVisible(true);
		if (success) {
			resultIcon.setIconType(IconType.CHECK_CIRCLE);
			resultIcon.setBackgroundColor(Color.GREEN);
			resultIcon.setIconFontSize(4.0, Unit.EM);
		} else {
			resultIcon.setIconType(IconType.ERROR);
			resultIcon.setBackgroundColor(Color.RED);
			resultIcon.setIconFontSize(4.0, Unit.EM);
		}
		MaterialAnimation animation = new MaterialAnimation();
		animation.setDelay(0);
		animation.setDuration(1000);
		animation.transition(Transition.FLIPINX);
		animation.animate(resultIcon);

		result.setLength(resultValue.length());
		result.setValue(resultValue);
		result.reinitialize();

	}

}