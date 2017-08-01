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
import com.sencha.gxt.theme.neptune.client.base.menu.Css3MenuAppearance;

public class SlicedMenuAppearance extends Css3MenuAppearance {

  public interface SlicedMenuResources extends Css3MenuResources {
    @Override
    @Source("SlicedMenu.css")
    SlicedMenuStyle style();

    @Source("menu-bg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource background();
  }
  public interface SlicedMenuStyle extends Css3MenuStyle {

  }

  public SlicedMenuAppearance() {
    super(GWT.<SlicedMenuResources>create(SlicedMenuResources.class),
            GWT.<BaseMenuTemplate>create(BaseMenuTemplate.class));
  }
}
