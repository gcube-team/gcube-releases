package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.EventHandler;


// TODO: Auto-generated Javadoc

/**
 * The Interface NavigationCourseEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 6, 2018
 */
public interface NavigationCourseEventHandler extends EventHandler {
	
	/**
	 * On navigation interaction course.
	 *
	 * @param activeGroupingView the active grouping view
	 */
	void onNavigationInteractionCourse(NavigationCourseEvent activeGroupingView);
}