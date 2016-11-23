/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface StatusProxyDetails {
  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', 'normal')", comment = "dnd proxy test styling")
  FontDetails text();

  @TypeDetails(sampleValue = "util.border('solid', '#dddddd #bbbbbb #bbbbbb #dddddd', 1)", comment = "border around the dnd proxy")
  BorderDetails border();

  @TypeDetails(sampleValue = "#ffffff", comment = "background color for the dnd proxy")
  String backgroundColor();

  @TypeDetails(sampleValue = "0.85", comment = "opacity of the status proxy")
  double opacity();
}
