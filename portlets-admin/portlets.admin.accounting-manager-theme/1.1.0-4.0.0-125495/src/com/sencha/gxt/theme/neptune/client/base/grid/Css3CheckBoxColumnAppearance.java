/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.grid;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel.CheckBoxColumnAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;

public class Css3CheckBoxColumnAppearance implements CheckBoxColumnAppearance {

  public interface CheckBoxColumnStyle extends CssResource {
    String headerChecked();

    String hdChecker();

    String cell();
    
    String cellInner();

    String rowChecker();
  }

  public interface CheckBoxColumnResources extends ClientBundle {
    @Source("Css3CheckBoxColumn.css")
    @Import({GridStateStyles.class, ColumnHeaderStyles.class})
    CheckBoxColumnStyle style();

    //to be placed in sliced impl after sliced job is created
//    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
//    ImageResource specialColumn();
//
//    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
//    ImageResource specialColumnSelected();

    ImageResource checked();

    ImageResource unchecked();

    ThemeDetails theme();
  }

  public interface CheckBoxColumnTemplates extends XTemplates {
    @XTemplate("<div class='{style.rowChecker}'>&#160;</div>")
    SafeHtml renderCell(CheckBoxColumnStyle style);

    @XTemplate("<div class='{style.hdChecker}'></div>")
    SafeHtml renderHeader(CheckBoxColumnStyle style);
  }

  private final CheckBoxColumnResources resources;
  private final CheckBoxColumnStyle style;
  private final CheckBoxColumnTemplates template;

  public Css3CheckBoxColumnAppearance() {
    this(GWT.<CheckBoxColumnResources> create(CheckBoxColumnResources.class), GWT.<CheckBoxColumnTemplates>create(CheckBoxColumnTemplates.class));
  }

  public Css3CheckBoxColumnAppearance(CheckBoxColumnResources resources, CheckBoxColumnTemplates template) {
    this.resources = resources;
    this.style = this.resources.style();
    this.template = template;

    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public String getCellClassName() {
    return style.cell();
  }

  @Override
  public void renderCellCheckBox(Context context, Object value, SafeHtmlBuilder sb) {
    sb.append(template.renderCell(style));
  }

  @Override
  public SafeHtml renderHeadCheckBox() {
    return template.renderHeader(style);
  }

  @Override
  public void onHeaderChecked(XElement header, boolean checked) {
    header.setClassName(style.headerChecked(), checked);
  }

  @Override
  public boolean isHeaderChecked(XElement header) {
    return header.findParent("." + style.headerChecked(), 3) != null;
  }

  @Override
  public boolean isRowChecker(XElement target) {
    return target.is("." + style.rowChecker());
  }

  @Override
  public String getCellInnerClassName() {
    return style.cellInner();
  }
}