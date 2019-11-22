/**
 *
 */
package org.gcube.portlets.user.workspace.client.util;

import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jan 9, 2017
 */
public class MaskPanel extends FlowPanel{

	private HorizontalPanel hp = new HorizontalPanel();
	private Image loading = Resources.getIconLoading().createImage();
	private Label label = new Label();

	
	public MaskPanel(String msg) {
		msg = msg!=null &&!msg.isEmpty()?msg:"Loading...";
		label.setText(msg);
		//label.getElement().getStyle().setMarginRight(2.0, Unit.PX);
		hp.add(label);
		hp.add(loading);
		add(hp);
	}
}
