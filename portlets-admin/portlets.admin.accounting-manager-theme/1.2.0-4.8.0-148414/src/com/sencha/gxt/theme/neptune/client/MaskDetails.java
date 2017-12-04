/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface MaskDetails {

  @TypeDetails(sampleValue = "0.5", comment = "opacity of the element that covers masked content")
  double opacity();

  @TypeDetails(sampleValue = "#000000", comment = "color of the element that covers masked content")
  String backgroundColor();

  BoxDetails box();
  public interface BoxDetails {

    @TypeDetails(sampleValue = "util.padding(5)", comment = "padding between the border and the content of the mask message")
    EdgeDetails padding();

    @TypeDetails(sampleValue = "#555555", comment = "border color for the mask's message")
    String borderColor();

    @TypeDetails(sampleValue = "'solid'", comment = "border style for the mask's message")
    String borderStyle();

    @TypeDetails(sampleValue = "2", comment = "border width for the mask's message")
    int borderWidth();

    @TypeDetails(sampleValue = "0", comment = "border radius for the mask's message")
    int borderRadius();

    @TypeDetails(sampleValue = "util.max(0, borderRadius - borderWidth)", comment = "helper for leftover space in css3 versus sliced images")
    int radiusMinusBorderWidth();


    @TypeDetails(sampleValue = "#dddddd", comment = "background color for the mask's message")
    String backgroundColor();

    @TypeDetails(sampleValue = "util.fontStyle('sans-serif', 'normal', '#555555')", comment = "font style for mask text")
    FontDetails text();

    @TypeDetails(sampleValue = "util.padding(21, 0, 0)", comment = "padding around the text, useful to provide space for loading image")
    EdgeDetails textPadding();

    @TypeDetails(sampleValue = "'center 0'", comment = "css background-position for the loading image, if any")
    String loadingImagePosition();
  }

}
