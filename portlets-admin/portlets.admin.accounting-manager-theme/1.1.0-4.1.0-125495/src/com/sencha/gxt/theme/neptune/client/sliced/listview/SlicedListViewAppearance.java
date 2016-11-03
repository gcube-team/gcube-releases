/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.listview;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.neptune.client.base.listview.Css3ListViewAppearance;

public class SlicedListViewAppearance<M> extends Css3ListViewAppearance<M> {

  public interface SlicedListViewResources extends Css3ListViewResources {
    @Override
    @Source("SlicedListView.css")
    SlicedListViewStyle css();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("item.png")
    ImageResource itemBackground();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("itemselected.png")
    ImageResource itemSelectedBackground();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("itemover.png")
    ImageResource itemOverBackground();
  }

  public interface SlicedListViewStyle extends Css3ListViewStyle {

  }

  public SlicedListViewAppearance() {
    super(GWT.<SlicedListViewResources>create(SlicedListViewResources.class));
  }
}
