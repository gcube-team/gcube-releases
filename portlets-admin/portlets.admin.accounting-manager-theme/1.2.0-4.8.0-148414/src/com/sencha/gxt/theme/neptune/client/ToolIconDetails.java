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
public interface ToolIconDetails {
  @TypeDetails(sampleValue = "util.mixColors('#ffffff', '#cecece', 0.5)", comment = "primary color of icons")
  String primaryColor();

  @TypeDetails(sampleValue = "util.mixColors('#ffffff', '#cecece', 0.3)", comment = "icon color when hover")
  String primaryOverColor();

  @TypeDetails(sampleValue = "util.mixColors('#ffffff', '#cecece', 0.25)", comment = "icon color when clicked")
  String primaryClickColor();

  @TypeDetails(sampleValue = "#ff0000", comment = "color used for warning actions, such as exclamation icon")
  String warningColor();

  @TypeDetails(sampleValue = "#00ff00", comment = "color used for allowed actions, such as allowed drop zones with DnD")
  String allowColor();
}
