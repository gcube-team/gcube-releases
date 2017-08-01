package gr.uoa.di.madgik.grs.record;

import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.record.exception.GRS2ThrowableWrapper;
import gr.uoa.di.madgik.grs.record.exception.GRS2ThrowableWrapperException;
import gr.uoa.di.madgik.grs.record.exception.ProducerException;
import gr.uoa.di.madgik.grs.xml.XMLHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GRS2ExceptionWrapper extends GenericRecord {
	private static final long serialVersionUID = 1L;
	private Throwable ex;
	
	public GRS2ExceptionWrapper() {
		super();
	}
	
	public GRS2ExceptionWrapper(Throwable ex) {
		super();
		this.ex = ex;
	}

	public Throwable getEx() {
		return ex;
	}

	public void setEx(Throwable ex) {
		this.ex = ex;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException, GRS2RecordDefinitionException, GRS2BufferException, GRS2ThrowableWrapperException{
		out.defaultWriteObject();
		GRS2ThrowableWrapper gtw = GRS2ThrowableWrapper.createFromThrowable(ex);
		out.writeObject(gtw);
	}
	
	private void readObject(java.io.ObjectInputStream in)  throws java.io.IOException, java.lang.ClassNotFoundException, GRS2ThrowableWrapperException{
		in.defaultReadObject();
		GRS2ThrowableWrapper gtw = (GRS2ThrowableWrapper) in.readObject();
		this.ex =  GRS2ThrowableWrapper.createFromGRS2ThrowableWrapper(gtw, ProducerException.class);
	}
	
	
	public void receiveFromXML(Element element) throws GRS2RecordSerializationException {
		try {
			super.receiveFromXML(element);
			
			XStream xstream = new XStream(new DomDriver());
			
			Node exceptionNode = element.getElementsByTagName("exception").item(0).getFirstChild();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLHelper.printXMLNode(exceptionNode, baos);
			
			String exceptionText = baos.toString();
			GRS2ThrowableWrapper gtw = (GRS2ThrowableWrapper) xstream.fromXML(exceptionText);
			this.ex = GRS2ThrowableWrapper.createFromGRS2ThrowableWrapper(gtw, ProducerException.class);
			
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
	public Element sendToXML(Document doc) throws GRS2RecordSerializationException {
		try {
			Element element = super.sendToXML(doc);
			
			Element elm = null;
			
			GRS2ThrowableWrapper gtw = GRS2ThrowableWrapper.createFromThrowable(ex);
			
			XStream xstream = new XStream(new DomDriver());
			String text = null;
			
			text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			text += xstream.toXML(gtw);
			
			elm = doc.createElement("exception");
			
			Node nd = XMLHelper.getXMLNode(text);
			Node tmpNode = doc.importNode(nd, true);
			
			elm.appendChild(tmpNode);
			element.appendChild(elm);
			
			return element;
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
	
	
	
}