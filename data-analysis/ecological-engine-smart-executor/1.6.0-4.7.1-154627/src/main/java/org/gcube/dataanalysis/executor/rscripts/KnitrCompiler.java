package org.gcube.dataanalysis.executor.rscripts;



import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.executor.rscripts.generic.GenericRScript;

public class KnitrCompiler extends GenericRScript {
	
	@Override
	public String getDescription() {
		return "An algorithm to compile Knitr documents. Developed by IRD (reference Julien Bard, julien.barde@ird.fr)";
	}
	
	protected void initVariables(){
		mainScriptName="IRDTunaAtlas-master/report/knitr/compileKnitR_CNR.R";
		packageURL="http://goo.gl/T7V8LV";
		
		environmentalvariables = new ArrayList<String>();
		inputvariables.add("zipfile");
		inputvariables.add("file.inout");
		outputvariables.add("pdfresult");		
	}
	
	@Override
	protected void setInputParameters() {
		inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, "zipfile", "The file containing R and the markdown (Rnw) files to compile","knitr_wfs.zip"));
		inputs.add(new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "file.inout", "The name of the R file in the zip package", "main.r"));
	}
	
	@Override
	public StatisticalType getOutput() {
		output.put("pdfresult",new PrimitiveType(File.class.getName(), new File(outputValues.get("pdfresult")), PrimitiveTypes.FILE, "pdfresult", "The compiled PDF file"));
		PrimitiveType o = new PrimitiveType(LinkedHashMap.class.getName(), output, PrimitiveTypes.MAP, "Output", "");
		return o;
	}
	
}
