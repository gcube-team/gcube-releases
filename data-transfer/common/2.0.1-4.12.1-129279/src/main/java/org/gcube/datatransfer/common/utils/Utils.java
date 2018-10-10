package org.gcube.datatransfer.common.utils;

import static org.gcube.data.trees.patterns.Patterns.*;
import static org.gcube.data.trees.streams.TreeStreams.*;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;


import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.Bindings;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.common.grs.FileOutcomeRecord.Outcome;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferOutcome;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.gcube.datatransfer.common.agent.Types.*;

/**
 * 
 * 
 * @author Fabio Simeoni (FAO)
 * @author Andrea Manzi (CERN)
 */
public class Utils {


	static Logger logger = LoggerFactory.getLogger(Utils.class);


	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


	/**
	 * Converts a {@link AnyHolder} returned by the service into a {@link Pattern}.
	 * @param h the holder
	 * @return the pattern
	 * @throws Exception if the conversion fails
	 */
	public static Pattern getPattern(AnyHolder h) throws Exception {

		return h==null?null: (Pattern) getUnMarshaller().unmarshal(h.element[0]);

	}


	public static AnyHolder toHolder(Pattern p) throws Exception {

		if (p==null) 
			return null;

		Document filterNode = factory.newDocumentBuilder().newDocument();
		Patterns.getMarshaller().marshal(p, filterNode);
		return toHolder(filterNode.getDocumentElement());

	}
	/* Converts an {@link Element} into a {@link AnyHolder} accepted by the service.
	 * @param e the element
	 * @return the holder
	 */
	public static AnyHolder toHolder(Element e) {

		if (e == null) return null;
		else {
			AnyHolder holder = new AnyHolder();
			holder.element = new Element[]{e};
			return holder;
		}
	}

	/**
	 * Transforms a {@link Node} into a {@link AnyHolder} accepted by the service.
	 * @param n the node
	 * @return the holder
	 * @throws Exception if the conversion fails
	 */
	public static AnyHolder toHolder(Node n) throws Exception {

		return n==null?null:toHolder(Bindings.nodeToElement(n));

	}


	public static void copyfile(File f1, File f2) throws IOException{

		InputStream in = new FileInputStream(f1);
		OutputStream out = new FileOutputStream(f2);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0){
			out.write(buf, 0, len);
		}
		in.close();
		out.close();

	}

