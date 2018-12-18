/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.progressbar;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.neptune.client.base.progressbar.Css3ProgressBarAppearance;

public class SlicedProgressBarAppearance extends Css3ProgressBarAppearance {
  public interface SlicedProgressBarResources extends Css3ProgressBarResources {
    @Source("progressbar-bar.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource bar();

    @Source("progressbar-bg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource innerBar();

    @Override
    @Source("SlicedProgressBar.css")
    SlicedProgressBarStyle styles();

  }
  public interface SlicedProgressBarStyle extends Css3ProgressBarStyles {

  }

  public SlicedProgressBarAppearance() {
    super(GWT.<SlicedProgressBarResources>create(SlicedProgressBarResources.class));
  }
}
