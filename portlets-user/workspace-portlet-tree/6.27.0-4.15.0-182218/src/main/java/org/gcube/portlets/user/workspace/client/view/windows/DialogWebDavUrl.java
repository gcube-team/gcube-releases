package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class DialogWebDavUrl extends Dialog {

	private TextField<String> txt;
	private int widht = 500;
	private int height = 200;
	private Label label = new Label();
	private VerticalPanel vp = new VerticalPanel();

	public DialogWebDavUrl(String headingTxt, String msgTitle, String urlValue) {

		setButtonAlign(HorizontalAlignment.CENTER);
		
		vp.setHorizontalAlign(HorizontalAlignment.CENTER);
		vp.setVerticalAlign(VerticalAlignment.MIDDLE);
		vp.getElement().getStyle().setPadding(5, Unit.PX);
		setHeading(headingTxt);
		setModal(true);
		setBodyStyle("padding: 9px; background: none");
		setWidth(widht);
		setHeight(height);
		setResizable(false);
		setButtons(Dialog.OK);
		
		Anchor anchorWebDavReadMore = new Anchor("Read More", true, ConstantsExplorer.WEBDAVURLLINKREADMORE, "_blank");
		label.setText(ConstantsExplorer.ACCESSWEBDAVMSG + " ");
		label.setStyleName("myWebDavStyle");
		anchorWebDavReadMore.setStyleName("myWebDavStyle");
	
		txt = new TextArea();
		txt.setStyleAttribute("padding-top", "20px");
		txt.setWidth(widht-30);
		txt.setFieldLabel(msgTitle);
		txt.setValue(urlValue);
		txt.setReadOnly(true);
		
		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
					hide();
			}

		});
		
		setFocusWidget(txt);
//		txt.selectAll();
		
		vp.add(label);
		vp.add(txt);
		
		add(label);
		add(anchorWebDavReadMore);
		add(vp);

		this.show();
	}

	public String getTxtValue() {

		return txt.getValue();
	}
	
	public void selectTxt(){
		txt.select(0, txt.getValue().length());
	}
}