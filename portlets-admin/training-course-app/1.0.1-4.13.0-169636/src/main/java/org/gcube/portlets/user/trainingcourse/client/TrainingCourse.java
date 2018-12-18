package org.gcube.portlets.user.trainingcourse.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TrainingCourse implements EntryPoint {
	private static final String TRAINING_COURSE_DIV = "training-course";

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	public static final int HIDE_TREE_PANEL_WHEN_WIDTH_LESS_THAN = 650;
	
	private TrainingCourseAppController training;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		training = new TrainingCourseAppController();

		RootPanel.get(TRAINING_COURSE_DIV).add(training.getView());

		// CreateCourseView newCourse = training.createNewCourse();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				GWT.log("onWindowResized width: " + event.getWidth() + " height: " + event.getHeight());
				updateSize();
			}
		});

		updateSize();
	}

	/**
	 * Update window size
	 */
	public void updateSize() {

		RootPanel workspace = RootPanel.get(TRAINING_COURSE_DIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int footer = 85; // footer is bottombar + sponsor

		int rootHeight = Window.getClientHeight() - topBorder - 4 - footer;// - ((footer ==
																			// null)?0:(footer.getOffsetHeight()-15));
		if (rootHeight < 550)
			rootHeight = 550;

		int rootWidth = Window.getClientWidth() - 2 * leftBorder; // - rightScrollBar;
		GWT.log("New "+TRAINING_COURSE_DIV+" dimension Height: " + rootHeight + " Width: " + rootWidth);
		training.getViewController().updateViewSize(rootWidth, rootHeight);

	}
}
