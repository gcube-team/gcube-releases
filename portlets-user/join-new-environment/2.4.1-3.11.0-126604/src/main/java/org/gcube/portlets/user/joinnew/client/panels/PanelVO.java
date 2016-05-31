package org.gcube.portlets.user.joinnew.client.panels;

import org.gcube.portlets.user.joinnew.client.commons.UIConstants;
import org.gcube.portlets.user.joinnew.shared.VO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
		HTML labelVO = new HTML(vo.getName());
		labelVO.setStyleName("font_family font_14 font_bold font_color_ask");
		main_panel.add(labelVO);

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