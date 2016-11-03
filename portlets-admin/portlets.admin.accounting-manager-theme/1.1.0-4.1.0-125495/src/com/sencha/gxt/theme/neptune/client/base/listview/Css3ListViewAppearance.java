/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.listview;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.ListView.ListViewAppearance;

public class Css3ListViewAppearance<M> implements ListViewAppearance<M> {

  public interface Css3ListViewResources extends ClientBundle {
    @Source("Css3ListView.css")
    Css3ListViewStyle css();

    ThemeDetails theme();
  }

  public interface Css3ListViewStyle extends CssResource {

    String item();

    String over();

    String sel();

    String view();
  }

  public interface Css3ListViewTemplates extends XTemplates {
    @XTemplate("<div class='{view}'></div>")
    SafeHtml renderBody(Css3ListViewStyle style);
    @XTemplate("<div class='{style.item}'>{content}</div>")
    SafeHtml renderItem(Css3ListViewStyle style, SafeHtml content);
  }

  private final Css3ListViewTemplates template;
  private final Css3ListViewStyle style;

  public Css3ListViewAppearance() {
    this(GWT.<Css3ListViewResources>create(Css3ListViewResources.class));
  }
  public Css3ListViewAppearance(Css3ListViewResources resources) {
    this.style = resources.css();
    this.style.ensureInjected();
    this.template = GWT.create(Css3ListViewTemplates.class);
  }

  @Override
  public Element findCellParent(XElement item) {
    return item;
  }

  @Override
  public Element findElement(XElement child) {
    return child.findParentElement("." + style.item(), 20);
  }

  @Override
  public List<Element> findElements(XElement parent) {
    NodeList<Element> nodes = parent.select("." + style.item());
    List<Element> temp = new ArrayList<Element>();
    for (int i = 0; i < nodes.getLength(); i++) {
      temp.add(nodes.getItem(i));
    }

    return temp;
  }

  @Override
  public void onOver(XElement item, boolean over) {
    item.setClassName(style.over(), over);
  }

  @Override
  public void onSelect(XElement item, boolean select) {
    item.setClassName(style.sel(), select);
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.append(template.renderBody(style));
  }

  @Override
  public void renderEnd(SafeHtmlBuilder builder) {
  }

  @Override
  public void renderItem(SafeHtmlBuilder sb, SafeHtml content) {
    sb.append(template.renderItem(style, content));
  }
}
