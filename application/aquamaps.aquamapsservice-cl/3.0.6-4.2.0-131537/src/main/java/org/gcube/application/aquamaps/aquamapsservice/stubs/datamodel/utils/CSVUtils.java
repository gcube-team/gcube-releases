package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;
import net.sf.csv4j.CSVWriter;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVUtils {

	private static final Logger logger = LoggerFactory.getLogger(CSVUtils.class);
	
	public static ArrayList<String> CSVToStringList(String theString){
		ArrayList<String> toReturn= new ArrayList<String>();
		if(theString!=null)
			for(String s:theString.split(","))
				if(s!=null&&!s.equals("")&&!s.equals(" "))toReturn.add(s.trim());
		return toReturn;
	}

	public static ArrayList<Integer> CSVTOIntegerList(String theString){
		ArrayList<Integer> toReturn= new ArrayList<Integer>();
		if(theString!=null)
			for(String s:theString.split(","))
				if(s!=null&&!s.equals("")&&!s.equals(" "))toReturn.add(Integer.parseInt(s.trim()));
		return toReturn;
	}
	
	public static String listToCSV(List values){
		StringBuilder toReturn=new StringBuilder();
		if((values!=null)&&(values.size()>0)){
			for(Object v:values)
				if(v!=null&&!v.equals("")&&!v.equals(" "))toReturn.append(v.toString().trim()+",");
			if(toReturn.lastIndexOf(",")>-1)toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		}
		return toReturn.toString();
	}

	public static long resultSetToCSVFile(ResultSet rs, String outFile, boolean writeHeaders)throws IOException,SQLException{
		final FileWriter fileWriter = new FileWriter(outFile);
		final CSVWriter csvWriter = new CSVWriter( fileWriter );	
		
		//csvWriter.writeLine( new String[] { "column1", "column2", "column3" } );
		ResultSetMetaData meta=rs.getMetaData();
		if(writeHeaders){
			List<String> metaFields=new ArrayList<String>();
			for(int i=1;i<=meta.getColumnCount();i++)
				metaFields.add(meta.getColumnName(i));
			csvWriter.writeLine(metaFields);
		}
		
		
		
		logger.trace("Writing record values ...");
		long count = 0;
		rs.beforeFirst();
		while(rs.next()){
			String[] record= new String[meta.getColumnCount()];
			for(int column=0;column<record.length;column++){
				String value=rs.getString(column+1);
				record[column]=(value!=null)?value:"null";
				//record[column]=value;
			}
			csvWriter.writeLine(record);
			count++;
		}
		fileWriter.close();
		if((new File(outFile)).exists())
			logger.trace("Wrote "+count+" records");
		else logger.error("File "+outFile+" not created");
		return count;
	}
	public static List<List<Field>> loadCSV(String path,char delimiter)throws Exception{
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(delimiter);
		processor.setHasHeader(true);
		final List<List<Field>> toReturn=new ArrayList<List<Field>>();
		final List<String> headers=new ArrayList<String>();
		Reader reader= new InputStreamReader(new FileInputStream(path), Charset.defaultCharset());
		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				List<Field> line= new ArrayList<Field>();
				for(int i=0;i<headers.size();i++)
					line.add(new Field(headers.get(i),arg1.get(i),FieldType.STRING));
				toReturn.add(line);
			}
			public void processHeaderLine(int arg0, List<String> arg1) {
				headers.addAll(arg1);
			}});
	
		return toReturn;
	}
	
	public static Long countCSVRows(String path,char delimiter,boolean hasHeaders)throws Exception{
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(delimiter);
		processor.setHasHeader(hasHeaders);
		final List<Long> counters=new ArrayList<Long>();
		counters.add(0l);
		Reader reader= new InputStreamReader(new FileInputStream(path), Charset.defaultCharset());
		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				counters.set(0, counters.get(0)+1);
			}
			public void processHeaderLine(int arg0, List<String> arg1) {				
			}});
	
		return counters.get(0);
	}
	
}
