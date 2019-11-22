/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface ProgressBarDetails {

  @TypeDetails(sampleValue = "util.border('solid', '#000000', 1)", comment = "border around the entire progressbar")
  BorderDetails border();

  @TypeDetails(sampleValue = "util.solidGradientString('#ffffff')", comment = "background gradient for the empty progressbar")
  String backgroundGradient();

  @TypeDetails(sampleValue = "util.fontStyle(\"sans-serif\", 'medium', '#000000', 'bold')", comment = "font style for the progressbar text")
  FontDetails text();

  @TypeDetails(sampleValue = "'center'", comment = "alignment for the progressbar text, may be 'center', 'left', or 'right'")
  String textAlign();

  @TypeDetails(sampleValue = "util.padding(4, 0)", comment = "padding around the progressbar, typically with zero left/right padding to prevent offsetting the bar itself")
  EdgeDetails textPadding();

  @TypeDetails(sampleValue = "util.solidGradientString('#add8e6')", comment = "gradient for the progress bar as it fills")
  String barGradient();

  @TypeDetails(sampleValue = "util.border('solid', '#000000', 0, 1, 0, 0)", comment = "border around the progressbar as it fills")
  BorderDetails barBorder();

  @TypeDetails(sampleValue = "#000000", comment = "text color for the text covered by the progress bar as it fills")
  String barTextColor();
}
