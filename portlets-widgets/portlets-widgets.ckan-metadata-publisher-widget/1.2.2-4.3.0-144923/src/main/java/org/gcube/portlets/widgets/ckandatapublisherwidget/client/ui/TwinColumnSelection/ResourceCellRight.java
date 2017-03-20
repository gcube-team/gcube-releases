package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.TwinColumnSelection;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.icons.Images;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;

/**
 * Cell that renders right side panel objects
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourceCellRight extends AbstractCell<ResourceElementBean>{

	private static final Images image = GWT.create(Images.class);
	private static final String tip = "Hold down the Control (CTRL) or Command button to select multiple options. Double click to access "
			+ "this resource information";

	public ResourceCellRight() {
		super("keydown", "dblclick");
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			ResourceElementBean value, SafeHtmlBuilder sb) {

		if (value == null || !value.isToBeAdded() || value.isFolder()) {
			return;
		}

		Image file = new Image(image.fileIcon());
		file.setWidth("15px");
		file.setHeight("15px");

		sb.appendHtmlConstant("<div title='" + tip + "' style='width:100%; min-height:30px; padding-top:5px;'>");
		sb.appendHtmlConstant("<span style='margin-left:5px'>");
		sb.appendHtmlConstant(file.toString());
		sb.appendHtmlConstant("</span>");
		sb.appendHtmlConstant("<span style='margin-left:10px'>");
		sb.appendHtmlConstant("<b>");
		sb.appendEscaped(value.getEditableName());
		sb.appendHtmlConstant("</b>");
		sb.appendHtmlConstant("</span>");
		sb.appendHtmlConstant("</div>");
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, ResourceElementBean value, NativeEvent event,
			ValueUpdater<ResourceElementBean> valueUpdater) {
		if(value == null || TwinColumnSelectionMainPanel.freezed)
			return;
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		ResourceInfoForm info = new ResourceInfoForm(value, valueUpdater);
		if(TwinColumnSelectionMainPanel.detailContainer.getWidget() != null)
			TwinColumnSelectionMainPanel.detailContainer.clear();
		TwinColumnSelectionMainPanel.detailContainer.add(info);
		TwinColumnSelectionMainPanel.detailContainer.setVisible(true);
	}
}
