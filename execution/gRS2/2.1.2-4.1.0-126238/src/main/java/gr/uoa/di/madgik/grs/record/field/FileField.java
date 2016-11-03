package gr.uoa.di.madgik.grs.record.field;

import gr.uoa.di.madgik.commons.utils.ZipUtils;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorProtocolErrorException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Field holding the payload of a File that is only available in the writer's host. In cases of remote access, the field 
 * is used to fully or partially and gradually move its payload to the remote host
 * 
 * @author gpapanikos
 *
 */
public class FileField extends Field
{
	private static final long serialVersionUID = 1L;
	
	private URI payload=null;
	private URI originalPayload=null;
	
	private long marshaledSize=0;
	private boolean marshaledCompleted=false;
	private boolean marshaledFile=false;
	
	private static Logger logger = Logger.getLogger(FileField.class.getName());
	
	
	
//	long originalSize=0;
//	long zipedSize=0;
//	long timeZip=0;

	/**
	 * Creates a new instance
	 */
	public FileField(){}
	
	/**
	 * Creates a new instance
	 * 
	 * @param payload the payload of the field
	 */
	public FileField(File payload)
	{
		this.setPayload(payload);
	}
	
	/**
	 * Sets the payload of the field
	 * 
	 * @param payload
	 */
	public void setPayload(File payload)
	{
		URI uriPayload=null;
		if(payload!=null) uriPayload=payload.toURI();
		this.payload=uriPayload;
		this.originalPayload=uriPayload;
	}
	
	/**
	 * Retrieves the payload of the field
	 * 
	 * @return the field payload
	 * @throws GRS2RecordException 
	 */
	public File getPayload()
	{
		return new File(this.payload);
	}

