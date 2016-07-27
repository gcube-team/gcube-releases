package gr.uoa.di.madgik.execution.event;

import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;

/**
 * Event signaling that the execution of a plan is canceled
 * 
 * @author gpapanikos
 */
public class ExecutionCancelStateEvent extends ExecutionStateEvent
{
	
	/** The Emit timestamp. */
	private long EmitTimestamp=0;
	
	/**
	 * Instantiates a new execution cancel state event.
	 */
	public ExecutionCancelStateEvent()
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.event.ExecutionStateEvent#GetEventName()
	 */
	@Override
	public EventName GetEventName()
	{
		return ExecutionStateEvent.EventName.ExecutionCancel;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.event.ExecutionStateEvent#GetEmitTimestamp()
	 */
	@Override
	public long GetEmitTimestamp()
	{
		return this.EmitTimestamp;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.event.ExecutionStateEvent#Decode(byte[])
	 */
	@Override
	public void Decode(byte[] buf) throws ExecutionSerializationException
	{
		try
		{
			ByteArrayInputStream bin=new ByteArrayInputStream(buf);
			DataInputStream din=new DataInputStream(bin);
			this.EmitTimestamp=din.readLong();
			din.close();
			bin.close();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not serialize event",ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.event.ExecutionStateEvent#Encode()
	 */
	@Override
	public byte[] Encode() throws ExecutionSerializationException
	{
		try
		{
			ByteArrayOutputStream bout=new ByteArrayOutputStream();
			DataOutputStream dout=new DataOutputStream(bout);
			dout.writeLong(EmitTimestamp);
			dout.flush();
			dout.close();
			bout.close();
			return bout.toByteArray();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not serialize event",ex);
		}
	}

}
