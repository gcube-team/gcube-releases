package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.EventHandler;


// TODO: Auto-generated Javadoc
/**
 * The Interface CourseChangeStatusEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 19, 2018
 */
public interface CourseChangeStatusEventHandler extends EventHandler {
	

	/**
	 * On change status.
	 *
	 * @param courseChangeStatusEvent the course change status event
	 */
	void onChangeStatus(CourseChangeStatusEvent courseChangeStatusEvent);
}