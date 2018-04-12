package org.gcube.portlets.user.dataminermanager.client.custom.progress;

import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.widget.core.client.ProgressBar;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GreenProgressBar extends ProgressBar {

	public GreenProgressBar() {
		super(new ProgressBarCell(new GreenProgressBarAppearance()));
	}

}
