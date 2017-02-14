/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem.HeaderMenuItemAppearance;

public class Css3HeaderMenuItemAppearance implements HeaderMenuItemAppearance {

  public interface Css3HeaderMenuItemResources extends ClientBundle {
    @Source("Css3HeaderMenuItem.css")
    Css3HeaderMenuItemStyle headerStyle();

    ThemeDetails theme();
  }

  public interface Css3HeaderMenuItemStyle extends CssResource {

    public String menuText();

  }

  private Css3HeaderMenuItemStyle style;

  public Css3HeaderMenuItemAppearance() {
    Css3HeaderMenuItemResources resources = GWT.create(Css3HeaderMenuItemResources.class);
    this.style = resources.headerStyle();
    this.style.ensureInjected();
  }

  @Override
  public void applyItemStyle(Element element) {
    element.addClassName(style.menuText());
  }

  @Override
  public void onActivate(XElement parent) {
  }

  @Override
  public void onDeactivate(XElement parent) {
  }
}
