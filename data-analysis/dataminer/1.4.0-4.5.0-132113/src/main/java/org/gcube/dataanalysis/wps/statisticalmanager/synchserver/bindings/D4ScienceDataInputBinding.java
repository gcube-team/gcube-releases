package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IComplexData;

public class D4ScienceDataInputBinding implements IComplexData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 625383192227478620L;
	protected GenericFileData payload; 
	
	public D4ScienceDataInputBinding(GenericFileData fileData){
		this.payload = fileData;
	}
	
	public GenericFileData getPayload() {
		return payload;
	}

	public Class<GenericFileData> getSupportedClass() {
		return GenericFileData.class;
	}
    
    @Override
	public void dispose(){
	}
}
