package org.gcube.portlets.user.td.tablewidget.client.custom;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.ResizeCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.HasSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class ValidationCell extends ResizeCell<Boolean> implements
		HasSelectHandlers {

	private final ValidationCellAppearance appearance;
	private ImageResource trueIcon;
	private ImageResource falseIcon;
	private String trueTitle;
	private String falseTitle;

	public ValidationCell() {
		this(
				GWT.<ValidationCellAppearance> create(ValidationCellAppearance.class));
	}

	public ValidationCell(ValidationCellAppearance appearance) {
		super("click");
		this.appearance = appearance;
	}

	public void setTrueIcon(ImageResource icon) {
		this.trueIcon = icon;
	}

	public void setFalseIcon(ImageResource icon) {
		this.falseIcon = icon;
	}

	
	public void setTrueTitle(String trueTitle) {
		this.trueTitle = trueTitle;
	}

	public void setFalseTitle(String falseTitle) {
		this.falseTitle = falseTitle;
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler) {
		return addHandler(handler, SelectEvent.getType());
	}

	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		this.appearance.trueIcon = trueIcon;
		this.appearance.falseIcon = falseIcon;
		if (trueTitle == null) {
			trueTitle = "";
		}
		if (falseTitle == null) {
			falseTitle = "";
		}
		this.appearance.trueTitle = trueTitle;
		this.appearance.falseTitle = falseTitle;
	
		this.appearance.render(sb, value);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Boolean value,
			NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		Element target = event.getEventTarget().cast();
		// ignore the parent element
		if (isDisableEvents()
				|| !parent.getFirstChildElement().isOrHasChild(target)) {
			return;
		}

		XElement p = parent.cast();

		String eventType = event.getType();
		if ("click".equals(eventType)) {
			onClick(context, p, value, event, valueUpdater);
		}
	}

	private void onClick(Context context, XElement p, Boolean value,
			NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		if (!isDisableEvents()
				&& fireCancellableEvent(context, new BeforeSelectEvent(context))) {
			fireEvent(context, new SelectEvent(context));
		}
	}

}