/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.GwtCreateResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.sencha.gxt.cell.core.client.SliderCell.HorizontalSliderAppearance;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.slider.SliderHorizontalBaseAppearance;
import com.sencha.gxt.theme.neptune.client.SliderDetails;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

/**
 *
 */
public class SlicedHorizontalSliderAppearance extends SliderHorizontalBaseAppearance implements HorizontalSliderAppearance {
  public interface SlicedHorizontalSliderResources extends SliderHorizontalResources, ClientBundle {
    @Override
    @Source({"com/sencha/gxt/theme/base/client/slider/Slider.css",
        "com/sencha/gxt/theme/base/client/slider/SliderHorizontal.css", "SlicedHorizontalSlider.css"})
    SlicedHorizontalSliderStyle style();

    GwtCreateResource<ThemeDetails> theme();


    @Source("thumb.png")
    ImageResource thumbHorizontal();

    @Source("thumbClick.png")
    ImageResource thumbHorizontalDown();

    @Source("thumbOver.png")
    ImageResource thumbHorizontalOver();

    @Source("trackHorizontalLeft.png")
    ImageResource trackHorizontalLeft();

    @Source("trackHorizontalRight.png")
    ImageResource trackHorizontalRight();

    @Source("trackHorizontalMiddle.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource trackHorizontalMiddle();
  }

  public interface SlicedHorizontalSliderStyle extends SliderHorizontalStyle, CssResource {

  }

  private final SlicedHorizontalSliderResources resources;

  public SlicedHorizontalSliderAppearance() {
    this(GWT.<SlicedHorizontalSliderResources>create(SlicedHorizontalSliderResources.class),
        GWT.<SliderHorizontalTemplate>create(SliderHorizontalTemplate.class));
  }

  public SlicedHorizontalSliderAppearance(SlicedHorizontalSliderResources resources, SliderHorizontalTemplate template) {
    super(resources, template);

    this.resources = resources;
  }


  @Override
  public int getSliderLength(XElement parent) {
    SliderDetails sliderDetails = resources.theme().create().field().slider();
    return super.getSliderLength(parent) + getTrackPadding() - (sliderDetails.thumbBorder().right() + sliderDetails.thumbBorder().left());
  }

  @Override
  public void setThumbPosition(Element parent, int pos) {
    XElement thumbElement = XElement.as(getThumb(parent));
    pos -= getHalfThumbWidth();
    pos = Math.max(-getHalfThumbWidth(), pos);
    thumbElement.getStyle().setLeft(pos, Unit.PX);
  }

  @Override
  protected SafeStyles createThumbStyles(double fractionalValue, int width) {
    SafeStylesBuilder thumbStylesBuilder = new SafeStylesBuilder();

    int offset = (int) (fractionalValue * width) - getHalfThumbWidth();
    offset = Math.max(-getHalfThumbWidth(), offset);
    offset = Math.min(width - resources.thumbHorizontal().getWidth(), offset);

    thumbStylesBuilder.appendTrustedString("left:" + offset + "px;");
    int thumbTop = (resources.trackHorizontalMiddle().getHeight() / 2) - (resources.thumbHorizontal().getHeight() / 2);

    thumbStylesBuilder.appendTrustedString("top:" + thumbTop + "px;");

    return thumbStylesBuilder.toSafeStyles();
  }

  @Override
  protected int getHalfThumbWidth() {
    return (int) Math.floor(resources.thumbHorizontal().getWidth() / 2);
  }

  @Override
  protected int getTrackPadding() {
    return resources.trackHorizontalLeft().getWidth();
  }
}
