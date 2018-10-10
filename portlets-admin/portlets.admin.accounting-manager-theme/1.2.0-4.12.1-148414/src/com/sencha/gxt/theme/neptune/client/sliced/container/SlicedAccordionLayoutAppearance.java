/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.container;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.neptune.client.base.container.Css3AccordionLayoutAppearance.Css3AccordionHeaderResources;
import com.sencha.gxt.theme.neptune.client.base.panel.Css3HeaderAppearance;
import com.sencha.gxt.theme.neptune.client.base.panel.Css3HeaderAppearance.Css3HeaderResources;
import com.sencha.gxt.theme.neptune.client.base.panel.Css3HeaderAppearance.Css3HeaderStyle;
import com.sencha.gxt.theme.neptune.client.sliced.panel.SlicedContentPanelAppearance;
import com.sencha.gxt.widget.core.client.Header.HeaderAppearance;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;

public class SlicedAccordionLayoutAppearance extends SlicedContentPanelAppearance implements AccordionLayoutAppearance {
  public interface SlicedAccordionLayoutResources extends SlicedContentPanelResources {
    @Override
    @Source("accordionlayout-background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource headerBackground();

    @Override
    @Source("SlicedAccordionLayout.css")
    SlicedAccordionLayoutStyle style();
  }
  public interface SlicedAccordionLayoutStyle extends SlicedContentPanelStyle {

  }

  public interface SlicedAccordionHeaderResources extends Css3HeaderResources {
    @Source("accordionlayout-background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource headerBackground();

    @Override
    @Source({"com/sencha/gxt/theme/base/client/widget/Header.css", "SlicedAccordionHeader.css"})
    SlicedAccordionHeaderStyle style();
  }
  public interface SlicedAccordionHeaderStyle extends Css3HeaderStyle {

  }

  public SlicedAccordionLayoutAppearance() {
    super(GWT.<SlicedAccordionLayoutResources>create(SlicedAccordionLayoutResources.class));
  }

  @Override
  public HeaderAppearance getHeaderAppearance() {
    return new Css3HeaderAppearance(GWT.<Css3AccordionHeaderResources>create(Css3AccordionHeaderResources.class));
  }

  @Override
  public IconConfig collapseIcon() {
    return ToolButton.MINUS;
  }

  @Override
  public IconConfig expandIcon() {
    return ToolButton.PLUS;
  }
}
