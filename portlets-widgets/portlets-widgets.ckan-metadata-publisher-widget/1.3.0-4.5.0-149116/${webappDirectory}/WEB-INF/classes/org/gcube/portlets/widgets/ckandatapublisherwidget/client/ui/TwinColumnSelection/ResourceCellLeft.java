package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.TwinColumnSelection;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.icons.Images;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
/**
 * Cell that renders left side panel objects
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourceCellLeft extends AbstractCell<ResourceElementBean>{
	private static final Images image = GWT.create(Images.class);
	private static final String tipFile = "Hold down the Control (ctrl) or Command button to select multiple options";
	private static final String tipFolder = "Click on the folder to navigate it";

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			ResourceElementBean value, SafeHtmlBuilder sb) {

		if (value == null || value.isToBeAdded()) {
			return;
		}

		Image file = new Image(image.fileIcon());
		Image folder = new Image(image.folderIcon());
		file.setWidth("15px");
		file.setHeight("15px");
		folder.setWidth("15px");
		folder.setHeight("15px");

		String whichTip = value.isFolder() ? tipFolder : tipFile;
		sb.appendHtmlConstant("<div title='" + value.getName() + "( " + whichTip + " )" + "' style='overflow-x:hidden;white-space:nowrap;text-overflow:ellipsis;max-width:240px; min-height:30px; padding-top:5px;'>");
		sb.appendHtmlConstant("<span style='margin-left:5px;'>");
		sb.appendHtmlConstant(value.isFolder() ? folder.toString() : file.toString());
		sb.appendHtmlConstant("</span>");
		sb.appendHtmlConstant("<span style='margin-left:10px;'>");
		sb.appendHtmlConstant("<b>");
		sb.appendEscaped(value.getName());
		sb.appendHtmlConstant("</b>");
		sb.appendHtmlConstant("</span>");
		sb.appendHtmlConstant("</div>");
	}
}
