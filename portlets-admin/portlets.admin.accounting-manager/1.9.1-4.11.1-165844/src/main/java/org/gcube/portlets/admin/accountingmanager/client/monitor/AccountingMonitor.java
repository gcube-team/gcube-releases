package org.gcube.portlets.admin.accountingmanager.client.monitor;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingMonitor extends AutoProgressMessageBox {

	public AccountingMonitor() {
		super("Waiting", "Please wait...");
		create();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public AccountingMonitor(SafeHtml headingHtml, SafeHtml messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public AccountingMonitor(SafeHtml headingHtml) {
		super(headingHtml);
		create();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public AccountingMonitor(String headingHtml, String messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public AccountingMonitor(String headingHtml) {
		super(headingHtml);
		create();
	}

	private void create() {
		setProgressText("Updating...");
		auto();
		show();
	}

}
