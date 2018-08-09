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
import com.sencha.gxt.widget.core.client.PlainTabPanel.PlainTabPanelAppearance;

public class SlicedPlainTabPanelAppearance extends SlicedTabPanelAppearance implements PlainTabPanelAppearance {
  public interface PlainPanelTemplate extends XTemplates {
    @XTemplate(source = "SlicedPlainTabPanel.html")
    SafeHtml renderPlain(SlicedPlainTabPanelStyle style);
  }
  public interface SlicedPlainTabPanelResources extends SlicedTabPanelResources {
    @Source({"SlicedTabPanel.css","SlicedPlainTabPanel.css"})
    @Override
    SlicedPlainTabPanelStyle style();
  }
  public interface SlicedPlainTabPanelStyle extends SlicedTabPanelStyle {
    String tabStripSpacer();
  }
  private final PlainPanelTemplate template = GWT.create(PlainPanelTemplate.class);

  public SlicedPlainTabPanelAppearance() {
    super(GWT.<SlicedPlainTabPanelResources>create(SlicedPlainTabPanelResources.class));
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.append(template.renderPlain((SlicedPlainTabPanelStyle)resources.style()));
  }
}
