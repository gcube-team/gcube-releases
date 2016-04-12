/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;

import org.gcube.portlets.user.statisticalmanager.client.bean.TemplateDescriptor;
import org.gcube.portlets.user.statisticalmanager.client.util.StringUtil;
import org.gcube.portlets.user.statisticalmanager.client.widgets.TemplatesWiki;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 *
 */
public class TemplateSelector extends VerticalPanel {

	private static String baseUrl = GWT.getModuleBaseURL()+"../images/templateIcons/";
	private RadioGroup radioGroup = new RadioGroup();
	private FieldSet fieldSet = new FieldSet();
	
	public TemplateSelector() {
		super();
		createFieldSet();
	}
	
	/**
	 * 
	 */
	private void createFieldSet() {
		fieldSet.setHeading("Select a Template");
		fieldSet.setLayout(new FitLayout());
		fieldSet.setStyleAttribute("padding", "0px");
		HorizontalPanel hp = new HorizontalPanel();
		hp.setHeight(102);
		hp.setWidth(350);
		hp.setScrollMode(Scroll.AUTOX);
		fieldSet.setStyleAttribute("margin-top", "0px");
				
		for (TemplateDescriptor templateDescriptor: TemplateDescriptor.descriptors) {
			String id = templateDescriptor.getId();
			String name = StringUtil.getCapitalWords(id);

			final Radio radio = new Radio();
			radio.setValueAttribute(id);
			radioGroup.add(radio);

			Image img = new Image(baseUrl+id+".png");
			img.addStyleName("tableImporter-templateIcon");
			img.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					radio.setValue(true);
				}
			});
			
			VerticalPanel vp = new VerticalPanel();
			vp.setHorizontalAlign(HorizontalAlignment.CENTER);
			vp.add(img);
			vp.add(new Html("<div class='tableImporter-templateText'><center>"+name+"</center></div>"));
			vp.add(radio);
			vp.setBorders(false);
			vp.setSize(80,100);
			hp.add(vp);
		}
		fieldSet.add(hp);
		fieldSet.add(createTemplatesDescriptor());
		this.add(fieldSet);
	}

	public boolean isValid() {
		return radioGroup.getValue()!=null;
	}
	
	public String getValue() {
		return isValid() ? radioGroup.getValue().getValueAttribute() : null;
	}

	/**
	 * 
	 */
	protected void resetField() {
		radioGroup.reset();
	}
	
	
	

	private Html createTemplatesDescriptor() {
//		Frame frame = w.setUrl(GWT.getModuleBaseURL()+"../templateDescriptions.html");
//		frame.addStyleName("tableImporter-templateDescriptionsFrame");

		Html html = new Html("Open Template Descriptions");
		html.addStyleName("tableImporter-templateDescriptionsButton");
		html.addListener(Events.OnClick, new Listener<BaseEvent>() {
			private TemplatesWiki templatesWiki;

			@Override
			public void handleEvent(BaseEvent be) {
				if (templatesWiki == null)
					templatesWiki = new TemplatesWiki();
				templatesWiki.show();
			}
		});
		return html;
	}

}
