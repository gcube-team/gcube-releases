package org.gcube.portlets.user.gcubelogin.client.panels;

import org.gcube.portlets.user.gcubelogin.client.GCubeLogin;
import org.gcube.portlets.user.gcubelogin.client.commons.ODLLink;
import org.gcube.portlets.user.gcubelogin.client.commons.UIConstants;
import org.gcube.portlets.user.gcubelogin.shared.UserBelonging;
import org.gcube.portlets.user.gcubelogin.shared.VO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class PanelVO extends Composite {
	private HorizontalPanel main_panel = new HorizontalPanel();
	private VO vo;

	public PanelVO(VO vo) {
		this.vo = vo;
		Init();

		initWidget(main_panel);
	}

	private void openDialog(VO vo) {
		String scope = vo.getGroupName();
		RequestMembershipDialog dlg = new RequestMembershipDialog(null, vo.getName(), scope, false);
		dlg.show();
	}

	private void Init() {
		ODLLink lbl;
		if (vo.getUserBelonging() == UserBelonging.BELONGING) {
			lbl = new ODLLink(vo.getName(),"font_family font_14 font_bold font_color_enter", new ClickHandler() {

				public void onClick(ClickEvent event) {
					GCubeLogin.showLoading();	
					String scope = vo.getGroupName();
					GCubeLogin.getService().loadLayout(scope, vo.getFriendlyURL(), new AsyncCallback<Void>() {
						public void onFailure(Throwable arg0) {							
							GCubeLogin.hideLoading();
						}

						public void onSuccess(Void arg0) {
							GCubeLogin.hideLoading();
							Window.open( vo.getFriendlyURL(), "_self", "");
						}
					});

				}
			});
		}
		else if (vo.getUserBelonging() == UserBelonging.NOT_BELONGING) {
			lbl = new ODLLink(vo.getName(),"font_family font_14 font_bold font_color_ask", new ClickHandler() {

				public void onClick(ClickEvent event) {
					openDialog(vo);
				}
			});			
		}
		else {
			lbl = new ODLLink(vo.getName(),"font_family font_14 font_bold font_color_pending", new ClickHandler() {

				public void onClick(ClickEvent event) {
					RequestMembershipDialog dlg = new RequestMembershipDialog(null, vo.getName(), "", true);
					dlg.show();
				}
			});
		}
		main_panel.add(lbl);

		Image img_info = new Image(UIConstants.INFO_IMAGE);
		img_info.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				InfoDialog p = new InfoDialog(vo.getName(), vo.getDescription());
				p.setAnimationEnabled(false);
				p.setPopupPosition(event.getRelativeElement().getAbsoluteLeft(), event.getRelativeElement().getAbsoluteTop());
				p.show();
			}
		});
		img_info.addStyleName("pointer");
		main_panel.add(lbl);
		main_panel.add(new HTML("&nbsp;"));
		main_panel.add(img_info);
	}

	public String getVOName() {
		return this.vo.getName();
	}


	public static native String getURL()/*-{
	return $wnd.location;
	}-*/;

}