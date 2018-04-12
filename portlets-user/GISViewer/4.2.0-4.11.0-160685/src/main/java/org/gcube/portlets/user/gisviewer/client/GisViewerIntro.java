/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 27, 2014
 *
 */
public class GisViewerIntro extends Dialog{
	
	private CheckBox checkNotShow;
	
	/**
	 * 
	 */
	public GisViewerIntro() {
		this.setItemId("ShowIntroGisViewer");
		this.setButtons(MessageBox.OK);
		this.setButtonAlign(HorizontalAlignment.CENTER);
		this.setClosable(false);
		this.setWidth(400);
		this.setMinWidth(400);
		this.setResizable(false);
		this.setModal(true);
		this.setHeading("Welcome to the GisViewer v."+Constants.VERSION);
		
		LayoutContainer base = new LayoutContainer();
		base.setStyleAttribute("padding", "5px");
		base.setStyleAttribute("background-color", "#FFF");
		LayoutContainer lc = new LayoutContainer();
		
		String html = "<div style=\"margin:10px;\">";
		html+="<img src=\""+GisViewerPanel.resources.gisViewerIcon().getSafeUri().asString()+"\" alt=\"gisviewer\" align=\"left\">";
		html+=GisViewerPanel.resources.gisViewerIntro().getText();
		lc.add(new Html(html));

		checkNotShow = new CheckBox();
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleAttribute("margin-left","10px");
//		hp.setStyleAttribute("margin", "10px");
//		hp.setHorizontalAlign(HorizontalAlignment.CENTER);
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		Html label = new Html("Do not show again");
		label.setStyleAttribute("margin-left", "5px");
		hp.add(checkNotShow);
		hp.add(label);
		
		base.add(lc);
		base.add(hp);
		this.add(base);
		
//		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				hide();
//			}
//		});	
		
//		box.addCallback(new Listener<MessageBoxEvent>(){
//		@Override
//		public void handleEvent(MessageBoxEvent be) {
//			if (be.getButtonClicked().getText().equals("Cancel"))
//				Cookies.setCookie(str, str);
//		}
//	});
	
	}

	public CheckBox getCheckNotShow() {
		return checkNotShow;
	}

	public void setCheckNotShow(CheckBox checkNotShow) {
		this.checkNotShow = checkNotShow;
	}
}
