/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.panel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.panel.FramedPanelBaseAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.Header.HeaderAppearance;

public class SlicedFramedPanelAppearance extends FramedPanelBaseAppearance {
  public interface FramedPanelStyle extends ContentPanelStyle {

  }

  public interface SlicedFramePanelResources extends ContentPanelResources {
    @Source("SlicedFramedPanel.css")
    @Override
    FramedPanelStyle style();

    ThemeDetails theme();
  }

  public interface SlicedFramedPanelDivFrameResources extends SlicedFramedPanelFrame.Resources {
    ThemeDetails theme();

    @Source("SlicedFramedPanelDivFrame.css")
    @Override
    SlicedFramePanelNestedDivFrameStyle style();

    @Source("framedpanel-background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @Source("framedpanel-tl.png")
    @Override
    ImageResource topLeftBorder();

    @Source("framedpanel-tc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Override
    ImageResource topBorder();

    @Source("framedpanel-tr.png")
    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource topRightBorder();

    @Source("framedpanel-l.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Override
    ImageResource leftBorder();

    @Source("framedpanel-r.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Override
    ImageResource rightBorder();

    @Source("framedpanel-bl.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Override
    ImageResource bottomLeftBorder();

    @Source("framedpanel-bc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Override
    ImageResource bottomBorder();

    @Source("framedpanel-br.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Override
    ImageResource bottomRightBorder();

  }
  public interface SlicedFramePanelNestedDivFrameStyle extends SlicedFramedPanelFrame.Style {

  }

  private final SlicedFramePanelResources resources;
  public SlicedFramedPanelAppearance() {
    this(GWT.<SlicedFramePanelResources>create(SlicedFramePanelResources.class),
            GWT.<SlicedFramedPanelDivFrameResources>create(SlicedFramedPanelDivFrameResources.class));
  }

  public SlicedFramedPanelAppearance(SlicedFramePanelResources res, SlicedFramedPanelDivFrameResources frameResources) {
    super(res, GWT.<FramedPanelTemplate> create(FramedPanelTemplate.class), new SlicedFramedPanelFrame(frameResources));
    this.resources = res;
  }

  @Override
  public HeaderAppearance getHeaderAppearance() {
    return new SlicedHeaderAppearance();
  }


  @Override
  public int getFrameHeight(XElement parent) {
    return resources.theme().framedPanel().border().top() + resources.theme().framedPanel().border().bottom();
  }

  @Override
  public int getFrameWidth(XElement parent) {
    return resources.theme().framedPanel().border().left() + resources.theme().framedPanel().border().right();
  }
}
