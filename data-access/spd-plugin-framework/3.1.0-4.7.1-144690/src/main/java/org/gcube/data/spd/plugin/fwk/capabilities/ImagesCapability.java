package org.gcube.data.spd.plugin.fwk.capabilities;


import org.gcube.data.spd.model.products.Image;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public interface ImagesCapability {

	public void getImagesById(ObjectWriter<Image> writer, String ... ids) throws Exception;
	
}
