/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStyles;

public class Css3GridAppearance implements GridAppearance {

  public interface GridResources extends ClientBundle {

    @Source("Css3Grid.css")
    @Import(GridStateStyles.class)
    GridStyle css();

    ThemeDetails theme();
  }

  public interface GridStyle extends GridStyles {
    String scroller();

    String body();
  }

  public interface GridTemplates extends XTemplates {
    @XTemplate(source = "Grid.html")
    SafeHtml render(GridStyle style);
  }

  protected final GridResources resources;
  protected final GridStyle style;
  private GridTemplates templates = GWT.create(GridTemplates.class);

  public Css3GridAppearance() {
    this(GWT.<GridResources>create(GridResources.class));
  }
  public Css3GridAppearance(GridResources resources) {
    this.resources = resources;
    this.style = this.resources.css();

    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(templates.render(style));
  }

  @Override
  public GridStyles styles() {
    return style;
  }

  @Override
  public Element findRow(Element elem) {
    if (Element.is(elem)) {
      return elem.<XElement> cast().findParentElement("." + style.row(), -1);
    }
    return null;
  }

  @Override
  public NodeList<Element> getRows(XElement parent) {
    return TableElement.as(parent.getFirstChildElement()).getTBodies().getItem(1).getRows().cast();
  }

  @Override
  public Element findCell(Element elem) {
    if (Element.is(elem)) {
      return elem.<XElement> cast().findParentElement("." + style.cell(), -1);
    }
    return null;
  }

  @Override
  public void onRowOver(Element row, boolean over) {
    row.<XElement> cast().setClassName(style.rowOver(), over);
  }

  @Override
  public void onRowHighlight(Element row, boolean highlight) {
    row.<XElement> cast().setClassName(style.rowHighlight(), highlight);
  }

  @Override
  public void onRowSelect(Element row, boolean select) {
  }

  @Override
  public void onCellSelect(Element cell, boolean select) {
  }

  @Override
  public Element getRowBody(Element row) {
    return TableElement.as(row.getFirstChildElement().getFirstChildElement().getFirstChildElement()).getTBodies().getItem(
            1).getRows().getItem(1).getCells().getItem(0).getFirstChildElement();
  }

  @Override
  public SafeHtml renderEmptyContent(String emptyText) {
    return SafeHtmlUtils.fromTrustedString(emptyText);
  }

}
