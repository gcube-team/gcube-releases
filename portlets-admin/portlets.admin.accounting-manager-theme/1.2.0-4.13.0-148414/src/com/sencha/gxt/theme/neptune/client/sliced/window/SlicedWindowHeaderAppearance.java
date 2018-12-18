/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.window;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.neptune.client.sliced.panel.SlicedHeaderAppearance;

public class SlicedWindowHeaderAppearance extends SlicedHeaderAppearance {
  public interface SlicedWindowHeaderResources extends SlicedHeaderResources {
    @Source({"com/sencha/gxt/theme/base/client/widget/Header.css",
            "com/sencha/gxt/theme/neptune/client/base/panel/Css3Header.css",
            "com/sencha/gxt/theme/neptune/client/sliced/panel/SlicedHeader.css"})
    @Override
    SlicedWindowHeaderStyle style();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("window-header-background.png")
    ImageResource headerBackground();
  }

  public interface SlicedWindowHeaderStyle extends SlicedHeaderStyle {
  }

  public SlicedWindowHeaderAppearance() {
    super(GWT.<SlicedWindowHeaderResources>create(SlicedWindowHeaderResources.class));
  }
}
