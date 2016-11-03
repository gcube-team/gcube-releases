/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface PanelDetails {

  @TypeDetails(sampleValue = "#ffffff", comment = "background color for the panel body")
  String backgroundColor();

  @TypeDetails(sampleValue = "util.padding(0)", comment = "entire panel padding")
  EdgeDetails padding();

  @TypeDetails(sampleValue = "util.padding(10)", comment = "header padding")
  EdgeDetails headerPadding();

  @TypeDetails(sampleValue = "#ccffff", comment = "background color to fill behind the header gradient")
  String headerBackgroundColor();

  @TypeDetails(sampleValue = "#ccffff, #ccffff", comment = "header gradient string")
  String headerGradient();

  @TypeDetails(sampleValue = "util.border('solid', '#000000', 1)", comment = "border around the contentpanel")
  BorderDetails border();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif','medium')", comment = "panel heading text style")
  FontDetails font();
}
