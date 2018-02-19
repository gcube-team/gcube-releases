package gr.uoa.di.madgik.grs.proxy.mirror;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IReaderProxy;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.Record;

/**
 * This interface defines the base operations that must be available for the management of the mirroring
 * implementation instances. The purpose of the {@link IMirror} implementations is to provide a means of 
 * synchronization between the {@link IBuffer} that is authored by a writer and the respective {@link IBuffer}
 * available to the reader. The way the synchronization is performed and the technologies employed is
 * entirely up to the implementation to be specified and the respective {@link IProxy}, {@link IReaderProxy}
 * and {@link IWriterProxy}.
 * 
 * @author gpapanikos
 *
 */
public interface IMirror
{
	/**
	 * The status of the mirroring procedure
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum MirroringState
	{
		/**
		 * The mirroring operation is on going
		 */
		Open,
		/**
		 * The mirroring operation is closed but still functional
		 */
		Close,
		/**
		 * The mirroring resources have been purged
		 */
		Purged
	}
	
	/**
	 * Dispose the resources employed by the mirroring implementations
	 */
	public void dispose();
	/**
	 * Retrieves the {@link IBuffer} that is managed by the {@link IMirror} implementation. Depending
	 * on the mirror side, this instance is either the one the writer is authoring, or the one the reader
	 * is accessing.
	 * 
	 * @return the {@link IBuffer} that is managed by the {@link IMirror} implementation
	 */
	public IBuffer getBuffer();
	/**
	 * This method is used by the reader side mirror to request payload that belongs to an {@link IBuffer}
	 * item that has been transfered only partially and more data is requested by the reader client
	 * 
	 * @param recordIndex The index of the record whose payload is requested. As described in {@link Record}, this index coincides with the record id.
	 * @param fieldIndex The index of the field belonging to the defined record for which additional data is requested
	 * @param override whether or not the field's {@link TransportDirective} should be overridden as explained in {@link IBuffer}
	 * @param notify A synchronization object that can be used for the requester to block on until the required data is provided
	 * @return An indicative amount of time that the requester can wait before the data is available. This value is only indicative and it
	 * does not imply that after this period the data will be available. For this. the {@link IMirror#pollPartial(long, int)} should be used 
	 * @throws GRS2ProxyMirrorException The status of the {@link IMirror} does not allow this operation to be completed
	 */
	public long requestPartial(long recordIndex, int fieldIndex, TransportOverride override, Object notify) throws GRS2ProxyMirrorException;
	/**
	 * @param recordIndex The record index / id, for which the requester has requested data to be delivered 
	 * @param fieldIndex The field index of the specific record for which data are requested
	 * @return false if the requester needs to wait again until data is returned false otherwise. In case true is returned, the data
	 * have either been retrieved, or no more data is expected to be send, possibly because of a closed connection 
	 * @throws GRS2ProxyMirrorException The status of the {@link IMirror} does not allow this operation to be completed
	 */
	public boolean pollPartial(long recordIndex, int fieldIndex) throws GRS2ProxyMirrorException;
}
