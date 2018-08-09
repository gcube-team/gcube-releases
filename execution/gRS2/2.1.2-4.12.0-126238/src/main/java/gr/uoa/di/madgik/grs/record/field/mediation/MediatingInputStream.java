package gr.uoa.di.madgik.grs.record.field.mediation;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordMediationException;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class extends the {@link InputStream} providing a utility by which a client can access the 
 * payload of a {@link Field} without needing to take actions depending on whether the full payload of the 
 * field is locally available or it is transfered using a transport directive of {@link TransportDirective#Partial}.
 * Its function makes sure that additional payload is requested whenever more data is requested. To avoid
 * unnecessary data traffic, additional payload requests are made whenever the client needs to access more
 * data than what is already available. If a client knows beforehand that he is going to need the full payload
 * of the field, an alternative would be to make a request through {@link Field#makeAvailable()} to make sure the full
 * payload is available once the invocation is completed. The amount of data that is transfered and made available
 * on every request is subject to the specific {@link FieldDefinition} and more particularly to the value set using
 * {@link FieldDefinition#setChunkSize(int)}. If the data is already available, then no remote request is needed
 * or performed.
 * 
 * @author gpapanikos
 *
 */
public class MediatingInputStream extends InputStream 
{
	private InputStream in=null;
	private Field field=null;
	
	/**
	 * Creates a new instance and uses {@link Field#getInputStream()} to retrieve the field's input stream
	 * 
	 * @param field the field for which the payload data needs mediation
	 * @throws IOException The field's input stream could not be provided
	 * @throws GRS2RecordMediationException No field provided
	 */
	protected MediatingInputStream(Field field)  throws IOException, GRS2RecordMediationException
	{
		if(field==null) throw new GRS2RecordMediationException("Provided field cannot be null");
		this.field=field;
		this.in=this.field.getInputStream();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported()
	{
		return this.in.markSupported();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public void mark(int readlimit)
	{
		this.in.mark(readlimit);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException
	{
		this.in.close();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException
	{
		return this.in.available();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Before forwarding the request to the underlying {@link InputStream}, it is checked if there is one byte
	 * available in the underlying {@link InputStream} and if there isn't, a request for more data is send
	 * </p>
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException
	{
		try
		{
			this.mediate(1);
		} catch (GRS2Exception e)
		{
			throw this.transformException(e);
		}
		return this.in.read();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Before forwarding the request to the underlying {@link InputStream}, it is checked if there are b.length bytes
	 * available in the underlying {@link InputStream} and if there aren't, a request for more data is send
	 * </p>
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException
	{
		try
		{
			this.mediate(b.length);
		} catch (GRS2Exception e)
		{
			throw this.transformException(e);
		}
		return this.in.read(b);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Before forwarding the request to the underlying {@link InputStream}, it is checked if there are <code>len</code> bytes
	 * available in the underlying {@link InputStream} and if there aren't, a request for more data is send
	 * </p>
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		try
		{
			this.mediate(len);
		} catch (GRS2Exception e)
		{
			throw this.transformException(e);
		}
		return this.in.read(b, off, len);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public void reset() throws IOException
	{
		this.in.reset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException
	{
		return this.in.skip(n);
	}
	
	private void mediate(int needed) throws GRS2RecordDefinitionException, GRS2BufferException, GRS2ProxyMirrorException, IOException
	{
		while(needed > this.in.available() && !this.field.isAvailable())
		{
//			long start=System.currentTimeMillis();
			this.field.makeAvailable(TransportOverride.Defined);
//			System.out.println("mediation call took "+(System.currentTimeMillis()-start));
		}
	}
	
	private IOException transformException(Exception ex)
	{
		StringBuilder buf=new StringBuilder();
		buf.append(ex.getMessage());
		buf.append("\n");
		StringWriter w=new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		buf.append(w.toString());
		return new IOException(buf.toString());
	}
}
