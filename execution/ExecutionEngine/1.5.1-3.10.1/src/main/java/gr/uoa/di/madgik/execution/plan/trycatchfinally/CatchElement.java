package gr.uoa.di.madgik.execution.plan.trycatchfinally;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CatchElement
{
	private static Logger logger=LoggerFactory.getLogger(CatchElement.class);
	public String Error = null;
	public boolean IsFullNameOfError = true;
	public IPlanElement Root = null;
	public boolean Rethrow = false;

	public void Validate() throws ExecutionValidationException
	{
		// The error flow can be left blank to hide an exception
		// The error condition can be left blank as a catch all
	}

	public boolean CanHandleError(Exception ex)
	{
		logger.debug("Checking if can handle error");
		if(this.Error==null || this.Error.trim().length()==0) return true;
		String IncomingSimpleName = ex.getClass().getSimpleName();
		String IncomingFullName = ex.getClass().getName();
		if (ex instanceof ExecutionRunTimeException)
		{
			IncomingSimpleName = ((ExecutionRunTimeException) ex).GetCauseSimpleName();
			IncomingFullName = ((ExecutionRunTimeException) ex).GetCauseFullName();
		}
		logger.debug("Caught error is "+IncomingFullName+"("+IncomingSimpleName+") and handlable error is "+this.Error+" and is full name ("+this.IsFullNameOfError+")");
		if (this.IsFullNameOfError && this.Error.equals(IncomingFullName)) return true;
		else if (!this.IsFullNameOfError && this.Error.equals(IncomingSimpleName)) return true;
		return false;
	}

	public boolean Execute(String ID,ExecutionHandle Handle, ExecutionRunTimeException Cause) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		if (!this.CanHandleError(Cause)) return false;
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID,"Error caught and is being handled"));
		if(this.Root!=null) this.Root.Execute(Handle);
		if (this.Rethrow)
		{
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID,"Handled error is being rethrown"));
			throw Cause;
		}
		return true;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		String errorName="";
		if(this.Error!=null) errorName=" name=\"" + this.Error + "\"";
		buf.append("<catch"+errorName+" isFullName=\"" + Boolean.toString(this.IsFullNameOfError) + "\" rethrow=\"" + this.Rethrow + "\">");
		if (this.Root != null) buf.append(this.Root.ToXML());
		buf.append("</catch>");
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
			throw new ExecutionSerializationException("Could not de3serialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "isFullName") || !XMLUtils.AttributeExists((Element)XML, "rethrow")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(XMLUtils.AttributeExists((Element)XML, "name"))this.Error = XMLUtils.GetAttribute((Element) XML, "name");
			else this.Error=null;
			this.IsFullNameOfError = Boolean.parseBoolean(XMLUtils.GetAttribute((Element) XML, "isFullName"));
			this.Rethrow = Boolean.parseBoolean(XMLUtils.GetAttribute((Element) XML, "rethrow"));
			Element rootnode = XMLUtils.GetChildElementWithName(XML, "planElement");
			if (rootnode == null) this.Root = null;
			else this.Root = PlanElementUtils.GetPlanElement(rootnode);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize catch element", ex);
		}
	}

	public Set<String> GetNeededVariableNames()
	{
		if(this.Root==null) return new HashSet<String>();
		return this.Root.GetNeededVariableNames();
	}

	public Set<String> GetModifiedVariableNames()
	{
		if(this.Root==null) return new HashSet<String>();
		return this.Root.GetModifiedVariableNames();
	}
}
