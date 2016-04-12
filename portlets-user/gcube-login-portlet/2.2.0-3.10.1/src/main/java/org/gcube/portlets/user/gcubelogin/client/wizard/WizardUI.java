package org.gcube.portlets.user.gcubelogin.client.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.gcube.portlets.user.gcubelogin.client.commons.LoadingPopUp;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginServiceAsync;
import org.gcube.portlets.user.gcubelogin.client.wizard.errors.WizardError;
import org.gcube.portlets.user.gcubelogin.shared.CheckResult;
import org.gcube.portlets.user.gcubelogin.shared.SelectedTheme;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WizardUI extends Composite {

	private static WizardUIUiBinder uiBinder = GWT.create(WizardUIUiBinder.class);

	interface WizardUIUiBinder extends UiBinder<Widget, WizardUI> {
	}

	private boolean checkEnabled = true;

	private NewLoginServiceAsync newLoginSvc;
	private VerticalPanel mainPanel;

	@UiField RadioButton genericRadioButton;
	@UiField RadioButton imarineRadioButton;
	@UiField Label label;
	@UiField Button checkButton;
	@UiField Button saveButton;
	@UiField Button loadButton;
	@UiField TextBox infraName; 
	@UiField TextBox voNames;
	@UiField CheckBox autoRedirect;
	@UiField HTML testResult;
	@UiField HTML reportHtml;



	public WizardUI(final NewLoginServiceAsync newLoginSvc, VerticalPanel mainPanel) {
		this.newLoginSvc = newLoginSvc;
		this.mainPanel = mainPanel;
		initWidget(uiBinder.createAndBindUi(this));
		genericRadioButton.setValue(true);
		autoRedirect.setValue(true);
		saveButton.setEnabled(false);
		genericRadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {			
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setGeneric();			
			}
		});
		imarineRadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {			
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setImarine();			
			}
		});
	}


	@UiHandler("saveButton")
	void onSaveClick(ClickEvent e) {
		if (Window.confirm("The portal setup procedure is about to Start, if you're sure with the data you've entered press ok.")) {
			showLoading();
			SelectedTheme theme;
			 if (genericRadioButton.getValue()) 
				theme = SelectedTheme.GENERIC;
			else
				theme = SelectedTheme.iMARINE;

			newLoginSvc.installPortalEnv(infraName.getText(), voNames.getText(), theme, autoRedirect.getValue(), new AsyncCallback<Boolean>() {

				public void onSuccess(Boolean result) {
					hideLoading();	
					mainPanel.clear();
					if (result)
						mainPanel.add(new VREChecker(newLoginSvc, infraName.getText(), voNames.getText(), mainPanel));
					else {
						mainPanel.add(new WizardError());
					}
				}

				public void onFailure(Throwable caught) {
					hideLoading();	
					mainPanel.clear();
					mainPanel.add(new WizardError());
					Window.alert("Errors during installation, please see server log " + caught.getMessage());								
				}
			});
		}

	}

	@UiHandler("loadButton")
	void onLoadClick(ClickEvent e) {

		showLoading();
		newLoginSvc.getConfigFromGCore(new AsyncCallback<String[]>() {

			public void onFailure(Throwable caught) {
				hideLoading();
				Window.alert("Could not load configuration: " + caught.getMessage());						
			}

			public void onSuccess(String[] result) {
				hideLoading();
				infraName.setText(result[0]);		
				voNames.setText(result[1]);		
			}
		});				
	}

	@UiHandler("checkButton")
	void onCheckClick(ClickEvent e) {
		if (! checkEnabled) {
			saveButton.setEnabled(false);
			infraName.setReadOnly(false);
			voNames.setReadOnly(false);
			checkEnabled = true;
			testResult.setText("you can't run the install until we checked your configuration");
			checkButton.setText("Check again");
		}
		else {
			removeBlanks(infraName);
			removeBlanks(voNames);
			if (infraName.getText().equals("") || voNames.getText().equals("")) {
				Window.alert("Infrastructure and Starting scopes are required");
			} else {
				reportHtml.setText("");
				showSpinner();
				newLoginSvc.checkInfrastructure(infraName.getText(), voNames.getText(), new AsyncCallback<HashMap<String,ArrayList<CheckResult>>>() {

					public void onFailure(Throwable caught) {
						hideSpinner();	
						Window.alert("Something wrong in the server, check your connection" + caught.getMessage());	
					}

					public void onSuccess(HashMap<String, ArrayList<CheckResult>> result) {
						hideSpinner();	
						if (getTestResult(result)) {
							testResult.setHTML("Alright, your configuration is correct!");
							saveButton.setEnabled(true);
							infraName.setReadOnly(true);
							voNames.setReadOnly(true);
							checkEnabled = false;
							checkButton.setText("Edit configuration");
						} else {
							getShowResult(result);
							testResult.setHTML("<div class=\"wizardComment\">Ops! there's something wrong, check report here:</div>");
						}
					}
				});
			}
		}
	}


	static void showLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.show();
	}
	static void hideLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.hide();		
	}
	void setGeneric() {
		label.getElement().getParentElement().removeClassName("thumb-imarine");
		label.getElement().getParentElement().removeClassName("thumb-classic");
		label.getElement().getParentElement().addClassName("thumb-generic");
	}
	void setClassic() {
		label.getElement().getParentElement().removeClassName("thumb-imarine");
		label.getElement().getParentElement().removeClassName("thumb-generic");
		label.getElement().getParentElement().addClassName("thumb-classic");
	}
	void setImarine() {
		label.getElement().getParentElement().removeClassName("thumb-generic");
		label.getElement().getParentElement().removeClassName("thumb-classic");
		label.getElement().getParentElement().addClassName("thumb-imarine");
	}

	private void removeBlanks(TextBox tb) {
		tb.setText(tb.getText().replaceAll(" ", ""));
	}

	private void showSpinner() {
		testResult.setText("");
		testResult.setSize("350px", "20px");
		testResult.addStyleName("spinner");
	}

	private void hideSpinner() {
		testResult.removeStyleName("spinner");
	}
	/**
	 * 
	 * @return the html of the result
	 */
	private String getShowResult(HashMap<String, ArrayList<CheckResult>> result) {
		String html ="<table>";
		for (Entry<String, ArrayList<CheckResult>> scope : result.entrySet()) {
			html += "<tr>";
			html += "<td><strong>"+scope.getKey()+":</strong></td>";
			for (CheckResult res : scope.getValue()) {
				if (res.isPassed())
					html += "<td><nobr><span style=\"color:green;\">"+res.getType()+": "+ res.isPassed()+" </span></nobr></td>";
				else
					html += "<td><nobr><span style=\"color:red;\">"+res.getType()+": "+ res.isPassed()+" </span></nobr></td>";
			}
			html += "</tr>";
		}
		html += "</table>";
		reportHtml.setHTML(html);
		return html;
	}
	/**
	 * 
	 * @param result
	 * @return true if you can proceed (result ok)
	 */
	private boolean getTestResult(HashMap<String, ArrayList<CheckResult>> result) {
		for (Entry<String, ArrayList<CheckResult>> scope : result.entrySet()) 
			for (CheckResult res : scope.getValue()) {
				if (! res.isPassed())
					return false;
			}
		return true;
	}
}

