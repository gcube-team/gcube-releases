/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.neptune.client.base.panel.Css3ContentPanelAppearance;
import com.sencha.gxt.theme.neptune.client.base.panel.Css3HeaderAppearance;
import com.sencha.gxt.widget.core.client.Header.HeaderAppearance;

/**
 *
 */
public class SlicedContentPanelAppearance extends Css3ContentPanelAppearance {

  public interface SlicedContentPanelResources extends Css3ContentPanelResources {
    @Source("SlicedContentPanel.css")
    @Override
    SlicedContentPanelStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("panel-background.png")
    ImageResource headerBackground();
  }

  public interface SlicedContentPanelStyle extends Css3ContentPanelStyle {
  }

  public SlicedContentPanelAppearance() {
    this(GWT.<SlicedContentPanelResources>create(SlicedContentPanelResources.class));
  }

  public SlicedContentPanelAppearance(SlicedContentPanelResources resources) {
    this(resources, GWT.<Css3ContentPanelTemplate>create(Css3ContentPanelTemplate.class));
  }

  public SlicedContentPanelAppearance(SlicedContentPanelResources resources, Css3ContentPanelTemplate template) {
    super(resources, template);
  }

  @Override
  public HeaderAppearance getHeaderAppearance() {
    return new Css3HeaderAppearance();
  }
}
