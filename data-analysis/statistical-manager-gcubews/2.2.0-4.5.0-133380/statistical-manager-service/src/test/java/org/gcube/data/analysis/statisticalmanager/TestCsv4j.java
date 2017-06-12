package org.gcube.data.analysis.statisticalmanager;

import java.io.IOException;
import java.util.List;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.DataTypeRecognizer;
import org.gcube.data.analysis.statisticalmanager.dataspace.importer.CSVLineProcessorGeneric;
import net.sf.csv4j.CSVFileProcessor;
import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;



public class TestCsv4j {
	
	private static final String DRIVER = "org.postgresql.Driver";
	
	public static void main(String[] args) throws ParseException, IOException, ProcessingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		DataTypeRecognizer dataType = null;

		final CSVFileProcessor fp = new CSVFileProcessor();
		
		CSVLineProcessorGeneric lineProcessor = new CSVLineProcessorGeneric(",");
		fp.setHasHeader(true);
		fp.setComment(',');
		fp.processFile( "/home/fabio/Downloads/Latimeria_chalumnae_scientific_name_occurrences_from_OBISGBIF_-_CSV_STANDARD.csv",lineProcessor );
		
		
		List<String> cols = lineProcessor.getColsName();
		if (cols.isEmpty()) {
			for (int index = 1; index < lineProcessor.getSqlType().size(); index ++) {
				cols.add("Field"+index);
			}			
		}
	
		System.out.println(cols);
		System.out.println(lineProcessor.getSqlType());
		String arg = "";
		int i = 0;
		for (String colName : cols){
			arg+=String.format("%s %s,", colName, lineProcessor.getSqlType().get(i++));
		}
		String sql = String.format("CREATE TABLE %s ( %s )", "TableName", arg.substring(0, arg.length() - 1));
		System.out.println(sql);
	}

}
