package org.gcube.portlets.user.joinnew.client;

import java.util.ArrayList;

import org.gcube.portlets.user.joinnew.client.commons.LoadingPopUp;
import org.gcube.portlets.user.joinnew.client.commons.UIConstants;
import org.gcube.portlets.user.joinnew.client.panels.AccessVREDialog;
import org.gcube.portlets.user.joinnew.client.panels.PanelBody;
import org.gcube.portlets.user.joinnew.client.panels.PanelFilter;
import org.gcube.portlets.user.joinnew.client.panels.PanelVRE;
import org.gcube.portlets.user.joinnew.client.panels.PanelVREs;
import org.gcube.portlets.user.joinnew.client.panels.RequestMembershipDialog;
import org.gcube.portlets.user.joinnew.shared.ResearchEnvironment;
import org.gcube.portlets.user.joinnew.shared.UserBelonging;
import org.gcube.portlets.user.joinnew.shared.VO;
import org.gcube.portlets.user.joinnew.shared.VRE;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Joinnew implements EntryPoint {
	public static final String GET_OID_PARAMETER = "orgid";

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final static JoinNewServiceAsync newLoginSvc = GWT.create(JoinNewService.class);

	public static final String JOIN_NEW = "joinnewDIV";

	protected  VerticalPanel mainPanel	= new VerticalPanel();


	private LoadingPopUp dboxLoading = null;

	private PanelBody body = null;

	public static JoinNewServiceAsync getService() {
		return newLoginSvc;
	}


	public Joinnew() {
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
		mainPanel.setStyleName("minheight");
		mainPanel.add(new HTML("<div style=\"height: 450px; text-align:center; vertical-align:text-top;\"><p><br /><br />Loading Environment please wait ...</p><p><br /><br /> " + getLoadingHTML() + "</p></div>" ));


		// Associate the new panel with the HTML host page.
		RootPanel.get(JOIN_NEW).add(mainPanel);

		dboxLoading.removeStyleName("gwt-DialogBox");
		dboxLoading.center();
		hideLoading();

		checkIsReferral();		
		resize() ;
	}

	private void checkIsRedirectedRequest() {

	}
	/**
	 * check if it has to show just one feed
	 * @return
	 */
	private String getOrganizationId() {
		return Window.Location.getParameter(GET_OID_PARAMETER);
	}

	/**
	 * first async callback
	 */
	private void checkIsReferral() {
		if (getOrganizationId() == null) {
			loadVO_VRE();
		}
		else {
			Long orgId = -1L;
			try {
				orgId = Long.parseLong(getOrganizationId());
			}
			catch (Exception ex) {
				GWT.log("org id is not a number " + ex.getMessage());
				loadVO_VRE();
				return;
			}
			final long organizationId = orgId;
			newLoginSvc.getSelectedVRE(orgId, new AsyncCallback<VRE>() {
				public void onSuccess(final VRE vre) {
					GWT.log("A VRE was Returned");	
					if (vre == null) {
						GWT.log("A VRE Returned is null");	
						loadVO_VRE();
						return;
					}
					else {
						GWT.log("A Valid VRE was Returned");	
						if (vre.getUserBelonging() == UserBelonging.BELONGING) //the user is already registered
							Window.open(vre.getFriendlyURL(), "_self", "");	
						else {

							//user does not belong to this organization
							newLoginSvc.getInfrastructureVOs(new AsyncCallback<ArrayList<VO>>() {
								public void onSuccess(ArrayList<VO> result) {
									if (result.size() == 0) {
										hideLoading();
										mainPanel.clear();
										mainPanel.add(new HTML(SERVER_ERROR));
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
										PanelBody.get().setVO(result);
										PanelFilter.get().setVO(result);
										setLastStateUsingCookie();		

										ArrayList<PanelVREs> vres = body.getPanelVREs();
										for (PanelVREs panelVREs : vres) {
											for (PanelVRE card : panelVREs.getVreCards()) {
												VRE vreObj = card.getVreObject();
												if (vreObj.getId() == vre.getId()) {
													if (vre.isUponRequest()) {
														checkInvitation(organizationId, card);
													} else {
														AccessVREDialog dlg = new AccessVREDialog(card.getEnter_Button(), vreObj, vreObj.getGroupName(), false);
														dlg.show();
													}
												}
											}
										}
										if (vre.isUponRequest()) {

										} else {

										}
									}
								}

								public void onFailure(Throwable arg0) {
									hideLoading();	
								}
							});

						}

					}
				}
				public void onFailure(Throwable arg0) {
					clearPanel();
					mainPanel.add(new HTML(SERVER_ERROR));
				}
			});
		}
	}

	private void checkInvitation(final long organizationId, final PanelVRE card) {
		final VRE vreObj = card.getVreObject();					
		newLoginSvc.isExistingInvite(organizationId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if (result != null) { //there exist an invite
					AccessVREDialog dlg = new AccessVREDialog(card.getEnter_Button(), vreObj, vreObj.getGroupName(), true);
					dlg.show();
				} else {
					RequestMembershipDialog dlg = new RequestMembershipDialog(
							card.getEnter_Button(), vreObj.getName(), vreObj.getGroupName(), vreObj.getUserBelonging()==UserBelonging.PENDING);
					dlg.show();
				}

			}
			@Override
			public void onFailure(Throwable caught) {
				hideLoading();					
			}
		});



	}

	private void resize() {
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				refreshLoading();				
				PanelBody.get().resizeHeader(RootPanel.get("joinnewDIV").getOffsetWidth());

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
	 * 
	 */
	private void loadVO_VRE() {

		newLoginSvc.getInfrastructureVOs(new AsyncCallback<ArrayList<VO>>() {

			public void onSuccess(ArrayList<VO> result) {
				if (result.size() == 0) {
					hideLoading();
					mainPanel.clear();
					mainPanel.add(new HTML(SERVER_ERROR));
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
					//					for (VO vo : result) {
					//						for (VRE vre : vo.getVres()) {
					//							GWT.log(vre.toString());
					//						}
					//					}
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
		PanelBody.get().setVisible(false);
		PanelBody.get().changeSizeWidth(100);
		PanelBody.get().setVisible(true);
		//		Date expiryDate = PanelConsole.get().getExpiryDate();
		//
		//		if (Cookies.getCookie(UIConstants.COOKIE_NAME) == null) {
		//			Cookies.setCookie(UIConstants.COOKIE_NAME, "1", expiryDate);
		//			PanelConsole.get().toggleButton50.setDown(true);
		//			PanelBody.get().changeSizeWidth(50);
		//		}
		//		else {
		//			String lastIconsState = Cookies.getCookie(UIConstants.COOKIE_NAME);
		//			if (lastIconsState.compareTo("2") == 0) {
		//				PanelConsole.get().toggleButton100.setDown(true);
		//				PanelBody.get().setVisible(false);
		//				PanelBody.get().changeSizeWidth(100);
		//				PanelBody.get().setVisible(true);
		//			}
		//			else if (lastIconsState.compareTo("1") == 0) {
		//				PanelConsole.get().toggleButton50.setDown(true);
		//				PanelBody.get().setVisible(false);
		//				PanelBody.get().changeSizeWidth(50);
		//				PanelBody.get().setVisible(true);
		//			}
		//			else if (lastIconsState.compareTo("0") == 0) {
		//				PanelConsole.get().toggleButton0.setDown(true);
		//				PanelBody.get().setVisible(false);
		//				PanelBody.get().changeSizeWidth(0);
		//				PanelBody.get().setVisible(true);
		//			}
		//		}
	}
	/**
	 */
	public static native String getURLa()/*-{
	return $wnd.location;
	}-*/;
}
