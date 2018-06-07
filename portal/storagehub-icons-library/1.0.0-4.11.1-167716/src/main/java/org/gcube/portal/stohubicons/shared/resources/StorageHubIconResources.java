/**
 * 
 */
package org.gcube.portal.stohubicons.shared.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public interface StorageHubIconResources extends ClientBundle {
	public static final StorageHubIconResources INSTANCE =  GWT.create(StorageHubIconResources.class);

	@Source("FOLDER.png")
	ImageResource FOLDER();
	
	@Source("SHARED_FOLDER.png")
	ImageResource SHARED_FOLDER();
	
	@Source("SHARED_FOLDER.png")
	ImageResource VRE_FOLDER();
	
	@Source("DOC.png")
	ImageResource DOC();
	
	@Source("PPTX.png")
	ImageResource PPT();
	
	@Source("XLS.png")
	ImageResource XLS();
	
	@Source("XML.png")
	ImageResource XML();
	
	@Source("SH.png")
	ImageResource SCRIPT();
	
	@Source("CSV.png")
	ImageResource CSV();
	
	@Source("CALENDAR.png")
	ImageResource CALENDAR();

	@Source("PDF.png")
	ImageResource PDF();
	
	@Source("CODE.png")
	ImageResource CODE();

	@Source("IMAGE.png")
	ImageResource IMAGE();
	
	@Source("TXT.png")
	ImageResource TXT();
	
	@Source("MOVIE.png")
	ImageResource MOVIE();
	
	@Source("HTML.png")
	ImageResource HTML();	
	
	@Source("ARCHIVE.png")
	ImageResource ARCHIVE();	

	@Source("LINK.png")
	ImageResource LINK();
	
	@Source("metadata.png")
	ImageResource metadata();

	@Source("timeseries.png")
	ImageResource timeseries();
	
	@Source("aquamaps.png")
	ImageResource aquamaps();	
	
	@Source("UNKNOWN.png")
	ImageResource unknownType();	
	
	@Source("gcubeItem.jpeg")
	ImageResource gucbeItem();
	
	@Source("UNKNOWN.png")
	ImageResource unknown();
}
