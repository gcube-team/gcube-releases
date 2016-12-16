/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.themebuilder.base.client;

/**
 * This class is for operations that can be done in the css file and don't belong in the theme config. All methods
 * here should be able to compile to static content.
 */
public class CssResourceThemeUtils {
  /**
   * Takes an opacity value 0.0-1.0 and converts it to a ie alpha filter function string. Given a constant value,
   * this should compile out to a constant string.
   */
  public static String opacityToIe8Filter(double opacity) {
    //-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=50)"
    return "alpha(opacity=" + ((int) (opacity * 100)) + ")";
  }
  /**
   * Takes two int values and returns the max of the two, with 'px' appended to the end. Given two constant values,
   * this should compile out to a constant string in the css.
   */
  public static String maxPxSize(int a, int b) {
    return Math.max(a, b) + "px";
  }

}
