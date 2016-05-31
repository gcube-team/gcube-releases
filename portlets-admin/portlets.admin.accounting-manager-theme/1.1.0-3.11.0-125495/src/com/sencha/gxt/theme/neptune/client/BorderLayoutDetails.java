/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface BorderLayoutDetails {
  @TypeDetails(sampleValue = "#f0f2f2", comment = "background for the borderlayoutcontainer, visible in margins and collapsed regions")
  String panelBackgroundColor();

  @TypeDetails(sampleValue = "util.border('solid', '#f0f2f2', 1)", comment = "border styling for a non-mini collapsed region")
  BorderDetails collapsePanelBorder();
}
