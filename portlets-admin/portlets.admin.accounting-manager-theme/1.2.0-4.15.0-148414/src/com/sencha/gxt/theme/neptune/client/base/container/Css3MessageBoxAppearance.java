/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.container;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.theme.base.client.container.MessageBoxDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3MessageBoxAppearance extends MessageBoxDefaultAppearance {
  public interface Css3MessageBoxResources extends MessageBoxResources {
    @Override
    @Source("Css3MessageBox.css")
    Css3MessageBoxStyles style();

    ThemeDetails theme();
  }
  public interface Css3MessageBoxStyles extends MessageBoxBaseStyle {

  }
  public Css3MessageBoxAppearance() {
    super(GWT.<Css3MessageBoxResources>create(Css3MessageBoxResources.class));
  }
}
