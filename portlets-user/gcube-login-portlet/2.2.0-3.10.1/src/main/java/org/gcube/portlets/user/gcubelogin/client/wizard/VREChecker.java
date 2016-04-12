package org.gcube.portlets.user.gcubelogin.client.wizard;

import java.util.ArrayList;

import org.gcube.portlets.user.gcubelogin.client.commons.LoadingPopUp;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginServiceAsync;
import org.gcube.portlets.user.gcubelogin.client.wizard.errors.WizardError;
import org.gcube.portlets.user.gcubelogin.shared.VO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VREChecker extends Composite {

	private static VRECheckerUiBinder uiBinder = GWT
			.create(VRECheckerUiBinder.class);

	interface VRECheckerUiBinder extends UiBinder<Widget, VREChecker> {
	}
	private NewLoginServiceAsync newLoginSvc;
	private VerticalPanel mainPanel;
	private String infrastructure;
	private String startingScopes;

	@UiField Button searchButton;
	@UiField Button goButton;
	@UiField HTML reportHtml;
	@UiField HTML searchHtml;

	public VREChecker(final NewLoginServiceAsync newLoginSvc, String infrastructure, String startingScopes, VerticalPanel mainPanel) {
		this.newLoginSvc = newLoginSvc;
		this.infrastructure = infrastructure;
		this.startingScopes = startingScopes;
		this.mainPanel = mainPanel;

		initWidget(uiBinder.createAndBindUi(this));
	}
	@UiHandler("searchButton")
	void onLoadClick(ClickEvent e) {
		showLoading();
		newLoginSvc.checkVresPresence(infrastructure, startingScopes, new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				hideLoading();	
				mainPanel.clear();
				mainPanel.add(new WizardError());
			}

			public void onSuccess(Boolean result) {
				hideLoading();	
				if (result) {
					newLoginSvc.getVresFromInfrastructure(infrastructure, startingScopes, new AsyncCallback<ArrayList<VO>>() {

						public void onFailure(Throwable caught) {
							hideLoading();	
							mainPanel.clear();
							mainPanel.add(new WizardError());							
						}

						public void onSuccess(ArrayList<VO> result) {
							hideLoading();	
							mainPanel.clear();
							mainPanel.add(new VRESelector(newLoginSvc, result));										
						}
					});
				}
				else {
					reportHtml.setHTML("We couldn't find any VRE in your infrastructure, this is not a problem as you can create them automatically <br /> " +
							"through the VRE Definition page at Virtual Organization level. Click the button below to complete the installation");
					goButton.setVisible(true);
					searchButton.setVisible(false);
					searchHtml.setVisible(false);
				}
			}
		});
	}

	@UiHandler("goButton")
	void onGoClick(ClickEvent e) {
		mainPanel.clear();
		mainPanel.add(new WizardResultOK());
	}
	
	static void showLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.show();
	}
	static void hideLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.hide();		
	}
}
