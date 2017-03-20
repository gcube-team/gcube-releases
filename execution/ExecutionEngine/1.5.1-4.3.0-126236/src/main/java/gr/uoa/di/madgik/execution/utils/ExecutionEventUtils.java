package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.execution.event.ExecutionCancelStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionCompletedStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPauseStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionResumeStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStartedStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ExecutionEventUtils
{
	public static byte[] SerializeEvent(ExecutionStateEvent event) throws ExecutionSerializationException
	{
		try
		{
			ByteArrayOutputStream bout=new ByteArrayOutputStream();
			DataOutputStream dout=new DataOutputStream(bout);
			dout.writeUTF(event.GetEventName().toString());
			byte[] payl=event.Encode();
			dout.writeInt(payl.length);
			dout.write(payl);
			dout.flush();
			dout.close();
			bout.close();
			return bout.toByteArray();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not serialize event",ex);
		}
	}
	
	public static ExecutionStateEvent DeserializeEvent(byte []payload) throws ExecutionSerializationException
	{
		try
		{
			ExecutionStateEvent event=null;
			DataInputStream din=new DataInputStream(new ByteArrayInputStream(payload));
			switch(ExecutionStateEvent.EventName.valueOf(din.readUTF()))
			{
				case ExecutionCancel:
				{
					event=new ExecutionCancelStateEvent();
					break;
				}
				case ExecutionExternalProgress:
				{
					event=new ExecutionExternalProgressReportStateEvent();
					break;
				}
				case ExecutionResume:
				{
					event=new ExecutionResumeStateEvent();
					break;
				}
				case ExecutionCompleted:
				{
					event=new ExecutionCompletedStateEvent();
					break;
				}
				case ExecutionPause:
				{
					event=new ExecutionPauseStateEvent();
					break;
				}
				case ExecutionPerformance:
				{
					event=new ExecutionPerformanceReportStateEvent();
					break;
				}
				case ExecutionProgress:
				{
					event=new ExecutionProgressReportStateEvent();
					break;
				}
				case ExecutionStarted:
				{
					event=new ExecutionStartedStateEvent();
					break;
				}
				default:
				{
					throw new ExecutionInternalErrorException("UInrecognized event type");
				}
			}
			int length=din.readInt();
			byte[] buf=new byte[length];
			din.readFully(buf);
			event.Decode(buf);
			return event;
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not serialize event",ex);
		}
	}
}
