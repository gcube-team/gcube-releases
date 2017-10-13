package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DialogShareLink extends Dialog {

	private TextField<String> txt;
	private int widht = 450;
	private int height = 150;
	private VerticalPanel vp = new VerticalPanel();
//	private Label label = new Label();

	public DialogShareLink(String headingTxt, final String urlValue) {
		setButtonAlign(HorizontalAlignment.CENTER);
		setIcon(Resources.getIconShareLink());
		vp.setHorizontalAlign(HorizontalAlignment.CENTER);
		vp.setVerticalAlign(VerticalAlignment.MIDDLE);
		vp.getElement().getStyle().setPadding(1, Unit.PX);
		setHeading(headingTxt);
		setModal(true);
		setBodyStyle("padding: 3px; background: none");
		setWidth(widht);
		setHeight(height);
		setResizable(false);
		setButtons(Dialog.CLOSE);
		
//		label.setText(msgTitle);
//		label.setStyleName("myWebDavStyle");
		
		txt = new TextArea();
//		txt.setStyleAttribute("padding-top", "3px");
		txt.setWidth(widht-20);
		txt.setHeight(height-74);
//		txt.setFieldLabel(msgTitle);
		
		txt.setReadOnly(true);
		
		
		txt.mask("Shortening link...");
		AppControllerExplorer.rpcWorkspaceService.getShortUrl(urlValue, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String shortUrl) {
				txt.unmask();
				txt.setValue(shortUrl);
				selectTxt();
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				txt.unmask();
				txt.setValue(urlValue);
				selectTxt();
				
			}
		});
		

		this.getButtonById(Dialog.CLOSE).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
					hide();
			}

		});
		

//		vp.add(label);
		vp.add(txt);
		
		setFocusWidget(txt);
		add(vp);
	}

	public String getTxtValue() {

		return txt.getValue();
	}
	
	public void selectTxt(){
		
		if(txt.getValue()!=null)
			txt.select(0, txt.getValue().length());
	}
}