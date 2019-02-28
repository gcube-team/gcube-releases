package org.gcube.portlets.user.workspace.client.view.toolbars;

import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 17, 2014
 *
 */
public class ACLDivInfo extends LayoutContainer {

	private HorizontalPanel hpPanel = new HorizontalPanel();

	private HTML text = new HTML();

	public ACLDivInfo(String caption, AbstractImagePrototype img) {
		super();
		super.setWidth(80);
		setId("ACLDivInfo");
		text.setPixelSize(50, 26);
		hpPanel.setWidth("100%");

		updateInfo(caption,img);
		
		hpPanel.add(text);
		add(hpPanel);
	}
	
	public void updateInfo(String caption, AbstractImagePrototype img){
		
		String html = "<div style=\"width: 100%; height: 24px; text-align:left; padding-top: 2px; opacity: 0.9;\">";
		
		if(img!=null)
			html+=addImage(img);
		
		if(caption!=null)
			html+=addCaption(caption);
		
		
		html+="</div>";
		
		text.setHTML(html);
		
		hpPanel.layout();
		this.layout();
	}
	
	private String addImage(AbstractImagePrototype img){
		
		return "<span style=\"display:inline-block; vertical-align:middle;\" >" + img.getHTML() + "</span>";
		
	}
	
	private String addCaption(String caption){
		
		return "<span style=\"padding-left: 5px; vertical-align:middle;\">"+ caption+"</span>";
		
	}
}

