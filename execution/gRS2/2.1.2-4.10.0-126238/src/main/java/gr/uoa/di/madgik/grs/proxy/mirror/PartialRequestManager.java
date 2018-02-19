package gr.uoa.di.madgik.grs.proxy.mirror;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import java.util.Hashtable;

/**
 * This class acts as a manager that can be used by reader side {@link IMirror} implementations to handle
 * the requests for additional payload issued against {@link Record}s and {@link Field}s that have been transfered
 * partially. 
 * 
 * @author gpapanikos
 *
 */
public class PartialRequestManager
{
	private Hashtable<PartialRequestEntry,PartialRequestEntry> entries=new Hashtable<PartialRequestEntry,PartialRequestEntry>();
	
	/**
	 * This method is used to add one new request to the ones that are pending data to be
	 * made available
	 * 
	 * @param recordIndex the {@link Record} index the request is issued against
	 * @param fieldIndex the {@link Record}'s {@link Field} that should transfer more data
	 * @param override the {@link TransportOverride} directive to use
	 * @param notify the synchronization object used to block and notify the requester
	 * @throws GRS2ProxyMirrorDisposedException The mirroring procedure has already been terminated
	 * @throws GRS2ProxyMirrorInvalidOperationException A request against the specific {@link Record} 
	 * and {@link Field} has already been issued and an new one cannot be maid until the previous one is served
	 */
	public synchronized void block(long recordIndex, int fieldIndex, TransportOverride override, Object notify) throws GRS2ProxyMirrorDisposedException, GRS2ProxyMirrorInvalidOperationException
	{
		if(this.entries==null) throw new GRS2ProxyMirrorDisposedException("Partial request manager is already disposed");
		PartialRequestEntry entry=new PartialRequestEntry(recordIndex, fieldIndex, override, notify);
		if(this.entries.contains(entry)) throw new GRS2ProxyMirrorInvalidOperationException("A request is already set for the record and field");
		this.entries.put(entry,entry);
	}
	
	/**
	 * data has been made available for the provided {@link Record} and {@link Field}. This method
	 * will use the respective synchronization object to notify the requester and remove the 
	 * served request
	 * 
	 * @param recordIndex the {@link Record} index the request is issued against
	 * @param fieldIndex the {@link Record}'s {@link Field} that transfered more data
	 * @throws GRS2ProxyMirrorDisposedException The mirroring procedure has already been terminated
	 * @throws GRS2ProxyMirrorInvalidOperationException No request fore the given {@link Record} and {@link Field} can be found
	 */
	public synchronized void unblock(long recordIndex, int fieldIndex) throws GRS2ProxyMirrorDisposedException, GRS2ProxyMirrorInvalidOperationException
	{
		if(this.entries==null) throw new GRS2ProxyMirrorDisposedException("Partial request manager is already disposed");
		PartialRequestEntry entry=new PartialRequestEntry(recordIndex, fieldIndex, TransportOverride.Defined, null);
		if(!this.entries.contains(entry)) throw new GRS2ProxyMirrorInvalidOperationException("No request has been set for the record and field");
		entry=this.entries.remove(entry);
		if(entry==null) throw new GRS2ProxyMirrorInvalidOperationException("No request has been set for the record and field");
		synchronized(entry.getNotify())
		{
			entry.getNotify().notify();
		}
	}
	
	/**
	 * Checks whether a request for the specific {@link Record} and {@link Field} is already pending
	 * 
	 * @param recordIndex the {@link Record} index
	 * @param fieldIndex the {@link Field} index
	 * @return true if a request exists, false otherwise
	 * @throws GRS2ProxyMirrorDisposedException The mirroring procedure has already been terminated
	 */
	public synchronized boolean requestExists(long recordIndex, int fieldIndex) throws GRS2ProxyMirrorDisposedException
	{
		if(this.entries==null) throw new GRS2ProxyMirrorDisposedException("Partial request manager is already disposed");
		PartialRequestEntry entry=new PartialRequestEntry(recordIndex, fieldIndex, TransportOverride.Defined, null);
		return this.entries.contains(entry);
	}
	
	/**
	 * Retrieves a new copy of the entries that are pending
	 * 
	 * @return a copy of the pending entries
	 */
	public synchronized PartialRequestEntry[] getEntries()
	{
		if(this.entries==null) return new PartialRequestEntry[0];
		PartialRequestEntry[] vals=this.entries.values().toArray(new PartialRequestEntry[0]);
		PartialRequestEntry[] tmp=new PartialRequestEntry[vals.length];
		for(int i=0;i<vals.length;i+=1) tmp[i]=vals[i].copy();
		return tmp;
	}
	
	/**
	 * Disposes all internal resources and notifies all the waiting requesters
	 */
	public synchronized void dispose()
	{
		if(this.entries==null) return;
		for(PartialRequestEntry entry : this.entries.values())
		{
			synchronized(entry.getNotify())
			{
				entry.getNotify().notify();
			}
		}
		this.entries.clear();
		this.entries=null;
	}
}
