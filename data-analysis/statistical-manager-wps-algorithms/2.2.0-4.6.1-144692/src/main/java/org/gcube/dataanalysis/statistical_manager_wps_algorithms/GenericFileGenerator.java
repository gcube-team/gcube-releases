package org.gcube.dataanalysis.statistical_manager_wps_algorithms;

import java.io.IOException;
import java.io.InputStream;

import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;

public class GenericFileGenerator extends AbstractGenerator {
	
	public GenericFileGenerator (){
		super();
		supportedIDataTypes.add(GenericFileDataBinding.class);
	}
	
	public InputStream generateStream(IData data, String mimeType, String schema) throws IOException {
		
		InputStream theStream = ((GenericFileDataBinding)data).getPayload().getDataStream();
		return theStream;
	}
	
	/**
	 * conversion method to support translation of output formats
	 * TODO: implement logic
	 * 
	 * @param inputFile
	 * @return  
	 */
	@SuppressWarnings("unused")
	private GenericFileData convertFile (GenericFileData inputFile){
		//not implemented
		return null;
	}
	
}
