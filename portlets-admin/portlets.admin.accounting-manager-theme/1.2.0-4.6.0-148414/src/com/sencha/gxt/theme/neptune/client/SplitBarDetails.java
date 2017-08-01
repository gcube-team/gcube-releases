/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface SplitBarDetails {
  @TypeDetails(sampleValue = "#b4b4b4", comment = "color of split bar drag")
  String dragColor();

  @TypeDetails(sampleValue = "0.5", comment = "split bar drag handle opacity (0.0 to 1.0)")
  double handleOpacity();

  @TypeDetails(sampleValue = "8", comment = "width of the split bar handle in east/west (height of the handle on north/south)")
  int handleWidth();

  @TypeDetails(sampleValue = "48", comment = "height of the split bar handle in easy/west (width of the handle on east/west)")
  int handleHeight();
}
