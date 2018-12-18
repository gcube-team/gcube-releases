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
import com.sencha.gxt.theme.base.client.menu.MenuBaseAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3MenuAppearance extends MenuBaseAppearance {
  public interface Css3MenuResources extends MenuResources, ClientBundle {
    @Override
    @Source("Css3Menu.css")
    Css3MenuStyle style();

    ImageResource miniTop();
    ImageResource miniBottom();

    ThemeDetails theme();
  }
  public interface Css3MenuStyle extends MenuStyle {

  }

  public Css3MenuAppearance() {
    this(GWT.<Css3MenuResources>create(Css3MenuResources.class),
            GWT.<BaseMenuTemplate>create(BaseMenuTemplate.class));
  }

  public Css3MenuAppearance(Css3MenuResources resources, BaseMenuTemplate template) {
    super(resources, template);
  }
}
