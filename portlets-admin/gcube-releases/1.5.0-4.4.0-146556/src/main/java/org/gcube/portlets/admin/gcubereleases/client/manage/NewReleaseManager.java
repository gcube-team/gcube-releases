/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage;

import org.gcube.portlets.admin.gcubereleases.client.dialog.DialogResult;
import org.gcube.portlets.admin.gcubereleases.client.manage.release.FormNewRelease;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The Class NewReleaseManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class NewReleaseManager {

	private FormNewRelease formNewRelease = new FormNewRelease();

	private BaseViewTemplate template;

	private DialogResult dialog = new DialogResult(null, "New Release");

	/**
	 * Instantiates a new new release manager.
	 */
	public NewReleaseManager() {

		template = new BaseViewTemplate();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				
			}
		});

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("400px");
		vp.add(template);

		template.addToMiddle(formNewRelease);

		dialog.addToCenterPanel(vp);
		dialog.setWidth("500px");
		
		dialog.center();
	}

	/**
	 * Show dialog.
	 */
	public void showDialog() {
		dialog.show();
	}
}
