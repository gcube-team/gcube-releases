package gr.uoa.di.madgik.grs.record;

import gr.uoa.di.madgik.grs.record.field.FieldDefinition;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This {@link RecordDefinition} extending class acts as a generic placeholder for record definitions. It does not add much in 
 * the general definition provided by the {@link RecordDefinition} super class, other than supplying a readily available, non 
 * abstract implementation
 * 
 * @author gpapanikos
 *
 */
public class GenericRecordDefinition extends RecordDefinition implements Serializable
{
	
	/**
	 * Create a new instance
	 * 
	 * @see RecordDefinition#RecordDefinition()
	 */
	public GenericRecordDefinition()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @see RecordDefinition#RecordDefinition(FieldDefinition[])
	 * 
	 * @param fieldDefinitions the field definitions to set for the record definition
	 */
	public GenericRecordDefinition(FieldDefinition[] fieldDefinitions)
	{
		super(Arrays.asList(fieldDefinitions));
	}
	
	public void copyFrom(GenericRecordDefinition other) throws GRS2RecordSerializationException
	{
		super.copyFrom(other);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method simply checks for type equality and does not base its decision an any other elements
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.RecordDefinition#extendEquals(java.lang.Object)
	 */
	@Override
	public boolean extendEquals(Object obj)
	{
		if(!(obj instanceof GenericRecordDefinition)) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * nothing to add to deflate
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.RecordDefinition#extendDeflate(java.io.DataOutput)
	 */
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException
	{
		//nothing to add
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * nothing to get from inflate
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.RecordDefinition#extendInflate(java.io.DataInput)
	 */
	@Override
	public void extendInflate(DataInput in) throws GRS2RecordSerializationException
	{
		//nothing to get
	}

	@Override
	public void extendFromXML(Element element) throws GRS2RecordSerializationException {
		// TODO Auto-generated method stub
		
	}

	public void extendToXML(Element element)
			throws GRS2RecordSerializationException {
		// TODO Auto-generated method stub
		
	}

}
