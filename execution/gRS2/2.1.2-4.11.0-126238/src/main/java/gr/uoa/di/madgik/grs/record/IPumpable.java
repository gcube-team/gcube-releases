package gr.uoa.di.madgik.grs.record;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementers of this interface must define a default no arguments constructor
 * 
 * @author gpapanikos
 *
 */
public interface IPumpable extends Serializable
{
	/**
	 * Deflates the state and data of the implementor in a from capable of reconstructing the exact same instance
	 * 
	 * @param out the stream to write to
	 * @throws GRS2RecordSerializationException the deflate could not be completed
	 */
	public void deflate(DataOutput out) throws GRS2RecordSerializationException;
	/**
	 * Inflate the state and data of the implementor as was previously deflated using {@link IPumpable#deflate(DataOutput)}
	 * 
	 * @param in the stream to inflate from
	 * @throws GRS2RecordSerializationException the inflate could not be completed 
	 */
	public void inflate(DataInput in) throws GRS2RecordSerializationException;
	/**
	 * Inflate the state and data of the implementor as was previously deflated using {@link IPumpable#deflate(DataOutput)}
	 * 
	 * @param in the stream to inflate from
	 * @param reset if the implementor keeps some state over actions previously taken and this is true, this state must be reset
	 * @throws GRS2RecordSerializationException the inflate could not be completed 
	 */
	public void inflate(DataInput in,boolean reset) throws GRS2RecordSerializationException;
	
	
	public Element toXML(Document doc) throws GRS2RecordSerializationException, GRS2RecordDefinitionException, DOMException;
	public void fromXML(Element element) throws GRS2RecordSerializationException, GRS2RecordDefinitionException, DOMException;
	
}
