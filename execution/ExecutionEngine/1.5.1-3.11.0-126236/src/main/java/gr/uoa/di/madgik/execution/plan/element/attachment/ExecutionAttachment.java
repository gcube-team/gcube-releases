package gr.uoa.di.madgik.execution.plan.element.attachment;

import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.ss.StorageSystem;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExecutionAttachment
{
	public enum AttachmentLocation
	{
		LocalFile,
		StorageSystem
	}
	
	public AttachmentLocation LocationType=AttachmentLocation.LocalFile;
	public String LocationValue=null;
	public String RestoreLocationValue=null;
	public boolean CleanUpRestored=true;
	public String Permissions=null;
	public File tmpFile=null;
	
	private static Logger logger = LoggerFactory.getLogger(ExecutionAttachment.class);
	
	public void MoveTmpToRestore(ExecutionHandle Handle) throws Exception
	{
		File rest=Handle.GetIsolatedFile(new File(this.RestoreLocationValue));
		if(rest.getParentFile()!=null) rest.getParentFile().mkdirs();
		//logger.info("Moving " + tmpFile.getAbsolutePath() + " to " + rest.getAbsolutePath());
		tmpFile.renameTo(rest);
		rest.deleteOnExit();
		if(Permissions!=null) FileUtils.MakeFilePermissions(rest, this.Permissions);
		tmpFile=null;
	}
	
	public void WriteLocalFromStorage(EnvHintCollection Hints) throws Exception
	{
		if(this.LocationType!=AttachmentLocation.StorageSystem) return;
		File f=StorageSystem.Retrieve(this.LocationValue,Hints);
		f.deleteOnExit();
		this.tmpFile=File.createTempFile(UUID.randomUUID().toString(), ".attachment.tmp");
		this.tmpFile.deleteOnExit();
		//logger.info("Retrieving attachment from storage system: " + f.getAbsolutePath() + " , renaming to " + this.tmpFile.getAbsolutePath());
		f.renameTo(this.tmpFile);
		if(Permissions!=null)
		{
			FileUtils.MakeFilePermissions(this.tmpFile, this.Permissions);
			FileUtils.MakeFilePermissions(f, this.Permissions);
		}
	}
	
	public void WriteLocalFromStream(DataInputStream din) throws Exception
	{
		BufferedOutputStream bout=null;
		try
		{
			this.tmpFile=File.createTempFile(UUID.randomUUID().toString(), ".attachment.tmp");
			this.tmpFile.deleteOnExit();
			//logger.info("Reading attachment from stream and storing to: " +  this.tmpFile.getAbsolutePath());
			bout=new BufferedOutputStream(new FileOutputStream(this.tmpFile));
			while(true)
			{
				int size=din.readInt();
				if(size<0) break;
				byte[] buf=new byte[size];
				din.readFully(buf);
				bout.write(buf);
			//	logger.info("Stored " + size + " bytes");
			}
			bout.flush();
			bout.close();
			if(Permissions!=null) FileUtils.MakeFilePermissions(this.tmpFile, this.Permissions);
		}catch(IOException ex)
		{
			throw ex;
		}
		finally
		{
			bout.close();
		}
	}
	
	public void WriteLocalToStream(DataOutputStream dout) throws IOException
	{
		BufferedInputStream bin=null;
		try
		{
			//logger.info("Writing local to stream, LocationType=" + this.LocationType + " LocationValue=" + this.LocationValue);
			if(this.LocationType!=AttachmentLocation.LocalFile) return;
			bin=new BufferedInputStream(new FileInputStream(new File(this.LocationValue)));
			while(true)
			{
				byte[] buf=new byte[4*1024];
				int read=bin.read(buf);
				if(read<0) { /*logger.info("Read failed");*/ break; }
				dout.writeInt(read);
				dout.write(buf, 0, read);
				//logger.info("Wrote " + read + " bytes");
			}
			dout.writeInt(-1);
		}
		catch(IOException ex)
		{
			throw ex;
		}
		finally
		{
			if(bin!=null) bin.close();
		}
	}
	
	public void Validate() throws ExecutionValidationException
	{
		if(this.LocationValue==null || this.LocationValue.trim().length()==0) throw new ExecutionValidationException("Needed attachement location value not provided");
		if(this.RestoreLocationValue==null || this.RestoreLocationValue.trim().length()==0) throw new ExecutionValidationException("Needed attachement restore location value not provided");
	}
	
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<attachment type=\""+this.LocationType.toString()+"\" cleanup=\""+this.CleanUpRestored+"\">");
		buf.append("<value>"+this.LocationValue+"</value>");
		buf.append("<restore>"+this.RestoreLocationValue+"</restore>");
		if(this.Permissions!=null && this.Permissions.trim().length()!=0) buf.append("<permissions value=\""+this.Permissions+"\"/>");
		buf.append("</attachment>");
		return buf.toString();
	}
	
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "type")) throw new ExecutionSerializationException("Invalid serialization provided");
			this.LocationType=AttachmentLocation.valueOf(XMLUtils.GetAttribute(XML, "type"));
			if(!XMLUtils.AttributeExists(XML, "cleanup")) throw new ExecutionSerializationException("Invalid serialization provided");
			this.CleanUpRestored=Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "cleanup"));
			Element valueElement=XMLUtils.GetChildElementWithName(XML, "value");
			if(valueElement==null) throw new ExecutionSerializationException("Invalid serialization provided");
			this.LocationValue=XMLUtils.GetChildText(valueElement);
			Element restoreElement=XMLUtils.GetChildElementWithName(XML, "restore");
			if(restoreElement==null) throw new ExecutionSerializationException("Invalid serialization provided");
			this.RestoreLocationValue=XMLUtils.GetChildText(restoreElement);
			Element permsElement=XMLUtils.GetChildElementWithName(XML, "permissions");
			if(permsElement!=null)
			{
				if(!XMLUtils.AttributeExists(permsElement, "value"))throw new ExecutionSerializationException("Invalid serialization provided");
				this.Permissions=XMLUtils.GetAttribute(permsElement, "value");
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}
}
