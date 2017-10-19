package org.gcube.portlets.user.dataminermanager.client;

import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.MenuEvent;
import org.gcube.portlets.user.dataminermanager.client.type.MenuType;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
//import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class HomePanel extends SimpleContainer {

	public HomePanel() {
		super();
		init();
		create();
	}

	private void init() {
		setItemId("HomePanel");
		setStylePrimaryName("smMenu");
		//setBodyStyle("backgroundColor:white;");
		//addStyleName("smLayoutContainer");
		//setHeaderVisible(false);
		setBorders(false);
		//setBodyBorder(false);

	}

	private void create() {
		VerticalLayoutContainer lc = new VerticalLayoutContainer();
		lc.setScrollMode(ScrollMode.AUTO);
		
		VerticalLayoutData layoutTop = new VerticalLayoutData(1, -1,
				new Margins(20,0,10,0));
		VerticalLayoutData layoutNext = new VerticalLayoutData(1, -1,
				new Margins(10,0,10,0));
		/*
		VerticalLayoutData layoutTop = new VerticalLayoutData(1, -1,
				new Margins(20, 90, 10, 90));
		VerticalLayoutData layoutNext = new VerticalLayoutData(1, -1,
				new Margins(10, 90, 10, 90));
		*/
		SimpleContainer itemDataSpace = createMenuItem(
				"Access to the Data Space",
				"The data space contains the set of input and output data sets of the users. It is possible to upload and share tables. Data sources can be chosen from those hosted by the infrastructure. Outputs of the computations can be even saved in this space.",
				DataMinerManager.resources.inputSpaceIcon(), new MouseDownHandler() {

					@Override
					public void onMouseDown(MouseDownEvent event) {
						MenuEvent menuEvent=new MenuEvent(MenuType.DATA_SPACE);
						EventBusProvider.INSTANCE.fireEvent(menuEvent);
					

					}
				});
		lc.add(itemDataSpace, layoutTop);

		SimpleContainer itemExperiment = createMenuItem(
				"Execute an Experiment",
				"This section allows to execute or prepare a Niche Modeling experiment. The section is endowed with a list of algorithms for training and executing statistical models for biological applications. Evaluation of the performances is possible by means of several kinds of measurement systems and processes.",
				DataMinerManager.resources.executionIcon(), new MouseDownHandler() {

					@Override
					public void onMouseDown(MouseDownEvent event) {
						MenuEvent menuEvent=new MenuEvent(MenuType.EXPERIMENT);
						EventBusProvider.INSTANCE.fireEvent(menuEvent);
						
						

					}
				});

		lc.add(itemExperiment, layoutNext);

		SimpleContainer itemComputations = createMenuItem(
				"Check the Computations",
				"This section allows to check the status of the computation. A list of processes launched by the user is shown along with meta-information. By clicking on the completed jobs it is possible to visualize the data set contents.",
				DataMinerManager.resources.computationsIcon(), new MouseDownHandler() {

					@Override
					public void onMouseDown(MouseDownEvent event) {
						MenuEvent menuEvent=new MenuEvent(MenuType.COMPUTATIONS);
						EventBusProvider.INSTANCE.fireEvent(menuEvent);
					}
				});

		lc.add(itemComputations, layoutNext);
		
		add(lc);

	}

	private SimpleContainer createMenuItem(String title, String description,
			ImageResource imgResource, MouseDownHandler handle) {
		HBoxLayoutContainer horiz=new HBoxLayoutContainer(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.CENTER);
		horiz.setEnableOverflow(false);
		
		//HorizontalLayoutContainer horiz = new HorizontalLayoutContainer();
	
		Image img = new Image(imgResource);

		HTML text = new HTML("<b>" + title + "</b><br>" + description);
		text.addStyleName("smMenuItemText");
		text.setWidth("400px");
		/*HorizontalLayoutData textLayoutData = new HorizontalLayoutData(400,
				140, new Margins(10, 5, 10, 10));
		HorizontalLayoutData imgLayoutData = new HorizontalLayoutData(140, 140,
				new Margins(10, 10, 10, 5));*/

		//horiz.add(text, textLayoutData);
		//horiz.add(img, imgLayoutData);

		horiz.add(text, new BoxLayoutData(new Margins(0)));
		horiz.add(img,new BoxLayoutData(new Margins(0)));

		
		SimpleContainer container = new SimpleContainer();
		container.addDomHandler(handle, MouseDownEvent.getType());

		container.setWidth(540);
		container.setHeight(160);
		container.addStyleName("smMenuItem");
		container.addStyleOnOver(container.getElement(), "smMenuItem:HOVER");
		container.add(horiz);
		
		SimpleContainer container2 = new SimpleContainer();
		HBoxLayoutContainer hbox=new HBoxLayoutContainer(HBoxLayoutAlign.MIDDLE);
		hbox.setPack(BoxLayoutPack.CENTER);
		hbox.setEnableOverflow(false);
		hbox.add(container, new BoxLayoutData(new Margins(0)));
		container2.add(hbox);
		return container2;
	}


	
	
}