	/**
	 * Retrieves the original field payload. In the writer's side, this value will point to the same location as the 
	 * {@link FileField#getPayload()}. In the reader side, this value will not necessarily point to a valid location
	 * 
	 * @return the original field payload
	 */
	public File getOriginalPayload()
	{
		if(this.originalPayload==null) return null;
		return new File(this.originalPayload);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#getFieldDefinition()
	 */
	public FileFieldDefinition getFieldDefinition() throws GRS2RecordDefinitionException
	{
		if(!(super.getFieldDefinition() instanceof FileFieldDefinition)) throw new GRS2RecordDefinitionException("Provided field definition is not of required type");
		return (FileFieldDefinition)super.getFieldDefinition();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#isAvailable()
	 */
	@Override
	public boolean isAvailable()
	{
		return (this.marshaledCompleted || !this.isRemoteCopy());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException
	{
		if(this.payload==null) return null;
		return new FileInputStream(new File(this.payload));
	}
	
	private int chunkSize = 512*1024;
	
	public void extendReadObject(java.io.ObjectInputStream in, TransportOverride override)
            throws java.io.IOException, java.lang.ClassNotFoundException{
		
		this.setDefinitionIndex(in.readInt());
		this.setRemoteCopy(in.readBoolean());
		
		BufferedOutputStream bout=null;
		try
		{
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("Marshaling of field is already completed");
			TransportDirective dir = null;//this.resolveTransportDirective();
			dir = (TransportDirective) in.readObject();
			
			if(override==TransportOverride.Override) dir=TransportDirective.Full;
			
			//this.record.markActivity();
			boolean firstMarshal=false;
			if(!this.marshaledFile)
			{
				firstMarshal=true;
				int len=in.readInt();
				
				if(len<0) this.originalPayload=null;
				else
				{
					byte[] pb=new byte[len];
					in.readFully(pb);
					this.originalPayload=new URI(new String(pb, "UTF-8"));
				}
				if(this.originalPayload!=null)
				{
					File tmp=File.createTempFile(UUID.randomUUID().toString(), null);
					tmp.deleteOnExit();
					this.payload=tmp.toURI();
				}
				this.marshaledFile=true;
			}
			
//			System.out.println("In extendReadObject    payload : " + payload);
//			System.out.println("In extendReadObject or payload : " + originalPayload);
			
			boolean isCompressed = in.readBoolean();// this.getFieldDefinition().isCompress();
			
			if(dir==TransportDirective.Full || (dir==TransportDirective.Partial && !firstMarshal))
			{
				bout=new BufferedOutputStream(new FileOutputStream(new File(this.payload),true));

				while (true){
					
					int size=in.readInt();
					
//					System.out.println("In extendReadObject    size    : " + size);
//					System.out.println("In extendReadObject    payload : " + payload);
//					System.out.println("In extendReadObject or payload : " + originalPayload);
					
					if(size<0) break;
					byte[] b=new byte[size];
					in.readFully(b);
					//this.record.markActivity();
					if(isCompressed)
					{
						byte[] bb=ZipUtils.UnzipBytes(b);
						bout.write(bb);
					}
					else
					{
						bout.write(b);
					}

					//this.record.markActivity();
					//bout.flush();
				}
				
				bout.flush();
				bout.close();
				this.marshaledCompleted=in.readBoolean();
				if(dir==TransportDirective.Full && !this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("Partial transfer detected although full was retrieved");
			}
		} catch (Exception e)
		{
			throw new IOException("unable to unmarshal field", e);
		}
		finally
		{
			if(bout!=null)
			{
				try{bout.flush();}catch(Exception ex){}
				try{bout.close();}catch(Exception ex){}
			}
		}
	}
	
	private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, java.lang.ClassNotFoundException{
		
		this.setDefinitionIndex(in.readInt());
		this.setRemoteCopy(in.readBoolean());
		
		in.defaultReadObject();
		BufferedOutputStream bout=null;
		try
		{
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("Marshaling of field is already completed");
			
			
			if(this.payload!=null && 
					this.originalPayload!=null 
					&& this.payload.equals(this.originalPayload)) 
				this.payload=File.createTempFile(UUID.randomUUID().toString(), null).toURI();

			//boolean isCompressed = (Boolean) in.readObject();
			boolean isCompressed = false;;
			
			
			if (this.payload != null) {
				File f = new File(this.payload);
				
				if(f.exists() && f.isDirectory()) throw new GRS2RecordSerializationException("File exists and is a directory");
				else if(f.exists() && !f.delete()) throw new GRS2RecordSerializationException("File exists and could not be deleted");
				bout=new BufferedOutputStream(new FileOutputStream(f));

				while(true)
				{
					int size=in.readInt();
					
//					System.out.println("In readObject size    : " + size);
//					System.out.println("In readObject payload : " + payload);
					if(size<0) break;
					byte[] b=new byte[size];
					in.readFully(b);
					if(isCompressed)
					{
						byte[] bb=ZipUtils.UnzipBytes(b);
						bout.write(bb);
					}
					else
					{
						bout.write(b);
					}
					
				}
				bout.flush();
				bout.close();
				
			}
			
		} catch (Exception e)
		{
			throw new IOException("unable to unmarshal field", e);
		}
	}
	
	public void extendWriteObject(java.io.ObjectOutputStream out, TransportOverride override) throws IOException {
		out.writeInt(this.getDefinitionIndex());
		out.writeBoolean(this.isRemoteCopy());
		
//		System.out.println("extendWriteObject");
		
		long start = System.currentTimeMillis();
		BufferedInputStream bin=null;
		try
		{
			TransportDirective dir = null;
			try {
				dir = this.resolveTransportDirective();
			} catch (Exception e) {
				//logger.log(Level.FINE, "transport directive could not be resolved");
				dir = TransportDirective.Partial;
			}
//			TransportDirective dir = TransportDirective.Partial;
			out.writeObject(dir);
			if(override==TransportOverride.Override) dir=TransportDirective.Full;
			
			
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("More to marshal requested but full payload is already provided");

			this.record.markActivity();
			boolean firstMarshal=false;
			if(!this.marshaledFile)
			{
				firstMarshal=true;
				this.marshaledSize=0;
				this.marshaledFile=true;
				if(this.originalPayload!=null) 
				{
					byte[] pb=this.originalPayload.toString().getBytes(this.getFieldDefinition().getCharset());
					out.writeInt(pb.length);
					out.write(pb);
					this.record.markActivity();
				}
			}
			if(dir==TransportDirective.Inherit) throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
			
			URI payloadFiUri=this.originalPayload;
			if(this.payload!=null)payloadFiUri=this.payload;
//			System.out.println(this.payload);
			bin = new BufferedInputStream(new FileInputStream(new File(payloadFiUri)));
//			bin = new BufferedInputStream(new FileInputStream(new File(this.originalPayload)));
			long toSkip=this.marshaledSize;
			while(toSkip!=0) toSkip-=bin.skip(toSkip);
			
			
			out.writeBoolean(this.getFieldDefinition().isCompress());
			
			int chunkSize = this.chunkSize;
			
			if (dir == TransportDirective.Full){
				byte[] buf=new byte[chunkSize];
				while(true)
				{
					int read=this.forwardBuffer(bin, out, buf);
					if(read<0) break;
					this.marshaledSize+=read;
				}
				
				out.writeInt(-1);
				
				this.marshaledCompleted=true;
				out.writeBoolean(marshaledCompleted);
				//out.flush();
			}
			else if(dir==TransportDirective.Partial && !firstMarshal)
			{
				boolean endOfFile=false;
				
				
				byte[] buf=new byte[chunkSize];
				int read=this.forwardBuffer(bin, out, buf);
				if(read<0) {endOfFile=true;}
				this.marshaledSize+=read;
				
				out.writeInt(-1);

				if(endOfFile) this.marshaledCompleted=true;
				out.writeBoolean(marshaledCompleted);
				
				//if (endOfFile)
				//out.flush();
			}
			
		} catch (Exception e)
		{
			throw new IOException("unable to marshal field", e);
		}
		finally
		{
			if(bin!=null) try{bin.close();}catch(Exception ex) {}
		}
		
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
		out.writeInt(this.getDefinitionIndex());
		out.writeBoolean(this.isRemoteCopy());
		long start = System.currentTimeMillis();
		out.defaultWriteObject();
		BufferedInputStream bin=null;
		File fp = null;
		try
		{
			if(this.marshaledCompleted) 
				throw new GRS2ProxyMirrorProtocolErrorException("More to marshal requested but full payload is already provided");

			URI payloadFiUri=this.originalPayload;
			if(this.payload!=null)
					payloadFiUri=this.payload;
			
			//out.writeObject(this.getFieldDefinition().isCompress());
			
			bin = new BufferedInputStream(new FileInputStream(new File(payloadFiUri)));
			
			byte[] buf=new byte[chunkSize];
			while (true) {
//				System.out.println("writing chunk : " + i);
				int read = bin.read(buf);
//				System.out.println("write chunk : " + new String(buf));
				if (read < 0)
					break;
				
				if (this.getFieldDefinition().isCompress()) {
					byte[] compressed = ZipUtils.ZipBytes(buf, read);
//					System.out.println("write : will write " + compressed.length + " bytes");
					out.writeInt(compressed.length);
					out.write(compressed);
				} else {
//					System.out.println("write : will write " + read + " bytes");
					out.writeInt(read);
					out.write(buf, 0, read);
				}
				//out.flush();
			}
			out.writeInt(-1);
			
			bin.close();
			this.record.markActivity();
		} catch (Exception e)
		{
			throw new IOException("unable to marshal field", e);
		}
		finally
		{
			if(bin!=null) try{bin.close();}catch(Exception ex) {}
		}

    }

	/*private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, java.lang.ClassNotFoundException{
		in.defaultReadObject();
		BufferedOutputStream bout=null;
		File compressed = null;
		File fp = null;
		try
		{
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("Marshaling of field is already completed");
			if (this.originalPayload != null) {
				File tmp = File.createTempFile(UUID.randomUUID().toString(), null);
				tmp.deleteOnExit();
				this.payload = tmp.toURI();
			}
			
			String payload = (String) in.readObject();
			this.originalPayload = new URI(payload);
			
			
			fp = new File(this.payload);
			Boolean isCompressed = in.readBoolean();
			
			if (isCompressed) {
				compressed = File.createTempFile("compressed_in", ".gzip");
				bout = new BufferedOutputStream(new FileOutputStream(compressed));
			} else {
				bout = new BufferedOutputStream(new FileOutputStream(fp));
			}
			
			
			long filelength =in.readLong();
			int totalRead = 0;
			
			int bufSize=128*1024;
			byte[] buf=new byte[bufSize];
			
			while (totalRead < filelength) {
				long remaining = filelength - totalRead;
				int toRead = (remaining < bufSize) ? (int)remaining : bufSize;
				
				int read = in.read(buf, 0, toRead);
				if (read < 0)
					break;
				
				totalRead += read;
				bout.write(buf, 0, read);
				bout.flush();
				
			}
			
//			System.out.println("totalRead : " + totalRead + " , filelength : " + filelength);
			this.marshaledCompleted = true;
			bout.flush();
			bout.close();
			if (isCompressed)
				ZipUtils.UnzipFile(compressed, fp);
		} catch (Exception e)
		{
			throw new IOException("unable to unmarshal field", e);
		}
		finally
		{
			if(bout!=null)
			{
				try{bout.flush();}catch(Exception ex){}
				try{bout.close();}catch(Exception ex){}
			}
		}
	}
	
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
		out.defaultWriteObject();
		BufferedInputStream bin=null;
		File fp = null;
		File compressed = null;
		try
		{
			if(this.marshaledCompleted) 
				throw new GRS2ProxyMirrorProtocolErrorException("More to marshal requested but full payload is already provided");

			this.record.markActivity();
			if (this.originalPayload != null) {
				byte[] pb = this.originalPayload.toString().getBytes(this.getFieldDefinition().getCharset());
				String payload = new String(pb);
				
				out.writeObject(payload);
				this.record.markActivity();
			}
			
			URI payloadFiUri=this.originalPayload;
			if(this.payload!=null)
					payloadFiUri=this.payload;
			
			fp = new File(payloadFiUri);
			
			out.writeBoolean(this.getFieldDefinition().isCompress());
			
			if (this.getFieldDefinition().isCompress()) {
				compressed = File.createTempFile("compressed", ".gzip");
				compressed.deleteOnExit();
				ZipUtils.ZipFile(fp, compressed);
				bin = new BufferedInputStream(new FileInputStream(compressed));
				out.writeLong(compressed.length());
			} else {
				bin = new BufferedInputStream(new FileInputStream(fp));
				out.writeLong(fp.length());
			}
			byte[] buf=new byte[128*1024];
			while(true) {
				
				int read = bin.read(buf);
				if (read < 0)
					break;
				this.record.markActivity();
				out.write(buf, 0 , read);
				out.flush();
			}
			out.flush();
			this.record.markActivity();
		} catch (Exception e)
		{
			throw new IOException("unable to marshal field", e);
		}
		finally
		{
			if(bin!=null) try{bin.close();}catch(Exception ex) {}
			
		}

    }*/
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendSend(java.io.DataOutput, gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride)
	 */
	@Override
	public void extendSend(DataOutput out, TransportOverride override) throws GRS2RecordSerializationException
	{
		BufferedInputStream bin=null;
		try
		{
			TransportDirective dir=this.resolveTransportDirective();
			if(override==TransportOverride.Override) dir=TransportDirective.Full;
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("More to marshal requested but full payload is already provided");

			this.record.markActivity();
			boolean firstMarshal=false;
			if(!this.marshaledFile)
			{
				firstMarshal=true;
				this.marshaledSize=0;
				this.marshaledFile=true;
				firstMarshal=true;
				if(this.originalPayload==null) out.writeInt(-1);
				else
				{
					byte[] pb=this.originalPayload.toString().getBytes(this.getFieldDefinition().getCharset());
					out.writeInt(pb.length);
					out.write(pb);
					this.record.markActivity();
				}
			}
			if(dir==TransportDirective.Inherit) throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
			
			URI payloadFiUri=this.originalPayload;
			if(this.payload!=null)payloadFiUri=this.payload;
//			System.out.println(this.payload);
			bin = new BufferedInputStream(new FileInputStream(new File(payloadFiUri)));
//			bin = new BufferedInputStream(new FileInputStream(new File(this.originalPayload)));
			long toSkip=this.marshaledSize;
			while(toSkip!=0) toSkip-=bin.skip(toSkip);
			int localBufferSize=this.getFieldDefinition().getLocalBuffer();
			int chunkSize=this.getFieldDefinition().getChunkSize();
			
			if(dir==TransportDirective.Full)
			{
				byte[] buf=new byte[localBufferSize];
				while(true)
				{
					int read=this.forwardBuffer(bin, out, buf);
					if(read<0) break;
					this.marshaledSize+=read;
				}
				out.writeInt(-1);
				this.marshaledCompleted=true;
				out.writeBoolean(marshaledCompleted);
//				System.out.println("Original size "+originalSize+" compressed size "+zipedSize+" time "+timeZip);
			}
			else if(dir==TransportDirective.Partial && !firstMarshal)
			{
				int send=0;
				boolean endOfFile=false;
				while(send<chunkSize)
				{
					byte[] buf=new byte[Math.min(chunkSize-send, localBufferSize)];
					int read=this.forwardBuffer(bin, out, buf);
					if(read<0) {endOfFile=true; break;}
					this.marshaledSize+=read;
					send+=read;
				}
				out.writeInt(-1);
				if(endOfFile) this.marshaledCompleted=true;
				out.writeBoolean(marshaledCompleted);
			}
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to marshal field", e);
		}
		finally
		{
			if(bin!=null) try{bin.close();}catch(Exception ex) {}
		}
	}

	private int forwardBuffer(BufferedInputStream bin, DataOutput out, byte[] buf) throws IOException, GRS2RecordDefinitionException, GRS2BufferException
	{
		int read=bin.read(buf);
		if(read<0) return -1;
		else
		{
			this.record.markActivity();
			if(this.getFieldDefinition().isCompress())
			{
//				long start=System.currentTimeMillis();
				byte[] p=ZipUtils.ZipBytes(buf, read);
//				timeZip+=(System.currentTimeMillis()-start);
				
				out.writeInt(p.length);
				out.write(p);
//				originalSize+=read;
//				zipedSize+=p.length;
			}
			else
			{
				out.writeInt(read);
				out.write(buf, 0, read);
			}
			this.record.markActivity();
		}
		return read;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendReceive(java.io.DataInput, gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride)
	 */
	@Override
	public void extendReceive(DataInput in, TransportOverride override) throws GRS2RecordSerializationException
	{
		BufferedOutputStream bout=null;
		try
		{
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("Marshaling of field is already completed");
			TransportDirective dir=this.resolveTransportDirective();
			if(override==TransportOverride.Override) dir=TransportDirective.Full;
			
			this.record.markActivity();
			boolean firstMarshal=false;
			if(!this.marshaledFile)
			{
				firstMarshal=true;
				int len=in.readInt();
				if(len<0) this.originalPayload=null;
				else
				{
					byte[] pb=new byte[len];
					in.readFully(pb);
					this.originalPayload=new URI(new String(pb, this.getFieldDefinition().getCharset()));
				}
				if(this.originalPayload!=null)
				{
					File tmp=File.createTempFile(UUID.randomUUID().toString(), null);
					tmp.deleteOnExit();
					this.payload=tmp.toURI();
				}
				this.marshaledFile=true;
			}
			if(dir==TransportDirective.Full || (dir==TransportDirective.Partial && !firstMarshal))
			{
				bout=new BufferedOutputStream(new FileOutputStream(new File(this.payload),true));
				while(true)
				{
					int len=in.readInt();
					if(len<0) break;
					byte[] bp=new byte[len];
					in.readFully(bp);
					this.record.markActivity();
					if(this.getFieldDefinition().isCompress()) bp=ZipUtils.UnzipBytes(bp);
					bout.write(bp);
					this.record.markActivity();
					this.marshaledSize+=bp.length;
				}
				bout.flush();
				bout.close();
				this.marshaledCompleted=in.readBoolean();
				if(dir==TransportDirective.Full && !this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("Partial transfer detected although full was retrieved");
			}
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to unmarshal field", e);
		}
		finally
		{
			if(bout!=null)
			{
				try{bout.flush();}catch(Exception ex){}
				try{bout.close();}catch(Exception ex){}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendDispose()
	 */
	@Override
	public void extendDispose()
	{
		if(this.payload!=null && 
			this.originalPayload!=null && 
			!this.payload.equals(this.originalPayload)) new File(this.payload).delete();
		try
		{
			if(this.getFieldDefinition().getDeleteOnDispose() && this.payload != null)
				new File(this.payload).delete();
		}
		catch(Exception e) 
		{ 
			logger.log(Level.WARNING, "Unable to delete local file", e);
		}
		this.payload=null;
		this.originalPayload=null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendDeflate(java.io.DataOutput)
	 */
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeUTF(this.getFieldDefinition().getCharset());
			out.writeBoolean(this.getFieldDefinition().isCompress());
			if(this.payload==null) out.writeInt(-1);
			else
			{
				byte[] b=this.payload.toString().getBytes(this.getFieldDefinition().getCharset());
				out.writeInt(b.length);
				out.write(b);
			}
			if(this.originalPayload==null) out.writeInt(-1);
			else
			{
				byte[] b=this.originalPayload.toString().getBytes(this.getFieldDefinition().getCharset());
				out.writeInt(b.length);
				out.write(b);
			}
			out.writeLong(this.marshaledSize);
			out.writeBoolean(this.marshaledCompleted);
			out.writeBoolean(this.marshaledFile);
			if(this.payload!=null)
			{
				BufferedInputStream bin=new BufferedInputStream(this.getMediatingInputStream());
				byte[] b=new byte[this.getFieldDefinition().getLocalBuffer()];
				while(true)
				{
					int read=bin.read(b);
					if(read<0) break;
					if(this.getFieldDefinition().isCompress())
					{
						byte[] bb=ZipUtils.ZipBytes(b,read);
						out.writeInt(bb.length);
						out.write(bb);
					}
					else
					{
						out.writeInt(read);
						out.write(b,0,read);
					}
				}
				out.writeInt(-1);
				bin.close();
			}
		}catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("unable to deflate field", ex);
		}
	}

	@Override
	public void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException {
		BufferedInputStream bin = null;
		try {
			Element elm = null;

			elm = doc.createElement("charset");
			elm.setTextContent(String.valueOf(this.getFieldDefinition().getCharset()));
			element.appendChild(elm);

			elm = doc.createElement("isCompressed");
			elm.setTextContent(String.valueOf(this.getFieldDefinition().isCompress()));
			element.appendChild(elm);

			if (this.payload != null) {
				elm = doc.createElement("payload");
				elm.setTextContent(this.payload.toString());
				element.appendChild(elm);
			}

			if (this.originalPayload != null) {
				elm = doc.createElement("originalPayload");
				elm.setTextContent(this.originalPayload.toString());
				element.appendChild(elm);
			}

			elm = doc.createElement("marshaledSize");
			elm.setTextContent(String.valueOf(this.marshaledSize));
			element.appendChild(elm);

			elm = doc.createElement("marshaledCompleted");
			elm.setTextContent(String.valueOf(this.marshaledCompleted));
			element.appendChild(elm);

			elm = doc.createElement("marshaledFile");
			elm.setTextContent(String.valueOf(this.marshaledFile));
			element.appendChild(elm);

			if (this.payload != null) {
				StringBuffer strBuf = new StringBuffer();

				bin = new BufferedInputStream(this.getMediatingInputStream());
				byte[] b = new byte[this.getFieldDefinition().getLocalBuffer()];

				while (true) {
					int read = bin.read(b);
					if (read < 0)
						break;
					if (this.getFieldDefinition().isCompress()) {
						byte[] bb = ZipUtils.ZipBytes(b, read);
						strBuf.append(DatatypeConverter.printBase64Binary(bb));
					} else {
						byte[] bbb = Arrays.copyOf(b, read);
						strBuf.append(DatatypeConverter.printBase64Binary(bbb));
					}
				}

				elm = doc.createElement("payloadData");
				elm.setTextContent(strBuf.toString());
				element.appendChild(elm);
			}

		} catch (Exception ex) {
			throw new GRS2RecordSerializationException("unable to deflate field", ex);
		} finally {
			try {
				if (bin != null)
					bin.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void extendFromXML(Element element, boolean reset) throws GRS2RecordSerializationException {

		try {
			String charset = element.getElementsByTagName("charset").item(0).getTextContent();
			// this.getFieldDefinition().setCharset(charset);

			Boolean isCompressed = Boolean.parseBoolean(element.getElementsByTagName("isCompressed").item(0)
					.getTextContent());
			// this.getFieldDefinition().setCompress(isCompressed);

			String payload = element.getElementsByTagName("payload").item(0).getTextContent();
			try {
				this.payload = new URI(payload);
			} catch (URISyntaxException e) {
				throw new GRS2RecordSerializationException("unable to get record from xml", e);
			}

			String originalPayload = element.getElementsByTagName("originalPayload").item(0).getTextContent();
			try {
				this.originalPayload = new URI(originalPayload);
			} catch (URISyntaxException e) {
				throw new GRS2RecordSerializationException("unable to get record from xml", e);
			}

			if (this.payload != null && this.originalPayload != null && this.payload.equals(this.originalPayload))
				this.payload = File.createTempFile(UUID.randomUUID().toString(), null).toURI();

			Long marshaledSize = Long.parseLong(element.getElementsByTagName("marshaledSize").item(0).getTextContent());
			this.marshaledSize = marshaledSize;

			Boolean marshaledCompleted = Boolean.parseBoolean(element.getElementsByTagName("marshaledCompleted")
					.item(0).getTextContent());
			this.marshaledCompleted = marshaledCompleted;

			Boolean marshaledFile = Boolean.parseBoolean(element.getElementsByTagName("marshaledFile").item(0)
					.getTextContent());
			this.marshaledFile = marshaledFile;

			if (reset) {
				this.marshaledSize = 0;
				this.marshaledCompleted = false;
				this.marshaledFile = false;
			}

			if (this.payload != null) {
				File f = new File(this.payload);
				if (f.exists() && f.isDirectory())
					throw new GRS2RecordSerializationException("File exists and is a directory");
				else if (f.exists() && !f.delete())
					throw new GRS2RecordSerializationException("File exists and could not be deleted");

				BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(f));

				String payloadData = element.getElementsByTagName("payloadData").item(0).getTextContent();

				byte[] decoded = DatatypeConverter.parseBase64Binary(payloadData);

				if (isCompressed) {
					byte[] bb = ZipUtils.UnzipBytes(decoded);
					bout.write(bb);
				} else
					bout.write(decoded);

				bout.flush();
				bout.close();
			}
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to get record from xml", e);
		}
		// TODO: close open stream??

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendInflate(java.io.DataInput, boolean)
	 */
	@Override
	public void extendInflate(DataInput in, boolean reset) throws GRS2RecordSerializationException
	{
		try
		{
			String charset=in.readUTF();
			boolean isCompressed=in.readBoolean();
			int len=in.readInt();
			if(len<0) this.payload=null;
			else
			{
				byte[] b=new byte[len];
				in.readFully(b);
				this.payload=new URI(new String(b,charset));
			}
			len=in.readInt();
			if(len<0) this.originalPayload=null;
			else
			{
				byte[] b=new byte[len];
				in.readFully(b);
				this.originalPayload=new URI(new String(b,charset));
			}
			//to allow even in cases of local deflate / inflate to work without the original file
			if(this.payload!=null && 
					this.originalPayload!=null 
					&& this.payload.equals(this.originalPayload)) 
				this.payload=File.createTempFile(UUID.randomUUID().toString(), null).toURI();

			this.marshaledSize=in.readLong();
			this.marshaledCompleted=in.readBoolean();
			this.marshaledFile=in.readBoolean();
			if(reset)
			{
				this.marshaledSize=0;
				this.marshaledCompleted=false;
				this.marshaledFile=false;
			}
			if(this.payload!=null)
			{
				File f=new File(this.payload);
				if(f.exists() && f.isDirectory()) throw new GRS2RecordSerializationException("File exists and is a directory");
				else if(f.exists() && !f.delete()) throw new GRS2RecordSerializationException("File exists and could not be deleted");
				BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(f));
				while(true)
				{
					int size=in.readInt();
					if(size<0) break;
					byte[] b=new byte[size];
					in.readFully(b);
					if(isCompressed)
					{
						byte[] bb=ZipUtils.UnzipBytes(b);
						bout.write(bb);
					}
					else
					{
						bout.write(b);
					}
				}
				bout.flush();
				bout.close();
			}
		}catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("unable to deflate field", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendMakeLocal()
	 */
	@Override
	protected void extendMakeLocal()
	{
		this.originalPayload = this.payload;
		this.marshaledSize=0;
		this.marshaledCompleted=false;
		this.marshaledFile=false;
	}

	@Override
	public void extendReceiveFromXML(Element element, TransportOverride override)
			throws GRS2RecordSerializationException {
		BufferedOutputStream bout = null;
		try {
			if (this.marshaledCompleted)
				throw new GRS2ProxyMirrorProtocolErrorException("Marshaling of field is already completed");
			TransportDirective dir = this.resolveTransportDirective();
			if (override == TransportOverride.Override)
				dir = TransportDirective.Full;

			this.record.markActivity();

			

			boolean firstMarshal = false;
			if (!this.marshaledFile) {
				firstMarshal = true;
				this.originalPayload = new URI(element.getElementsByTagName("originalPayload").item(0).getTextContent());

				if (this.originalPayload != null) {
					File tmp = File.createTempFile(UUID.randomUUID().toString(), null);
					tmp.deleteOnExit();
					this.payload = tmp.toURI();
				}
				this.marshaledFile = true;
			}

			if (dir == TransportDirective.Full || (dir == TransportDirective.Partial && !firstMarshal)) {
				bout = new BufferedOutputStream(new FileOutputStream(new File(this.payload), true));

				String payloadData = element.getElementsByTagName("payload").item(0).getTextContent();

				byte[] decoded = DatatypeConverter.parseBase64Binary(payloadData);

				Boolean isCompressed = Boolean.parseBoolean(element.getElementsByTagName("isCompressed").item(0).getTextContent());
				if (isCompressed) {
					byte[] bb = ZipUtils.UnzipBytes(decoded);
					bout.write(bb);
					this.marshaledSize += bb.length;
				} else {
					bout.write(decoded);
					this.marshaledSize += decoded.length;
				}
				this.record.markActivity();

				bout.flush();
				bout.close();
				this.marshaledCompleted = Boolean.parseBoolean(element.getElementsByTagName("marshaledCompleted")
						.item(0).getTextContent());
				
				if (dir == TransportDirective.Full && !this.marshaledCompleted)
					throw new GRS2ProxyMirrorProtocolErrorException(
							"Partial transfer detected although full was retrieved");
			}
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to get record from xml", e);
		} finally {
			if (bout != null) {
				try {
					bout.flush();
				} catch (Exception ex) {
				}
				try {
					bout.close();
				} catch (Exception ex) {
				}
			}
		}

	}

	@Override
	public void extendSendToXML(Document doc, Element element, TransportOverride override)
			throws GRS2RecordSerializationException {
		BufferedInputStream bin = null;
		try {
			TransportDirective dir = this.resolveTransportDirective();
			if (override == TransportOverride.Override)
				dir = TransportDirective.Full;
		
			if (this.marshaledCompleted)
				throw new GRS2ProxyMirrorProtocolErrorException(
						"More to marshal requested but full payload is already provided");

			this.record.markActivity();

			Element elm = null;

			boolean firstMarshal = false;
			if (!this.marshaledFile) {
				firstMarshal = true;
				this.marshaledSize = 0;
				this.marshaledFile = true;
				firstMarshal = true;
				if (this.originalPayload != null) {
					elm = doc.createElement("originalPayload");
					elm.setTextContent(this.originalPayload.toString());
					element.appendChild(elm);
					this.record.markActivity();
				}
			}

			URI payloadFiUri = this.originalPayload;
			if (this.payload != null)
				payloadFiUri = this.payload;
			bin = new BufferedInputStream(new FileInputStream(new File(payloadFiUri)));
			long toSkip = this.marshaledSize;
			while (toSkip != 0)
				toSkip -= bin.skip(toSkip);
			int localBufferSize = this.getFieldDefinition().getLocalBuffer();
			int chunkSize = this.getFieldDefinition().getChunkSize();

			if (dir == TransportDirective.Full) {
				byte[] buf = new byte[localBufferSize];
				StringBuffer strBuf = new StringBuffer();
				while (true) {

					int read = bin.read(buf);
					if (read < 0)
						break;
					else {
						this.record.markActivity();
						if (this.getFieldDefinition().isCompress()) {
							byte[] bb = ZipUtils.ZipBytes(buf, read);
							strBuf.append(DatatypeConverter.printBase64Binary(bb));
						} else {
							byte[] bbb = Arrays.copyOf(buf, read);
							strBuf.append(DatatypeConverter.printBase64Binary(bbb));
						}

						// if (this.getFieldDefinition().isCompress()) {
						// byte[] p = ZipUtils.ZipBytes(buf, read);
						// strBuf.append(new String(p));
						// } else {
						// strBuf.append(new String(buf));
						// }

					}
				}
				this.record.markActivity();
				this.marshaledSize += strBuf.length();


				elm = doc.createElement("payload");
				elm.setTextContent(strBuf.toString());
				element.appendChild(elm);

				this.marshaledCompleted = true;
				elm = doc.createElement("marshaledCompleted");
				elm.setTextContent(String.valueOf(this.marshaledCompleted));
				element.appendChild(elm);

				elm = doc.createElement("isCompressed");
				elm.setTextContent(String.valueOf(this.getFieldDefinition().isCompress()));
				element.appendChild(elm);

			} else if (dir == TransportDirective.Partial && !firstMarshal) {
				int send = 0;
				boolean endOfFile = false;
				StringBuffer strBuf = new StringBuffer();
				while (send < chunkSize) {
					byte[] buf = new byte[Math.min(chunkSize - send, localBufferSize)];

					int read = bin.read(buf);
					if (read < 0){
						endOfFile = true;
						break;
					}
					else {
						this.record.markActivity();
						if (this.getFieldDefinition().isCompress()) {
							byte[] bb = ZipUtils.ZipBytes(buf, read);
							strBuf.append(DatatypeConverter.printBase64Binary(bb));
						} else {
							byte[] bbb = Arrays.copyOf(buf, read);
							strBuf.append(DatatypeConverter.printBase64Binary(bbb));
						}
					}
					if (read < 0) {
						endOfFile = true;
						break;
					}
					this.marshaledSize += read;
					send += read;
				}

				if (endOfFile) {
					this.marshaledCompleted = true;
				} else 
					this.marshaledCompleted = false;
				
				elm = doc.createElement("marshaledCompleted");
				elm.setTextContent(String.valueOf(this.marshaledCompleted));
				element.appendChild(elm);
				
				elm = doc.createElement("payload");
				elm.setTextContent(strBuf.toString());
				element.appendChild(elm);
				
				elm = doc.createElement("isCompressed");
				elm.setTextContent(String.valueOf(this.getFieldDefinition().isCompress()));
				element.appendChild(elm);

			}
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to marshal field", e);
		} finally {
			if (bin != null)
				try {
					bin.close();
				} catch (Exception ex) {
				}
		}

	}

}
