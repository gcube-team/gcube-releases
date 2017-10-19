/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.slider;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.SliderCell.HorizontalSliderAppearance;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.theme.neptune.client.FieldDetails;
import com.sencha.gxt.theme.neptune.client.SliderDetails;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

/**
 *
 */
public class Css3HorizontalSliderAppearance implements HorizontalSliderAppearance {

  public interface Css3HorizontalSliderResources extends ClientBundle {
    @Source("Css3HorizontalSlider.css")
    Css3HorizontalSliderStyle style();

    ThemeDetails theme();
  }

  public interface Css3HorizontalSliderStyle extends CssResource {
    String slider();

    String drag();

    String over();

    String track();

    String thumb();

    String thumbCenter();
  }

  public interface Css3HorizontalSliderTemplate extends XTemplates {
    @XTemplate(source = "Css3Slider.html")
    SafeHtml render(Css3HorizontalSliderStyle style, SafeStyles sliderStyle, SafeStyles trackStyle, SafeStyles thumbStyle, SafeStyles thumbCenterStyle);
  }

  private Css3HorizontalSliderResources resources;
  private Css3HorizontalSliderStyle style;
  protected Css3HorizontalSliderTemplate template;

  public Css3HorizontalSliderAppearance() {
    this(GWT.<Css3HorizontalSliderResources>create(Css3HorizontalSliderResources.class));
  }

  public Css3HorizontalSliderAppearance(Css3HorizontalSliderResources resources) {
    this(resources, GWT.<Css3HorizontalSliderTemplate>create(Css3HorizontalSliderTemplate.class));
  }

  public Css3HorizontalSliderAppearance(Css3HorizontalSliderResources resources, Css3HorizontalSliderTemplate template) {
    this.resources = resources;
    this.template = template;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public int getClickedValue(Context context, Element parent, Point location) {
    XElement track = getTrack(parent);
    return location.getX() - track.getLeft(false) - (resources.theme().field().slider().thumbWidth());
  }

  @Override
  public int getSliderLength(XElement parent) {
    SliderDetails sliderDetails = resources.theme().field().slider();
    return getTrack(parent).getOffsetWidth() - sliderDetails.thumbWidth();
  }

  @Override
  public Element getThumb(Element parent) {
    return parent.<XElement>cast().selectNode("." + style.thumb());
  }

  @Override
  public boolean isVertical() {
    return false;
  }

  @Override
  public void onEmpty(Element parent, boolean empty) {
    // Not possible to "empty" a slider
  }

  @Override
  public void onFocus(Element parent, boolean focus) {
    // No visible effect on focussing
  }

  @Override
  public void onMouseDown(Context context, Element parent) {
    parent.addClassName(style.drag());
  }

  @Override
  public void onMouseOut(Context context, Element parent) {
    parent.removeClassName(style.over());
  }

  @Override
  public void onMouseOver(Context context, Element parent) {
    parent.addClassName(style.over());
  }

  @Override
  public void onMouseUp(Context context, Element parent) {
    parent.removeClassName(style.drag());
  }

  @Override
  public void onValid(Element parent, boolean valid) {
    // Always valid
  }

  @Override
  public void setReadOnly(Element parent, boolean readonly) {
    // TODO Not currently disableable
  }


  @Override
  public void render(double fractionalValue, int width, int height, SafeHtmlBuilder sb) {
    if (width == -1) {
      // default
      width = 200;
    }

    FieldDetails fieldDetails = resources.theme().field();

    int thumbWidth = fieldDetails.slider().thumbWidth();
    int thumbHeight = fieldDetails.slider().thumbHeight();

    int fieldHeight = Math.max(fieldDetails.height(), thumbHeight);
    fieldHeight = Math.max(fieldHeight, fieldDetails.slider().trackHeight());

    int halfThumbWidth = thumbWidth / 2;
    int maxTrackLength = width - thumbWidth;

    int offset = (int) (fractionalValue * maxTrackLength) - halfThumbWidth;
    offset = Math.max(-halfThumbWidth, offset);
    offset = Math.min(maxTrackLength + halfThumbWidth, offset);

    SafeStylesBuilder sliderStyleBuilder = new SafeStylesBuilder();
    sliderStyleBuilder.appendTrustedString("width:" + width + "px;");
    sliderStyleBuilder.appendTrustedString("height:" + fieldHeight + "px;");


    SafeStylesBuilder trackStyleBuilder = new SafeStylesBuilder();
    trackStyleBuilder.appendTrustedString("width: " + width + "px;");
    trackStyleBuilder.appendTrustedString("top:" + ((fieldHeight - fieldDetails.slider().trackHeight()) / 2) + "px;");

    SafeStylesBuilder thumbStyleBuilder = new SafeStylesBuilder();
    thumbStyleBuilder.appendTrustedString("left:" + offset + "px;");
    int thumbTop = ((fieldDetails.slider().trackHeight() - thumbHeight) / 2)
        - (fieldDetails.slider().trackBorder().top() + fieldDetails.slider().trackBorder().bottom());
    thumbStyleBuilder.appendTrustedString("top:" + thumbTop + "px;");
    thumbStyleBuilder.appendTrustedString("margin-left:" + halfThumbWidth + "px;");


    SafeStylesBuilder thumbCenterStyleBuilder = new SafeStylesBuilder();
    updateThumbCenterStyle(thumbCenterStyleBuilder, "left", "width", thumbWidth,
        fieldDetails.slider().thumbBorder().left(), fieldDetails.slider().thumbBorder().right());
    updateThumbCenterStyle(thumbCenterStyleBuilder, "top", "height", thumbHeight,
        fieldDetails.slider().thumbBorder().top(), fieldDetails.slider().thumbBorder().bottom());

    sb.append(template.render(resources.style(), sliderStyleBuilder.toSafeStyles(),
        trackStyleBuilder.toSafeStyles(), thumbStyleBuilder.toSafeStyles(), thumbCenterStyleBuilder.toSafeStyles()));
  }

  @Override
  public void setThumbPosition(Element parent, int pos) {
    XElement thumbElement = XElement.as(getThumb(parent));
    int halfThumbSize = resources.theme().field().slider().thumbWidth() / 2;
    pos = Math.max(-halfThumbSize, pos);
    thumbElement.getStyle().setLeft(pos, Unit.PX);
  }

  protected XElement getTrack(Element parent) {
    return parent.<XElement>cast().selectNode("." + style.track());
  }

  protected void updateThumbCenterStyle(SafeStylesBuilder style, String position, String dimension, int full, int border1, int border2) {
    int remainingArea = full - (border1 + border2);
    int margin = remainingArea / 4;
    int size = remainingArea - (margin * 2);

    style.appendTrustedString(position + ":" + margin + "px;");
    style.appendTrustedString(dimension + ":" + size + "px;");
  }
}
