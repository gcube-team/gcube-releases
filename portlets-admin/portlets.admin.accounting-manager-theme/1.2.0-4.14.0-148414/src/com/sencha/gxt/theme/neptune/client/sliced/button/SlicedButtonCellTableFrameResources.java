/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.sliced.button;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.button.ButtonTableFrameResources;
import com.sencha.gxt.theme.base.client.frame.TableFrame.TableFrameStyle;

/**
 *
 */
public interface SlicedButtonCellTableFrameResources extends SlicedToolBarButtonCellTableFrameResources, ButtonTableFrameResources {
  public interface TableFrameStyleSliced extends TableFrameStyle {
  }

  @Override
  @Source({"com/sencha/gxt/theme/base/client/frame/TableFrame.css", "SlicedButtonTableFrame.css", "SlicedToolBarButtonTableFrame.css"})
  TableFrameStyleSliced style();

  @Override
  @Source("button-background.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource background();

  @Override
  @Source("button-over-background.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource backgroundOverBorder();

  @Override
  @Source("button-pressed-background.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource backgroundPressedBorder();

  @Override
  @Source("button-tl.png")
  ImageResource topLeftBorder();

  @Override
  @Source("button-over-tl.png")
  ImageResource topLeftOverBorder();

  @Override
  @Source("button-pressed-tl.png")
  ImageResource topLeftPressedBorder();

  @Override
  @Source("button-tc.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource topBorder();

  @Override
  @Source("button-over-tc.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource topOverBorder();

  @Override
  @Source("button-pressed-tc.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource topPressedBorder();

  @Override
  @Source("button-tr.png")
  ImageResource topRightBorder();

  @Override
  @Source("button-over-tr.png")
  ImageResource topRightOverBorder();

  @Override
  @Source("button-pressed-tr.png")
  ImageResource topRightPressedBorder();

  @Override
  @Source("button-l.png")
  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource leftBorder();

  @Override
  @Source("button-over-l.png")
  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource leftOverBorder();

  @Override
  @Source("button-pressed-l.png")
  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource leftPressedBorder();

  @Override
  @Source("button-r.png")
  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource rightBorder();

  @Override
  @Source("button-over-r.png")
  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource rightOverBorder();

  @Override
  @Source("button-pressed-r.png")
  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource rightPressedBorder();

  @Override
  @Source("button-bl.png")
  ImageResource bottomLeftBorder();

  @Override
  @Source("button-over-bl.png")
  ImageResource bottomLeftOverBorder();

  @Override
  @Source("button-pressed-bl.png")
  ImageResource bottomLeftPressedBorder();


  @Override
  @Source("button-bc.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource bottomBorder();

  @Override
  @Source("button-over-bc.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource bottomOverBorder();

  @Override
  @Source("button-pressed-bc.png")
  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource bottomPressedBorder();

  @Override
  @Source("button-br.png")
  ImageResource bottomRightBorder();

  @Override
  @Source("button-over-br.png")
  ImageResource bottomRightOverBorder();

  @Override
  @Source("button-pressed-br.png")
  ImageResource bottomRightPressedBorder();
}
