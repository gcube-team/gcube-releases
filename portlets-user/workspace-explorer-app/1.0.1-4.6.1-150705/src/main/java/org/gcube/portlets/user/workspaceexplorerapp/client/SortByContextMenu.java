/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client;

import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialRow;

import java.util.List;

import org.gcube.portlets.user.workspaceexplorerapp.client.event.OrderDataByEvent;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 21, 2016
 */
public class SortByContextMenu extends PopupPanel{

	/**
	 * @param handlerManager
	 * @param links
	 */
	public SortByContextMenu(final HandlerManager handlerManager, List<MaterialLink> links) {
		setAutoHideEnabled(true);
		getElement().addClassName("popup-order");
		MaterialRow mr = new MaterialRow();
		MaterialColumn mc = new MaterialColumn();
		mr.add(mc);
		for (int i= 0; i<links.size(); i++) {
			final MaterialLink materialLink = links.get(i);
			MaterialRow mrs = new MaterialRow();
			materialLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					SortByContextMenu.this.hide();
					GWT.log("clicked : "+event.getSource().toString());
					handlerManager.fireEvent(new OrderDataByEvent(materialLink.getText()));

				}
			});
//			mr.setWidget(i, 0, materialLink);
			mrs.add(materialLink);
			mc.add(mrs);
		}
		add(mr);
		getElement().getStyle().setZIndex(5000);
	}
}