//   moved in agent-service in order to remove the dependency on gcf on
//	 this component
//	/**
//	 * Returns a given fault after serialising into it an original cause.
//	 * @param fault the fault
//	 * @param cause the cause
//	 * @return the fault
//	 * @param <E> the type of the fault
//	 */
//	public static <E extends GCUBEFault> E newFault(E fault, Throwable cause) {
//
//		fault.setFaultMessage(cause.getMessage());
//
//		fault.removeFaultDetail(new QName("http://xml.apache.org/axis/","stackTrace"));
//
//		//adds whole stacktrace as single detail element
//		StringWriter w = new StringWriter();
//		cause.printStackTrace(new PrintWriter(w));
//		fault.addFaultDetailString(w.toString());
//
//		try {
//			fault.addFaultDetail(ExceptionProxy.newInstance(cause).toElement());
//		}
//		catch(Exception e) {}
//
//		return fault;
//
//	}


	public static void copyfileToFolder(File file, File outFolder, String fileName) throws IOException{

		InputStream in = new FileInputStream(file);
		OutputStream out = new FileOutputStream(outFolder.getAbsolutePath()+File.separator+fileName);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0){
			out.write(buf, 0, len);
		}
		in.close();
		out.close();

	}

	public static <T extends TransferOutcome> ArrayList<T> getFileOutcomes(String outcomeRSURI) throws Exception{

		logger.debug("Outcome grs URI"+ outcomeRSURI);

		IRecordReader<GenericRecord> reader=new ForwardReader<GenericRecord>(new URI(outcomeRSURI));
		//reader = new KeepAliveReader<GenericRecord>(reader, 30, TimeUnit.SECONDS);
		ArrayList<T> outcomes = new ArrayList<T>();

		for ( int i = 0 ; i<reader.getCapacity() ; i++){
			GenericRecord rec = null;

			while ((rec = reader.get()) == null){
				Thread.sleep(100);
			}

			StringField sourceURLField = ((StringField) rec.getField("SourceURLField"));
			logger.trace("FileName :" +sourceURLField.getPayload());
			StringField destURLField = ((StringField) rec.getField("DestURLField"));
			logger.trace("dest :" +destURLField.getPayload());
			StringField outcomeField = ((StringField) rec.getField("OutcomeField"));
			logger.trace("Outcome :" +outcomeField.getPayload());
			StringField timeField = ((StringField) rec.getField("TransferTimeField"));
			logger.trace("Transfer Time: "+timeField.getPayload());
			StringField bytesField = ((StringField) rec.getField("TransferredBytesField"));
			logger.trace("Transferred Bytes: "+bytesField.getPayload());
			StringField sizeField = ((StringField) rec.getField("SizeField"));
			logger.trace("Size: "+sizeField.getPayload());
			StringField exceptionField = ((StringField) rec.getField("ExceptionField"));
			logger.trace("Exception: "+exceptionField.getPayload());

			FileTransferOutcome outcome = new FileTransferOutcome(sourceURLField.getPayload());
			outcome.setException(exceptionField.getPayload());
			outcome.setDest(destURLField.getPayload());
			outcome.setTransferTime(Long.valueOf(timeField.getPayload()));
			outcome.setTransferredBytes(Long.valueOf(bytesField.getPayload()));
			outcome.setTotal_size(Long.valueOf(sizeField.getPayload()));
			outcomes.add((T) outcome);
		}

		try {
			reader.close();
			logger.debug("reader closed");
		} catch (GRS2ReaderException e1) {
			e1.printStackTrace();
		}
		return outcomes;
	}

	public static <T extends TransferOutcome> ArrayList<T> getTreeOutcomes(String outcomeRSURI) throws Exception{

		logger.debug("Outcome grs URI"+ outcomeRSURI);

		IRecordReader<GenericRecord> reader=new ForwardReader<GenericRecord>(new URI(outcomeRSURI));
		//reader = new KeepAliveReader<GenericRecord>(reader, 30, TimeUnit.SECONDS);
		ArrayList<T> outcomes = new ArrayList<T>();

		int cap = reader.getCapacity();
		if(cap>1){
			logger.debug("reader.getCapacity()>1 - it must be 1 !! we're taking the first one (tree outcome refers " +
					"to the transfer and not to the tree objects.)");
		}

		GenericRecord rec = null;
		int maxWaitTime= 2 * 60 * 1000; // maxWaitTime 2 minutes
		while ((rec = reader.get()) == null && maxWaitTime>0){
			Thread.sleep(100);
			maxWaitTime=maxWaitTime-100;
		}

		StringField sourceIDField = ((StringField) rec.getField("SourceIDField"));
		logger.trace("SourceID :" +sourceIDField.getPayload());
		StringField  destIDField= ((StringField) rec.getField("DestIDField"));
		logger.trace("DestID :" +destIDField.getPayload());
		StringField  readTreesField= ((StringField) rec.getField("ReadTreesField"));
		logger.trace("ReadTrees :" +readTreesField.getPayload());
		StringField  writtenTreesField= ((StringField) rec.getField("WrittenTreesField"));
		logger.trace("WrittenTrees :" +writtenTreesField.getPayload());		
		StringField outcomeField = ((StringField) rec.getField("OutcomeField"));
		logger.trace("Outcome :" +outcomeField.getPayload());
		StringField exceptionField = ((StringField) rec.getField("ExceptionField"));
		logger.trace("Exception :"+exceptionField.getPayload());

		int readTrees = Integer.parseInt(readTreesField.getPayload());
		int writtenTrees = Integer.parseInt(writtenTreesField.getPayload());
		
		TreeTransferOutcome	outcome = new TreeTransferOutcome();
		outcome.setSourceID(sourceIDField.getPayload());
		outcome.setDestID(destIDField.getPayload());
		outcome.setTotalReadTrees(readTrees);
		outcome.setTotalWrittenTrees(writtenTrees);
		outcome.setException(exceptionField.getPayload());
		outcomes.add((T)outcome);

		try {
			reader.close();
			logger.debug("reader closed");
		} catch (GRS2ReaderException e1) {
			e1.printStackTrace();
		}
		return outcomes;
	}
}
