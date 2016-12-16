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
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem.CheckMenuItemAppearance;

public class Css3CheckMenuItemAppearance extends Css3MenuItemAppearance implements CheckMenuItemAppearance {

  public interface Css3CheckMenuItemResources extends ClientBundle {
    ImageResource checked();
    ImageResource unchecked();
    ImageResource groupChecked();
  }

  private final Css3CheckMenuItemResources resources = GWT.create(Css3CheckMenuItemResources.class);

  @Override
  public void applyChecked(XElement parent, boolean state) {
  }

  @Override
  public ImageResource checked() {
    return resources.checked();
  }

  @Override
  public ImageResource unchecked() {
    return resources.unchecked();
  }

  @Override
  public ImageResource radio() {
    return resources.groupChecked();
  }
}
