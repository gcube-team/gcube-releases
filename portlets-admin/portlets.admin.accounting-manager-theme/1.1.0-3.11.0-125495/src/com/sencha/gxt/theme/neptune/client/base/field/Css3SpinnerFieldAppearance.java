/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell.SpinnerFieldAppearance;

/**
 *
 */
public class Css3SpinnerFieldAppearance extends Css3TwinTriggerFieldAppearance implements SpinnerFieldAppearance {
  public interface Css3SpinnerFieldResources extends Css3TwinTriggerFieldResources {
    @Override
    @Source({"Css3ValueBaseField.css", "Css3TextField.css", "Css3TriggerField.css", "Css3SpinnerField.css"})
    Css3SpinnerFieldStyle style();

    @Override
    @Source("spinnerUp.png")
    ImageResource triggerArrow();

    @Override
    @Source("spinnerUpOver.png")
    ImageResource triggerArrowOver();

    @Override
    @Source("spinnerUpClick.png")
    ImageResource triggerArrowClick();

    @Source("spinnerDown.png")
    public ImageResource twinTriggerArrow();

    @Source("spinnerDownOver.png")
    public ImageResource twinTriggerArrowOver();

    @Source("spinnerDownClick.png")
    public ImageResource twinTriggerArrowClick();
  }

  public interface Css3SpinnerFieldStyle extends Css3TwinTriggerFieldStyle {

  }


  private final Css3SpinnerFieldResources resources;

  public Css3SpinnerFieldAppearance() {
    this(GWT.<Css3SpinnerFieldResources>create(Css3SpinnerFieldResources.class));
  }

  public Css3SpinnerFieldAppearance(Css3SpinnerFieldResources resources) {
    super(resources);

    this.resources = resources;
  }

  @Override
  protected int getTriggerWrapHeight() {
    return resources.triggerArrow().getHeight() + resources.twinTriggerArrow().getHeight();
  }
}
