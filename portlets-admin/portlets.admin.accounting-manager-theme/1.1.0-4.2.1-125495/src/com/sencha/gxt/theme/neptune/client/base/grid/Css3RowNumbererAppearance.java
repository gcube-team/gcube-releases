/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.grid;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;
import com.sencha.gxt.widget.core.client.grid.RowNumberer.RowNumbererAppearance;

public class Css3RowNumbererAppearance implements RowNumbererAppearance {

  public interface RowNumbererResources extends ClientBundle {
    @Import(GridStateStyles.class)
    @Source("Css3RowNumberer.css")
    RowNumbererStyles styles();

    //to be placed in sliced impl after sliced job is created
//    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
//    ImageResource specialColumn();
//
//    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
//    ImageResource specialColumnSelected();

    ThemeDetails theme();
  }
  public interface RowNumbererStyles extends CssResource{
    String numberer();
    String cell();
  }

  private final RowNumbererResources resources;

  public Css3RowNumbererAppearance() {
    resources = com.google.gwt.core.shared.GWT.create(RowNumbererResources.class);
    resources.styles().ensureInjected();
  }

  @Override
  public String getCellClassName() {
    return resources.styles().cell();
  }

  @Override
  public void renderCell(int rowNumber, SafeHtmlBuilder sb) {
    sb.appendHtmlConstant("<div class='"+resources.styles().numberer()+"'>").append(rowNumber).appendHtmlConstant("</div>");
  }

  @Override
  public SafeHtml renderHeader() {
    return SafeHtmlUtils.EMPTY_SAFE_HTML;
  }
}