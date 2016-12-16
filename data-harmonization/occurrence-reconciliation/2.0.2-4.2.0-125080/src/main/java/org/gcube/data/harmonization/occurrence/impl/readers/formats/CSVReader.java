package org.gcube.data.harmonization.occurrence.impl.readers.formats;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import net.sf.csv4j.CSVReaderProcessor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.gcube.data.harmonization.occurrence.impl.readers.CSVParserConfiguration;
import org.gcube.data.harmonization.occurrence.impl.readers.OccurrenceReader;
import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress.OperationState;

public class CSVReader extends OccurrenceReader{

	public CSVReader(File toRead, CSVParserConfiguration configuration) {
		super(toRead, configuration);		
	}

	@Override
	public void streamData() {
		final CSVReaderProcessor processor=new CSVReaderProcessor();
		processor.setDelimiter(configuration.getDelimiter());
		processor.setHasHeader(configuration.isHasHeader());
		processor.setComment(configuration.getComment());
		Thread t=new Thread(){
			public void run() {
				Reader reader=null;
				FileInputStream fis=null;
				CountingInputStream cis=null;
				try{
					fis=new FileInputStream(toRead);
					cis=new CountingInputStream(fis); 
					reader= new InputStreamReader(cis, configuration.getCharset());
					progress.setTotalLenght(toRead.length());
					OccurrenceCSVProcessor lineProcessor=new OccurrenceCSVProcessor(wrapper,progress,(CSVParserConfiguration) configuration,cis);
					processor.processStream(reader , lineProcessor);	
					progress.setState(OperationState.COMPLETED);
				}catch(Throwable t){
					progress.setFailureReason("Unable to stream data");
					progress.setFailureDetails(t.getMessage());
					progress.setState(OperationState.FAILED);
				}finally{
					try {
						wrapper.close();
					} catch (Exception e) {
						logger.error("Unable to close wrapper ",e);
					}
					IOUtils.closeQuietly(cis);
					try {
						reader.close();
					} catch (Exception e) {
						logger.error("Unable to close wrapper ",e);
					}
				}
			};
		};
		t.start();
	}
	
	
	
//	@Override
//	public void streamFile(InputStream toRead,
//			ResultWrapper<OccurrencePoint> wrapper) throws Exception {
//
//		CSVReaderProcessor processor=new CSVReaderProcessor();
//
//		processor.setDelimiter(DEFAULT_DELIMITER);
//		processor.setHasHeader(HAS_HEADERS);
//		processor.setComment('§');
//		OccurrenceCSVProcessor lineProcessor=new OccurrenceCSVProcessor(wrapper);
//
//		Reader reader= new InputStreamReader(toRead, Charset.defaultCharset());
//
//		processor.processStream(reader , lineProcessor);
//		if(!lineProcessor.isAllClear()) throw new Exception(lineProcessor.getMessage());
//	}



}
