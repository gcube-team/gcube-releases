package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IComplexData;

public class PngFileDataBinding implements IComplexData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 625383192227478620L;
	protected GenericFileData payload; 
	
	public PngFileDataBinding(GenericFileData fileData){
		this.payload = fileData;
	}
	
	public GenericFileData getPayload() {
		return payload;
	}

	public Class<String> getSupportedClass() {
		return String.class;
	}
    
    @Override
	public void dispose(){
	}
}
