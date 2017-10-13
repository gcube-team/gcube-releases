/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface StatusDetails {

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif','small')", comment = "status box text")
  FontDetails text();

  @TypeDetails(sampleValue = "'normal'", comment = "line height of the status widget")
  String lineHeight();

  @TypeDetails(sampleValue = "util.padding(0, 2)", comment = "padding around the status text")
  EdgeDetails padding();

  @TypeDetails(sampleValue = "util.border('solid', '#dddddd #ffffff #ffffff #dddddd', 1)", comment = "status box border, only applies to BoxStatusAppearance")
  BorderDetails border();
}
