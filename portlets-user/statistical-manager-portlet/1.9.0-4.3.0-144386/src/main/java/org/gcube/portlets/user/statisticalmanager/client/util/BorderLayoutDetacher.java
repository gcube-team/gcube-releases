/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.util;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author ceras
 *
 */
public class BorderLayoutDetacher {

	/**
	 * @param jobViewer
	 * @param south
	 * @param jobsPanel
	 */
	public static void setDetachable(final ContentPanel targetPanel, final BorderLayoutData southPanelData, final LayoutContainer mainContainer) {
		final Button toggle = new Button("detach");
		final Window w = new Window();
		w.setLayout(new FitLayout());
		w.setSize(640, 480);
		w.add(targetPanel);
		
		toggle.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (mainContainer.getItems().contains(targetPanel)) {
//					mainContainer.remove(targetPanel);					
					w.show();
					toggle.setText("attach");
				} else {
					w.hide();
//					mainContainer.add(targetPanel, southPanelData);
					toggle.setText("detach");		
				}
			}
		});
		
		targetPanel.getHeader().addTool(toggle);
	}

}
