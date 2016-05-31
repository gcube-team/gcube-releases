package org.gcube.portlets.user.joinnew.client.commons;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author federico Biagini
 * 
 * This is the VO Label UI Component
 *
 */
public class ODLLink extends HTML {
	
	@SuppressWarnings("unused")
	private String _class = "";
	public ODLLink(String html, final String _class, ClickHandler handler) {
		super(html);
		this._class = _class;
		this.setStyleName("pointer pad_bottom " + _class);
		this.addClickHandler(handler);
		this.addMouseListener(new MouseListener() {
			
			public void onMouseUp(Widget arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			public void onMouseMove(Widget arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			public void onMouseLeave(Widget arg0) {
				setStyleName("pointer pad_bottom no_underline " + _class);
				
			}
			
			public void onMouseEnter(Widget arg0) {
				setStyleName("pointer pad_bottom underline " + _class);
				
			}
			
			public void onMouseDown(Widget arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public ODLLink(String html, String title, final String _class, ClickListener handler) {
		super(html);
		
		final PopupPanel pop = new PopupPanel();
		pop.setTitle(title);
		this._class = _class;
		this.setStyleName("pointer " + _class);
		
		this.addClickListener(handler);
		this.addMouseListener(new MouseListener() {
			
			public void onMouseUp(Widget arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			public void onMouseMove(Widget arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			public void onMouseLeave(Widget arg0) {
				setStyleName("pointer no_underline " + _class);
				pop.setVisible(false);
			}
			
			public void onMouseEnter(Widget arg0) {
				setStyleName("pointer underline " + _class);
				pop.setVisible(true);
			}
			
			public void onMouseDown(Widget arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}