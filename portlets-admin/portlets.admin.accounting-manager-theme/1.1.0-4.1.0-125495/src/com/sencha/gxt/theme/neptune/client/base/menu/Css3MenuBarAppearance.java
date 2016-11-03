/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.menu;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.menu.MenuBar.MenuBarAppearance;

public class Css3MenuBarAppearance implements MenuBarAppearance {
  public interface Css3MenuBarResources extends ClientBundle {
    @Source("Css3MenuBar.css")
    Css3MenuBarStyle style();

    ThemeDetails theme();
  }

  public interface Css3MenuBarStyle extends CssResource {
    String menuBar();
  }

  public interface MenuBarTemplate extends XTemplates {
    @XTemplate("<div class='{cssClasses}'></div>")
    SafeHtml render(String cssClasses);
  }

  private Css3MenuBarStyle style;
  private MenuBarTemplate template = GWT.create(MenuBarTemplate.class);

  public Css3MenuBarAppearance() {
    this(GWT.<Css3MenuBarResources>create(Css3MenuBarResources.class));
  }

  public Css3MenuBarAppearance(Css3MenuBarResources resources) {
    style = resources.style();
    style.ensureInjected();
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.append(template.render(style.menuBar() + " " + CommonStyles.get().noFocusOutline()));
  }
}
