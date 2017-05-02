package org.gcube.dataanalysis.executor.rscripts;



import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.utils.DynamicEnum;
import org.gcube.dataanalysis.executor.rscripts.generic.GenericRScript;

public class TemplateRScripts extends GenericRScript {
	
	
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
	
	
	//Op12345 e Eop12345 are two automatically generated names - Use UUID
	static class Op12345 extends DynamicEnum {
		public enum Eop12345 {};
		public Field[] getFields() {
			Field[] fields = Eop12345.class.getDeclaredFields();
			return fields;
		}
	}
	
	@Override
	protected void setInputParameters() {
		if (org.gcube.dataanalysis.executor.rscripts.TemplateRScripts.Op12345.Eop12345.values().length==0){
			Op12345 en = new Op12345();
			en.addEnum(org.gcube.dataanalysis.executor.rscripts.TemplateRScripts.Op12345.Eop12345.class, "CIAO");
			en.addEnum(org.gcube.dataanalysis.executor.rscripts.TemplateRScripts.Op12345.Eop12345.class, "TEST");
			en.addEnum(org.gcube.dataanalysis.executor.rscripts.TemplateRScripts.Op12345.Eop12345.class, "MIAO *_$");
		}
		//Add Enumerate Type
		addEnumerateInput(org.gcube.dataanalysis.executor.rscripts.TemplateRScripts.Op12345.Eop12345.values(), "Name", "Description", "Hello");
		
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
