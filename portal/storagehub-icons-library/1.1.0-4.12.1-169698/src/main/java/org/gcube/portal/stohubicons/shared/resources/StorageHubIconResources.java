/**
 * 
 */
package org.gcube.portal.stohubicons.shared.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public interface StorageHubIconResources extends ClientBundle {
	public static final StorageHubIconResources INSTANCE =  GWT.create(StorageHubIconResources.class);
	

	@Source("CATEGORY.png")
	ImageResource CATEGORY();
	
	@Source("HISTORY.png")
	ImageResource HISTORY();
	
	@Source("UNARCHIVE.png")
	ImageResource UNARCHIVE();
	
	@Source("INFO.png")
	ImageResource INFO();
	
	@Source("UNSHARE.png")
	ImageResource UNSHARE();
	
	@Source("GROUP.png")
	ImageResource GROUP();
	
	@Source("USER.png")
	ImageResource USER();
	
	@Source("FOLDER_LINK.png")
	ImageResource FOLDER_LINK();
	
	@Source("GET_LINK.png")
	ImageResource GET_LINK();

	@Source("PUBLIC_LINK.png")
	ImageResource PUBLIC_LINK();
	
	@Source("OPEN.png")
	ImageResource OPEN();
	
	@Source("PREVIEW.png")
	ImageResource PREVIEW();
	
	@Source("VERSIONS.png")
	ImageResource VERSIONS();
	
	@Source("PASTE.png")
	ImageResource PASTE();
	
	@Source("COPY.png")
	ImageResource COPY();
		
	@Source("EDIT.png")
	ImageResource EDIT();
	
	@Source("DELETE.png")
	ImageResource DELETE();
	
	@Source("RELOAD.png")
	ImageResource RELOAD();
	
	@Source("BIN.png")
	ImageResource BIN();
	
	@Source("SEARCH.png")
	ImageResource SEARCH();
	

	@Source("DOWNLOAD.png")
	ImageResource FILE_DOWNLOAD();
	
	@Source("FILE_UPLOAD.png")
	ImageResource FILE_UPLOAD();
	
	@Source("USER_INFO.png")
	ImageResource USER_CIRCLE();
	
	@Source("FOLDER.png")
	ImageResource FOLDER();
	
	@Source("SHARED_FOLDER.png")
	ImageResource SHARED_FOLDER();
	
	@Source("SHARED_FOLDER.png")
	ImageResource VRE_FOLDER();
	
	@Source("FOLDER_SPECIAL.png")
	ImageResource FOLDER_SPECIAL();
	
	@Source("NEW_FOLDER.png")
	ImageResource NEW_FOLDER();
	
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
