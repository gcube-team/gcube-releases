package gr.uoa.di.madgik.workflow.adaptor.utils.hadoop;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HadoopVerboseProgressOutputFilter implements IExternalFilter
{	
	public String PlanNodeID=null;
	
	public Set<String> GetInputVariableNames()
	{
		return new HashSet<String>();
	}

	public Set<String> GetStoreOutputVariableName()
	{
		return new HashSet<String>();
	}

	public boolean StoreOutput()
	{
		return false;
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.PlanNodeID==null || this.PlanNodeID.trim().length()==0) throw new ExecutionValidationException("Needed value not set");
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
	}

	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ValidateForOnlineFiltering();
	}

	public boolean SupportsOnLineFiltering()
	{
		return true;
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("Only on line processing is supported");
	}

	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		BufferedReader bin=null;
		BufferedWriter bout=null;
		try
		{
			if(AdditionalValueProviders!=null && AdditionalValueProviders.size()==1)
			{
				String filename=AdditionalValueProviders.iterator().next().Value.GetStringValue();
				if(filename!=null) bout=new BufferedWriter(new FileWriter(new File(filename)));
			}
			if(OnLineFilteredValue instanceof String)
			{
				bin=new BufferedReader(new StringReader((String)OnLineFilteredValue));
			}
			else if (OnLineFilteredValue instanceof InputStream)
			{
				bin=new BufferedReader(new InputStreamReader((InputStream) OnLineFilteredValue));
			}
			else throw new ExecutionRunTimeException("Unsupported input");
			this.FilterStream(bin,bout,Handle);
			bin.close();
			return null;
		}catch(Exception ex)
		{
			throw new ExecutionRunTimeException("Could not complete online processing", ex);
		}
		finally
		{
			try{if(bin!=null) bin.close();}catch(Exception ex){}
			try{if(bout!=null) bout.close();}catch(Exception ex){}
		}
	}
	
	private void FilterStream(BufferedReader reader,BufferedWriter writer, ExecutionHandle Handle) throws IOException
	{
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			Handle.EmitEvent(new ExecutionExternalProgressReportStateEvent(this.PlanNodeID, "hadoop jar submit parsed output", line));
			if(writer!=null)
			{
				writer.write(line);
				writer.write("\n");
			}
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<external type=\""+this.getClass().getName()+"\">");
		buf.append("<planNodeID name=\""+this.PlanNodeID+"\"/>");
		buf.append("</external>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			Element tmp=XMLUtils.GetChildElementWithName(XML, "planNodeID");
			if(!XMLUtils.AttributeExists(tmp, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.PlanNodeID=XMLUtils.GetAttribute(tmp, "name");
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
