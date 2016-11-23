/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.grid;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.grid.GroupingViewDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;

public class Css3GroupingViewAppearance extends GroupingViewDefaultAppearance {

  public interface Css3GroupingViewResources extends GroupingViewResources {

    @Override
    ImageResource groupBy();

    ImageResource expand();

    ImageResource collapse();

    @Override
    @Import(GridStateStyles.class)
    @Source("Css3GroupingView.css")
    Css3GroupingViewStyle style();

    ThemeDetails theme();
  }

  public interface Css3GroupingViewStyle extends GroupingViewStyle {

  }

  public Css3GroupingViewAppearance() {
    this(GWT.<Css3GroupingViewResources>create(Css3GroupingViewResources.class));
  }

  public Css3GroupingViewAppearance(Css3GroupingViewResources resources) {
    super(resources);
  }
}
