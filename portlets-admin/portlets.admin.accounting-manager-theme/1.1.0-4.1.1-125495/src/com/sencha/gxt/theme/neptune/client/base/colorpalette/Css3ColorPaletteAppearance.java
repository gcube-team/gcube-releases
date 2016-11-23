/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.colorpalette;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.base.client.colorpalette.ColorPaletteBaseAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3ColorPaletteAppearance extends ColorPaletteBaseAppearance {
  public interface Css3ColorPaletteResources extends ColorPaletteResources, ClientBundle {
    @Override
    @Source({"com/sencha/gxt/theme/base/client/colorpalette/ColorPalette.css","Css3ColorPalette.css"})
    Css3ColorPaletteStyle style();

    ThemeDetails theme();
  }

  public interface Css3ColorPaletteStyle extends ColorPaletteStyle {

  }

  public Css3ColorPaletteAppearance() {
    super(GWT.<Css3ColorPaletteResources>create(Css3ColorPaletteResources.class), GWT.<BaseColorPaletteTemplate>create(BaseColorPaletteTemplate.class));
  }
}
