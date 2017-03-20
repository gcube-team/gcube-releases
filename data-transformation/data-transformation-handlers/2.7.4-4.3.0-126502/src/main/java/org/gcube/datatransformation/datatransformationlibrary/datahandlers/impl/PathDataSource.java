package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.LocalFileDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;

/**
 * @author Dimitris Katris, NKUA
 * 
 *         <p>
 * 
 *         </p>
 */
public class PathDataSource implements DataSource, ContentTypeDataSource {

	private boolean closed = false;
	private int fileCounter = 0;
	private String sinkpath;
	private String[] children;
	private static Logger log = LoggerFactory.getLogger(PathDataSource.class);

	
	public static void main(String[] args) throws Exception {
		
		PathDataSource dataSource = new PathDataSource("/tmp/source", null);
		PathDataSink   dataSink   = new PathDataSink("/tmp/sink", null);
		
		while(dataSource.hasNext()){
			try {
				DataElement elm = dataSource.next();
				if(elm==null)continue;
				System.out.println(elm.getContentType().getMimeType());
//				Thread.sleep(360000);
				System.out.println("Going to store: "+elm.getId());
				dataSink.append(elm);
			} catch (Exception e) {
				log.error("Did not manage to append data element to the data sink", e);
			}
		}
		System.out.println("----- Statistics -----");
		System.out.println(StatisticsManager.toXML());
	}

	
	public PathDataSource(String input, Parameter[] inputParameters)
			throws Exception {
		initializeDistributableDataSource(input, inputParameters);
	}

	public boolean isClosed() {
		return closed;
	}

	public boolean hasNext() {
		if (children == null || fileCounter >= children.length) {
			return false;
		}
		return true;
	}

	public DataElement next() {
		if (children == null || fileCounter >= children.length) {
			log.warn("Did not manage to get next element...");
			return null;
		}
		String localFileName = sinkpath + children[fileCounter];
		File localFile = new File(localFileName);
		LocalFileDataElement elm = new LocalFileDataElement();
		elm.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME,
				children[fileCounter]);
		elm.setContent(localFile);
		elm.setId(children[fileCounter]);
		fileCounter++;
		return elm;
	}

	public void initializeDistributableDataSource(String input,
			Parameter[] inputParameters) throws Exception {
		sinkpath = input;
		File dir = new File(sinkpath);
		if (!sinkpath.endsWith(File.pathSeparator))
			sinkpath += File.separator;
		children = dir.list();
		if (children == null) {
			closed = true;
			log.error("Either dir does not exist or is not a directory");
			throw new Exception(
					"Either dir does not exist or is not a directory");
		}
	}

	public void close() {
		closed = true;
	}

	public ContentType nextContentType(){
		DataElement de = next();

		return de == null? null : de.getContentType();
	}
}
