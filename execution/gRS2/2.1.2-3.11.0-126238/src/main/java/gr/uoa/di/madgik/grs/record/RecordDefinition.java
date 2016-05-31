package gr.uoa.di.madgik.grs.record;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The record definition represents the scheme of a single {@link Record} along with the respective {@link FieldDefinition}
 * holds all the metadata available for the {@link Record} and the hoste4d {@link Field}s. All extending classes of this
 * class must define a default no arguments constructor
 * 
 * @author gpapanikos
 *
 */
public abstract class RecordDefinition implements Serializable
{
	/**
	 * 
	 */
	private static final long   serialVersionUID = 1L;
	/**
	 * The field definitions available for the hosted {@link Field}s
	 */
	protected List<FieldDefinition> Fields=null;
	/**
	 * The transport directive of the {@link Record}, initially set to {@link TransportDirective#Inherit}
	 */
	protected TransportDirective directive=TransportDirective.Inherit;
	
	/**
	 * Creates a new instance
	 */
	public RecordDefinition()
	{
		this.Fields=new ArrayList<FieldDefinition>();
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param fieldDefinitions the field definitions
	 */
	public RecordDefinition(List<FieldDefinition> fieldDefinitions)
	{
		this.Fields=fieldDefinitions;
	}
	
	public void copyFrom(RecordDefinition other) throws GRS2RecordSerializationException 
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		other.deflate(new DataOutputStream(out));
		this.inflate(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
	}
	
	/**
	 * Retrieves the field definition with the provided index
	 * 
	 * @param index the index of the definition
	 * @return the Filed definition
	 */
	public FieldDefinition getDefinition(int index)
	{
		return this.Fields.get(index);
	}
	
	/**
	 * Retrieves the field definition with the provided name
	 * 
	 * @param name the name of the definition
	 * @return the field definition
	 */
	public int getDefinition(String name)
	{
		int i = 0;
		for (FieldDefinition fd : this.Fields){
			if(fd.getName().equalsIgnoreCase(name)) 
				return i;
			i++;
		}
		
		return -1;
	}
	
	/**
	 * Retrieves the length of the field definitions
	 * 
	 * @return the length of the field definitions
	 */
	public int getDefinitionSize()
	{
		return this.Fields.size();
	}

	/**
	 * Sets the transport directive for this {@link Record}
	 * 
	 * @param directive the directive
	 */
	public void setTransportDirective(TransportDirective directive)
	{
		this.directive = directive;
	}

	/**
	 * Retrieves the transport directive
	 * 
	 * @return the directive
	 */
	public TransportDirective getTransportDirective()
	{
		return this.directive;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * To check for equality, the type of the argument is checked. The field definitions length in both definitions must be
	 * the same as well as the defined {@link TransportDirective}. For each field definition hosted, {@link FieldDefinition#equals(Object)}
	 * is invoked and finally the {@link RecordDefinition#extendEquals(Object)} is consulted. If any of the above conditions are 
	 * not met, the two instances are not equal 
	 * </p>
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(!(obj instanceof RecordDefinition)) return false;
		if(this.Fields.size()!=((RecordDefinition)obj).Fields.size()) return false;
		if(!this.directive.equals(((RecordDefinition)obj).directive)) return false;
		for(FieldDefinition thisField : this.Fields)
		{
			boolean found=false;
			for(FieldDefinition otherField : ((RecordDefinition)obj).Fields)
			{
				if(thisField.equals(otherField))
				{
					found=true;
					break;
				}
			}
			if(!found) return false;
		}
		return this.extendEquals(obj);
	}
	
	/**
	 * Method that needs to be implemented by the {@link RecordDefinition} extenders to extend the equality logic
	 * based on additional logic contained in the implementations 
	 * 
	 * @param obj the object to check equality for
	 * @return true if the two instances are equal, false otherwise
	 */
	public abstract boolean extendEquals(Object obj);
	
	/**
	 * Deflates the state and information kept in the {@link RecordDefinition}. After deflating the locally maintained
	 * information, for each field definition hosted, {@link FieldDefinition#deflate(DataOutput)} is invoked
	 * and finally, {@link Field#extendDeflate(java.io.DataOutput)}
	 * 
	 * @param out the stream to deflate the information to
	 * @throws GRS2RecordSerializationException there was a problem serializing the definition state
	 */
	public void deflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeUTF(this.directive.toString());
			out.writeInt(this.Fields.size());
			for(FieldDefinition def : this.Fields)
			{
				out.writeUTF(def.getClass().getName());
				def.deflate(out);
			}
			this.extendDeflate(out);
		}
		catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("Could not complete marshalling of definition",ex);
		}
	}
	
