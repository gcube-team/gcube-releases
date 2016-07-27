package org.gcube.portlets.user.gcubewidgets.client;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetFactory implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//showSample2();
	}
	
	private void showSample2() {
			ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
									
				}				
				@Override
				public void onFailure(Throwable caught) {					
				}
			});
	}
	
	
	private void showSample() {
		
		//
		PushButton button = new PushButton("Click me");
		

		PushButton button2 = new PushButton("Click me");
		button2.setEnabled(false);
		button.setWidth("100px");
		button2.setWidth("100px");
		Image img = new Image();
		// or we can set an id on a specific element for styling
		img.getElement().setId("pc-template-img");

		//  GCubeFrame mainLayout = new GCubeFrame( "My Header Caption", "http://myporlet-usersguide-url");
		GCubePanel vPanel = new GCubePanel("http://myporlet-usersguide-url");


		vPanel.addHeaderWidget(new Button("Button"));
		vPanel.addHeaderWidget(new Button("Button2"));

		// Create the File menu bar
		MenuBar menuBar = new MenuBar();
		menuBar.setAutoOpen(false);
		menuBar.setWidth("100px");
		menuBar.setAnimationEnabled(true);
		menuBar.addSeparator();
//		menuBar.addItem(getOptionsMenu());		
		vPanel.addHeaderWidget(menuBar);

		vPanel.setSize("505", "50%");
		vPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);


		vPanel.add(img);
		vPanel.add(button);
		vPanel.add(button2);
		RootPanel.get().add(vPanel);

//		 Create the dialog box
		final GCubeDialog dialogBox = new GCubeDialog();
		dialogBox.setText("Welcome to GWT!");
		dialogBox.setAnimationEnabled(true);
		Button closeButton = new Button("close");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.setWidth("100%");
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dialogVPanel.add(new HTML("Lorem ipsum .....sine sfjsahf jwef apweFH "));
		dialogVPanel.add(closeButton);
//
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		// Set the contents of the Widget
		dialogBox.setWidget(dialogVPanel);
		dialogBox.center();
		dialogBox.show();
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.center();
				dialogBox.show();
			}
		});
	}

	private MenuItem getOptionsMenu() {
		Command openPageProperties = new Command() {	
			public void execute() {

			}
		};

		Command openHelp = new Command() {
			public void execute() {

			}
		};
		//		Create the Options menu
		MenuBar optionsMenu = new MenuBar(true);

		optionsMenu.setAnimationEnabled(true);
		MenuItem toReturn = new MenuItem("Menu", optionsMenu);
		optionsMenu.addItem("Menu item first", openPageProperties);
		optionsMenu.addItem("Menu item second", openPageProperties);
		//optionsMenu.addItem(optionPDF);
		optionsMenu.addItem("Menu item thrid", openPageProperties);
		optionsMenu.addItem("....", openHelp);	
		return toReturn;
	}

//	/**
//	 * 
//	 * @return .
//	 */
//	public Widget getStackPanel() {
//		// Get the images
//		Image images = (Image) GWT.create(Image.class);
//
//		// Create a new stack panel
//		StackPanel stackPanel = new StackPanel();
//		stackPanel.setWidth("200px");
//		
//		
//		stackPanel.add();
//
//		// Add a list of filters
//		String filtersHeader = getHeaderString(
//				constants.cwStackPanelFiltersHeader(), images.filtersgroup());
//		stackPanel.add(createFiltersItem(), filtersHeader, true);
//
//		// Add a list of contacts
//		String contactsHeader = getHeaderString(
//				constants.cwStackPanelContactsHeader(), images.contactsgroup());
//		stackPanel.add(createContactsItem(images), contactsHeader, true);
//
//		// Return the stack panel
//		stackPanel.ensureDebugId("cwStackPanel");
//		return stackPanel;
//	}
}
