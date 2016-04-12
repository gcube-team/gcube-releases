package org.gcube.data.harmonization.occurrence.impl.readers.formats;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.input.CountingInputStream;
import org.gcube.data.harmonization.occurrence.impl.readers.OccurrenceReader;
import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress.OperationState;
import org.gcube.data.harmonization.occurrence.impl.readers.XMLParserConfiguration;
import org.xml.sax.helpers.DefaultHandler;

public class XMLReader extends OccurrenceReader {

	public XMLReader(File toRead, XMLParserConfiguration configuration) {
		super(toRead, configuration);
	}

	@Override
	public void streamData() {
		Thread t=new Thread(){
			public void run() {
				CountingInputStream cis=null;
				try{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				cis=new CountingInputStream(new FileInputStream(toRead));
				progress.setTotalLenght(toRead.length());
				DefaultHandler handler=new DarwinCoreReader(wrapper,progress,(XMLParserConfiguration) configuration,cis);
				parser.parse(toRead, handler);
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
				}
			};
		};
		t.start();
	}

}
