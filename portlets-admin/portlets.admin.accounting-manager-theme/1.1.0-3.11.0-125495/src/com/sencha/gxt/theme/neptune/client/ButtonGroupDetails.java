/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface ButtonGroupDetails {

  @TypeDetails(sampleValue = "#FFFFFF", comment = "the body background color")
  String bodyBackgroundColor();

  @TypeDetails(sampleValue = "util.padding(4)", comment = "the group's body padding")
  EdgeDetails bodyPadding();

  @TypeDetails(sampleValue = "util.border('solid', '#f0f2f2', 3)", comment = "the groups border")
  BorderDetails border();

  @TypeDetails(sampleValue = "3", comment = "border radius of the button group")
  int borderRadius();

  @TypeDetails(sampleValue = "util.fontStyle('helvetica, arial, verdana, sans-serif','13px','#666666')", comment = "the groups text")
  FontDetails font();

  @TypeDetails(sampleValue = "#1bbcca 0%, #31c2cf 50%, #5fd0d9 51%, #31c2cf", comment = "the header gradient")
  String headerGradient();

  @TypeDetails(sampleValue = "util.padding(2)", comment = "the group's header padding")
  EdgeDetails headerPadding();
}
