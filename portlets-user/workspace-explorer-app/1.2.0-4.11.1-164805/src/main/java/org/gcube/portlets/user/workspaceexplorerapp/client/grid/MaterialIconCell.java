/**
 *
 */

package org.gcube.portlets.user.workspaceexplorerapp.client.grid;

import gwt.material.design.client.ui.MaterialIcon;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Mar 8, 2016
 */
public class MaterialIconCell extends AbstractCell<MaterialIcon> {

	/**
	 *
	 */
	public MaterialIconCell() {

		/*
		 * Sink the click and keydown events. We handle click events in this
		 * class. AbstractCell will handle the keydown event and call
		 * onEnterKeyDown() if the user presses the enter key while the cell is
		 * selected.
		 */
		super("click", "keydown");
	}

	@Override
	public void render(
		com.google.gwt.cell.client.Cell.Context context, MaterialIcon value,
		SafeHtmlBuilder sb) {

		if(value==null)
			return;

		sb.appendHtmlConstant(value.getElement().getString());
	}

	/**
	 * Called when an event occurs in a rendered instance of this Cell. The
	 * parent element refers to the element that contains the rendered cell, NOT
	 * to the outermost element that the Cell rendered.
	 */

	/* (non-Javadoc)
	 * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent, com.google.gwt.cell.client.ValueUpdater)
	 */
	@Override
	public void onBrowserEvent(
		com.google.gwt.cell.client.Cell.Context context, Element parent,
		MaterialIcon value, NativeEvent event,
		ValueUpdater<MaterialIcon> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	}
}
