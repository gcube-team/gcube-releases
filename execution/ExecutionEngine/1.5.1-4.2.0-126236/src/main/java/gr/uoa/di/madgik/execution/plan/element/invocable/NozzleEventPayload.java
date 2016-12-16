package gr.uoa.di.madgik.execution.plan.element.invocable;

import gr.uoa.di.madgik.commons.channel.events.ISerializable;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.utils.ExecutionEventUtils;

public class NozzleEventPayload implements ISerializable
{
	public ExecutionStateEvent ExecutionEngineEvent=null;
	
	public NozzleEventPayload(){}
	
	public NozzleEventPayload(ExecutionStateEvent ExecutionEngineEvent)
	{
		this.ExecutionEngineEvent=ExecutionEngineEvent;
	}

	public void Decode(byte[] payload) throws Exception
	{
		this.ExecutionEngineEvent=ExecutionEventUtils.DeserializeEvent(payload);
	}

	public byte[] Encode() throws Exception
	{
		return ExecutionEventUtils.SerializeEvent(ExecutionEngineEvent);
	}

	public String GetSerializableClassName()
	{
		return NozzleEventPayload.class.getName();
	}

}
