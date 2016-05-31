package org.gcube.portlets.user.joinnew.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PanelRight extends Composite {
	private static PanelRight singleton = null;
	private VerticalPanel vp;
	private VerticalPanel main_panel = null;
	//private PanelTree tree_vo;
	private PanelFilter filter;
	
	public static PanelRight get()
	{ 
		return singleton;
	}
	
	public PanelRight() {
		Init();
		initWidget(main_panel);
		if (singleton == null) singleton = this;
	}
	
	private void Init() {
		
		this.filter = new PanelFilter();
		//this.tree_vo = new PanelTree();
		vp = new VerticalPanel();
		vp.add(this.filter);
		//vp.add(this.tree_vo);
		//vp.add(this.slider);
	    // Wrap the static tree in a DecoratorPanel
	    this.main_panel = new VerticalPanel();
	    this.main_panel.add(vp);
	}
}
