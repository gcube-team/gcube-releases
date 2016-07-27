package gr.uoa.di.madgik.execution.event;

import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;

/**
 * Event signaling the progress of the execution of a plan element. This event can either just
 * contain a message that is emitted back to the client or it can also report a progress indication.
 * 
 * @author gpapanikos
 */
public class ExecutionProgressReportStateEvent extends ExecutionStateEvent
{
	
	/** The Emit timestamp. */
	private long EmitTimestamp=0;
	
	/** The Reports progress. */
	private boolean ReportsProgress=false;
	
	/** True if the event reports node progress */
	private boolean ReportsNodeProgress=false;
	
	/** True if the event reports node execution completion status. */
	private boolean ReportsNodeStatus=false;
	
	/** The Current step. */
	private int CurrentStep=0;
	
	/** The Total steps. */
	private int TotalSteps=0;
	
	/** The Message. */
	private String Message="";
	
	/** The ID. */
	private String ID="";
	
	/** The name of the node. */
	private String NodeName="";
	
	/** The host name of the node. */
	private String NodeHostName="";
	
	/** The port number of the node. */
	private int NodePort=-1;
	
	/**
	 * Instantiates a new execution progress report state event.
	 */
	public ExecutionProgressReportStateEvent()
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=false;
		this.ReportsNodeProgress=false;
		this.ReportsNodeStatus=false;
	}
	
	/**
	 * Instantiates a new execution progress report state event.
	 * 
	 * @param ID the iD of the plan element emitting the event
	 * @param Message the message
	 */
	public ExecutionProgressReportStateEvent(String ID,String Message)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=false;
		this.ReportsNodeProgress=false;
		this.ReportsNodeStatus=false;
		this.Message=Message;
		this.ID=ID;
	}
	
	/**
	 * Instantiates a new execution progress report state event.
	 * 
	 * @param ID the iD of the plan element emitting the event
	 * @param CurrentStep the current step the current step of the execution 
	 * @param TotalSteps the total steps the total steps of the execution
	 */
	public ExecutionProgressReportStateEvent(String ID,int CurrentStep, int TotalSteps)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=true;
		this.ReportsNodeProgress=false;
		this.ReportsNodeStatus=false;
		this.CurrentStep=CurrentStep;
		this.TotalSteps=TotalSteps;
		this.ID=ID;
	}
	
	/**
	 * Instantiates a new execution progress report state event.
	 * 
	 * @param ID the iD of the plan element emitting the event
	 * @param CurrentStep the current step the current step of the execution 
	 * @param TotalSteps the total steps the total steps of the execution
	 * @param Message the message
	 */
	public ExecutionProgressReportStateEvent(String ID,int CurrentStep, int TotalSteps,String Message)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=true;
		this.ReportsNodeProgress=false;
		this.ReportsNodeStatus=false;
		this.CurrentStep=CurrentStep;
		this.TotalSteps=TotalSteps;
		this.Message=Message;
		this.ID=ID;
	}
	
	/**
	 * Instantiates a new execution progress report state event.
	 * 
	 * @param ID the iD of the plan element emitting the event
	 * @param SuccessfulNodes the number of nodes which completed execution successfully 
	 * @param TotalNodes the total number of nodes in the execution
	 * @param ReportsNodeStatus should be true to enable node status retrieval
	 * @param Message the message
	 */
	public ExecutionProgressReportStateEvent(String ID,int SuccessfulNodes, int TotalNodes,String Message, boolean ReportsNodeStatus)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=false;
		this.ReportsNodeProgress=false;
		this.ReportsNodeStatus=ReportsNodeStatus;
		this.CurrentStep=SuccessfulNodes;
		this.TotalSteps=TotalNodes;
		this.Message=Message;
		this.ID=ID;
	}
	
	/**
	 * Instantiates a new execution progress report state event.
	 * 
	 * @param ID the iD of the plan element emitting the event
	 * @param CurrentStep the current step the current step of the execution 
	 * @param TotalSteps the total steps the total steps of the execution
	 * @param NodeName
	 * @param NodeHostName
	 * @param NodePort
	 * @param Message the message
	 */
	public ExecutionProgressReportStateEvent(String ID,int CurrentStep, int TotalSteps, String Message, String NodeName, String NodeHostName, int NodePort)
	{
		this.EmitTimestamp=Calendar.getInstance().getTimeInMillis();
		this.ReportsProgress=true;
		this.ReportsNodeProgress=true;
		this.ReportsNodeStatus=false;
		this.CurrentStep=CurrentStep;
		this.TotalSteps=TotalSteps;
		this.Message=Message;
		this.NodeName=NodeName;
		this.NodeHostName=NodeHostName;
		this.NodePort=NodePort;
		this.ID=ID;
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
	 * Does report node progress.
	 * 
	 * @return true, if successful
	 */
	public boolean DoesReportNodeProgress()
	{
		return this.ReportsNodeProgress;
	}
	
	/**
	 * Does report node status.
	 * 
	 * @return true, if successful
	 */
	public boolean DoesReportNodeStatus()
	{
		return this.ReportsNodeStatus;
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
	 * Gets the node name.
	 * 
	 * @return the string
	 */
	public String GetNodeName()
	{
		return this.NodeName;
	}
	
	/**
	 * Gets the node host name.
	 * 
	 * @return the string
	 */
	public String GetNodeHostName()
	{
		return this.NodeHostName;
	}
	
	/**
	 * Gets the node port.
	 * 
	 * @return the string
	 */
	public int GetNodePort()
	{
		return this.NodePort;
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

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.event.ExecutionStateEvent#GetEventName()
	 */
	@Override
	public EventName GetEventName()
	{
		return ExecutionStateEvent.EventName.ExecutionProgress;
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
			this.ReportsNodeProgress=din.readBoolean();
			this.ReportsNodeStatus=din.readBoolean();
			this.CurrentStep=din.readInt();
			this.TotalSteps=din.readInt();
			this.ID=din.readUTF();
			this.Message=din.readUTF();
			this.NodeName=din.readUTF();
			this.NodeHostName=din.readUTF();
			this.NodePort=din.readInt();
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
			dout.writeBoolean(ReportsNodeProgress);
			dout.writeBoolean(ReportsNodeStatus);
			dout.writeInt(CurrentStep);
			dout.writeInt(TotalSteps);
			dout.writeUTF((ID==null ? "" : ID));
			dout.writeUTF((Message==null ? "" : Message));
			dout.writeUTF((NodeName==null ? "" : NodeName));
			dout.writeUTF((NodeHostName==null ? "" : NodeHostName));
			dout.writeInt(NodePort);
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
