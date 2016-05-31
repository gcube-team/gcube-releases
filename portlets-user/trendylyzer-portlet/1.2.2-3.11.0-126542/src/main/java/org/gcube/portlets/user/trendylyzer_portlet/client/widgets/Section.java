/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.widgets;

import java.util.Map;
import java.util.Map.Entry;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class Section extends VerticalPanel {
	
	private LayoutContainer lc = new LayoutContainer();
	/**
	 * 
	 */
	public Section(String title, ImageResource img) {
		super();
		
		createHeader(title, img);			
		lc.addStyleName("jobViewer-section");
		this.add(lc);
	}
	
	public Section(String title, ImageResource img, Map<String, String> map) {
		super();
		
		lc.addStyleName("jobViewer-section");
		
		createHeader(title, img);
		addMap(map);
		
		this.add(lc);
	}
	
	/**
	 * 
	 */
	private void createHeader(String title, ImageResource img) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("jobViewer-section-header");
		hp.setVerticalAlign(VerticalAlignment.BOTTOM);
		
		if (img!=null) {
			Image image = new Image(img);
			image.addStyleName("jobViewer-section-header-image");
			hp.add(image);
		}

		if (title!=null) {
			Html htmlTitle = new Html(title);
			htmlTitle.addStyleName("jobViewer-section-header-title");
			hp.add(htmlTitle);
		}
		
		
		this.add(hp);
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.LayoutContainer#add(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean add(Widget widget) {
		return lc.add(widget);
	}
	
	public void addMap(Map<String, String> map) {
		lc.add(new HashMapViewer(map));
	}
	
	public void setFrameHidden() {
		this.lc.removeStyleName("jobViewer-section");
	}
	
	public void setPadding(int padding) {
		lc.setStyleAttribute("padding", padding+"px");
	}
}
