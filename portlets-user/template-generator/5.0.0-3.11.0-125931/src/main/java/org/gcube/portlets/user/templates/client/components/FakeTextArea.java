package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.user.templates.client.Templates;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;


public class FakeTextArea extends TextArea {

	Presenter c;
	PopupPanel popPanel;
	
	@SuppressWarnings("deprecation")
	public FakeTextArea(int i, final Presenter c) {
		this.c = c;
		setWidth("90%");
		setHeight("25px");
		setStyleName("noBorder");
		popPanel = new PopupPanel(true);
		
		SimplePanel panel = new SimplePanel();
		MenuBar menu = new MenuBar();
		MenuItem toinsert = Templates.get().getHeader().getInsertMenu();
		menu.addItem(toinsert);
		panel.add(menu);
		popPanel.add(panel);
		//setText(""+i);

		addKeyboardListener(new KeyboardListener() {
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {			
				popPanel.setPopupPosition(sender.getAbsoluteLeft(), sender.getAbsoluteTop());
				popPanel.show();
				setText("");
			}
			public void onKeyPress(Widget sender, char keyCode, int modifiers) { 	 }
			public void onKeyUp(Widget sender, char keyCode, int modifiers) { 
				setText("");
			}			
		});

//		MouseListener listener = new MouseListener() {
//			public void onMouseDown(Widget sender, int x, int y) {
//				
//			}
//			public void onMouseEnter(Widget sender) {	}
//			public void onMouseLeave(Widget sender) {	}
//			public void onMouseMove(Widget sender, int x, int y) {	}
//			public void onMouseUp(Widget sender, int x, int y) {
//				Window.alert("pupp");
//				int myIndex = c.getWorkSpacePanel().getMainLayout().getWidgetIndex(sender);
//				c.setCurrCursorPos(myIndex+1);	
//				GWT.log("Selected Index is " + myIndex, null);
//				setText("");
//			}			
//		};
		
		addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				int myIndex = c.getWorkSpacePanel().getMainLayout().getWidgetIndex(sender);
				c.setCurrCursorPos(myIndex+1);	
				GWT.log("Selected Index is " + myIndex, null);
				setText("");				
			}			
		});
	}
}
