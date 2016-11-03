/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.menu;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.neptune.client.base.menu.Css3MenuBarItemAppearance;

public class SlicedMenuBarItemAppearance extends Css3MenuBarItemAppearance {
  public interface SlicedMenuBarItemResources extends Css3MenuBarItemResources  {
    @Override
    @Source("SlicedMenuBarItem.css")
    SlicedMenuBarItemStyle css();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("menubaritem-hover.png")
    ImageResource itemOver();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("menubaritem-active.png")
    ImageResource itemActive();
  }

  public interface SlicedMenuBarItemStyle extends Css3MenuBarItemStyle {

  }

  public SlicedMenuBarItemAppearance() {
    super(GWT.<SlicedMenuBarItemResources>create(SlicedMenuBarItemResources.class));
  }

}
