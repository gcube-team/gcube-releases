package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.shared.PublicLink;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DialogPublicLink extends Dialog {

	private TextField<String> txtCompleteURL;
	private TextField<String> txtShortURL;
	private int widht = 450;
	private int height = 210;
	private VerticalPanel vp = new VerticalPanel();
//	private Label label = new Label();

	public DialogPublicLink(String headingTxt, final String itemId) {
		setButtonAlign(HorizontalAlignment.CENTER);
		setIcon(Resources.getIconPublicLink());
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
		setScrollMode(Scroll.AUTOY);
		
//		label.setText(msgTitle);
//		label.setStyleName("myWebDavStyle");
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setStyleAttribute("margin-top", "8px");
		txtCompleteURL = new TextField<String>();
		txtCompleteURL.setStyleAttribute("margin-top", "1px");
		txtCompleteURL.setWidth(widht-20);
		txtCompleteURL.setReadOnly(true);
//		txtCompleteURL.mask("Getting Link...");
		vp1.add(new Label("Link"));
		vp1.add(txtCompleteURL);
		
		VerticalPanel vp2 = new VerticalPanel();
		vp2.setStyleAttribute("margin-top", "8px");
		txtShortURL = new TextField<String>();
		txtShortURL.setStyleAttribute("margin-top", "1px");
		txtShortURL.setWidth(widht-20);
//		txtShortURL.mask("Getting Link...");
		vp2.add(new Label("Short Link"));
		vp2.add(txtShortURL);
		
		vp.mask("Getting Link...");
		
		if(itemId!=null && !itemId.isEmpty()){
			AppControllerExplorer.rpcWorkspaceService.getPublicLinkForFolderItemId(itemId, true, new AsyncCallback<PublicLink>() {
				
				@Override
				public void onSuccess(PublicLink publicLink) {
					vp.unmask();
					txtCompleteURL.setValue(publicLink.getCompleteURL());
					txtShortURL.setValue(publicLink.getShortURL());
					selectTxt();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					vp.unmask();
					new MessageBoxAlert("Error", caught.getMessage(), null);
				}
			});
		
		}else{
			txtCompleteURL.unmask();
			new MessageBoxAlert("Error", "The item identifier is null", null);
		}

		this.getButtonById(Dialog.CLOSE).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
					hide();
			}

		});
		

//		vp.add(label);
		vp.add(vp1);
		vp.add(vp2);
		
		setFocusWidget(txtCompleteURL);
		
		add(vp);
	}

	public String getTxtValue() {

		return txtCompleteURL.getValue();
	}
	
	public void selectTxt(){
		
		if(txtCompleteURL.getValue()!=null)
			txtCompleteURL.select(0, txtCompleteURL.getValue().length());
	}
}