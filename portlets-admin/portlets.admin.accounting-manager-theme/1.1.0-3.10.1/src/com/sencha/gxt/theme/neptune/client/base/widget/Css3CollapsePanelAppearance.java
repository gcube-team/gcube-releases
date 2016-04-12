/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.CollapsePanel.CollapsePanelAppearance;

/**
 *
 */
public class Css3CollapsePanelAppearance implements CollapsePanelAppearance {

	public interface Css3CollapsePanelResources extends ClientBundle {

		@Source("Css3CollapsePanel.css")
		Css3CollapsePanelStyle style();

		ThemeDetails theme();
	}

	public interface Css3CollapsePanelStyle extends CssResource {
		String panel();

		String iconWrap();

		String west();

		String east();

		String north();

		String south();
	}

	private final Css3CollapsePanelResources resources;
	private final Css3CollapsePanelStyle style;

	public Css3CollapsePanelAppearance() {
		this(
				GWT.<Css3CollapsePanelResources> create(Css3CollapsePanelResources.class));
	}

	public Css3CollapsePanelAppearance(Css3CollapsePanelResources resources) {
		this.resources = resources;
		this.style = this.resources.style();

		StyleInjectorHelper.ensureInjected(style, true);
	}

	@Override
	public void render(SafeHtmlBuilder sb, LayoutRegion region) {
		String cls = style.panel();

		switch (region) {
		case WEST:
			cls += " " + style.west();
			break;
		case EAST:
			cls += " " + style.east();
			break;
		case NORTH:
			cls += " " + style.north();
			break;
		case SOUTH:
			cls += " " + style.south();
			break;
		case CENTER:
			break;
		default:
			break;
		}

		sb.appendHtmlConstant("<div class='" + cls + "'>");
		sb.appendHtmlConstant("<div class='" + style.iconWrap() + "'></div>");
		sb.appendHtmlConstant("</div>");
	}

	@Override
	public XElement iconWrap(XElement parent) {
		return parent.getFirstChildElement().cast();
	}

}
