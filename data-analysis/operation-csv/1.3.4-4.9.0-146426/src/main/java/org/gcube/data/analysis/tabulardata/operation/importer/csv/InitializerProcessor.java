package org.gcube.data.analysis.tabulardata.operation.importer.csv;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InitializerProcessor implements CSVLineProcessor {

	private Logger logger = LoggerFactory.getLogger(InitializerProcessor.class);

	private boolean tableCreated = false;

	private int rows = 0;

	private List<Column> columns = new ArrayList<Column>();

	private List<Boolean> fieldMask;

	private int headerSize;

	private File outputFile;

	private static final char tempSeparator=',';
	private static final char tempQuoting='"';

	int[] fieldlenghts;
	
	private OutputStreamWriter streamWriter;

	public InitializerProcessor(List<Boolean> fieldMask) throws IOException {
		this.fieldMask = fieldMask;
		this.outputFile = File.createTempFile("modifiedFileImport",".csv" );
		this.streamWriter = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)), "UTF-8");
	}

	public void processHeaderLine(int lineNumber, List<String> fields) {
		this.onHeaderLine(lineNumber, fields);
	}

	public void processDataLine(int lineNumber, List<String> fields) {
		this.onDataLine(lineNumber, fields);
	}

	public boolean continueProcessing() {
		//return (!tableCreated || skipError);
		return true;
	}

	public void onHeaderLine(int lineNumber, List<String> fields) {
		if (fieldMask!=null && fieldMask.size()!=fields.size())
			throw new IllegalArgumentException("a wrong fieldMask have been passed as argument");
		try{
			//creating the others fields
			for (int i=0; i<fields.size(); i++){
				//logger.debug("the max for field"+(k)+" is "+fields.size());
				if (fieldMask==null || fieldMask.get(i)){
					Column column = null;
					if (fields.get(i)!=null && fields.get(i)!="") column = new AttributeColumnFactory().create(new ImmutableLocalizedText(fields.get(i)), new TextType()); 
					else column = new AttributeColumnFactory().create(new ImmutableLocalizedText("field"+i), new TextType()); 
					columns.add(column);
				}
			}
			tableCreated=true;
			headerSize = fields.size();
		}catch (Exception e) {
			logger.error("erorr reading the header line",e);
		}
	}


	public void onDataLine(int lineNumber, List<String> fields) {
		if (fieldMask!=null && fieldMask.size()!=fields.size()) return;
		if (!tableCreated)
			try{
				for (int i=0; i<fields.size(); i++){
					if (fieldMask==null || fieldMask.get(i)){
						//logger.debug("the max for field"+(k)+" is "+fields.size());
						Column column = new AttributeColumnFactory().create(new ImmutableLocalizedText("field"+i), new TextType()); 
						List<LocalizedText> names = new ArrayList<LocalizedText>();
						names.add(new ImmutableLocalizedText("field"+i, "en"));
						NamesMetadata nMeta = new NamesMetadata(names);
						column.setMetadata(nMeta);
						columns.add(column);
					}
				}
				tableCreated=true;
				headerSize = fields.size();
			}catch (Exception e) {
				logger.error("error reading data line", e);
			}

		if (fields.size()!=headerSize){
			logger.trace("the line "+lineNumber+" is not correct ("+fields.size()+"<>"+headerSize+")");
			return;
		}
						
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i<fields.size(); i++)
			if (fieldMask==null || fieldMask.get(i)){
				String value = fields.get(i);
				buffer.append(tempQuoting+value.replace("\"", "\"\"" )+tempQuoting+tempSeparator);
			}
		try {
			String toAdd = buffer.substring(0, buffer.length()-1)+'\n';
			streamWriter.write(toAdd);
		} catch (IOException e) {
			logger.warn("error writing line",e);
		}
	}
		
	public int[] getFieldlenghts() {
		return fieldlenghts;
	}

	public int getRows() {
		return rows;
	}

	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * @return the outputFile
	 */
	protected File getOutputFile() {
		return outputFile;
	}

	public void close(){
		try {
			if (streamWriter!=null)
				streamWriter.close();
		} catch (IOException e) {
			logger.warn("erorr closing streamWriter",e);
		}
	}
	
	
}
