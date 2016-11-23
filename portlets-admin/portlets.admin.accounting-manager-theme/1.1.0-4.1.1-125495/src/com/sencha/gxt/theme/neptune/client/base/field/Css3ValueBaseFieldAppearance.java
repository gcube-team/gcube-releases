/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.field;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.resources.client.CssResource;
import com.sencha.gxt.cell.core.client.form.ValueBaseInputCell.ValueBaseFieldAppearance;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.form.Field.FieldStyles;

public abstract class Css3ValueBaseFieldAppearance implements ValueBaseFieldAppearance {

  public interface Css3ValueBaseFieldResources {
    Css3ValueBaseFieldStyle style();

    ThemeDetails theme();
  }

  public interface Css3ValueBaseFieldStyle extends CssResource, FieldStyles {
    @Override
    String focus();

    @Override
    String invalid();

    String empty();

    String field();

    String readonly();

    String wrap();

  }

  private final Css3ValueBaseFieldStyle style;

  public Css3ValueBaseFieldAppearance(Css3ValueBaseFieldResources resources) {
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  @Override
  public void onEmpty(Element parent, boolean empty) {
    getInputElement(parent).setClassName(style.empty(), empty);
  }

  @Override
  public void onFocus(Element parent, boolean focus) {
    parent.<XElement>cast().setClassName(style.focus(), focus);
  }

  @Override
  public void onValid(Element parent, boolean valid) {
    parent.<XElement>cast().setClassName(style.invalid(), !valid);
  }

  @Override
  public void setReadOnly(Element parent, boolean readOnly) {
    getInputElement(parent).<InputElement>cast().setReadOnly(readOnly);
    getInputElement(parent).setClassName(style.readonly(), readOnly);
  }
}
