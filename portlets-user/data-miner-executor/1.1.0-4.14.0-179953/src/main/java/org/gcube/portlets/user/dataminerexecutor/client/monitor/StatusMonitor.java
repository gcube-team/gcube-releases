package org.gcube.portlets.user.dataminerexecutor.client.monitor;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StatusMonitor extends AutoProgressMessageBox {

	public StatusMonitor() {
		super("Waiting", "Please wait...");
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 *            head
	 * @param messageHtml
	 *            message
	 */
	public StatusMonitor(SafeHtml headingHtml, SafeHtml messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 *            head
	 */
	public StatusMonitor(SafeHtml headingHtml) {
		super(headingHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 *            head
	 * @param messageHtml
	 *            message
	 */
	public StatusMonitor(String headingHtml, String messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 *            head
	 */
	public StatusMonitor(String headingHtml) {
		super(headingHtml);
		create();
	}

	private void create() {
		setProgressText("In progress...");
		auto();
		show();
	}

}
