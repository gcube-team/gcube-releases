/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.info.Info.InfoAppearance;

public class Css3InfoAppearance implements InfoAppearance {
  interface Template extends XTemplates {
    @XTemplate("<div class='{infoWrap}'><div class='{info}'></div></div>")
    SafeHtml render(Styles styles);
  }
  interface Styles extends CssResource {
    String infoWrap();
    String info();
  }
  interface Resources extends ClientBundle {
    Styles info();
    ThemeDetails theme();
  }

  private final Template template;
  private final Styles styles;

  public Css3InfoAppearance() {
    this.template = GWT.create(Template.class);
    Resources res = GWT.create(Resources.class);
    styles = res.info();
    styles.ensureInjected();
  }

  @Override
  public XElement getContentElement(XElement parent) {
    return parent.getFirstChildElement().getFirstChildElement().cast();
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.render(styles));
  }
}
