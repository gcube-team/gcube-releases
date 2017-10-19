/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.cell.core.client.form.FieldCell.FieldAppearanceOptions;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell.TriggerFieldAppearance;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.neptune.client.FieldDetails;
import com.sencha.gxt.theme.neptune.client.base.field.Css3TextFieldAppearance.Css3TextFieldStyle;

/**
 *
 */
public class Css3TriggerFieldAppearance extends Css3ValueBaseFieldAppearance implements TriggerFieldAppearance {

  public interface Css3TriggerFieldResources extends Css3ValueBaseFieldResources, ClientBundle {
    @Override
    @Source({"Css3ValueBaseField.css", "Css3TextField.css", "Css3TriggerField.css"})
    Css3TriggerFieldStyle style();

    ImageResource triggerArrow();

    ImageResource triggerArrowClick();

    ImageResource triggerArrowOver();
  }

  public interface Css3TriggerFieldStyle extends Css3TextFieldStyle {
    String click();

    String noedit();

    String over();

    String trigger();
  }

  private final Css3TriggerFieldResources resources;
  private final Css3TriggerFieldStyle style;

  public Css3TriggerFieldAppearance() {
    this(GWT.<Css3TriggerFieldResources>create(Css3TriggerFieldResources.class));
  }

  public Css3TriggerFieldAppearance(Css3TriggerFieldResources resources) {
    super(resources);

    this.resources = resources;
    this.style = resources.style();
  }

  @Override
  public XElement getInputElement(Element parent) {
    return parent.<XElement>cast().selectNode("input");
  }

  @Override
  public void onFocus(Element parent, boolean focus) {
    parent.<XElement>cast().setClassName(getResources().style().focus(), focus);
  }

  @Override
  public void onResize(XElement parent, int width, int height, boolean hideTrigger) {
    if (width != -1) {
      width = Math.max(0, width);
      parent.getFirstChildElement().getStyle().setPropertyPx("width", width);
    }
  }

  @Override
  public void onTriggerClick(XElement parent, boolean click) {
    parent.setClassName(getResources().style().click(), click);
  }

  @Override
  public void onTriggerOver(XElement parent, boolean over) {
    parent.setClassName(getResources().style().over(), over);
  }

  @Override
  public void render(SafeHtmlBuilder sb, String value, FieldAppearanceOptions options) {
    int width = options.getWidth();
    boolean hideTrigger = options.isHideTrigger();

    if (width == -1) {
      width = 150;
    }

    SafeStylesBuilder inputStylesBuilder = new SafeStylesBuilder();
    inputStylesBuilder.appendTrustedString("width:100%;");

    // outer div needed for widgets like comboBox that need the full width to set for listview width
    sb.appendHtmlConstant("<div style='width:" + width + "px;'>");

    if (hideTrigger) {
      sb.appendHtmlConstant("<div class='" + style.wrap() + "'>");
      renderInput(sb, value, inputStylesBuilder.toSafeStyles(), options);
    } else {
      FieldDetails fieldDetails = getResources().theme().field();

      int rightPadding = fieldDetails.padding().right();
      sb.appendHtmlConstant("<div class='" + style.wrap() + "' style='padding-right:" + (getResources().triggerArrow().getWidth() + rightPadding) + "px;'>");
      renderInput(sb, value, inputStylesBuilder.toSafeStyles(), options);


      int right = fieldDetails.borderWidth() + 1;
      int halfHeight = getResources().triggerArrow().getHeight() / 2;
      SafeStyles triggerStyle = SafeStylesUtils.fromTrustedString("margin-top:-" + halfHeight + "px;right:" + right + "px;");
      sb.appendHtmlConstant("<div class='" + getStyle().trigger() +
          "' style='" + triggerStyle.asString() + "'></div>");
    }

    sb.appendHtmlConstant("</div></div>");
  }

  @Override
  public void setEditable(XElement parent, boolean editable) {
    getInputElement(parent).setClassName(getStyle().noedit(), !editable);
  }

  @Override
  public boolean triggerIsOrHasChild(XElement parent, Element target) {
    return parent.isOrHasChild(target) && target.<XElement>cast().is("." + getStyle().trigger());
  }


  protected Css3TriggerFieldResources getResources() {
    return resources;
  }

  protected Css3TriggerFieldStyle getStyle() {
    return style;
  }


  protected void renderInput(SafeHtmlBuilder shb, String value, SafeStyles inputStyles, FieldAppearanceOptions options) {
    StringBuilder sb = new StringBuilder();
    sb.append("<input ");

    if (options.isDisabled()) {
      sb.append("disabled=true");
    }

    if (options.getName() != null) {
      sb.append("name='").append(SafeHtmlUtils.htmlEscape(options.getName())).append("' ");
    }

    if (options.isReadonly() || !options.isEditable()) {
      sb.append("readonly ");
    }

    if (inputStyles != null) {
      sb.append("style='").append(inputStyles.asString()).append("' ");
    }

    sb.append("class='").append(getStyle().field()).append(" ").append(getStyle().text());

    String placeholder = options.getEmptyText() != null ? " placeholder='" + SafeHtmlUtils.htmlEscape(options.getEmptyText()) + "' " : "";

    if ("".equals(value) && options.getEmptyText() != null) {
      sb.append(" ").append(getStyle().empty());
      if (GXT.isIE8() || GXT.isIE9()) {
        value = options.getEmptyText();
      }
    }

    if (!options.isEditable()) {
      sb.append(" ").append(getStyle().noedit());
    }

    sb.append("' ");
    sb.append(placeholder);

    sb.append("type='text' value='").append(SafeHtmlUtils.htmlEscape(value)).append("' ");

    sb.append("/>");

    shb.append(SafeHtmlUtils.fromTrustedString(sb.toString()));
  }

}
