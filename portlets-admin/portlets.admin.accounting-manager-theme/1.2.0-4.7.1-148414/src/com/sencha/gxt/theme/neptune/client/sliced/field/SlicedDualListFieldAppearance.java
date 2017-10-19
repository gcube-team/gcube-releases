/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.button.IconButtonDefaultAppearance.IconButtonStyle;
import com.sencha.gxt.theme.neptune.client.base.field.Css3DualListFieldAppearance;

/**
 *
 */
public class SlicedDualListFieldAppearance extends Css3DualListFieldAppearance {
  public interface SlicedDualListFieldResources extends Css3DualListFieldResources {
    @Override
    @Source("SlicedDualListField.css")
    @Import(IconButtonStyle.class)
    SlicedDualListFieldStyle style();

    @Source("dualListField-button-background.png")
    ImageResource background();
  }

  public interface SlicedDualListFieldStyle extends Css3DualListFieldStyle {

  }


  public SlicedDualListFieldAppearance() {
    this(GWT.<SlicedDualListFieldResources>create(SlicedDualListFieldResources.class));
  }

  public SlicedDualListFieldAppearance(SlicedDualListFieldResources resources) {
    super(resources);
  }
}
