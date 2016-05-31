package org.gcube.portlets.user.results.client.panels;

import org.gcube.portlets.user.results.client.components.BasketView;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.model.BasketModel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <code> HeaderBar </code> class is the Left part of the UI 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * 
 * @version september 2008 (0.1) 
 */

public class LeftPanel extends Composite {
	/**
	 * 
	 */
	public static final int LEFTPANEL_WIDTH = 280;
	
	private VerticalPanel container = new VerticalPanel();
	
	private VerticalPanel mainLayout = new VerticalPanel();

	/**
	 * The topMenu 
	 */
	private VerticalPanel topMenu = new VerticalPanel();

	protected String currPath = "";

	private TextBox textBoxPath = new TextBox();

	private Controller controller;
	
	private BasketView myBasket;
	
	private PickupDragController dragc;
	
	private ScrollPanel scroller;
	
	

	HTML basketLabel;
	/**
	 * constructor
	 */
	public LeftPanel(Controller control, PickupDragController dragc) {
		this.dragc = dragc;
		controller = control;

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setVerticalAlignment(HasAlignment.ALIGN_TOP);
		vPanel.add(mainLayout);
		scroller = new ScrollPanel(vPanel);
		
		int scrollerHeight = Window.getClientHeight() - mainLayout.getAbsoluteTop();
		mainLayout.setSize(""+(LEFTPANEL_WIDTH-10), "50%");
		container.setSize(""+(LEFTPANEL_WIDTH-10), "95%");
		/*mainLayout.setWidth(""+LEFTPANEL_WIDTH);*/

		mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		container.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		//mainLayout.setStyleName("newresultset-leftpanel-background");
		container.setStyleName("gcube_panel");
		
//		***   placing the top menu for page browsing 	
		topMenu.setPixelSize(LEFTPANEL_WIDTH-15, 15);
		topMenu.setStyleName("topMenu");
		topMenu.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		topMenu.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		

		textBoxPath.setSize("100%", "16px");

		//textBoxPath.setStyleName("pathTextBox");
		
		textBoxPath.setStyleName("gcube_selected");	
		
		textBoxPath.setReadOnly(true);
		topMenu.add(textBoxPath);
		topMenu.add(new HTML("&nbsp;"));
		topMenu.setSpacing(1); 

		//mainLayout.add(topMenu);
		//scroller = new ScrollPanel(new HTML());
		container.add(mainLayout);
		scroller = new ScrollPanel(container);
		scroller.setPixelSize(LEFTPANEL_WIDTH + 20, scrollerHeight + 20);
		initWidget(scroller);		
	}

	/**
	 * Set the current chosen basket path on the top og the panel
	 * @param currPath .
	 */
	public void setCurrPath(String currPath) {
		this.currPath = currPath;
		textBoxPath.setText(currPath);
	}
	/**
	 * 
	 * @param basketModel
	 */
	public void showBasket(BasketModel basketModel) {
		int scrollerHeight = Window.getClientHeight() - mainLayout.getAbsoluteTop();
		mainLayout.clear();
		mainLayout.add(topMenu);
		Log.debug("show basket");
		myBasket = new BasketView(basketModel, dragc, controller);
		mainLayout.add(myBasket);
		//mainLayout.addStyleName("border");
		scroller.setPixelSize(LEFTPANEL_WIDTH+20, scrollerHeight + 20);
	}
	
	public void resizeBasket() {

	}

	public VerticalPanel getMainLayout() {
		return mainLayout;
	}

	public BasketView getMyBasket() {
		return myBasket;
	}
	
	public ScrollPanel getScroller() {
		return scroller;
	}
}
