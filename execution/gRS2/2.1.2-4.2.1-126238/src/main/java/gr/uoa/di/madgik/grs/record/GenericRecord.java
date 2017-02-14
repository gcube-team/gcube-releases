package gr.uoa.di.madgik.grs.record;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * This {@link Record} extending class acts as a generic placeholder for records. It does not add much in the general 
 * definition provided by the {@link Record} super class, other than supplying a readily available, non abstract implementation
 * 
 * @author gpapanikos
 *
 */
public class GenericRecord extends Record
{

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Nothing is added to the send serialization
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.Record#extendSend(java.io.DataOutput)
	 */
	@Override
	public void extendSend(DataOutput out) throws GRS2RecordSerializationException
	{
		//nothing to add
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Nothing is read from the receive serialization
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.Record#extendReceive(java.io.DataInput)
	 */
	@Override
	public void extendReceive(DataInput in) throws GRS2RecordSerializationException
	{
		//nothing to get
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Nothing is disposed. No internal resources managed
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.Record#extendDispose()
	 */
	@Override
	public void extendDispose()
	{
		//nothing to dispose
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Nothing is added to the deflate serialization
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.Record#extendDeflate(java.io.DataOutput)
	 */
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException
	{
		//nothing to deflate
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Nothing is read from the inflate serialization
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.Record#extendInflate(java.io.DataInput, boolean)
	 */
	@Override
	public void extendInflate(DataInput in, boolean reset) throws GRS2RecordSerializationException
	{
		//nothing to inflate
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Nothing more to reset
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.Record#extendMakeLocal()
	 */
	@Override
	protected void extendMakeLocal()
	{
		//nothing to reset
	}
}
