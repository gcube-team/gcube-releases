package org.gcube.portlet.user.userstatisticsportlet.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Costantino Perciante at ISTI-CNR
 */
public class Statistics implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		RootPanel.get("statistics-container").add(new StatisticsPanel());

	}
}
