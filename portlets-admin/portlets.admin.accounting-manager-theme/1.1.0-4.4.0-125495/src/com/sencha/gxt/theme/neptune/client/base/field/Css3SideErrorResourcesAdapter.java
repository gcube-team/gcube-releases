/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.field;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.widget.core.client.form.error.SideErrorHandler.SideErrorResources;

public class Css3SideErrorResourcesAdapter implements SideErrorResources {

  interface Css3SideErrorResources extends SideErrorResources {
    @Override
    @Source("exclamation.png")
    ImageResource errorIcon();
  }

  private final Css3SideErrorResources resources;

  public Css3SideErrorResourcesAdapter() {
    this.resources = GWT.create(Css3SideErrorResources.class);
  }

  public ImageResource errorIcon() {
    return resources.errorIcon();
  }
}