	public final Element toXML(Document doc) throws GRS2RecordSerializationException, GRS2RecordDefinitionException, DOMException {
		Element element = doc.createElement("recordDefinition");

		Element elm = null;

		elm = doc.createElement("directive");
		elm.setTextContent(String.valueOf(this.directive.toString()));
		element.appendChild(elm);


		
		
		Element fieldDefinitions = doc.createElement("fields");

		for (FieldDefinition def : this.Fields) {
			
			Element fieldDefinition = def.toXML(doc);
			
			Element fieldDefinitionClass = doc.createElement("fieldDefinitionClass");
			fieldDefinitionClass.setTextContent(def.getClass().getName());
			
			fieldDefinition.appendChild(fieldDefinitionClass);
			
			fieldDefinitions.appendChild(fieldDefinition);
		}
		
		this.extendToXML(element);
		
		element.appendChild(fieldDefinitions);
		return element;
	}
	
	public final void fromXML(Element element) throws GRS2RecordSerializationException, GRS2RecordDefinitionException, DOMException {
		
		try
		{
			this.directive=TransportDirective.valueOf(element.getElementsByTagName("directive").item(0).getTextContent());
			
			NodeList fieldList = element.getElementsByTagName("fields").item(0).getChildNodes();
			
			int fieldsLength=fieldList.getLength();
			
			this.Fields=new ArrayList<FieldDefinition>();
			for(int i=0;i<fieldsLength;i+=1)
			{
				Element fieldDefinition = (Element) fieldList.item(i);
				
								
				
				String fieldDefType=fieldDefinition.getElementsByTagName("fieldDefinitionClass").item(0).getTextContent();;
				FieldDefinition def=(FieldDefinition)Class.forName(fieldDefType).newInstance();
				
				def.fromXML(fieldDefinition) ;
				this.Fields.add(def);
			}
			extendFromXML(element);
			
		}
		catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("Could not complete unmarshalling of definition",ex);
		}
		
	}	
	
	
	/**
	 * Method that needs to be implemented by the {@link RecordDefinition} extenders to deflate any additional information
	 * kept
	 * 
	 * @param out the stream to deflate to
	 * @throws GRS2RecordSerializationException there was a serialization error
	 */
	public abstract void extendDeflate(DataOutput out) throws GRS2RecordSerializationException;
	
	/**
	 * Inflates the previously deflated information of this {@link RecordDefinition}. After retrieving the locally maintained
	 * information, for all the hosted fields, the {@link FieldDefinition#inflate(DataInput)} is called, and finally 
	 * {@link RecordDefinition#extendInflate(DataInput)} is invoked
	 * 
	 * @param in the stream to inflate from
	 * @throws GRS2RecordSerializationException there was a problem deserializing the definition state
	 */
	public void inflate(DataInput in) throws GRS2RecordSerializationException
	{
		try
		{
			this.directive=TransportDirective.valueOf(in.readUTF());
			int fieldsLength=in.readInt();
			this.Fields=new ArrayList<FieldDefinition>();
			for(int i=0;i<fieldsLength;i+=1)
			{
				String fieldDefType=in.readUTF();
				FieldDefinition def=(FieldDefinition)Class.forName(fieldDefType).newInstance();
				def.inflate(in);
				this.Fields.add(def);
			}
			this.extendInflate(in);
		}
		catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("Could not complete unmarshalling of definition",ex);
		}
	}
	
	/**
	 * Method that needs to be implemented by the {@link RecordDefinition} extenders to inflate any additional information
	 * previously deflated by the respective extender method
	 * 
	 * @param in the stream to inflate from
	 * @throws GRS2RecordSerializationException there was a problem deserializing the definition state
	 */
	public abstract void extendInflate(DataInput in) throws GRS2RecordSerializationException;
	
	public abstract void extendFromXML(Element element) throws GRS2RecordSerializationException;
	public abstract void extendToXML(Element element) throws GRS2RecordSerializationException;
}
