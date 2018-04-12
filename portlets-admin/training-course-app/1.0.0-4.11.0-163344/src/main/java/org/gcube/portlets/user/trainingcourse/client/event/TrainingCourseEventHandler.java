package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.EventHandler;


// TODO: Auto-generated Javadoc

/**
 * The Interface TrainingCourseEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 6, 2018
 */
public interface TrainingCourseEventHandler extends EventHandler {
	

	/**
	 * On course event.
	 *
	 * @param trainingCourseCreatedEvent the training course created event
	 */
	void onCourseEvent(TrainingCourseEvent trainingCourseCreatedEvent);
}