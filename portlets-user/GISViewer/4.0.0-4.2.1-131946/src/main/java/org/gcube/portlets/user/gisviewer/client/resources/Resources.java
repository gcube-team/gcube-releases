package org.gcube.portlets.user.gisviewer.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

	@Source("atompub.png")
	ImageResource iconAtompub();

	@Source("georss.gif")
	ImageResource iconGeorss();

	@Source("gif.gif")
	ImageResource iconGif();

	@Source("jpeg.gif")
	ImageResource iconJpeg();

	@Source("kml.png")
	ImageResource iconKml();

	@Source("pdf.gif")
	ImageResource iconPdf();

	@Source("png.gif")
	ImageResource iconPng();

	@Source("svg.png")
	ImageResource iconSvg();

	@Source("tiff.gif")
	ImageResource iconTiff();

	@Source("export.gif")
	ImageResource iconExport();

	@Source("filter.gif")
	ImageResource iconFilter();

	@Source("table_ico.gif")
	ImageResource iconTable();

	@Source("refresh_ico.gif")
	ImageResource iconRefresh();

	@Source("wave.png")
	ImageResource iconTransect();

	@Source("legend.png")
	ImageResource iconLegend();

	@Source("triangle-right.png")
	ImageResource iconTriangleRight();

	@Source("triangle-down.png")
	ImageResource iconTriangleDown();

	@Source("saveLayer.png")
	ImageResource iconSave();

	@Source("arrowBlueRight.png")
	ImageResource iconArrowBlueRight();

	@Source("remove.png")
	ImageResource iconRemoveCqlFilter();

	@Source("cancel_icon.png")
	ImageResource iconToolbarRemove();

	@Source("cqlFilterTip.png")
	ImageResource iconCqlTip();

	@Source("cqlFilterTipDelete.png")
	ImageResource iconCqlTipDelete();

	@Source("transectTip.png")
	ImageResource iconTransectTip();

	@Source("transectTipDelete.png")
	ImageResource iconTransectTipDelete();

	@Source("close.png")
	ImageResource iconCloseLayer();

	@Source("closeOver.png")
	ImageResource iconCloseLayerOver();


	@Source("icon_zoomfull2.png")
	ImageResource iconMaxExtent();

	@Source("zoom_in.png")
	ImageResource iconZoomIn();

	@Source("zoom_out.png")
	ImageResource iconZoomOut();

	@Source("hand.png")
	ImageResource iconPan();

	@Source("info_icon.gif")
	ImageResource iconClickData();

	@Source("selection.gif")
	ImageResource iconBoxData();

	@Source("no_legend_available.png")
	ImageResource noLegendAvailable();

	@Source("loading_spinner.gif")
	ImageResource loadingImg();

	@Source("GisViewerIntro.html")
	TextResource gisViewerIntro();

	/**
	 * @return
	 */
	@Source("gisviewer-icon.png")
	ImageResource gisViewerIcon();


	@Source("baselayer.txt")
	TextResource baseLayer();
}
