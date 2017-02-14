package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

import com.google.common.collect.Lists;

/**
 * This {@link DataSource} fetches records through HTTP protocol with XML
 * structure.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class SRUDataSource implements DataSource, ContentTypeDataSource {

	private static final String COLLECTION = "searchRetrieveResponse";
	private static final String RECORDS = "records";
	private static final String RECORD = "record";
	private static final String ID = "recordSchema";
	private static final String MIMETYPE = "recordPacking";
	private static final String PAYLOAD = "recordData";

	private static final String RECORDIDPATH = "/" + COLLECTION + "/" + RECORDS + "/" + RECORD + "/" + ID;
	private static final String FIELDMIMETYPEPATH = "/" + COLLECTION + "/" + RECORDS + "/" + RECORD + "/" + MIMETYPE;
	private static final String FIELDPAYLOADPATH = "/" + COLLECTION + "/" + RECORDS + "/" + RECORD + "/" + PAYLOAD;

	private HTTPDataSource source;
	
	public SRUDataSource(String input, Parameter[] inputParameters) throws XMLStreamException, IOException {
		List<Parameter> params = Lists.newArrayList(
				new Parameter(HTTPDataSource.CollPaths.COLLNAMEPATH.toString(), input),
				new Parameter(HTTPDataSource.CollPaths.COLLPROVENANCEPATH.toString(), input),
				new Parameter(HTTPDataSource.CollPaths.COLLTIMESTAMPPATH.toString(), new Date().toString()),
				new Parameter(HTTPDataSource.CollPaths.RECORDIDPATH.toString(), RECORDIDPATH),
				new Parameter(HTTPDataSource.CollPaths.FIELDMIMETYPEPATH.toString(), FIELDMIMETYPEPATH),
				new Parameter(HTTPDataSource.CollPaths.FIELDPAYLOADPATH.toString(), FIELDPAYLOADPATH));
		
		if (inputParameters != null)
			for (Parameter param : inputParameters) {
				params.add(param);
			}

		source = new HTTPDataSource(input, params.toArray(new Parameter[params.size()]));
	}

	@Override
	public void close() {
		source.close();
		
	}

	@Override
	public boolean isClosed() {
		return source.isClosed();
	}

	@Override
	public ContentType nextContentType() {
		ContentType ct = source.nextContentType();
		if (ct.getMimeType().equals("xml"))
			ct.setMimeType("text/xml");
		return ct;
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public DataElement next() throws Exception {
		DataElement de = source.next();
		if (de instanceof DTSExceptionWrapper)
			throw new Exception(((DTSExceptionWrapper)de).getThrowable());
		
		if (de.getContentType().getMimeType().equals("xml"))
			de.getContentType().setMimeType("text/xml");;
		return de;
	}

	public static void main(String[] args) throws Exception {
		DTSScope.setScope("/gcube/devNext");
		String input = "http://dl08.di.uoa.gr:8080/sru-geonetwork-adapter-service/sru?operation=searchRetrieve&query=tuna";
//		String input = "http://meteora.di.uoa.gr/sru.xml";
		SRUDataSource source = new SRUDataSource(input, null);
		Thread.sleep(1000);
		while (source.hasNext()) {
			StrDataElement de = ((StrDataElement)source.next());
			System.out.println(de.getId());
			System.out.println(de.getContentType());
			System.out.println(de.getStringContent());
			System.out.println(de.getAllAttributes());
		}
	}
}
