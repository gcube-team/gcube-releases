package org.gcube.portlets.user.geoexplorer.client;

import org.gcube.portlets.user.geoexplorer.client.resources.Images;

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
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 7, 2014
 *
 */
public class DialogGisLink extends Dialog {

	private TextField<String> txt;
	private int widht = 450;
	private int height = 150;
	private VerticalPanel vp = new VerticalPanel();

	public DialogGisLink(String headingTxt, final String UUID) {
		setButtonAlign(HorizontalAlignment.CENTER);
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
		setIcon(Images.iconMapLink());
		
		txt = new TextArea();
		txt.setWidth(widht-20);
		txt.setHeight(height-74);
		txt.setReadOnly(true);
		
		txt.mask("Generating Gis link...");
		
		if(UUID!=null && !UUID.isEmpty()){
			try {
				GeoExplorer.service.getGisViewerLinkForUUID(UUID, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable arg0) {
						txt.unmask();
						txt.setValue(arg0.getMessage());
						
					}

					@Override
					public void onSuccess(String url) {
						txt.unmask();
						txt.setValue(url);
						selectTxt();
						
					}
				});
			} catch (Exception e) {
				txt.setValue(e.getMessage());
			}

		}else{
			txt.unmask();
			txt.setValue("The UUID is null");
		}

		this.getButtonById(Dialog.CLOSE).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
					hide();
			}

		});

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