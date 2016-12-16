/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;

public class Css3ColumnHeaderAppearance implements ColumnHeaderAppearance {

  public interface Css3ColumnHeaderResources extends ClientBundle {
    /** column header menu button visible on mouseover */
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource columnHeaderBtn();

    /** Menu icon */
    ImageResource columnsIcon();

    /** grid header marker */
    ImageResource sortAscHeaderIcon();

    /** grid header marker */
    ImageResource sortDescHeaderIcon();

    /** Menu icon */
    ImageResource sortAscendingIcon();

    /** Menu icon */
    ImageResource sortDescendingIcon();

    /** Column dnd indicator 1 */
    ImageResource columnMoveTop();

    /** Column dnd indicator 2 */
    ImageResource columnMoveBottom();

    @Source("Css3ColumnHeader.css")
    Styles style();

    ThemeDetails theme();

  }

  public interface Styles extends ColumnHeaderStyles {

  }

  public interface ColumnHeaderTemplate extends XTemplates {
    @XTemplate(source = "Css3ColumnHeader.html")
    SafeHtml render(ColumnHeaderStyles style);
  }

  private final Css3ColumnHeaderResources resources;
  private final ColumnHeaderStyles style;
  private ColumnHeaderTemplate templates = GWT.create(ColumnHeaderTemplate.class);

  public Css3ColumnHeaderAppearance() {
    this(GWT.<Css3ColumnHeaderResources> create(Css3ColumnHeaderResources.class));
  }

  public Css3ColumnHeaderAppearance(Css3ColumnHeaderResources resources) {
    this.resources = resources;
    this.style = this.resources.style();

    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public ImageResource columnsIcon() {
    return resources.columnsIcon();
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(templates.render(style));
  }

  @Override
  public ImageResource sortAscendingIcon() {
    return resources.sortAscendingIcon();
  }

  @Override
  public ImageResource sortDescendingIcon() {
    return resources.sortDescendingIcon();
  }

  @Override
  public ColumnHeaderStyles styles() {
    return style;
  }

  @Override
  public String columnsWrapSelector() {
    return "." + style.headerInner();
  }

  @Override
  public int getColumnMenuWidth() {
    return resources.theme().grid().columnHeader().menuButtonWidth();
  }
}
