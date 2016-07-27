package org.gcube.portlets.user.tdtemplate.client.templatecreator.smart;

import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 13, 2014
 *
 */
public class SmartHtmlDiv extends LayoutContainer {

	private HorizontalPanel hpPanel = new HorizontalPanel();

	private HTML text = new HTML();
	private String defaultWidth = "110px";

	public SmartHtmlDiv(String title, String caption, String width) {
		super();
		
		if(width==null)
			hpPanel.setWidth(defaultWidth);
		else
			hpPanel.setWidth(width);
			

		update(caption,title);
		
		hpPanel.add(text);
		add(hpPanel);
	}
	
	public void update(String caption, String title){
		
		String html = "<div style=\"width: 100%; text-align:left; padding: 2px;\">";
		
		if(title!=null)
			html+=addTitle(title);
		
		if(caption!=null)
			html+=addCaption(caption);
		
		
		html+="</div>";
		
		text.setHTML(html);
		
		hpPanel.layout();
		this.layout();
	}
	
	private String addTitle(String title){
		
		return "<span style=\"vertical-align:middle; font-weight: bold;\">"+ title+"</span><br/>";
		
	}
	
	private String addCaption(String caption){
		
		return "<span style=\"padding-left: 5px; vertical-align:middle;\">"+ caption+"</span>";
		
	}
}

