package org.gcube.portlets.widgets.pickitem.client;

import java.util.ArrayList;

import org.gcube.portlets.widgets.pickitem.client.bundle.CssAndImages;
import org.gcube.portlets.widgets.pickitem.client.dialog.PickItemsDialog;
import org.gcube.portlets.widgets.pickitem.client.events.PickedItemEvent;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * Use this widget to display a  a dropdown user list you can attach to a textbox to make select portal users typing @
 * uncomment //sample in the onModuleLoad() to see it working
 * 
 * To get to know which user was selected listen for the {@link PickedItemEvent} on the {@link HandlerManager} instance you pass to this widget.
 *
 */
public class PickItem implements EntryPoint {

	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//sample();
	}
	
	private void sample() {
		HandlerManager eventbus = new HandlerManager(null);
		ArrayList<ItemBean> users = new ArrayList<ItemBean>();
		users.add(new ItemBean("id", "test.user1", "TestFoo", "phot URL"));
		users.add(new ItemBean("id", "test.user2", "TestFie", "phot URL"));
		users.add(new ItemBean("id", "test.user3", "tAbbaFoo", "phot URL"));
		users.add(new ItemBean("id", "test.user4", "#ABabbaFie", "phot URL"));
		users.add(new ItemBean("id", "test.user5", "#ACaroFoo", "phot URL"));
		users.add(new ItemBean("id", "test.user6", "#DarioFie", "phot URL"));
		users.add(new ItemBean("id", "test.user7", "#ErgoFie", "phot URL"));
	
		final TextBox tb = new TextBox();
		final int popUpY = tb.getAbsoluteTop()+30;
	
		final PickItemsDialog pickUserDlg = new PickItemsDialog('#', users, eventbus, 300);
		pickUserDlg.withTriggerCharIncluded();
		tb.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				pickUserDlg.onKeyPress(-1, event.getNativeKeyCode(), tb.getAbsoluteLeft(), popUpY, tb.getText());			
			}
		});
		
		RootPanel.get().add(tb);
	}
}
