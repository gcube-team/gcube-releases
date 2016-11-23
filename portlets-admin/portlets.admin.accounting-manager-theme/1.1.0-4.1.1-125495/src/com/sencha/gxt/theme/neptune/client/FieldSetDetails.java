/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface FieldSetDetails {
  @TypeDetails(sampleValue = "util.fontStyle('sans-serif','large')", comment = "text details for the fieldset's legend")
  FontDetails text();
  @TypeDetails(sampleValue = "util.border('solid', '#bbbbbb', 1)", comment = "border styling and colors around the fieldset")
  BorderDetails border();
}
