/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;

public class Css3TabPanelBottomAppearance extends Css3TabPanelAppearance implements TabPanelBottomAppearance {

  public interface Css3TabPanelBottomTemplate extends XTemplates {
    @XTemplate(source = "Css3TabPanelBottom.html")
    SafeHtml render(Css3TabPanelBottomStyle style);
  }

  public interface Css3TabPanelBottomResources extends Css3TabPanelResources {
    @Source("Css3TabPanelBottom.css")
    @Override
    Css3TabPanelBottomStyle style();
  }

  public interface Css3TabPanelBottomStyle extends Css3TabPanelStyle {
  }

  private final Css3TabPanelBottomTemplate template = GWT.create(Css3TabPanelBottomTemplate.class);

  public Css3TabPanelBottomAppearance() {
    this(GWT.<Css3TabPanelBottomResources>create(Css3TabPanelBottomResources.class));
  }

  public Css3TabPanelBottomAppearance(Css3TabPanelBottomResources resources) {
    super(resources);
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.append(template.render((Css3TabPanelBottomStyle) style));
  }

  @Override
  public XElement getBar(XElement parent) {
    return parent.getLastChild().cast();
  }
}
