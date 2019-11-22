/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.mask;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.Mask.MaskAppearance;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.frame.DivFrame;
import com.sencha.gxt.theme.base.client.frame.DivFrame.DivFrameResources;
import com.sencha.gxt.theme.base.client.frame.DivFrame.DivFrameStyle;
import com.sencha.gxt.theme.base.client.frame.Frame;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class SlicedMaskAppearance implements MaskAppearance {
  public interface SlicedMaskTemplate extends XTemplates {
    @XTemplate("<div class=\"{style.text}\">{message}</div>")
    SafeHtml template(SlicedMaskStyles style, String message);
  }

  public interface SlicedMaskResources extends DivFrameResources, ClientBundle {
    @Override
    @Source({"com/sencha/gxt/theme/base/client/frame/DivFrame.css", "SlicedMask.css"})
    SlicedMaskStyles style();

    @Source("com/sencha/gxt/theme/base/client/grid/loading.gif")
    ImageResource loading();

    @Source("background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @Override
    @Source("corner-bc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource bottomBorder();

    @Override
    @Source("corner-bl.png")
    ImageResource bottomLeftBorder();

    @Override
    @Source("corner-br.png")
    ImageResource bottomRightBorder();

    @Override
    @Source("side-l.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource leftBorder();

    @Override
    @Source("side-r.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource rightBorder();

    @Override
    @Source("corner-tc.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource topBorder();

    @Override
    @Source("corner-tl.png")
    ImageResource topLeftBorder();

    @Override
    @Source("corner-tr.png")
    ImageResource topRightBorder();

    ThemeDetails theme();
  }
  public interface SlicedMaskStyles extends DivFrameStyle {
    String mask();
    String text();

    String masked();
    String positioned();
  }

  private final SlicedMaskResources resources;
  private final DivFrame frame;
  private final SlicedMaskTemplate template = GWT.create(SlicedMaskTemplate.class);

  public SlicedMaskAppearance() {
    resources = GWT.create(SlicedMaskResources.class);
    resources.style().ensureInjected();
    frame = new DivFrame(resources);
  }

  @Override
  public void mask(XElement parent, String message) {
    XElement mask = XElement.createElement("div");
    mask.setClassName(resources.style().mask());
    parent.appendChild(mask);

    XElement box = null;
    if (message != null) {
      SafeHtmlBuilder sb = new SafeHtmlBuilder();
      SafeHtml content = template.template(resources.style(), SafeHtmlUtils.htmlEscape(message));
      frame.render(sb, Frame.EMPTY_FRAME, content);
      box = XDOM.create(sb.toSafeHtml()).cast();
      parent.appendChild(box);
    }

    if (GXT.isIE() && !(GXT.isIE7()) && "auto".equals(parent.getStyle().getHeight())) {
      mask.setSize(parent.getOffsetWidth(), parent.getOffsetHeight());
    }

    if (box != null) {
      box.updateZIndex(0);
      box.center(parent);
    }
  }

  @Override
  public String masked() {
    return resources.style().masked();
  }

  @Override
  public String positioned() {
    return resources.style().positioned();
  }

  @Override
  public void unmask(XElement parent) {
    XElement mask = parent.selectNode("> ." + resources.style().mask());
    if (mask != null) {
      mask.removeFromParent();
    }
    XElement box = parent.selectNode("> ." + resources.style().contentArea());
    if (box != null) {
      box.removeFromParent();
    }
  }
}
