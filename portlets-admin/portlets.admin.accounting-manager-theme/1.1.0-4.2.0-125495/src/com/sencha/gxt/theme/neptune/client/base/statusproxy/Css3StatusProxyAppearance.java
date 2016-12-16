/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.statusproxy;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.statusproxy.StatusProxyBaseAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3StatusProxyAppearance extends StatusProxyBaseAppearance {
  public interface Css3StatusProxyResources extends StatusProxyResources, ClientBundle {
    @Override
    @Source({"com/sencha/gxt/theme/base/client/statusproxy/StatusProxy.css", "Css3StatusProxy.css"})
    Css3StatusProxyStyle style();

    @Override
    ImageResource dropAllowed();

    @Override
    ImageResource dropNotAllowed();

    ThemeDetails theme();
  }
  public interface Css3StatusProxyStyle extends StatusProxyStyle {

  }

  public Css3StatusProxyAppearance() {
    super(GWT.<Css3StatusProxyResources>create(Css3StatusProxyResources.class), GWT.<StatusProxyTemplates>create(StatusProxyTemplates.class));
  }
}
