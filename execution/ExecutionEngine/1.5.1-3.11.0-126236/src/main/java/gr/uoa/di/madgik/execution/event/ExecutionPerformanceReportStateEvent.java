package gr.uoa.di.madgik.execution.event;

import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;

/**
 * Event signaling some performance related measurements after the completion of some plan element.
 * 
 * @author gpapanikos
 */
public class ExecutionPerformanceReportStateEvent extends ExecutionStateEvent
{
	
	/** The Emit timestamp. */
	private long EmitTimestamp=0;
	
	/** The ID. */
	private String ID="";
	
	/** The Total time in millisecond. */
	private long TotalTime=0;
	
	/** The Initialization time. */
	private long InitializationTime=0;
	
	/** The Finalization time. */
	private long FinilizationTime=0;
	
	/** The Children total time. */
	private long ChildrenTotalTime=0;
	
	/** The number of Sub calls. */
	private int SubCalls=0;
	
	/** The total time the sub calls took in milliseconds. */
	private long CallTotalTime=0;
	
	/**
	 * Instantiates a new execution performance report state event.
	 */
	public ExecutionPerformanceReportStateEvent()
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * Instantiates a new execution performance report state event.
	 * 
	 * @param ID the iD of the3 plan element reporting its performance indicators
	 */
	public ExecutionPerformanceReportStateEvent(String ID)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ID=ID;
	}
	
	/**
	 * Instantiates a new execution performance report state event.
	 * 
	 * @param ID the iD of the3 plan element reporting its performance indicators
	 * @param TotalTime the total time
	 * @param InitializationTime the initialization time
	 * @param FinilizationTime the finalization time
	 * @param ChildrenTotalTime the children total time
	 * @param SubCalls the number of sub calls
	 * @param CallTotalTime the total time of the sub calls
	 */
	public ExecutionPerformanceReportStateEvent(String ID,long TotalTime, long InitializationTime, long FinilizationTime, long ChildrenTotalTime,int SubCalls,long CallTotalTime)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ID=ID;
		this.TotalTime=TotalTime;
		this.InitializationTime=InitializationTime;
		this.FinilizationTime=FinilizationTime;
		this.ChildrenTotalTime=ChildrenTotalTime;
		this.SubCalls=SubCalls;
		this.CallTotalTime=CallTotalTime;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the string
	 */
	public String GetID()
	{
		return this.ID;
	}
	
	/**
	 * Gets the total time.
	 * 
	 * @return the long
	 */
	public long GetTotalTime()
	{
		return this.TotalTime;
	}
	
	/**
	 * Sets the total time.
	 * 
	 * @param TotalTime the total time
	 */
	public void SetTotalTime(long TotalTime)
	{
		this.TotalTime=TotalTime;
	}
	
	/**
	 * Gets the initialization time.
	 * 
	 * @return the long
	 */
	public long GetInitializationTime()
	{
		return this.InitializationTime;
	}
	
	/**
	 * Sets the initialization time.
	 * 
	 * @param InitializationTime the initialization time
	 */
	public void SetInitializationTime(long InitializationTime)
	{
		this.InitializationTime=InitializationTime;
	}
	
	/**
	 * Gets the finalization time.
	 * 
	 * @return the long
	 */
	public long GetFinilizationTime()
	{
		return this.FinilizationTime;
	}
	
	/**
	 * Sets the finalization time.
	 * 
	 * @param FinilizationTime the finalization time
	 */
	public void SetFinilizationTime(long FinilizationTime)
	{
		this.FinilizationTime=FinilizationTime;
	}
	
	/**
	 * Gets the children total time.
	 * 
	 * @return the long
	 */
	public long GetChildrenTotalTime()
	{
		return this.ChildrenTotalTime;
	}
	
	/**
	 * Sets the children total time.
	 * 
	 * @param ChildrenTotalTime the children total time
	 */
	public void SetChildrenTotalTime(long ChildrenTotalTime)
	{
		this.ChildrenTotalTime=ChildrenTotalTime;
	}
	
	/**
	 * Gets the sub call total time.
	 * 
	 * @return the long
	 */
	public long GetSubCallTotalTime()
	{
		return this.CallTotalTime;
	}
	
	/**
	 * Sets the sub call total time.
	 * 
	 * @param CallTotalTime the call total time
	 */
	public void SetSubCallTotalTime(long CallTotalTime)
	{
		this.CallTotalTime=CallTotalTime;
	}
	
	/**
	 * Gets the sub calls.
	 * 
	 * @return the int
	 */
	public int GetSubCalls()
	{
		return this.SubCalls;
	}
	
	/**
	 * Sets the sub calls.
	 * 
	 * @param SubCalls the sub calls
	 */
	public void SetSubCalls(int SubCalls)
	{
		this.SubCalls=SubCalls;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.event.ExecutionStateEvent#GetEventName()
	 */
	@Override
	public EventName GetEventName()
	{
		return ExecutionStateEvent.EventName.ExecutionPerformance;
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
			this.ID=din.readUTF();
			this.TotalTime=din.readLong();
			this.InitializationTime=din.readLong();
			this.FinilizationTime=din.readLong();
			this.ChildrenTotalTime=din.readLong();
			this.SubCalls=din.readInt();
			this.CallTotalTime=din.readLong();
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
			dout.writeUTF((ID==null ? "" : ID));
			dout.writeLong(this.TotalTime);
			dout.writeLong(this.InitializationTime);
			dout.writeLong(this.FinilizationTime);
			dout.writeLong(this.ChildrenTotalTime);
			dout.writeInt(this.SubCalls);
			dout.writeLong(this.CallTotalTime);
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
