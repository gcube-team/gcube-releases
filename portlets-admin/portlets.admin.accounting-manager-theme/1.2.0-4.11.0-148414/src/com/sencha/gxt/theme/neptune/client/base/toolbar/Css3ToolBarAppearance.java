/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.toolbar;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.theme.neptune.client.base.container.Css3HBoxLayoutContainerAppearance;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar.ToolBarAppearance;

/**
 *
 */
public class Css3ToolBarAppearance extends Css3HBoxLayoutContainerAppearance implements ToolBarAppearance {

  public interface Css3ToolBarResources extends Css3HBoxLayoutContainerResources {
    @Source({"com/sencha/gxt/theme/base/client/container/BoxLayout.css", "com/sencha/gxt/theme/neptune/client/base/container/Css3HBoxLayoutContainer.css", "Css3ToolBar.css"})
    Css3ToolBarStyle style();

    ThemeDetails theme();
  }

  public interface Css3ToolBarStyle extends Css3HBoxLayoutContainerStyle {
    String toolBar();
  }

  @Override
  public String toolBarClassName() {
    return style.toolBar();
  }


  private final Css3ToolBarResources resources;
  private final Css3ToolBarStyle style;

  public Css3ToolBarAppearance() {
    this(GWT.<Css3ToolBarResources>create(Css3ToolBarResources.class));
  }

  public Css3ToolBarAppearance(Css3ToolBarResources resources) {
    this.resources = resources;
    this.style = this.resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }
}
