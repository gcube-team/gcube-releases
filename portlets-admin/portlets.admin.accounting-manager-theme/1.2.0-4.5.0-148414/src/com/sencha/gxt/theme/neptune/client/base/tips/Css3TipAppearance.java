/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.tips;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.tips.Tip.TipAppearance;

public class Css3TipAppearance implements TipAppearance {
  public interface Css3TipResources extends ClientBundle {
    @Source("Css3Tip.css")
    Css3TipStyle style();

    ImageResource anchorBottom();
    ImageResource anchorLeft();
    ImageResource anchorRight();
    ImageResource anchorTop();

    ThemeDetails theme();
  }
  public interface Css3TipStyle extends CssResource {
    String tip();

    String tipWrap();

    String anchorTop();

    String anchorRight();

    String anchorBottom();

    String anchorLeft();

    String anchor();

    String heading();

    String headingWrap();

    String text();

    String textWrap();

    String tools();
  }

  public interface Css3TipTemplate extends XTemplates {
    @XTemplate("<div class='{style.tipWrap}'><div class='{style.tip}'>" +
            "<div class='{style.tools}'></div>" +
            "<div class='{style.headingWrap}'><span class='{style.heading}'></span></div>" +
            "<div class='{style.textWrap}'><span class='{style.text}'></span></div>" +
            "</div></div>")
    SafeHtml render(Css3TipStyle style);
  }
  
  private final Css3TipStyle style;
  private final Css3TipTemplate template = GWT.create(Css3TipTemplate.class);

  public Css3TipAppearance() {
    this(GWT.<Css3TipResources>create(Css3TipResources.class));
  }

  public Css3TipAppearance(Css3TipResources resources) {
    style = resources.style();
    style.ensureInjected();
  }

  @Override
  public void applyAnchorDirectionStyle(XElement anchorEl, Side anchor) {
    anchorEl.setClassName(style.anchorTop(), anchor == Side.TOP);
    anchorEl.setClassName(style.anchorBottom(), anchor == Side.BOTTOM);
    anchorEl.setClassName(style.anchorRight(), anchor == Side.RIGHT);
    anchorEl.setClassName(style.anchorLeft(), anchor == Side.LEFT);
  }

  @Override
  public void applyAnchorStyle(XElement anchorEl) {
    anchorEl.addClassName(style.anchor());
  }

  public XElement getHeaderElement(XElement parent) {
    return parent.selectNode("." + style.heading());
  }

  @Override
  public XElement getTextElement(XElement parent) {
    return parent.selectNode("." + style.text());
  }

  @Override
  public XElement getToolsElement(XElement parent) {
    return parent.selectNode("." + style.tools());
  }

  @Override
  public void removeAnchorStyle(XElement anchorEl) {
    anchorEl.removeClassName(style.anchor());
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.render(style));
  }


  @Override
  public int autoWidth(XElement parent, int minWidth, int maxWidth) {
    int tw = getTextElement(parent).getTextWidth();
    int hw = getHeaderElement(parent).getTextWidth();

    int w = Math.max(tw, hw);
    // framing
    w += 10;

    w += getToolsElement(parent).getOffsetWidth();

    return Util.constrain(w, minWidth, maxWidth);
  }

  @Override
  public void updateContent(XElement parent, String heading, String text) {
    XElement header = getHeaderElement(parent);
    if (heading != null && !heading.equals("")) {
      header.setInnerHTML(heading);
      header.getParentElement().getStyle().clearDisplay();
    } else {
      header.getParentElement().getStyle().setDisplay(Display.NONE);
    }

    getTextElement(parent).setInnerHTML(text);
  }
}
