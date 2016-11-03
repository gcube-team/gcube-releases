/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.toolbar;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.theme.base.client.toolbar.LabelToolItemDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3LabelToolItemAppearance extends LabelToolItemDefaultAppearance {
  public interface Css3LabelToolItemResources extends LabelToolItemResources {
    @Override
    @Source("Css3LabelToolItem.css")
    Css3LabelToolItemStyle css();

    ThemeDetails theme();
  }
  public interface Css3LabelToolItemStyle extends LabelToolItemStyle {

  }
  public Css3LabelToolItemAppearance() {
    super(GWT.<Css3LabelToolItemResources>create(Css3LabelToolItemResources.class));
  }
}
