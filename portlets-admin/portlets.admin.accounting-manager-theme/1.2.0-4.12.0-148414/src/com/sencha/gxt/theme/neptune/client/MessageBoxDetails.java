/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface MessageBoxDetails {
  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', 'normal')", comment = "style of the text in the body of the message box")
  FontDetails text();

  @TypeDetails(sampleValue = "util.padding(10, 10, 5)", comment = "padding around the message box")
  EdgeDetails messagePadding();

  @TypeDetails(sampleValue = "util.padding(5, 10, 10)", comment = "padding around the body of the message box")
  EdgeDetails bodyPadding();

  @TypeDetails(sampleValue = "util.padding(10)", comment = "padding around the icon")
  EdgeDetails iconPadding();
}
