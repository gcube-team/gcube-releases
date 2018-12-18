/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

/**
 *
 */
public interface ToolBarDetails {
  @TypeDetails(sampleValue = "util.solidGradientString('#ffffff')", comment = "background gradient of the toolbar")
  String gradient();

  @TypeDetails(sampleValue = "util.border('none')", comment = "border around the toolbar")
  BorderDetails border();

  @TypeDetails(sampleValue = "util.padding(2)", comment = "padding between the toolbar's border and its contents")
  EdgeDetails padding();

  ButtonDetails buttonOverride();

  LabelToolItemDetails labelItem();

  public interface LabelToolItemDetails {
    @TypeDetails(sampleValue = "util.fontStyle('sans-serif', 'medium')", comment = "LabelToolItem text styling")
    FontDetails text();
    @TypeDetails(sampleValue = "'medium'", comment = "LabelToolItem text styling")
    String lineHeight();
    @TypeDetails(sampleValue = "util.padding(2, 2, 0)", comment = "label padding")
    EdgeDetails padding();

  }
}
