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
import com.sencha.gxt.theme.base.client.menu.MenuBarItemBaseAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem.MenuBarItemAppearance;

public class Css3MenuBarItemAppearance extends MenuBarItemBaseAppearance implements MenuBarItemAppearance {
  public interface Css3MenuBarItemResources extends MenuBarItemResources, ClientBundle {
    @Override
    @Source("Css3MenuBarItem.css")
    Css3MenuBarItemStyle css();

    ThemeDetails theme();
  }

  public interface Css3MenuBarItemStyle extends MenuBarItemStyle {

  }

  public Css3MenuBarItemAppearance() {
    this(GWT.<Css3MenuBarItemResources>create(Css3MenuBarItemResources.class));
  }

  public Css3MenuBarItemAppearance(Css3MenuBarItemResources resources) {
    super(resources);
  }
}
