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
public interface DatePickerDetails {
  @TypeDetails(sampleValue = "util.border('solid', '#000000', 1)", comment = "DatePicker border")
  BorderDetails border();

  @TypeDetails(sampleValue = "#ffffff", comment = "DatePicker background color")
  String backgroundColor();

  @TypeDetails(sampleValue = "util.padding(8,6)", comment = "padding in the header")
  EdgeDetails headerPadding();

  @TypeDetails(sampleValue = "#f5f5f5", comment = "header background color")
  String headerBackgroundColor();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '12px', '#000000')", comment = "header font styling")
  FontDetails headerText();

  @TypeDetails(sampleValue = "util.padding(5)", comment = "header text padding")
  EdgeDetails headerTextPadding();

  @TypeDetails(sampleValue = "#ffffff", comment = "day of week header background color")
  String dayOfWeekBackgroundColor();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '12px', '#000000')", comment = "day of week header font style")
  FontDetails dayOfWeekText();

  @TypeDetails(sampleValue = "24px", comment = "day of week header line height")
  String dayOfWeekLineHeight();

  @TypeDetails(sampleValue = "util.padding(5)", comment = "day of week header padding")
  EdgeDetails dayOfWeekPadding();


  @TypeDetails(sampleValue = "util.border('solid', '#ffffff', 1)", comment = "day border")
  BorderDetails dayBorder();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '12px', '#000000')", comment = "day font style")
  FontDetails dayText();

  @TypeDetails(sampleValue = "24px", comment = "day line height")
  String dayLineHeight();

  @TypeDetails(sampleValue = "util.padding(5)", comment = "day padding")
  EdgeDetails dayPadding();


  @TypeDetails(sampleValue = "#e8e8e8", comment = "disabled day background color")
  String dayDisabledBackgroundColor();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '12px', '#000000')", comment = "disabled day font style")
  FontDetails dayDisabledText();

  @TypeDetails(sampleValue = "#ffffff", comment = "next month day background color")
  String dayNextBackgroundColor();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '12px', '#000000')", comment = "next month day font style")
  FontDetails dayNextText();

  @TypeDetails(sampleValue = "#ffffff", comment = "previous month day background color")
  String dayPreviousBackgroundColor();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '12px', '#000000')", comment = "previous month day font style")
  FontDetails dayPreviousText();

  @TypeDetails(sampleValue = "#000000", comment = "over text color")
  String itemOverColor();

  @TypeDetails(sampleValue = "#cecece", comment = "over background color")
  String itemOverBackgroundColor();

  @TypeDetails(sampleValue = "util.border('solid', '#0000ff', 1)", comment = "selected item border")
  BorderDetails itemSelectedBorder();

  @TypeDetails(sampleValue = "#cacaca", comment = "selected item background color")
  String itemSelectedBackgroundColor();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '12px', '#000000')", comment = "selected item font style")
  FontDetails itemSelectedText();

  @TypeDetails(sampleValue = "util.border('solid', '#ff0000', 1)", comment = "current date border")
  BorderDetails todayBorder();

  @TypeDetails(sampleValue = "util.padding(5)", comment = "footer padding")
  EdgeDetails footerPadding();

  @TypeDetails(sampleValue = "#e1e1e1", comment = "footer background color")
  String footerBackgroundColor();

  @TypeDetails(sampleValue = "util.margin(0, 3)", comment = "button margins")
  EdgeDetails buttonMargin();

  @TypeDetails(sampleValue = "212px", comment = "width of the DatePicker widget")
  String width();
}
