/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.resources.ThemeStyles.Styles;
import com.sencha.gxt.core.client.resources.ThemeStyles.ThemeAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

/**
 *
 */
public class Css3ThemeAppearance implements ThemeAppearance {

  public interface Css3ThemeResources extends ClientBundle {
    @Source("Css3Theme.css")
    Css3ThemeStyles style();

    ThemeDetails theme();
  }

  public interface Css3ThemeStyles extends Styles {

  }


  private final Css3ThemeStyles style;
  private final Css3ThemeResources resources;

  public Css3ThemeAppearance() {
    this(GWT.<Css3ThemeResources>create(Css3ThemeResources.class));
  }

  public Css3ThemeAppearance(Css3ThemeResources resources) {
    this.resources = resources;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(style, true);
  }


  @Override
  public Styles style() {
    return style;
  }

  @Override
  public String borderColor() {
    return resources.theme().borderColor();
  }

  @Override
  public String borderColorLight() {
    return resources.theme().borderColor();
  }

  @Override
  public String backgroundColorLight() {
    return resources.theme().backgroundColor();
  }
}
