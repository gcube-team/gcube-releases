/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.field;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.theme.base.client.field.FieldLabelDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3FieldLabelAppearance extends FieldLabelDefaultAppearance {
  public interface Css3FieldLabelResources extends FieldLabelResources {
    @Override
    @Source("Css3FieldLabel.css")
    Css3FieldLabelStyles css();

    ThemeDetails theme();
  }
  public interface Css3FieldLabelStyles extends FieldLabelDefaultAppearance.Style {

  }

  public Css3FieldLabelAppearance() {
    this(GWT.<Css3FieldLabelResources>create(Css3FieldLabelResources.class));
  }

  public Css3FieldLabelAppearance(Css3FieldLabelResources resources) {
    super(resources, GWT.<FieldLabelTemplate>create(FieldLabelTemplate.class));
  }
}
