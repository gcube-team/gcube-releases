/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.field.ErrorTipDefaultAppearance;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

/**
 *
 */
public class SlicedErrorTipAppearance extends ErrorTipDefaultAppearance {

  public interface SlicedErrorTipFrameResources extends ErrorTipFrameResources {
    @Override
    @Source("tip-background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @Override
    @Source("tip-bc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomBorder();

    @Override
    @Source("tip-bl.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomLeftBorder();

    @Override
    @Source("tip-br.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomRightBorder();

    @Override
    @Source("tip-l.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource leftBorder();

    @Override
    @Source("tip-r.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource rightBorder();

    @Override
    @Source("tip-tc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource topBorder();

    @Override
    @Source("tip-tl.png")
    ImageResource topLeftBorder();

    @Override
    @Source("tip-tr.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource topRightBorder();
  }


  public interface SlicedErrorTipResources extends ErrorTipResources {
    @Override
    @Source("com/sencha/gxt/theme/neptune/client/base/field/exclamation.png")
    @ImageOptions(preventInlining = true)
    ImageResource errorIcon();

    @Override
    @Source({"com/sencha/gxt/theme/base/client/tips/TipDefault.css", "SlicedErrorTip.css"})
    SlicedErrorTipStyle style();

    ThemeDetails theme();
  }

  public interface SlicedErrorTipStyle extends ErrorTipStyle {

  }

  public SlicedErrorTipAppearance() {
    this(GWT.<SlicedErrorTipResources>create(SlicedErrorTipResources.class));
  }

  public SlicedErrorTipAppearance(SlicedErrorTipResources resources) {
    super(resources);

    frame = new NestedDivFrame(GWT.<SlicedErrorTipFrameResources>create(SlicedErrorTipFrameResources.class));
  }
}
