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
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.menu.MenuItemBaseAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3MenuItemAppearance extends MenuItemBaseAppearance {

  public interface Css3MenuItemResources extends MenuItemResources, ClientBundle {

    @Override
    @Source("Css3MenuItem.css")
    Css3MenuItemStyle style();

    ThemeDetails theme();

    ImageResource menuParent();
  }

  public interface Css3MenuItemStyle extends MenuItemStyle {

  }

  public Css3MenuItemAppearance() {
    this(GWT.<Css3MenuItemResources>create(Css3MenuItemResources.class),
            GWT.<MenuItemTemplate>create(MenuItemTemplate.class));

  }
  public Css3MenuItemAppearance(Css3MenuItemResources resources, MenuItemTemplate template) {
    super(resources, template);
  }
}
