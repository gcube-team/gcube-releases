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
import com.sencha.gxt.cell.core.client.form.DateCell.DateCellAppearance;

/**
 *
 */
public class Css3DateCellAppearance extends Css3TriggerFieldAppearance implements DateCellAppearance {

  public interface Css3DateCellResources extends Css3TriggerFieldResources {
    @Override
    @Source({"Css3ValueBaseField.css", "Css3TextField.css", "Css3TriggerField.css"})
    Css3DateCellStyle style();

    @Override
    @Source("dateTrigger.png")
    ImageResource triggerArrow();

    @Override
    @Source("dateTriggerOver.png")
    ImageResource triggerArrowOver();

    @Override
    @Source("dateTriggerClick.png")
    ImageResource triggerArrowClick();
  }

  public interface Css3DateCellStyle extends Css3TriggerFieldStyle {

  }

  public Css3DateCellAppearance() {
    this(GWT.<Css3DateCellResources>create(Css3DateCellResources.class));
  }

  public Css3DateCellAppearance(Css3DateCellResources resources) {
    super(resources);
  }
}
