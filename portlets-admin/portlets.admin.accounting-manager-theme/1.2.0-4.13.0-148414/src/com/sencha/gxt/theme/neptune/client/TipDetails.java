/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface TipDetails {

  @TypeDetails(sampleValue="#ffffff", comment = "background color")
  String backgroundColor();

  @TypeDetails(sampleValue = "1", comment = "tooltip opacity, 0.0-1.0")
  double opacity();

  @TypeDetails(sampleValue = "util.padding(2)", comment = "padding")
  EdgeDetails padding();

  @TypeDetails(sampleValue = "6", comment = "border radius")
  int borderRadius();

  @TypeDetails(sampleValue = "util.border('solid', '#cccccc', 1)", comment = "border parameters")
  BorderDetails border();

  @TypeDetails(sampleValue = "util.radiusMinusBorderWidth(border, borderRadius)", comment = "helper for leftover space in css3 versus sliced images")
  EdgeDetails radiusMinusBorderWidth();

  @TypeDetails(sampleValue = "util.margin(0)", comment = "margin")
  EdgeDetails margin();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif','medium','#000000','normal')", comment = "info header text style")
  FontDetails headerText();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif','medium','#000000','normal')", comment = "info message text style")
  FontDetails messageText();

  @TypeDetails(sampleValue = "util.padding(0)", comment = "padding around the header text")
  EdgeDetails headerPadding();

  @TypeDetails(sampleValue = "util.padding(0)", comment = "padding around the message text")
  EdgeDetails messagePadding();
}
