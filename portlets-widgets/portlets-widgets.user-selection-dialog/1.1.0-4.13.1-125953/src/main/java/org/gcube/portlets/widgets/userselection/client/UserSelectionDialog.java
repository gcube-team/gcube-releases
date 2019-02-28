package org.gcube.portlets.widgets.userselection.client;

import java.util.ArrayList;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.userselection.client.events.SelectedUserEvent;
import org.gcube.portlets.widgets.userselection.client.events.UsersFetchedEvent;
import org.gcube.portlets.widgets.userselection.client.events.UsersFetchedEventHandler;
import org.gcube.portlets.widgets.userselection.client.resources.DialogImages;
import org.gcube.portlets.widgets.userselection.client.ui.UserDisplay;
import org.gcube.portlets.widgets.userselection.shared.ItemSelectableBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * Use this widget to display a dialog containing portal users from where one che choose.
 * 
 * To get to know which user was selected listen for the {@link SelectedUserEvent} on the {@link HandlerManager} instance you pass to this widget.
 *
 */
public class UserSelectionDialog extends GCubeDialog {

	private static final int WIDTH = 420;
	private CellPanel mainPanel = new VerticalPanel();
	private VerticalPanel topPanel = new VerticalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();
	private Image loadingImage;
	private HandlerManager eventBus;
	
	/**
	 * Constructor to use if you load the users via an async call and the fire the event	
	 * @param headerText the header text of the dialog
	 * @param eventBus the HandlerManager instance where you will fire the {@link UsersFetchedEvent} and where you will listen for the {@link SelectedUserEvent}
	 */
	public UserSelectionDialog(String headerText, HandlerManager eventBus) {
		super(true);
		this.eventBus = eventBus;
		bind();
		setText(headerText);
		DialogImages images = GWT.create(DialogImages.class);
		loadingImage = new Image(images.loader());
		setSize(""+WIDTH, "100");
		topPanel.setPixelSize(WIDTH, 100);
		bottomPanel.setPixelSize(WIDTH, 25);
		
		topPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		topPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		topPanel.add(loadingImage);
		ScrollPanel scroller = new ScrollPanel();
		scroller.setPixelSize(WIDTH+40, 300);
		
		scroller.add(topPanel);
		mainPanel.add(scroller);
		mainPanel.add(bottomPanel);
		
		bottomPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		Button close = new Button("Close");
		bottomPanel.add(close);
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
		});
		
		mainPanel.setCellHeight(bottomPanel, "25px");
		setWidget(mainPanel);		
		
	}
	
	@Override
	public void show() {
		super.show();
	}
	/**
	 *constructor to use
	 * @param headerText the header text of the dialog
	 * @param eventBus the HandlerManager instance where you will listen for the {@link SelectedUserEvent}
	 * @param users the users to show in the dialog
	 */
	public UserSelectionDialog(String headerText, HandlerManager eventBus, ArrayList<ItemSelectableBean> users) {
		this(headerText, eventBus);
		setUsers(users);
	}
	/**
	 * events binder
	 */
	private void bind() {
		eventBus.addHandler(UsersFetchedEvent.TYPE, new UsersFetchedEventHandler() {
			@Override
			public void onUsersFetched(UsersFetchedEvent event) {
				setUsers(event.getUsers());
			}
		});  
	}
	
	private void setUsers(ArrayList<ItemSelectableBean> users) {
		topPanel.remove(loadingImage);
		bottomPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		for (ItemSelectableBean user : users) {
			topPanel.add(new UserDisplay(user, eventBus));
		}
	}
}
