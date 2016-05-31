package org.gcube.portlets.user.gcubelogin.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.gcubelogin.client.commons.LoadingPopUp;
import org.gcube.portlets.user.gcubelogin.client.commons.UIConstants;
import org.gcube.portlets.user.gcubelogin.client.panels.PanelBody;
import org.gcube.portlets.user.gcubelogin.client.panels.PanelConsole;
import org.gcube.portlets.user.gcubelogin.client.panels.PanelFilter;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginService;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginServiceAsync;
import org.gcube.portlets.user.gcubelogin.client.wizard.VREChecker;
import org.gcube.portlets.user.gcubelogin.client.wizard.WizardAdminAccount;
import org.gcube.portlets.user.gcubelogin.client.wizard.WizardUI;
import org.gcube.portlets.user.gcubelogin.client.wizard.errors.WizardError;
import org.gcube.portlets.user.gcubelogin.shared.ResearchEnvironment;
import org.gcube.portlets.user.gcubelogin.shared.VO;
import org.gcube.portlets.user.gcubelogin.shared.VRE;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * @author Federico Biagini ISTI-CNR
 * 
 * @version 2.0 Jan 10th 2012
 */
public class GCubeLogin implements EntryPoint {


	protected  VerticalPanel mainPanel	= new VerticalPanel();

	protected static NewLoginServiceAsync newLoginSvc = (NewLoginServiceAsync)GWT.create(NewLoginService.class);
	private static ServiceDefTarget endpoint = (ServiceDefTarget) newLoginSvc;

	private LoadingPopUp dboxLoading = null;

	private PanelBody body = null;

	public static NewLoginServiceAsync getService() {
		return newLoginSvc;
	}


	public GCubeLogin() {
		super();

		this.body = new PanelBody();

		this.dboxLoading = new LoadingPopUp(false, true, UIConstants.LOADING_IMAGE);
		this.dboxLoading.hide();
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		mainPanel.setWidth("100%");

		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()+"LoginServiceImpl");
		mainPanel.add(new HTML("<div style=\"height: 450px; text-align:center; vertical-align:text-top;\"><p><br /><br />Loading Environment please wait ...</p><p><br /><br /> " + getLoadingHTML() + "</p></div>" ));


		// Associate the new panel with the HTML host page.
		RootPanel.get("NewLoginPortletDiv").add(mainPanel);

		dboxLoading.removeStyleName("gwt-DialogBox");
		dboxLoading.center();
		hideLoading();


