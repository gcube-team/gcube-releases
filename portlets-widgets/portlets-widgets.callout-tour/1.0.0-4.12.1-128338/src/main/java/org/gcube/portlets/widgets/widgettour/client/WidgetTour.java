package org.gcube.portlets.widgets.widgettour.client;

import org.gcube.portlets.widgets.widgettour.client.extendedclasses.GCubeTour;

import com.ait.toolkit.hopscotch.client.Placement;
import com.ait.toolkit.hopscotch.client.TourStep;
import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetTour implements EntryPoint {


	public void onModuleLoad() {

		//		 uncomment the lines below to test the widget
		//		Button b = new Button("Start Tour");
		//		b.addClickHandler(new ClickHandler() {
		//
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				testWidget();
		//			}
		//		});
		//
		//		// add to the page
		//		RootPanel.get("button-container").add(b);


	}

	@SuppressWarnings("unused")
	private void testWidget() {

		// Build a tour example (please look at WidgetTour.html)
		GCubeTour tour = new GCubeTour("tour-example", "portlet-id", 1, null, null);
		tour.setShowPrevButton(true);

		// add steps
		TourStep firstStep = new TourStep(Placement.BOTTOM, "header");
		firstStep.setContent("This is the banner of the page...");
		firstStep.setTitle("First step");
		firstStep.centerXOffset();
		firstStep.centerArrowOffset();
		tour.addStep(firstStep);

		// another
		TourStep secondStep = new TourStep(Placement.BOTTOM, "content-1");
		secondStep.setContent("This is the content of the page...");
		secondStep.setTitle("Second step");
		tour.addStep(secondStep);

		// another
		TourStep thirdStep = new TourStep(Placement.BOTTOM, "content-2");
		thirdStep.setContent("This is boh...");
		thirdStep.setTitle("Third step");
		tour.addStep(thirdStep);

		// start it
		tour.startTour();

	}
}
