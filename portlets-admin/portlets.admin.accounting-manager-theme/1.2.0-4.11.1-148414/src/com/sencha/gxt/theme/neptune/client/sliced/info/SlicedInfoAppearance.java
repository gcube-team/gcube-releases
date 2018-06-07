/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.info;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.info.InfoDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class SlicedInfoAppearance extends InfoDefaultAppearance {
  interface SlicedInfoResources extends InfoResources {
    @Source({"com/sencha/gxt/theme/base/client/frame/DivFrame.css", "Info.css"})
    @Override
    SlicedInfoStyle style();

    @Source("background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @Source("corner-bc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource bottomBorder();

    @Source("corner-bl.png")
    ImageResource bottomLeftBorder();

    @Source("corner-br.png")
    ImageResource bottomRightBorder();

    @Source("side-l.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource leftBorder();

    @Source("side-r.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource rightBorder();

    @Source("corner-tc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource topBorder();

    @Source("corner-tl.png")
    ImageResource topLeftBorder();

    @Source("corner-tr.png")
    ImageResource topRightBorder();

    ThemeDetails theme();
  }
  interface SlicedInfoStyle extends InfoStyle {

  }

  public SlicedInfoAppearance() {
    super(GWT.<Template>create(Template.class), GWT.<SlicedInfoResources>create(SlicedInfoResources.class));
  }
}
