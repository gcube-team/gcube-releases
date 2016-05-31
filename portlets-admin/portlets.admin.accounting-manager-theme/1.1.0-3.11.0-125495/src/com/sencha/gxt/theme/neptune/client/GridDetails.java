/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client;

import com.sencha.gxt.themebuilder.base.client.config.TypeDetails;

public interface GridDetails {
  ColumnHeaderDetails columnHeader();
  
  public interface RowEditorDetails {

    @TypeDetails(sampleValue = "#f0f2f2", comment = "background color")
    String backgroundColor();

    @TypeDetails(sampleValue = "util.border('solid', '#e1e1e1', 1, 0)", comment = "border parameters")
    BorderDetails border();
  }

  public interface ColumnHeaderDetails {
    @TypeDetails(sampleValue = "1", comment = "width of borders between header cells")
    int borderWidth();
    @TypeDetails(sampleValue = "#222222", comment = "color of borders around header cells")
    String borderColor();
    @TypeDetails(sampleValue = "'solid'", comment = "style of borders around header cells")
    String borderStyle();

    @TypeDetails(sampleValue = "util.solidGradientString('#e2e7ec')", comment = "css gradient string for column headers")
    String gradient();
    @TypeDetails(sampleValue = "util.solidGradientString('#e5e5e5')", comment = "css gradient string for column header with mouseover")
    String overGradient();

    @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '13px', '#666666', 'bold')", comment = "Styling to use for grid headers")
    FontDetails text();

    @TypeDetails(sampleValue = "15px", comment = "line height for grid headers")
    String lineHeight();

    @TypeDetails(sampleValue = "util.padding(4,3,4,5)", comment = "column header padding")
    EdgeDetails padding();

    @TypeDetails(sampleValue = "18", comment = "width of the menu icon")
    int menuButtonWidth();

    @TypeDetails(sampleValue = "util.solidGradientString('#e8e8e8')", comment = "background gradient of the menu icon")
    String menuGradient();
    @TypeDetails(sampleValue = "util.border('solid', '#222222', 0, 0, 0, 1)", comment = "border around the menu icon")
    BorderDetails menuBorder();

    @TypeDetails(sampleValue = "util.solidGradientString('#828282')", comment = "mouseover background gradient of the menu icon")
    String menuHoverGradient();
    @TypeDetails(sampleValue = "util.border('solid', '#222222', 0, 0, 0, 1)", comment = "mouseover border around the menu icon")
    BorderDetails menuHoverBorder();

    @TypeDetails(sampleValue = "util.solidGradientString('add2ed')", comment = "active background gradient of the menu icon")
    String menuActiveGradient();
    @TypeDetails(sampleValue = "util.border('solid', '#222222', 0, 0, 0, 1)", comment = "active border around the menu icon")
    BorderDetails menuActiveBorder();
  }

  @TypeDetails(sampleValue = "util.padding(4,3,3,5)", comment = "cell padding")
  EdgeDetails cellPadding();

  @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '13px')", comment = "Styling to use for grid cell text")
  FontDetails cellText();
  @TypeDetails(sampleValue = "15px", comment = "line height for grid cell text")
  String cellLineHeight();

  @TypeDetails(sampleValue = "#666666", comment = "border color between grid cells (if column lines enabled)")
  String cellVBorderColor();
  @TypeDetails(sampleValue = "#666666", comment = "border color between grid rows")
  String cellHBorderColor();

  @TypeDetails(sampleValue = "1", comment = "width of borders between grid rows (and cells, if column lines enabled)")
  int cellBorderWidth();
  @TypeDetails(sampleValue = "#ffffff", comment = "cell background")
  String cellBackgroundColor();
  @TypeDetails(sampleValue = "#cccccc", comment = "cell background for alt rows, if enabled")
  String cellAltBackgroundColor();

  @TypeDetails(sampleValue = "#666666", comment = "mouseover border color between grid cells (if column lines enabled)")
  String cellOverVBorderColor();
  @TypeDetails(sampleValue = "'solid'", comment = "mouseover border style between grid cells (if column lines enabled)")
  String cellOverVBorderStyle();

  @TypeDetails(sampleValue = "#666666", comment = "mouseover border color between grid rows")
  String cellOverHBorderColor();
  @TypeDetails(sampleValue = "'solid'", comment = "mouseover border style between grid rows")
  String cellOverHBorderStyle();

  @TypeDetails(sampleValue = "#e5e5e5", comment = "mouseover background color for grid rows")
  String cellOverBackgroundColor();

  @TypeDetails(sampleValue = "#666666", comment = "border color between grid cells for selected rows/columns (if column lines enabled)")
  String cellSelectedVBorderColor();
  @TypeDetails(sampleValue = "'solid'", comment = "border style between grid cells for selected rows/columns (if column lines enabled)")
  String cellSelectedVBorderStyle();

  @TypeDetails(sampleValue = "#666666", comment = "border color between grid rows for selected rows/columns")
  String cellSelectedHBorderColor();
  @TypeDetails(sampleValue = "'solid'", comment = "border style between grid rows for selected rows/columns")
  String cellSelectedHBorderStyle();

  @TypeDetails(sampleValue = "#dbdbdb", comment = "background color for selected rows/columns")
  String cellSelectedBackgroundColor();


  @TypeDetails(sampleValue = "util.solidGradientString('#e8e8e8')", comment = "gradient to use as a background for columns such as RowNumberer and CheckBoxSelectionModel")
  String specialColumnGradient();

  @TypeDetails(sampleValue = "util.solidGradientString('#666666')", comment = "gradient to use as a background for columns such as RowNumberer and CheckBoxSelectionModel when selected")
  String specialColumnGradientSelected();


  GroupDetails group();

  public interface GroupDetails {
    @TypeDetails(sampleValue = "#ffffff", comment = "background color for group row")
    String backgroundColor();

    @TypeDetails(sampleValue = "util.border('solid', '#000000', 0, 0, 1)", comment = "border around group row")
    BorderDetails border();

    @TypeDetails(sampleValue = "util.fontStyle('san-serif', '13px', '#000000', 'bold')", comment = "styling for group row text")
    FontDetails text();

    @TypeDetails(sampleValue = "util.padding(8, 4)", comment = "padding around the group row")
    EdgeDetails padding();

    @TypeDetails(sampleValue = "17", comment = "spacing to the side of the text to allow for the icon")
    int iconSpacing();

    SummaryDetails summary();

    public interface SummaryDetails {
      @TypeDetails(sampleValue = "util.fontStyle('san-serif', '13px', '#000000', 'bold')", comment = "styling for group summary cell text")
      FontDetails text();

      @TypeDetails(sampleValue = "#ffffff", comment = "background color for group summary row")
      String backgroundColor();
    }
  }

  RowNumbererDetails rowNumberer();

  RowEditorDetails rowEditor();

  public interface RowNumbererDetails {
    @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '13px')", comment = "styling to use for row numberer text")
    FontDetails text();

    @TypeDetails(sampleValue = "util.padding(4,3,3,5)", comment = "row numberer text padding")
    EdgeDetails padding();
  }

  FooterDetails footer();

  public interface FooterDetails {
    @TypeDetails(sampleValue = "util.fontStyle('sans-serif', '13px')", comment = "text style to use on footer text")
    FontDetails text();

    @TypeDetails(sampleValue = "#ffffff", comment = "background color for footer cells")
    String backgroundColor();

    @TypeDetails(sampleValue = "util.border('solid', '#666666', 1, 0, 0)", comment = "border around footer cells")
    BorderDetails cellBorder();
  }
}
