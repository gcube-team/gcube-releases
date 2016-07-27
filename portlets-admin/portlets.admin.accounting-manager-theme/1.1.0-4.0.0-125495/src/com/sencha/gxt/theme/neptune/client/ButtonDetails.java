/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface ButtonDetails {

  @TypeDetails(sampleValue = "#ffffff", comment = "Color of the menu arrow")
  String arrowColor();

  @TypeDetails(sampleValue = "util.border('solid', '#c4c7c7', 1)", comment = "the buttons border")
  BorderDetails border();

  @TypeDetails(sampleValue = "3", comment = "border radius of the button")
  int borderRadius();
  
  @TypeDetails(sampleValue = "util.radiusMinusBorderWidth(border, borderRadius)", comment = "helper for leftover space in css3 versus sliced images")
  EdgeDetails radiusMinusBorderWidth();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif','12px','#FFFFFF')", comment = "the buttons text")
  FontDetails font();

  @TypeDetails(sampleValue = "#f6f8f9 0%, #e5ebee 50%, #d7dee3 51%, #f5f7f9 100%", comment = "the normal state gradient")
  String gradient();

  @TypeDetails(sampleValue = "16", comment = "the large font size")
  String largeFontSize();

  @TypeDetails(sampleValue = "32", comment = "the large line height")
  String largeLineHeight();

  @TypeDetails(sampleValue = "14", comment = "the medium font size")
  String mediumFontSize();

  @TypeDetails(sampleValue = "24", comment = "the medium line height")
  String mediumLineHeight();

  @TypeDetails(sampleValue = "#e5e5e5 0%, #aeafaf 50%, #b5b5b5 51%, #aeafaf", comment = "the mouseover state gradient")
  String overGradient();

  @TypeDetails(sampleValue = "util.padding(0)", comment = "the button's padding")
  EdgeDetails padding();

  @TypeDetails(sampleValue = "#dbdbdb 0%, #a3a3a3 50%, #9e9e9e 51%, #a3a3a3", comment = "the pressed state gradient")
  String pressedGradient();

  @TypeDetails(sampleValue = "12", comment = "the small font size")
  String smallFontSize();

  @TypeDetails(sampleValue = "18", comment = "the small line height")
  String smallLineHeight();
}
