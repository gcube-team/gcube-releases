package org.gcube.portlets.user.statisticalalgorithmsimporter.client.monitor;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class StatAlgoImporterMonitor extends AutoProgressMessageBox {
	
	public StatAlgoImporterMonitor(){
		super("Waiting", "Please wait...");
		create();
	}
	
	/**
	 * 
	 * @param headingHtml
	 * @param messageHtml
	 */
	public StatAlgoImporterMonitor(SafeHtml headingHtml, SafeHtml messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}
	
	/**
	 * 
	 * @param headingHtml
	 */
	public StatAlgoImporterMonitor(SafeHtml headingHtml) {
		super(headingHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 * @param messageHtml
	 */
	public StatAlgoImporterMonitor(String headingHtml, String messageHtml) {
		super(headingHtml, messageHtml);
		create();
	}

	/**
	 * 
	 * @param headingHtml
	 */
	public StatAlgoImporterMonitor(String headingHtml) {
		super(headingHtml);
		create();
	}

	private void create() {
		setProgressText("In progress...");
		auto();
		show();
	}
	
	
	
}
