/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface ColorPaletteDetails {
  @TypeDetails(sampleValue = "14", comment = "height and width of each color swatch")
  int itemSize();

  @TypeDetails(sampleValue = "util.padding(3)", comment = "padding between each color swatch")
  EdgeDetails itemPadding();

  @TypeDetails(sampleValue = "#ffffff", comment = "background color behind all of the items")
  String backgroundColor();

  @TypeDetails(sampleValue = "util.border('solid', '#e1e1e1', 1)", comment = "border around each color swatch")
  BorderDetails itemBorder();

  @TypeDetails(sampleValue = "#e6e6e6", comment = "background to surround a selected or hovered color swatch")
  String selectedBackgroundColor();

  @TypeDetails(sampleValue = "util.border('solid', '#666666', 1)", comment = "border to draw around a selected or hovered color swatch")
  BorderDetails selectedBorder();
}