		resize() ;
		//this.body.setHeight(this.right.getOffsetHeight() + "px");
		checkLayoutLoaded();		
	}


	private void resize() {
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				refreshLoading();				
				PanelBody.get().resizeHeader(RootPanel.get("NewLoginPortletDiv").getOffsetWidth());

			}
		});

	}
	/**
	 * 
	 * @param re
	 * @return whether is a vre or not (a VO)
	 */
	private boolean isVre(ResearchEnvironment re) {
		return (re instanceof VRE); 		
	}

	/**
	 * first async callback
	 */
	private void checkLayoutLoaded() {
		newLoginSvc.isLayoutLoaded(new AsyncCallback<Boolean>() {

			public void onFailure(Throwable arg0) {
				hideLoading();	
				Window.alert("We're sorry we couldn't reach the server, try again later" + arg0.getMessage());
			}

			public void onSuccess(Boolean arg0) {
				if (arg0.booleanValue()) {
					newLoginSvc.getSelectedRE(new AsyncCallback<ResearchEnvironment>() {
						public void onFailure(Throwable arg0) {
							clearPanel();
							mainPanel.add(new WizardError());
						}
						public void onSuccess(ResearchEnvironment re) {
							clearPanel();					
							mainPanel.add(getShowLoadedEnvPanel(re));
						}						
					});
				}
				else 
					loadVO_VRE();
			}

		});
	}



	/**
	 * 
	 */
	private void loadVO_VRE() {

		newLoginSvc.getInfrastructureVOs(new AsyncCallback<ArrayList<VO>>() {

			public void onSuccess(ArrayList<VO> result) {
				if (result.size() == 0) {
					hideLoading();
					mainPanel.clear();
					//mainPanel.add(new VREChecker(newLoginSvc, "gcube", "devsec,devNext", mainPanel));
					mainPanel.add(new WizardUI(newLoginSvc, mainPanel));
					//mainPanel.add(new WizardAdminAccount(newLoginSvc, mainPanel));
				}
				else {
					mainPanel.clear();
					mainPanel.setWidth("90%");
					mainPanel.add(body);
					mainPanel.setCellVerticalAlignment(body, HasVerticalAlignment.ALIGN_TOP);

					//set the root vo
					for (VO client : result) {
						if (client.isRoot()) 
							break;

					}
					//PanelTree.get().set_collections((ArrayList<CollectionTmpl>) result);
					PanelBody.get().setVO(result);
					PanelFilter.get().setVO(result);

					setLastStateUsingCookie();		
	
				}
			}

			public void onFailure(Throwable arg0) {
				hideLoading();	
			}
		});
	}

	public static void showLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.show();
	}
	public static void hideLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.hide();		
	}

	/**
	 * 
	 * @param vobj
	 * @return
	 */
	private VerticalPanel getShowLoadedEnvPanel(final ResearchEnvironment re) {

		VerticalPanel toReturn = new VerticalPanel();

		HorizontalPanel header = new HorizontalPanel();

		//String msg = isVre(vobj) ? "VRE Loaded Successfully" : "VO Loaded Successfully";
		HTML home = new HTML("<a href=\"Javascript:();\"><strong>Home</strong></a>", true);
		header.add(home);	
		header.add(new HTML("&nbsp;>&nbsp;", true));
		HTML scope;
		if (re == null)
			scope = new HTML("<strong>Layout Loaded, could not read Enviroment Description</strong>", true);
		else
			scope = new HTML("<strong>" + re.getGroupName() + "</strong>", true);
		header.add(scope);

		/**
		 * listener to go back and select another VRE/VO
		 */
		ClickHandler listener = new ClickHandler() {

			public void onClick(ClickEvent event) {
				clearPanel();
				mainPanel.add(new HTML("<div style=\"height: 450px; text-align:center; vertical-align:text-top;\"><p><br /><br />Loading Environment please wait ...</p><p><br /><br /> " + getLoadingHTML() + "</p></div>" ));

				newLoginSvc.getRootVO(new AsyncCallback<VO>() {
					public void onFailure(Throwable arg0) {
						Window.alert("We're sorry we couldn't reach the server, try again later ... " + arg0.getMessage());
					}
					public void onSuccess(VO rootVO) {
						Window.open(rootVO.getFriendlyURL(), "_self", "");									
					}						
				});				
			}
		};
		home.addClickHandler(listener);


		toReturn.add(header);
		toReturn.add(new HTML("<hr align=\"left\" size=\"1\" width=\"90%\" color=\"gray\" noshade>"));

		HorizontalPanel body = new HorizontalPanel();

		VerticalPanel leftPanel = new VerticalPanel();
		VerticalPanel rightPanel = new VerticalPanel();

		if (re != null) {
			leftPanel.add(new Image(re.getImageURL()));
			rightPanel.setWidth("75%");
			rightPanel.add(new HTML(re.getDescription()));
			rightPanel.setWidth("75%");
		}

		HorizontalPanel bottom = new HorizontalPanel();
		bottom.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		bottom.setWidth("100%");
		Button selectAnother = new Button("Select another Environment");
		selectAnother.addClickHandler(listener);
		bottom.add(selectAnother);
		rightPanel.add(new HTML("<br />"));
		rightPanel.add(bottom);		

		body.add(leftPanel);
		body.add(rightPanel);

		toReturn.add(body);
		return toReturn;

	}

	public void clearPanel() {
		mainPanel.clear();
		mainPanel.setWidth("90%");
		mainPanel.add(body);
		mainPanel.setCellVerticalAlignment(body, HasVerticalAlignment.ALIGN_TOP);
	}
	public static void refreshLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		boolean hidden = dlg.isHidden();
		dlg.center();
		if (hidden) dlg.hide();
		else dlg.show();

		//main_panel.setSize("100%", "100%");
		PanelBody.get().refreshSize();
	}

	/**
	 * 
	 * @return
	 */
	private  String getLoadingHTML() {
		return 
		"<center><table border='0'>"+
		"<tr>"+
		"<td>"+
		"<img src='" + UIConstants.LOADING_IMAGE + "'>"+
		"</td></tr>"+
		"</table></center>" ;
	}

	/**
	 * 
	 */
	private void setLastStateUsingCookie() {
		Date expiryDate = PanelConsole.get().getExpiryDate();

		if (Cookies.getCookie(UIConstants.COOKIE_NAME) == null) {
			Cookies.setCookie(UIConstants.COOKIE_NAME, "1", expiryDate);
			PanelConsole.get().toggleButton50.setDown(true);
			PanelBody.get().changeSizeWidth(50);
		}
		else {
			String lastIconsState = Cookies.getCookie(UIConstants.COOKIE_NAME);
			if (lastIconsState.compareTo("2") == 0) {
				PanelConsole.get().toggleButton100.setDown(true);
				PanelBody.get().setVisible(false);
				PanelBody.get().changeSizeWidth(100);
				PanelBody.get().setVisible(true);
			}
			else if (lastIconsState.compareTo("1") == 0) {
				PanelConsole.get().toggleButton50.setDown(true);
				PanelBody.get().setVisible(false);
				PanelBody.get().changeSizeWidth(50);
				PanelBody.get().setVisible(true);
			}
			else if (lastIconsState.compareTo("0") == 0) {
				PanelConsole.get().toggleButton0.setDown(true);
				PanelBody.get().setVisible(false);
				PanelBody.get().changeSizeWidth(0);
				PanelBody.get().setVisible(true);
			}
		}
	}
	/**
	 */
	public static native String getURLa()/*-{
	return $wnd.location;
	}-*/;
}
