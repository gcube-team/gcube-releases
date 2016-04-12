/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.status;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.theme.base.client.status.StatusDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3StatusAppearance extends StatusDefaultAppearance {
  public interface Css3StatusResources extends StatusResources {
    @Override
    @Source("Css3Status.css")
    Css3StatusStyles style();

    ThemeDetails theme();
  }
  public interface Css3StatusStyles extends StatusStyle {

  }

  public Css3StatusAppearance() {
    super(GWT.<Css3StatusResources>create(Css3StatusResources.class), GWT.<Template>create(Template.class));
  }
}
