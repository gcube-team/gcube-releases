/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.panel;


import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

/**
 */
public class Css3HeaderAppearance extends HeaderDefaultAppearance {

  public interface Css3HeaderStyle extends HeaderStyle {
    @Override
    String header();

    @Override
    String headerBar();

    @Override
    String headerHasIcon();

    @Override
    String headerIcon();

    @Override
    String headerText();
  }

  public interface Css3HeaderResources extends HeaderResources {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/widget/Header.css", "Css3Header.css"})
    Css3HeaderStyle style();
    
    ThemeDetails theme();
  }
  
  public Css3HeaderAppearance() {
    this(GWT.<Css3HeaderResources>create(Css3HeaderResources.class));
  }

  public Css3HeaderAppearance(Css3HeaderResources resources) {
    super(resources);
  }
}
