/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.base.client.button.IconButtonDefaultAppearance.IconButtonStyle;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.form.DualListField.DualListFieldAppearance;

/**
 *
 */
public class Css3DualListFieldAppearance implements DualListFieldAppearance {
	public interface Css3DualListFieldResources extends ClientBundle {
		@Source("Css3DualListField.css")
		@Import(IconButtonStyle.class)
		Css3DualListFieldStyle style();

		ThemeDetails theme();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		@Source("up.png")
		ImageResource upBtn();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		@Source("allRight.png")
		ImageResource allRightBtn();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		@Source("left.png")
		ImageResource leftBtn();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		@Source("right.png")
		ImageResource rightBtn();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		@Source("allLeft.png")
		ImageResource allLeftBtn();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		@Source("down.png")
		ImageResource downBtn();
	}

	public interface Css3DualListFieldStyle extends CssResource {
		String up();

		String allRight();

		String right();

		String left();

		String allLeft();

		String down();
	}

	@SuppressWarnings("unused")
	private Css3DualListFieldResources resources;
	private Css3DualListFieldStyle style;

	public Css3DualListFieldAppearance() {
		this(
				GWT.<Css3DualListFieldResources> create(Css3DualListFieldResources.class));
	}

	public Css3DualListFieldAppearance(Css3DualListFieldResources resources) {
		this.resources = resources;
		this.style = resources.style();

		StyleInjectorHelper.ensureInjected(style, false);
	}

	@Override
	public IconConfig allLeft() {
		return new IconConfig(style.allLeft());
	}

	@Override
	public IconConfig allRight() {
		return new IconConfig(style.allRight());
	}

	@Override
	public IconConfig down() {
		return new IconConfig(style.down());
	}

	@Override
	public IconConfig left() {
		return new IconConfig(style.left());
	}

	@Override
	public IconConfig right() {
		return new IconConfig(style.right());
	}

	@Override
	public IconConfig up() {
		return new IconConfig(style.up());
	}
}
