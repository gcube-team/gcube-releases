package org.gcube.portlets.user.speciesdiscovery.client.window;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceRepositoryInfo;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class WindowCredits extends Dialog {

	private TextField<String> txt;
	private int widht = 950;
	private int height = 400;
	private VerticalPanel vpContainer = new VerticalPanel();

	public WindowCredits(String headingTxt, List<DataSourceModel> listDasourceModel) {

		initLayout(headingTxt);
		
		
		for (DataSourceModel dataSource : listDasourceModel)
			addCredits(dataSource);
			
		add(vpContainer);
		
		this.show();
	}
	
	private void initLayout(String headingTxt){
		
		
		setButtonAlign(HorizontalAlignment.CENTER);
		setLayout(new FitLayout());
		
//		vpContainer.setHorizontalAlign(HorizontalAlignment.CENTER);
		vpContainer.setVerticalAlign(VerticalAlignment.MIDDLE);
		vpContainer.getElement().getStyle().setPadding(5, Unit.PX);
		vpContainer.setScrollMode(Scroll.AUTOY);
		setHeading(headingTxt);
		setScrollMode(Scroll.AUTO);
		setModal(true);
		setBodyStyle("padding: 10px; background: none");
		setWidth(widht);
		setHeight(height);
		setResizable(true);
		setButtons(Dialog.OK);
		
		getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
					hide();
			}

		});
	}
	
	private void addCredits(DataSourceModel dataSource){
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setWidth(900);
		cp.setScrollMode(Scroll.AUTOX);
		
		cp.setStyleAttribute("margin-top", "20px");
		cp.setStyleAttribute("margin-bottom", "20px");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		final DataSourceRepositoryInfo dataSourceRepositoryInfo = dataSource.getDataSourceRepositoryInfo();

		if(dataSourceRepositoryInfo!=null){
			
			Image img;
			
			if(dataSourceRepositoryInfo.getLogoUrl()!=null){
				img = new Image(dataSourceRepositoryInfo.getLogoUrl());
			}
			else{
				img = new Image(Resources.INSTANCE.getImageNotFound());
			}
			
			img.setStyleName("logoDataSource");
			
//			img.setWidth("80px");
//			img.setHeight("80px");

			Anchor anchor = new Anchor(dataSource.getName());
			anchor.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String url = dataSourceRepositoryInfo.getPageUrl();
					new WindowOpenUrl(url, "_blank", null);
					
				}
			});
			
			HorizontalPanel hp = new HorizontalPanel();
			hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			Text webPage = new Text("Web Page:");
			webPage.setStyleAttribute("margin", "20px");
			hp.add(webPage);
			hp.add(anchor);
			
			VerticalPanel vp = new VerticalPanel();
			Text description = new Text("Description: " +dataSourceRepositoryInfo.getDescription());
			description.setStyleAttribute("margin", "20px");
			
			vp.add(description);
			addParameters(dataSourceRepositoryInfo.getProperties(),vp);

			verticalPanel.add(img);
			verticalPanel.add(hp);
			verticalPanel.add(vp);
			cp.add(verticalPanel);
			vpContainer.add(cp);
			
		}
		
		
	}
	
	public WindowCredits(String headingTxt, HashMap<String, DataSourceModel> hashMapDataSourceClassification) {
		
		initLayout(headingTxt);
		
		for (String key : hashMapDataSourceClassification.keySet())
			addCredits(hashMapDataSourceClassification.get(key));
			
		add(vpContainer);
		
		this.show();
		
		
	}

	private void addParameters(Map<String, String> mapParameters, VerticalPanel vpParameters){
		
		if(mapParameters!=null && mapParameters.size()>0){
			
			//ALPHABETICAL ORDER OF THE PARAMETERS
			List<String> listKey = Arrays.asList((mapParameters.keySet().toArray(new String[mapParameters.keySet().size()])));
			
			Collections.sort(listKey);
			
			for (String parameter:  listKey) {

				String value = mapParameters.get(parameter);
				
				HorizontalPanel hp = new HorizontalPanel();

				Text txtParameter = new Text(parameter+":");	

				TextField<String> txtValue = new TextField<String>();
				txtValue.setReadOnly(true);
				setTextFieldAttr(txtValue, "background-image", "none");
				setTextFieldAttr(txtValue, "background-color", "none");
				setTextFieldAttr(txtValue, "border-style", "none");

				if(value!=null && !value.isEmpty())
					txtValue.setValue(value);
				else
					txtValue.setValue("not found");
				
				txtParameter.addStyleName("labelParameters");
				txtValue.addStyleName("valueParameters");
				
				hp.add(txtParameter);
				hp.add(txtValue);
				
				hp.setCellWidth(txtParameter, "185px");
				hp.setCellVerticalAlignment(txtParameter, HasVerticalAlignment.ALIGN_MIDDLE);
				
				hp.setCellHeight(txtParameter, "27px");
			
				hp.setCellHeight(txtValue,"27px");
				hp.setCellVerticalAlignment(txtValue, HasVerticalAlignment.ALIGN_MIDDLE);
				
				vpParameters.add(hp);
			
			}
		}
	}

	public String getTxtValue() {

		return txt.getValue();
	}
	
	public void selectTxt(){
		txt.select(0, txt.getValue().length());
	}
	
	public interface Function {
	    public void execute ();
	}

	/**
	* Safe function call on a component, which was rendered or not.
	*
	* @param c Component object that must be not null.
	* @param f Function object with the function that must be called.
	*/
	public static void safeFunctionCallOn(final Component c, final Function f) {
	    c.enableEvents(true);
	    if (c.isRendered()) {
	        f.execute();
	    } else {
	        final Listener<BaseEvent> lsnr = new Listener<BaseEvent>() {
				@Override
				public void handleEvent(final BaseEvent be) {
					  f.execute();
					
				}
	        };
	        c.addListener(Events.Render, lsnr);
	    }
	}
	
	 /* Sets a style attribute for the  text-field control */
	public static void setTextFieldAttr(final Field<?> textField, final String cssAttrNm, final String attrVal) {
		safeFunctionCallOn(textField, new Function() {
			@Override
			public void execute() {
			textField.el().firstChild().setStyleAttribute(cssAttrNm, attrVal);
			}
		});
	}
}
