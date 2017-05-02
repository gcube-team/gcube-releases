package seadatanetconnector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;

public class prova extends StandardLocalExternalAlgorithm {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Creazione file";
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		
	}

	//File outputfile;
	@Override
	protected void process() throws Exception {
		status=0;
		/*
		String name= getInputParameter("fileName");
		AnalysisLogger.getLogger().debug("Received File: "+name);
		File outfile = new File(name);
		//usare per scrivere più veloce
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		
		writer.write("Il nome del file è :"+name+"\n");
		
		writer.close();
		outputfile = outfile;
	*/
		status=100;
	}

	@Override
	protected void setInputParameters() {
		addStringInput("fileName", "inserire nome file", "file");
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	public StatisticalType getOutput(){
			/*PrimitiveType file = new PrimitiveType(File.class.getName(),
					outputfile, 
					PrimitiveTypes.FILE, 
					"OutputFile", 
					"nome del file");
			
		return file;
		*/
		return null;
	}

}
