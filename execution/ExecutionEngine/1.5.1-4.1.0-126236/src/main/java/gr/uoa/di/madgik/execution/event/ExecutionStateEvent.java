package gr.uoa.di.madgik.execution.event;

import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import java.util.Observable;

/**
 * Base class for every object that can be used to be registered by an Observer or send as Event.
 * Event handling must follow well known guidelines. The most important of all as it could have a
 * dramatic effect on the framework usage is the processing of a caught event. When an event is caught
 * the processing to be performed should be kept minimal. 
 * 
 * @author gpapanikos
 */
public abstract class ExecutionStateEvent extends Observable
{
	
	/**
	 * The typed name of event
	 * 
	 * @author gpapanikos
	 */
	public enum EventName
	{
		
		/** The Execution has completed. */
		ExecutionCompleted,
		
		/** The Execution has started. */
		ExecutionStarted,
		
		/** The Execution is paused. */
		ExecutionPause,
		
		/** The Execution should resume. */
		ExecutionResume,
		
		/** The Execution is canceled. */
		ExecutionCancel,
		
		/** Event reporting on the progress of the execution. */
		ExecutionProgress,
		
		/** Event reporting on the progress of the execution of a component external to the engine. */
		ExecutionExternalProgress,
		
		/** Event reporting performance measurements. */
		ExecutionPerformance
	}

	/**
	 * Sets that the event is carrying updated information.
	 */
	public void SetChanged()
	{
		this.setChanged();
	}

	/**
	 * Gets the event name.
	 * 
	 * @return the event name
	 */
	public abstract EventName GetEventName();
	
	/**
	 * Gets the timestamp the event was emitted.
	 * 
	 * @return the long
	 */
	public abstract long GetEmitTimestamp();
	
	/**
	 * Encode.
	 * 
	 * @return the byte array containing the encoded event 
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public abstract byte[] Encode() throws ExecutionSerializationException;
	
	/**
	 * Decode.
	 * 
	 * @param buf the byte array containing the encoded event as serialized by {@link ExecutionStateEvent#Encode()}
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public abstract void Decode(byte []buf) throws ExecutionSerializationException;
	
}
