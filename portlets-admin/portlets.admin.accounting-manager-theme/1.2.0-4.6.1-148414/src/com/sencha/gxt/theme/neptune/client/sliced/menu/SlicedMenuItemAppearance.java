/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.menu;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.neptune.client.base.menu.Css3MenuItemAppearance;

public class SlicedMenuItemAppearance extends Css3MenuItemAppearance {
  public interface SlicedMenuItemResources extends Css3MenuItemResources {

    @Override
    @Source("SlicedMenuItem.css")
    SlicedMenuItemStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("menuitem-hover.png")
    ImageResource itemOver();
  }

  public interface SlicedMenuItemStyle extends Css3MenuItemStyle {
  }

  public SlicedMenuItemAppearance() {
    super(GWT.<SlicedMenuItemResources>create(SlicedMenuItemResources.class),
            GWT.<MenuItemTemplate>create(MenuItemTemplate.class));
  }
}
