/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.fieldset;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.theme.base.client.field.FieldSetDefaultAppearance;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3FieldSetAppearance extends FieldSetDefaultAppearance {
  public interface Css3FieldSetResources extends FieldSetResources {
    @Source({"com/sencha/gxt/theme/base/client/field/FieldSet.css", "Css3FieldSet.css"})
    @Override
    Css3FieldSetStyle css();

    ThemeDetails theme();
  }
  public interface Css3FieldSetStyle extends FieldSetStyle {

  }

  public Css3FieldSetAppearance() {
    this(GWT.<Css3FieldSetResources>create(Css3FieldSetResources.class));
  }

  public Css3FieldSetAppearance(Css3FieldSetResources resources) {
    super(resources);
  }
}
