package org.gcube.portlets.widgets.dataminermanagerwidget.client.custom.progress;

import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.widget.core.client.ProgressBar;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RedProgressBar extends ProgressBar {

	public RedProgressBar() {
		super(new ProgressBarCell(new RedProgressBarAppearance()));
	}

}
