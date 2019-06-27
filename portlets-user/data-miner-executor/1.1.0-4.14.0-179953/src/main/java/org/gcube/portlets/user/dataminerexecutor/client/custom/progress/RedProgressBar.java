package org.gcube.portlets.user.dataminerexecutor.client.custom.progress;

import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.widget.core.client.ProgressBar;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class RedProgressBar extends ProgressBar {

	public RedProgressBar() {
		super(new ProgressBarCell(new RedProgressBarAppearance()));
	}

}
