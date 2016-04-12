/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.widgets;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.statisticalmanager.client.bean.TemplateDescriptor;
import org.gcube.portlets.user.statisticalmanager.client.util.StringUtil;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;



/**
 * @author ceras
 *
 */
public class TemplatesWiki extends Window {
	
	private static String baseImgUrl = GWT.getModuleBaseURL()+"../images/templateIcons/";
	private static String baseTemplateUrl = GWT.getModuleBaseURL()+"../templateDescriptions/";
	private ContentPanel templateArea;
	private ContentPanel templatesListPanel;
	private Map<TemplateDescriptor, TemplateDescriptorPanel> panels = new HashMap<TemplateDescriptor, TemplateDescriptorPanel>();


	public TemplatesWiki() {
		super();
		this.setHeading("Templates Descriptor");
		this.setSize("80%", "500");
		this.setMaximizable(true);
		this.setLayout(new FitLayout());
//		this.setStyleAttribute("background-color", "white");
		this.setLayout(new BorderLayout());
		this.setBodyStyle("border: none;background: white");
	}
	
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		createTemplateAreaPanel();
		createTemplatesListPanel();
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		centerData.setMargins(new Margins(0, 0, 0, 5));  

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200, 150, 400);  
		westData.setSplit(true);
		
		this.add(templatesListPanel, westData);
		this.add(templateArea, centerData);
		
		selectTemplate(TemplateDescriptor.defaultDescriptor);

	}
	
	private void createTemplateAreaPanel() {
		templateArea = new ContentPanel(new FitLayout());
		templateArea.setScrollMode(Scroll.AUTO);
		templateArea.setHeaderVisible(false);
		templateArea.setBodyStyle("padding: 10px");
	}
	
	private void createTemplatesListPanel() {
		templatesListPanel = new ContentPanel();
		templatesListPanel.setScrollMode(Scroll.AUTO);
		templatesListPanel.setHeaderVisible(false);
		templatesListPanel.setBodyStyle("padding: 5px");

		VerticalPanel vp = new VerticalPanel();
		vp.setHorizontalAlign(HorizontalAlignment.CENTER);
		vp.setBorders(false);
		
		Html html = new Html("Select a template to show the table template structure.");
		html.addStyleName("tableImporter-templateDescriptions-textSelectTemplate");
		vp.add(html);
		vp.add(new Html("<hr>"));
		for (final TemplateDescriptor td : TemplateDescriptor.descriptors) {
			String id = td.getId();
			String name = StringUtil.getCapitalWords(id);

			Image img = new Image(baseImgUrl+id+".png");
			img.addStyleName("tableImporter-templateIcon");
			img.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					selectTemplate(td);
				}
			});
			
			vp.add(img);
			vp.add(new Html("<div class='tableImporter-templateText'><center>"+name+"</center></div>"));
			vp.add(new Html("<hr>"));
		}
		templatesListPanel.add(vp);
	}


	private void selectTemplate(TemplateDescriptor td) {
		templateArea.removeAll();
		TemplateDescriptorPanel panel = panels.get(td);
		if (panel==null) {
			panel = new TemplateDescriptorPanel(td);
			panels.put(td, panel);
		}
		templateArea.add(panel);
		templateArea.layout();
	}
	
	private class TemplateDescriptorPanel extends LayoutContainer {

		/**
		 * @param td
		 */
		public TemplateDescriptorPanel(TemplateDescriptor td) {
			super();
			this.addStyleName("tableImporter");
			this.addStyleName("tableImporter-templateDescriptorPanel");
			this.setAutoHeight(true);
			this.setAutoWidth(true);
			
			Image img = new Image(baseImgUrl+td.getId()+".png");
			img.addStyleName("tableImporter-templateDescriptorPanel-img");
			this.add(img);

			Html title = new Html(td.getTitle());
			title.addStyleName("tableImporter-title");
			this.add(title);

//			Html description = new Html(td.getDescription());
//			description.addStyleName("tableImporter-templateDescriptorPanel-description");
//			this.add(description);

			HtmlContainer hc = new HtmlContainer();
			hc.setUrl(baseTemplateUrl+td.getId()+".html");
			this.add(hc);

//
//			
//			
//			vp.add(new Html("<div style='tableImporter-templateDescription-title'>"+descriptor.getTitle()+"</div>"));
//			
//			HorizontalPanel hp = new HorizontalPanel();
//			TableData td = new TableData();
//			td.setWidth("100px");
//			hp.add( new Image(baseUrl+descriptor.getId()+".png"), td);
//			
//			td = new TableData();
//			td.setWidth("100%");
//			hp.add(new Html(descriptor.getTitle()), td);
//			
//			vp.add(hp);
		}

	}
}
