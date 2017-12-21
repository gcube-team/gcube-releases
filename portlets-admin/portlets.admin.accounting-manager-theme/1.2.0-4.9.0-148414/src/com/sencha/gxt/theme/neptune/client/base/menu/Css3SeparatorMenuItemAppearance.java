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
import com.sencha.gxt.theme.base.client.menu.SeparatorMenuItemBaseAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3SeparatorMenuItemAppearance extends SeparatorMenuItemBaseAppearance {

  public interface Css3SeparatorMenuItemResources extends SeparatorMenuItemResources, ClientBundle {
    @Override
    @Source("Css3SeparatorMenuItem.css")
    Css3SeparatorMenuItemStyle style();

    ThemeDetails theme();
  }

  public interface Css3SeparatorMenuItemStyle extends SeparatorMenuItemStyle {

  }

  public Css3SeparatorMenuItemAppearance() {
    super(GWT.<Css3SeparatorMenuItemResources>create(Css3SeparatorMenuItemResources.class), GWT.<SeparatorMenuItemTemplate>create(SeparatorMenuItemTemplate.class));
  }
}
