package org.gcube.portlets.user.td.resourceswidget.client.resources;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface ResourceBundle extends ClientBundle {
	
	public static final ResourceBundle INSTANCE=GWT.create(ResourceBundle.class);
	
	@Source("testImage.jpg")
	ImageResource testImage();
	
	
	@Source("arrow-refresh_16.png")
	ImageResource refresh16();
	
	@Source("arrow-refresh_24.png")
	ImageResource refresh24();
	
	
	@Source("arrow-refresh_32.png")
	ImageResource refresh32();
	
	@Source("magnifier.png")
	ImageResource magnifier();
	
	@Source("magnifier_32.png")
	ImageResource magnifier32();
	
	@Source("close-red.png")
	ImageResource close();
	
	@Source("close-red_32.png")
	ImageResource close32();
	
	@Source("disk.png")
	ImageResource save();
	
	@Source("disk_32.png")
	ImageResource save32();

	@Source("add.png")
	ImageResource add();
	
	@Source("add_32.png")
	ImageResource add32();
	
	@Source("delete.png")
	ImageResource delete();
	
	@Source("delete_32.png")
	ImageResource delete32();

	@Source("chart-bar.png")
	ImageResource chart();
	
	@Source("chart-bar_24.png")
	ImageResource chart24();
	
	@Source("chart-bar_32.png")
	ImageResource chart32();
	
	@Source("chart-bar_80.png")
	ImageResource chart80();
	
	@Source("chart-bar_160.png")
	ImageResource chart160();
	
	@Source("codelist.png")
	ImageResource codelist();
	
	@Source("codelist_24.png")
	ImageResource codelist24();
	
	@Source("codelist_32.png")
	ImageResource codelist32();
	
	@Source("codelist_80.png")
	ImageResource codelist80();
	
	@Source("codelist_160.png")
	ImageResource codelist160();
	
	@Source("csv.png")
	ImageResource csv();
	
	@Source("csv_24.png")
	ImageResource csv24();
	
	@Source("csv_32.png")
	ImageResource csv32();
	
	@Source("csv_80.png")
	ImageResource csv80();
	
	@Source("csv_160.png")
	ImageResource csv160();
	
	@Source("gis.png")
	ImageResource gis();
	
	@Source("gis_24.png")
	ImageResource gis24();
	
	@Source("gis_32.png")
	ImageResource gis32();
	
	@Source("gis_80.png")
	ImageResource gis80();
	
	@Source("gis_160.png")
	ImageResource gis160();
	
	@Source("json.png")
	ImageResource json();
	
	@Source("json_24.png")
	ImageResource json24();
	
	@Source("json_32.png")
	ImageResource json32();
	
	@Source("json_80.png")
	ImageResource json80();
	
	@Source("json_160.png")
	ImageResource json160();
	
	@Source("sdmx.png")
	ImageResource sdmx();
	
	@Source("sdmx_24.png")
	ImageResource sdmx24();
	
	@Source("sdmx_32.png")
	ImageResource sdmx32();
	
	@Source("sdmx_80.png")
	ImageResource sdmx80();
	
	@Source("sdmx_160.png")
	ImageResource sdmx160();
	
	@Source("world_24.png")
	ImageResource world24();
	
	@Source("world_32.png")
	ImageResource world32();
	
	@Source("world_80.png")
	ImageResource world80();
	
	@Source("world_160.png")
	ImageResource world160();
	
	@Source("table.png")
	ImageResource table();
	
	@Source("table_24.png")
	ImageResource table24();
	
	@Source("table_32.png")
	ImageResource table32();
	
	@Source("table_80.png")
	ImageResource table80();
	
	@Source("table_160.png")
	ImageResource table160();
	
	@Source("resources.png")
	ImageResource resources();
	
	@Source("resources_24.png")
	ImageResource resources24();

	@Source("resources_32.png")
	ImageResource resources32();
	
	@Source("resources_80.png")
	ImageResource resources80();
	
	@Source("resources_160.png")
	ImageResource resources160();
	
	@Source("file.png")
	ImageResource file();
	
	@Source("file_24.png")
	ImageResource file24();
	
	@Source("file_32.png")
	ImageResource file32();
	
	@Source("file_80.png")
	ImageResource file80();
	
	@Source("file_160.png")
	ImageResource file160();
	
	@Source("picture.png")
	ImageResource picture();
	
	@Source("picture_24.png")
	ImageResource picture24();
	
	@Source("picture_32.png")
	ImageResource picture32();
	
	@Source("picture_80.png")
	ImageResource picture80();
	
	@Source("picture_160.png")
	ImageResource picture160();
	
	
	@Source("magnifier-zoom-in_32.png")
	ImageResource magnifierZoomIn32();
	
	@Source("magnifier-zoom-in.png")
	ImageResource magnifierZoomIn();
	
	@Source("magnifier-zoom-out_32.png")
	ImageResource magnifierZoomOut32();
	
	@Source("magnifier-zoom-out.png")
	ImageResource magnifierZoomOut();
	
	@Source("arrow-move_32.png")
	ImageResource move32();
	
	@Source("arrow-move.png")
	ImageResource move();
	
	@Source("application_32.png")
	ImageResource application32();
	
	@Source("application.png")
	ImageResource application();	
	
	@Source("Resources.css")
	ResourceCSS resourceCSS();
}
 