package org.gcube.portlets.user.td.tablewidget.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class ActionButtonCellNoFirst extends ActionButtonCell {
	private final ActionButtonCellAppearance appearance;
	private ImageResource icon;
	private String title;

	public ActionButtonCellNoFirst() {
		this(
				GWT.<ActionButtonCellAppearance> create(ActionButtonCellAppearance.class));
	}

	public ActionButtonCellNoFirst(ActionButtonCellAppearance appearance) {
		super(appearance);
		this.appearance = appearance;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		int rowIndex = context.getIndex();
		if (rowIndex != 0) {
			this.appearance.icon = icon;
			this.appearance.title = title;
			this.appearance.render(sb);
		} else {
			sb.appendHtmlConstant(""); 
		}
	}

	public ImageResource getIcon() {
		return icon;
	}

	public void setIcon(ImageResource icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
