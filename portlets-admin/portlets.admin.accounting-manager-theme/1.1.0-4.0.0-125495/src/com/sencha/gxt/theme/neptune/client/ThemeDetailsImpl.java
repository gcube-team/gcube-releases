package com.sencha.gxt.theme.neptune.client;

public class ThemeDetailsImpl implements ThemeDetails {

  @Override
  public com.sencha.gxt.theme.neptune.client.PanelDetails panel() {
  return new com.sencha.gxt.theme.neptune.client.PanelDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails font() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#101010";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "arial,helvetica,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#cecece";
  }

  @Override
  public java.lang.String headerGradient() {
  return "#cecece 0%, #cecece 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderLayoutDetails borderLayout() {
  return new com.sencha.gxt.theme.neptune.client.BorderLayoutDetails() {

  @Override
  public java.lang.String panelBackgroundColor() {
  return "#f0f2f2";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails collapsePanelBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#eeeeee";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ColorPaletteDetails colorpalette() {
  return new com.sencha.gxt.theme.neptune.client.ColorPaletteDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails itemBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String selectedBackgroundColor() {
  return "#e6e6e6";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails selectedBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#8bb8f3";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails itemPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int itemSize() {
  return 16;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.MaskDetails mask() {
  return new com.sencha.gxt.theme.neptune.client.MaskDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.MaskDetails.BoxDetails box() {
  return new com.sencha.gxt.theme.neptune.client.MaskDetails.BoxDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails textPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 21;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#e5e5e5";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public java.lang.String borderStyle() {
  return "none";
  }

  @Override
  public int borderWidth() {
  return 0;
  }

  @Override
  public java.lang.String borderColor() {
  return "";
  }

  @Override
  public int radiusMinusBorderWidth() {
  return 3;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public java.lang.String loadingImagePosition() {
  return "center 0";
  }
    };
  }

  @Override
  public double opacity() {
  return 0.7;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.DatePickerDetails datePicker() {
  return new com.sencha.gxt.theme.neptune.client.DatePickerDetails() {

  @Override
  public java.lang.String itemOverColor() {
  return "#000000";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails dayBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemOverBackgroundColor() {
  return "#e6e6e6";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails todayBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#8b0000";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemSelectedBackgroundColor() {
  return "#cacaca";
  }

  @Override
  public java.lang.String dayOfWeekBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 8;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String dayOfWeekLineHeight() {
  return "24px";
  }

  @Override
  public java.lang.String dayDisabledBackgroundColor() {
  return "#eeeeee";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails dayPreviousText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#bfbfbf";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails dayText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String dayPreviousBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails dayNextText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#bfbfbf";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String width() {
  return "212px";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails dayOfWeekPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 9;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails buttonMargin() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails itemSelectedBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#eeeeee";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails dayPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String footerBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public java.lang.String dayNextBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails dayDisabledText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#808080";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails itemSelectedText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String dayLineHeight() {
  return "23px";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails headerText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#505050";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerTextPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails footerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails dayOfWeekText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.StatusProxyDetails statusproxy() {
  return new com.sencha.gxt.theme.neptune.client.StatusProxyDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#dddddd #bbbbbb #bbbbbb #dddddd";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public double opacity() {
  return 0.85;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.StatusDetails status() {
  return new com.sencha.gxt.theme.neptune.client.StatusDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#cccccc #d9d9d9 #d9d9d9";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "16px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FramedPanelDetails framedPanel() {
  return new com.sencha.gxt.theme.neptune.client.FramedPanelDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails font() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "arial,helvetica,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public java.lang.String color() {
  return "#cecece";
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#cecece";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails radiusMinusBorderWidth() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#cecece 0%, #cecece 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 4;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.AccordionLayoutDetails accordionLayout() {
  return new com.sencha.gxt.theme.neptune.client.AccordionLayoutDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails font() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 8;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#f0f2f2";
  }

  @Override
  public java.lang.String headerGradient() {
  return "#f0f2f2 0%, #f0f2f2 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.TabDetails tabs() {
  return new com.sencha.gxt.theme.neptune.client.TabDetails() {

  @Override
  public java.lang.String tabItemBorderLeft() {
  return "none";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails tabTextPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails hoverHeadingText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#505050";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public java.lang.String borderColor() {
  return "#dfdfdf";
  }

  @Override
  public int scrollerWidth() {
  return 18;
  }

  @Override
  public java.lang.String tabStripGradient() {
  return "#ffffff 0%, #f1f1f1 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#a3a3a3 0%, #a3a3a3 100%";
  }

  @Override
  public int tabHeight() {
  return 31;
  }

  @Override
  public java.lang.String tabBarBorder() {
  return "none";
  }

  @Override
  public java.lang.String scrollerBackgroundColor() {
  return "#007cd1";
  }

  @Override
  public java.lang.String bodyBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails activeHeadingText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails headingText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#505050";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public java.lang.String lastStopColor() {
  return "#a3a3a3";
  }

  @Override
  public int iconLeftOffset() {
  return 6;
  }

  @Override
  public java.lang.String tabBodyBorder() {
  return "none";
  }

  @Override
  public int tabBarBottomHeight() {
  return 4;
  }

  @Override
  public java.lang.String hoverGradient() {
  return "#aeafaf 0%, #aeafaf 100%";
  }

  @Override
  public int tabSpacing() {
  return 1;
  }

  @Override
  public java.lang.String tabItemBorderRight() {
  return "none";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails paddingWithClosable() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 19;
  }

  @Override
  public int right() {
  return 19;
  }

  @Override
  public int bottom() {
  return 19;
  }

  @Override
  public int left() {
  return 19;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String tabItemBorderTop() {
  return "none";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails tabStripPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String inactiveGradient() {
  return "#e7e7e7 0%, #e7e7e7 100%";
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public java.lang.String inactiveLastStopColor() {
  return "#e7e7e7";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails paddingWithIcon() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 18;
  }

  @Override
  public int right() {
  return 18;
  }

  @Override
  public int bottom() {
  return 18;
  }

  @Override
  public int left() {
  return 18;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String tabStripBottomBorder() {
  return "none";
  }

  @Override
  public int iconTopOffset() {
  return 5;
  }
    };
  }

  @Override
  public java.lang.String disabledTextColor() {
  return "#f1f1f1";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ButtonDetails button() {
  return new com.sencha.gxt.theme.neptune.client.ButtonDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails radiusMinusBorderWidth() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String mediumFontSize() {
  return "14";
  }

  @Override
  public java.lang.String overGradient() {
  return "#e5e5e5 0%, #aeafaf 50%, #b5b5b5 51%, #aeafaf";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#f6f8f9 0%, #e5ebee 50%, #d7dee3 51%, #f5f7f9 100%";
  }

  @Override
  public java.lang.String largeFontSize() {
  return "16";
  }

  @Override
  public java.lang.String smallLineHeight() {
  return "18";
  }

  @Override
  public java.lang.String largeLineHeight() {
  return "32";
  }

  @Override
  public java.lang.String pressedGradient() {
  return "#dbdbdb 0%, #a3a3a3 50%, #9e9e9e 51%, #a3a3a3";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails font() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#505050";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String arrowColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#747474";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String smallFontSize() {
  return "12";
  }

  @Override
  public java.lang.String mediumLineHeight() {
  return "24";
  }

  @Override
  public int borderRadius() {
  return 4;
  }
    };
  }

  @Override
  public java.lang.String borderColor() {
  return "#cecece";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ButtonGroupDetails buttonGroup() {
  return new com.sencha.gxt.theme.neptune.client.ButtonGroupDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails font() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public java.lang.String color() {
  return "#f0f2f2";
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails bodyPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 4;
  }

  @Override
  public int right() {
  return 4;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 4;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#f0f2f2 0%, #f0f2f2 100%";
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public java.lang.String bodyBackgroundColor() {
  return "#FFFFFF";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.MessageBoxDetails messagebox() {
  return new com.sencha.gxt.theme.neptune.client.MessageBoxDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails messagePadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails iconPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails bodyPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.SplitBarDetails splitbar() {
  return new com.sencha.gxt.theme.neptune.client.SplitBarDetails() {

  @Override
  public int handleWidth() {
  return 8;
  }

  @Override
  public java.lang.String dragColor() {
  return "#B4B4B4";
  }

  @Override
  public int handleHeight() {
  return 48;
  }

  @Override
  public double handleOpacity() {
  return 0.5;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.TipDetails tip() {
  return new com.sencha.gxt.theme.neptune.client.TipDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails messagePadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#e6e6e6";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails margin() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails radiusMinusBorderWidth() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public double opacity() {
  return 1.0;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails headerText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails messageText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FieldDetails field() {
  return new com.sencha.gxt.theme.neptune.client.FieldDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.FieldDetails.FieldLabelDetails sideLabel() {
  return new com.sencha.gxt.theme.neptune.client.FieldDetails.FieldLabelDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails fieldPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String textAlign() {
  return "left";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails labelPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public int invalidBorderWidth() {
  return 1;
  }

  @Override
  public java.lang.String borderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String borderColor() {
  return "#c0c0c0";
  }

  @Override
  public java.lang.String focusBorderColor() {
  return "#eeeeee";
  }

  @Override
  public java.lang.String invalidBorderColor() {
  return "#D94E37";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String emptyTextColor() {
  return "#808080";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FieldDetails.FieldLabelDetails topLabel() {
  return new com.sencha.gxt.theme.neptune.client.FieldDetails.FieldLabelDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails fieldPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String textAlign() {
  return "left";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails labelPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String invalidBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.SliderDetails slider() {
  return new com.sencha.gxt.theme.neptune.client.SliderDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails thumbBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#777777";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int thumbRadius() {
  return 8;
  }

  @Override
  public java.lang.String trackBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public int thumbHeight() {
  return 15;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails trackBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#d4d4d4";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int trackRadius() {
  return 4;
  }

  @Override
  public java.lang.String thumbBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public int thumbWidth() {
  return 15;
  }

  @Override
  public int trackHeight() {
  return 8;
  }
    };
  }

  @Override
  public int borderWidth() {
  return 1;
  }

  @Override
  public java.lang.String lineHeight() {
  return "18px";
  }

  @Override
  public int height() {
  return 24;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ToolIconDetails tools() {
  return new com.sencha.gxt.theme.neptune.client.ToolIconDetails() {

  @Override
  public java.lang.String warningColor() {
  return "#D94E37";
  }

  @Override
  public java.lang.String primaryClickColor() {
  return "#c4dff2";
  }

  @Override
  public java.lang.String primaryColor() {
  return "#8abfe5";
  }

  @Override
  public java.lang.String primaryOverColor() {
  return "#b8d8ef";
  }

  @Override
  public java.lang.String allowColor() {
  return "#C6E38A";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ToolBarDetails toolbar() {
  return new com.sencha.gxt.theme.neptune.client.ToolBarDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.ButtonDetails buttonOverride() {
  return new com.sencha.gxt.theme.neptune.client.ButtonDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails radiusMinusBorderWidth() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String mediumFontSize() {
  return "14";
  }

  @Override
  public java.lang.String overGradient() {
  return "#e5e5e5 0%, #aeafaf 50%, #b5b5b5 51%, #aeafaf";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#f6f8f9 0%, #e5ebee 50%, #d7dee3 51%, #f5f7f9 100%";
  }

  @Override
  public java.lang.String largeFontSize() {
  return "16";
  }

  @Override
  public java.lang.String smallLineHeight() {
  return "18";
  }

  @Override
  public java.lang.String largeLineHeight() {
  return "32";
  }

  @Override
  public java.lang.String pressedGradient() {
  return "#dbdbdb 0%, #a3a3a3 50%, #9e9e9e 51%, #a3a3a3";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails font() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String arrowColor() {
  return "#666666";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#cecece";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String smallFontSize() {
  return "12";
  }

  @Override
  public java.lang.String mediumLineHeight() {
  return "24";
  }

  @Override
  public int borderRadius() {
  return 4;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ToolBarDetails.LabelToolItemDetails labelItem() {
  return new com.sencha.gxt.theme.neptune.client.ToolBarDetails.LabelToolItemDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "17px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.TreeDetails tree() {
  return new com.sencha.gxt.theme.neptune.client.TreeDetails() {

  @Override
  public java.lang.String dropBackgroundColor() {
  return "#e2eff8";
  }

  @Override
  public java.lang.String selectedBackgroundColor() {
  return "#c1ddf1";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String overBackgroundColor() {
  return "#e2eff8";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails nodePadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemHeight() {
  return "25px";
  }

  @Override
  public java.lang.String dragOverBackgroundColor() {
  return "#e2eff8";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails iconMargin() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 4;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FieldSetDetails fieldset() {
  return new com.sencha.gxt.theme.neptune.client.FieldSetDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#b5b8c8";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.WindowDetails window() {
  return new com.sencha.gxt.theme.neptune.client.WindowDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails font() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "arial,helvetica,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public java.lang.String color() {
  return "#e7e7e7";
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#cecece";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails radiusMinusBorderWidth() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#e7e7e7 0%, #e7e7e7 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 4;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ProgressBarDetails progressbar() {
  return new com.sencha.gxt.theme.neptune.client.ProgressBarDetails() {

  @Override
  public java.lang.String backgroundGradient() {
  return "";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails textPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String barTextColor() {
  return "#666666";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails barBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String barGradient() {
  return "#cacaca 0%, #cacaca 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public java.lang.String textAlign() {
  return "center";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.TipDetails errortip() {
  return new com.sencha.gxt.theme.neptune.client.TipDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails messagePadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#e6e6e6";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails margin() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails radiusMinusBorderWidth() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public double opacity() {
  return 1.0;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails headerText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails messageText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }
    };
  }

  @Override
  public double disabledOpacity() {
  return 0.5;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ListViewDetails listview() {
  return new com.sencha.gxt.theme.neptune.client.ListViewDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.ListViewDetails.ItemDetails overItem() {
  return new com.sencha.gxt.theme.neptune.client.ListViewDetails.ItemDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#cecece 0%, #cecece 100%";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ListViewDetails.ItemDetails item() {
  return new com.sencha.gxt.theme.neptune.client.ListViewDetails.ItemDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.ListViewDetails.ItemDetails selectedItem() {
  return new com.sencha.gxt.theme.neptune.client.ListViewDetails.ItemDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#cacaca 0%, #cacaca 100%";
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "22px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.InfoDetails info() {
  return new com.sencha.gxt.theme.neptune.client.InfoDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails messagePadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public java.lang.String color() {
  return "#cccccc";
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails headerPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails margin() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails radiusMinusBorderWidth() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 6;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public double opacity() {
  return 1.0;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails headerText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#555555";
  }

  @Override
  public java.lang.String size() {
  return "15px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 7;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 7;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 8;
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails messageText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#555555";
  }

  @Override
  public java.lang.String size() {
  return "14px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.GridDetails grid() {
  return new com.sencha.gxt.theme.neptune.client.GridDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails cellPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int cellBorderWidth() {
  return 1;
  }

  @Override
  public java.lang.String cellOverHBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String cellBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String cellSelectedVBorderColor() {
  return "#ededed";
  }

  @Override
  public java.lang.String specialColumnGradientSelected() {
  return "";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.GridDetails.RowEditorDetails rowEditor() {
  return new com.sencha.gxt.theme.neptune.client.GridDetails.RowEditorDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#d5d5d5";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String cellSelectedVBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String specialColumnGradient() {
  return "";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.GridDetails.RowNumbererDetails rowNumberer() {
  return new com.sencha.gxt.theme.neptune.client.GridDetails.RowNumbererDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String cellVBorderColor() {
  return "#ededed";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.GridDetails.ColumnHeaderDetails columnHeader() {
  return new com.sencha.gxt.theme.neptune.client.GridDetails.ColumnHeaderDetails() {

  @Override
  public java.lang.String menuGradient() {
  return "#f5f5f5 0%, #f5f5f5 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails menuBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String borderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String borderColor() {
  return "#c0c0c0";
  }

  @Override
  public java.lang.String overGradient() {
  return "#c5c5c5 0%, #c5c5c5 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 7;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 7;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#f5f5f5 0%, #f5f5f5 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String menuActiveGradient() {
  return "#c5c5c5 0%, #c5c5c5 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails menuHoverBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderWidth() {
  return 1;
  }

  @Override
  public int menuButtonWidth() {
  return 18;
  }

  @Override
  public java.lang.String lineHeight() {
  return "15px";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails menuActiveBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String menuHoverGradient() {
  return "#c5c5c5 0%, #c5c5c5 100%";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails cellText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String cellLineHeight() {
  return "15px";
  }

  @Override
  public java.lang.String cellSelectedHBorderColor() {
  return "#e2eff8";
  }

  @Override
  public java.lang.String cellHBorderColor() {
  return "#ededed";
  }

  @Override
  public java.lang.String cellOverHBorderColor() {
  return "#e2eff8";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.GridDetails.FooterDetails footer() {
  return new com.sencha.gxt.theme.neptune.client.GridDetails.FooterDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails cellBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#ededed";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String cellSelectedHBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String cellOverBackgroundColor() {
  return "#e5e5e5";
  }

  @Override
  public java.lang.String cellOverVBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String cellSelectedBackgroundColor() {
  return "#dbdbdb";
  }

  @Override
  public java.lang.String cellAltBackgroundColor() {
  return "#fafafa";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.GridDetails.GroupDetails group() {
  return new com.sencha.gxt.theme.neptune.client.GridDetails.GroupDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.GridDetails.GroupDetails.SummaryDetails summary() {
  return new com.sencha.gxt.theme.neptune.client.GridDetails.GroupDetails.SummaryDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails text() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 8;
  }

  @Override
  public int right() {
  return 4;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 4;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int iconSpacing() {
  return 17;
  }
    };
  }

  @Override
  public java.lang.String cellOverVBorderColor() {
  return "#ededed";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.MenuDetails menu() {
  return new com.sencha.gxt.theme.neptune.client.MenuDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.MenuDetails.MenuBarDetails bar() {
  return new com.sencha.gxt.theme.neptune.client.MenuDetails.MenuBarDetails() {

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails activeItemText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails activeItemBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String hoverItemGradient() {
  return "#cecece 0%, #cecece 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails itemPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public int right() {
  return 8;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 8;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemLineHeight() {
  return "24px";
  }

  @Override
  public java.lang.String activeItemGradient() {
  return "#cacaca 0%, #cacaca 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails hoverItemText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails itemText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails hoverItemBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails activeItemText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String itemLineHeight() {
  return "24px";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.MenuDetails.HeaderItemDetails header() {
  return new com.sencha.gxt.theme.neptune.client.MenuDetails.HeaderItemDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#D6E3F2";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#99bbe8";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails itemPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemLineHeight() {
  return "13px";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails itemText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#15428b";
  }

  @Override
  public java.lang.String size() {
  return "10px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "tahoma,arial,verdana,sans-serif";
  }
    };
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails padding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.FontDetails itemText() {
  return new com.sencha.gxt.theme.neptune.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.MenuDetails.MenuSeparatorDetails separator() {
  return new com.sencha.gxt.theme.neptune.client.MenuDetails.MenuSeparatorDetails() {

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails margin() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int height() {
  return 1;
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails activeItemBorder() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.BorderDetails border() {
  return new com.sencha.gxt.theme.neptune.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public com.sencha.gxt.theme.neptune.client.EdgeDetails itemPadding() {
  return new com.sencha.gxt.theme.neptune.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String activeItemGradient() {
  return "#cacaca 0%, #cacaca 100%";
  }

  @Override
  public java.lang.String lastGradientColor() {
  return "#ffffff";
  }
    };
  }
public String getName() {return "theme";}

}

