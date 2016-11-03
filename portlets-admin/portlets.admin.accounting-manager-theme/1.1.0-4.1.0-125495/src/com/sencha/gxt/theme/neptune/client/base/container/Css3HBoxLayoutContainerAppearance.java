/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.container.HBoxLayoutDefaultAppearance;

public class Css3HBoxLayoutContainerAppearance extends HBoxLayoutDefaultAppearance {

  public interface Css3HBoxLayoutContainerResources extends HBoxLayoutBaseResources {
    @Override
    @Source({"com/sencha/gxt/theme/base/client/container/BoxLayout.css", "Css3HBoxLayoutContainer.css"})
    HBoxLayoutStyle style();

    @Override
    ImageResource moreIcon();

    ImageResource moreIconToolBar();
  }

  public interface Css3HBoxLayoutContainerStyle extends HBoxLayoutStyle {

  }

  public Css3HBoxLayoutContainerAppearance() {
    this(GWT.<Css3HBoxLayoutContainerResources>create(Css3HBoxLayoutContainerResources.class));
  }

  public Css3HBoxLayoutContainerAppearance(Css3HBoxLayoutContainerResources resources) {
    super(resources);
  }
}
