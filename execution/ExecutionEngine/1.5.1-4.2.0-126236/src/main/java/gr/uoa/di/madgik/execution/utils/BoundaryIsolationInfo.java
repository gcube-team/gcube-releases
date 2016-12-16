package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.ss.StorageSystem;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BoundaryIsolationInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	public boolean Isolate=false;
	public boolean CleanUp=false;
	public IInputOutputParameter BaseDir=null;
	private File BaseDirFile=null;
	
	public boolean IsIsolationInitialized(ExecutionHandle Handle)
	{
		if(!this.Isolate) return false;
		for(String s : this.BaseDir.GetNeededVariableNames())
		{
			if(!Handle.GetPlan().Variables.Contains(s)) return false;
			NamedDataType ndt= Handle.GetPlan().Variables.Get(s);
			if(ndt==null) return false;
			if(!ndt.IsAvailable) return false;
		}
		return true;
	}
	
	public void InitializeIsolation(ExecutionHandle Handle,EnvHintCollection Hints) throws ExecutionValidationException, ExecutionRunTimeException
	{
		if(!this.Isolate) return;
		if(this.IsIsolationInitialized(Handle))
		{
			if(this.BaseDirFile==null)
			{
				String IsolationBaseDir= DataTypeUtils.GetValueAsString(this.BaseDir.GetParameterValue(Handle));
				if(IsolationBaseDir==null || IsolationBaseDir.trim().length()==0) throw new ExecutionRunTimeException("No isolation directory has been set");
				File localTMP=null;
				try
				{
					localTMP=StorageSystem.GetLocalFSBufferLocation(Hints);
				}catch(Exception ex)
				{
					 throw new ExecutionRunTimeException("Could not retrieve base dir of local filesystm buffer"+IsolationBaseDir,ex);
				}
				this.BaseDirFile=new File(localTMP, IsolationBaseDir);
			}
		}
		this.BaseDir.SetParameterValue(Handle, UUID.randomUUID().toString());
		String IsolationBaseDir= DataTypeUtils.GetValueAsString(this.BaseDir.GetParameterValue(Handle));
		if(IsolationBaseDir==null || IsolationBaseDir.trim().length()==0) throw new ExecutionRunTimeException("No isolation directory has been set");
		File localTMP=null;
		try
		{
			localTMP=StorageSystem.GetLocalFSBufferLocation(Hints);
		}catch(Exception ex)
		{
			 throw new ExecutionRunTimeException("Could not retrieve base dir of local filesystm buffer"+IsolationBaseDir,ex);
		}
		File bd=new File(localTMP, IsolationBaseDir);
		if(!bd.exists())
		{
			if(!bd.mkdirs()) throw new ExecutionRunTimeException("Could not create isolation directory "+IsolationBaseDir);
		}
		if(!bd.isDirectory()) throw new ExecutionRunTimeException("Isolation directory exists and is not a directory "+IsolationBaseDir);
		this.BaseDirFile=new File(localTMP, IsolationBaseDir);
	}
	
	public File GetBaseDirFile()
	{
		return this.BaseDirFile;
	}
	
	public void FinalizeIsolation(ExecutionHandle Handle,EnvHintCollection Hints) throws ExecutionValidationException, ExecutionRunTimeException
	{
		if(!this.Isolate) return;
		if(!this.IsIsolationInitialized(Handle)) return;
		if(!this.CleanUp) return;
		String IsolationBaseDir= DataTypeUtils.GetValueAsString(this.BaseDir.GetParameterValue(Handle));
		if(IsolationBaseDir==null || IsolationBaseDir.trim().length()==0) throw new ExecutionRunTimeException("No isolation directory has been set");
		File localTMP=null;
		try
		{
			localTMP=StorageSystem.GetLocalFSBufferLocation(Hints);
		}catch(Exception ex)
		{
			 throw new ExecutionRunTimeException("Could not retrieve base dir of local filesystm buffer"+IsolationBaseDir,ex);
		}
		File bd=new File(localTMP,IsolationBaseDir);
		if(bd.exists()) FileUtils.CleanUp(bd);
	}
	
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			this.Isolate = DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute((Element) XML, "isolate"));
			this.CleanUp = DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute((Element) XML, "cleanUp"));
			if(this.Isolate)
			{
				Element baseDirElement=XMLUtils.GetChildElementWithName(XML, "baseDir");
				if(baseDirElement==null) throw new ExecutionSerializationException("Provided serialization not valid");
				Element parelem=XMLUtils.GetChildElementWithName(baseDirElement, "param");
				if(parelem==null) throw new ExecutionSerializationException("Provided serialization not valid");
				this.BaseDir=(IInputOutputParameter)ParameterUtils.GetParameter(parelem);
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<isolation cleanUp=\""+this.CleanUp+"\" isolate=\""+this.Isolate+"\">");
		if(this.Isolate)
		{
			buf.append("<baseDir>");
			buf.append(this.BaseDir.ToXML());
			buf.append("</baseDir>");
		}
		buf.append("</isolation>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.Isolate)
		{
			if(this.BaseDir==null) throw new ExecutionValidationException("base directory for isolation not set");
			this.BaseDir.Validate();
		}
	}

	public Set<String> GetNeededVariableNames()
	{
		if(this.Isolate)
		{
			return this.BaseDir.GetNeededVariableNames();
		}
		else
		{
			return new HashSet<String>();
		}
	}

	public Set<String> GetModifiedVariableNames()
	{
		if(this.Isolate)
		{
			return this.BaseDir.GetModifiedVariableNames();
		}
		else
		{
			return new HashSet<String>();
		}
	}
}
