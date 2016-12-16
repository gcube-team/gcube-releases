package org.gcube.portlets.user.dataminermanager.client.monitor;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
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
	 * @param messageHtml
	 */
	public StatusMonitor(SafeHtml headingHtml, SafeHtml messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 */
	public StatusMonitor(SafeHtml headingHtml) {
		super(headingHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 * @param messageHtml
	 */
	public StatusMonitor(String headingHtml, String messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
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
