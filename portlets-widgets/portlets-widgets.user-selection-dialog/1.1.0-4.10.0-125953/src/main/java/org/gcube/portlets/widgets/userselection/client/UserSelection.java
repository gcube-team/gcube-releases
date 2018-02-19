package org.gcube.portlets.widgets.userselection.client;

import java.util.ArrayList;

import org.gcube.portlets.widgets.userselection.client.events.SelectedUserEvent;
import org.gcube.portlets.widgets.userselection.client.events.UsersFetchedEvent;
import org.gcube.portlets.widgets.userselection.shared.ItemSelectableBean;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * Use this widget to display a dialog containing portal users from where one che choose. 
 * uncomment //sample in the onModuleLoad() to see it working
 * 
 * To get to know which user was selected listen for the {@link SelectedUserEvent} on the {@link HandlerManager} instance you pass to this widget.
 *
 */
public class UserSelection implements EntryPoint {
	private final HandlerManager eventBus = new HandlerManager(null);
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//sample();
	}
	private void sample() {
		//SAMPLE USAGE
		final UserSelectionDialog dlg = new UserSelectionDialog("People who set this as Favorite", eventBus);
		dlg.center();
		dlg.show();	
		ArrayList<ItemSelectableBean> toShow = new ArrayList<ItemSelectableBean>();
		toShow.add(new ItemSelectableBean("", "Pippo", "photo"));
		toShow.add(new ItemSelectableBean("", "Pippo1", "photo"));
		toShow.add(new ItemSelectableBean("", "Pippo2", "photo"));
		toShow.add(new ItemSelectableBean("", "Pippo3", "photo"));
		eventBus.fireEvent(new UsersFetchedEvent(toShow));
	}
}
