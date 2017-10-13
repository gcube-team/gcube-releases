package org.gcube.dataanalysis.statistical_manager_wps_algorithms.output;

import java.util.ArrayList;

import org.n52.wps.io.data.IComplexData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

public class LiteralArrayBinding implements IComplexData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<LiteralStringBinding> payload= new ArrayList<LiteralStringBinding>();
 	public Object getPayload() {
		return payload;
	}
 	
 	
 	public LiteralArrayBinding (ArrayList<LiteralStringBinding> payload)
 	{
 		this.payload=payload;
 	}

	public Class getSupportedClass() {
		// TODO Auto-generated method stub
		return payload.getClass();
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}
