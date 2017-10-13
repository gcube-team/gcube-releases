/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.mask;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.Mask.MaskAppearance;
import com.sencha.gxt.core.client.dom.Mask.MaskDefaultAppearance.MaskStyle;
import com.sencha.gxt.core.client.dom.Mask.MessageTemplates;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3MaskAppearance implements MaskAppearance {

  public interface Css3MaskResources extends ClientBundle {
    @Source("Css3Mask.css")
    Css3MaskStyles styles();

    @Source("com/sencha/gxt/theme/base/client/grid/loading.gif")
    ImageResource loading();

    ThemeDetails theme();
  }

  public interface Css3MaskStyles extends MaskStyle {

  }

  private final Css3MaskResources resources = GWT.create(Css3MaskResources.class);
  private final MessageTemplates template = GWT.create(MessageTemplates.class);

  public Css3MaskAppearance() {
    StyleInjectorHelper.ensureInjected(resources.styles(), true);
  }

  @Override
  public void mask(XElement parent, String message) {
    XElement mask = XElement.createElement("div");
    mask.setClassName(resources.styles().mask());
    parent.appendChild(mask);

    XElement box = null;
    if (message != null) {
      box = XDOM.create(template.template(resources.styles(), SafeHtmlUtils.htmlEscape(message))).cast();
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
    return resources.styles().masked();
  }

  @Override
  public String positioned() {
    return resources.styles().positioned();
  }

  @Override
  public void unmask(XElement parent) {
    XElement mask = parent.selectNode("> ." + resources.styles().mask());
    if (mask != null) {
      mask.removeFromParent();
    }
    XElement box = parent.selectNode("> ." + resources.styles().box());
    if (box != null) {
      box.removeFromParent();
    }
  }
}
