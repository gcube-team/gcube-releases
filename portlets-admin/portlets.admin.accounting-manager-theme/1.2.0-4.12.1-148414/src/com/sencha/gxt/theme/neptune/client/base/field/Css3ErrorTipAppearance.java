/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.field;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.neptune.client.base.tips.Css3TipAppearance;
import com.sencha.gxt.widget.core.client.form.error.SideErrorHandler.SideErrorTooltipAppearance;

public class Css3ErrorTipAppearance extends Css3TipAppearance implements SideErrorTooltipAppearance {
  interface Css3ErrorTipResources extends Css3TipResources {
    @Source("exclamation.png")
    ImageResource errorIcon();

    @Override
    @Source("Css3ErrorTip.css")
    Css3ErrorTipStyle style();
  }
  interface Css3ErrorTipStyle extends Css3TipStyle {

  }
  public Css3ErrorTipAppearance() {
    super(GWT.<Css3ErrorTipResources>create(Css3ErrorTipResources.class));
  }
}
