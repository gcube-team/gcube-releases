/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.DetailTemplate;

/**
 *
 */
public interface EdgeDetails {
  int top();
  int right();
  int bottom();
  int left();


  @Override
  @DetailTemplate("{top}px {right}px {bottom}px {left}px")
  public String toString();
}
