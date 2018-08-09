/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.PlainTabPanel.PlainTabPanelBottomAppearance;

/**
 *
 */
public class SlicedPlainTabPanelBottomAppearance extends SlicedTabPanelBottomAppearance implements PlainTabPanelBottomAppearance {

  public interface SlicedPlainTabPanelBottomResources extends SlicedTabPanelBottomResources {
    @Override
    @Source({"SlicedTabPanelBottom.css", "SlicedPlainTabPanelBottom.css"})
    SlicedPlainTabPanelBottomStyle style();
  }

  public interface SlicedPlainTabPanelBottomStyle extends SlicedTabPanelBottomStyle {
    String tabStripSpacer();
  }

  public interface SlicedPlainTabPanelBottomTemplates extends XTemplates {
    @XTemplate(source = "SlicedPlainTabPanelBottom.html")
    SafeHtml render(SlicedPlainTabPanelBottomStyle style);
  }

  private final SlicedPlainTabPanelBottomTemplates template;
  private final SlicedPlainTabPanelBottomStyle style;

  public SlicedPlainTabPanelBottomAppearance() {
    this(GWT.<SlicedPlainTabPanelBottomResources>create(SlicedPlainTabPanelBottomResources.class));
  }

  public SlicedPlainTabPanelBottomAppearance(SlicedPlainTabPanelBottomResources resources) {
    this(resources, GWT.<SlicedPlainTabPanelBottomTemplates>create(SlicedPlainTabPanelBottomTemplates.class));
  }

  public SlicedPlainTabPanelBottomAppearance(SlicedPlainTabPanelBottomResources resources, SlicedPlainTabPanelBottomTemplates template) {
    super(resources);
    this.style = resources.style();
    this.template = template;
  }


  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.append(template.render(style));
  }
}
