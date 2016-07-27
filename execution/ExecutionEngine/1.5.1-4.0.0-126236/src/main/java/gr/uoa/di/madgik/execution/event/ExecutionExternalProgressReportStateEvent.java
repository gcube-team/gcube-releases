package gr.uoa.di.madgik.execution.event;

import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;

/**
 * Event signaling the progress of the execution of an external to the engine element. This event can either just
 * contain a message that is emitted back to the client or it can also report a progress indication.
 * 
 * @author gpapanikos
 */
public class ExecutionExternalProgressReportStateEvent extends ExecutionStateEvent
{
	
	/** The Emit timestamp. */
	private long EmitTimestamp=0;
	
	/** The Reports progress. */
	private boolean ReportsProgress=false;
	
	/** The Current step. */
	private int CurrentStep=0;
	
	/** The Total steps. */
	private int TotalSteps=0;
	
	/** The Message. */
	private String Message="";
	
	/** The ID. */
	private String ID="";
	
	/** The External sender name. */
	private String ExternalSender="";
	
	/**
	 * Instantiates a new execution external progress report state event.
	 */
	public ExecutionExternalProgressReportStateEvent()
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=false;
	}
	
	/**
	 * Instantiates a new execution external progress report state event.
	 * 
	 * @param ID the iD of the plan element responsible for the external component emitting the event
	 * @param ExternalSender the external sender
	 * @param Message the message
	 */
	public ExecutionExternalProgressReportStateEvent(String ID,String ExternalSender,String Message)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=false;
		this.Message=Message;
		this.ID=ID;
		this.ExternalSender=ExternalSender;
	}
	
	/**
	 * Instantiates a new execution external progress report state event.
	 * 
	 * @param ID the iD of the plan element responsible for the external component emitting the event
	 * @param ExternalSender the external sender
	 * @param CurrentStep the current step the current step of the execution 
	 * @param TotalSteps the total steps the total steps of the execution
	 */
	public ExecutionExternalProgressReportStateEvent(String ID,String ExternalSender,int CurrentStep, int TotalSteps)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=true;
		this.CurrentStep=CurrentStep;
		this.TotalSteps=TotalSteps;
		this.ID=ID;
		this.ExternalSender=ExternalSender;
	}
	
	/**
	 * Instantiates a new execution external progress report state event.
	 * 
	 * @param ID the iD of the plan element responsible for the external component emitting the event
	 * @param ExternalSender the external sender
	 * @param CurrentStep the current step the current step of the execution 
	 * @param TotalSteps the total steps the total steps of the execution
	 * @param Message the message
	 */
	public ExecutionExternalProgressReportStateEvent(String ID,String ExternalSender,int CurrentStep, int TotalSteps,String Message)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=true;
		this.CurrentStep=CurrentStep;
		this.TotalSteps=TotalSteps;
		this.Message=Message;
		this.ID=ID;
		this.ExternalSender=ExternalSender;
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
	 * Does report progress.
	 * 
	 * @return true, if successful
	 */
	public boolean DoesReportProgress()
	{
		return this.ReportsProgress;
	}
	
	/**
	 * Does report progress.
	 * 
	 * @param ReportsProgress the reports progress
	 */
	public void DoesReportProgress(boolean ReportsProgress)
	{
		this.ReportsProgress=ReportsProgress;
	}
	
	/**
	 * Gets the current step.
	 * 
	 * @return the int
	 */
	public int GetCurrentStep()
	{
		return this.CurrentStep;
	}
	
	/**
	 * Sets the current step.
	 * 
	 * @param CurrentStep the current step
	 */
	public void SetCurrentStep(int CurrentStep)
	{
		this.CurrentStep=CurrentStep;
	}
	
	/**
	 * Gets the total steps.
	 * 
	 * @return the int
	 */
	public int GetTotalSteps()
	{
		return this.TotalSteps;
	}
	
	/**
	 * Sets the total steps.
	 * 
	 * @param TotalSteps the total steps
	 */
	public void SetTotalSteps(int TotalSteps)
	{
		this.TotalSteps=TotalSteps;
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the string
	 */
	public String GetMessage()
	{
		return this.Message;
	}
	
	/**
	 * Sets the message.
	 * 
	 * @param Message the message
	 */
	public void SetMessage(String Message)
	{
		this.Message=Message;
	}
	
	/**
	 * Gets the external sender.
	 * 
	 * @return the string
	 */
	public String GetExternalSender()
	{
		return this.ExternalSender;
	}
	
	/**
	 * Sets the external sender.
	 * 
	 * @param ExternalSender the external sender
	 */
	public void SetExternalSender(String ExternalSender)
	{
		this.ExternalSender=ExternalSender;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.event.ExecutionStateEvent#GetEventName()
	 */
	@Override
	public EventName GetEventName()
	{
		return ExecutionStateEvent.EventName.ExecutionExternalProgress;
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
			this.ReportsProgress=din.readBoolean();
			this.CurrentStep=din.readInt();
			this.TotalSteps=din.readInt();
			this.ID=din.readUTF();
			this.Message=din.readUTF();
			this.ExternalSender=din.readUTF();
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
			dout.writeBoolean(ReportsProgress);
			dout.writeInt(CurrentStep);
			dout.writeInt(TotalSteps);
			dout.writeUTF((ID==null ? "" : ID));
			dout.writeUTF((Message==null ? "" : Message));
			dout.writeUTF((ExternalSender==null ? "" : ExternalSender));
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
