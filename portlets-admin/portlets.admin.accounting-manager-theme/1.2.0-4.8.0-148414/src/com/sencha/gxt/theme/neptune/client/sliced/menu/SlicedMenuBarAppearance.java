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
import com.sencha.gxt.theme.neptune.client.base.menu.Css3MenuBarAppearance;

public class SlicedMenuBarAppearance extends Css3MenuBarAppearance {
  public interface SlicedMenuBarResources extends Css3MenuBarResources {
    @Override
    @Source("SlicedMenuBar.css")
    SlicedMenuBarStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("menubar-bg.png")
    ImageResource background();
  }

  public interface SlicedMenuBarStyle extends Css3MenuBarStyle {
  }

  public SlicedMenuBarAppearance() {
    super(GWT.<SlicedMenuBarResources>create(SlicedMenuBarResources.class));
  }
}
