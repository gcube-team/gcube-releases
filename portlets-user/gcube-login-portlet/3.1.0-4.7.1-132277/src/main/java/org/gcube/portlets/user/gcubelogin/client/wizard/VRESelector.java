package org.gcube.portlets.user.gcubelogin.client.wizard;

import java.util.ArrayList;

import org.gcube.portlets.user.gcubelogin.client.commons.LoadingPopUp;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginServiceAsync;
import org.gcube.portlets.user.gcubelogin.client.wizard.errors.WizardError;
import org.gcube.portlets.user.gcubelogin.shared.UserBelonging;
import org.gcube.portlets.user.gcubelogin.shared.VO;
import org.gcube.portlets.user.gcubelogin.shared.VRE;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 2.0 Jan 10th 2012
 */
public class VRESelector extends Composite{

	private ArrayList<VO> currentVOs;
	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel contentPanel = new VerticalPanel();
	private Button nextButton = new Button("Install selected");

	/**
	 * 
	 * @param newLoginSvc . 
	 * @param myCurrentVOs .
	 */
	public VRESelector(final NewLoginServiceAsync newLoginSvc, ArrayList<VO> myCurrentVOs) {
		super();
		this.currentVOs = myCurrentVOs;
		mainPanel.setWidth("100%");
		mainPanel.add(new WizardMenu());
		mainPanel.add(contentPanel);
		contentPanel.setStyleName("wizardPanel");
		nextButton.setStyleName("wizardButton");
		HTML title = new HTML("Virtual Research Environments lookup");
		title.setStyleName("wizardTitle");
		HTML select = new HTML("The following VREs were found, select which one of the following you want install:");
		select.setStyleName("wizardText");

		contentPanel.add(title);
		contentPanel.add(select);

		for (VO vo : currentVOs) {
			if (vo.getVres().size() > 0) {
				HTML vosel = new HTML("VREs found on " + vo.getName());
				vosel.setStyleName("wizardH2");
				contentPanel.add(vosel);
				final Grid vreGrid = getVREGrid(vo.getVres());
				int i = 1;
				for (final VRE vre : vo.getVres()) {
					vreGrid.setText(i, 1, vre.getName());
					vreGrid.setText(i, 2, vre.getDescription());
					final CheckBox box = new CheckBox();
					box.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							if (box.getValue()) {
								vre.setUserBelonging(UserBelonging.BELONGING); //this would mean it has to be installed 	
							} else
								vre.setUserBelonging(null); //this would mean it has not
						}
					});
					vreGrid.setWidget(i, 0, box);
					i++;
				}
				contentPanel.add(vreGrid);
			}
		}
		contentPanel.add(nextButton);
		initWidget(mainPanel);

		nextButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (isAnyVRESelectedByUser().isEmpty()) {
					if (Window.confirm("You are not installing any of your Infrastructure VREs on your portal, presso ok to confirm")) {
						mainPanel.clear();
						mainPanel.add(new WizardResultOK());
					}							
				} else {
					if (Window.confirm("The following Infrastructure VREs will be installed on your portal: " + isAnyVRESelectedByUser().toString() + " presso ok to confirm")) {
						showLoading();
						newLoginSvc.installVREs(currentVOs, new AsyncCallback<Boolean>() {							
							public void onSuccess(Boolean result) {
								hideLoading();	
								mainPanel.clear();
								mainPanel.add(new WizardResultOK());
							}
							
							public void onFailure(Throwable caught) {
								hideLoading();	
								mainPanel.clear();
								mainPanel.add(new WizardError());										
							}
						});

					}
				}
			}
		});

	}	
	/**
	 * 
	 * @return a list containing the vre name selected
	 */
	private ArrayList<String> isAnyVRESelectedByUser() {
		ArrayList<String> toReturn = new ArrayList<String>();
		for (VO vo : currentVOs) {
			if (vo.getVres().size() > 0) {
				for (final VRE vre : vo.getVres()) {
					if (vre.getUserBelonging() != null)
						toReturn.add(vre.getName());
				}
			}
		}
		return toReturn;
	}
	/**
	 * 
	 * @param vres
	 * @return a widget for displaying VREs Info
	 */
	private Grid getVREGrid(ArrayList<VRE> vres) {
		Grid vreGrid = new Grid(vres.size()+1, 3);
		vreGrid.setStyleName("wizardGrid");
		HTML name = new HTML("VRE name");
		name.setStyleName("wizardGridLabel");
		HTML description = new HTML("VRE description");
		description.setStyleName("wizardGridLabel");
		HTML select = new HTML("Select");
		select.setStyleName("wizardGridLabel");

		vreGrid.setWidget(0, 0, select);
		vreGrid.setWidget(0, 1, name);
		vreGrid.setWidget(0, 2, description);
		return vreGrid;
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
