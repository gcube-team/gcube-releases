package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.ContingencyReactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContingencyTrigger
{
	private static Logger logger= LoggerFactory.getLogger(ContingencyTrigger.class);
	public String TriggeringError=null;
	public boolean IsFullNameOfError=true;
	public IContingencyReaction Reaction=null;

	public boolean CanHandleError(Exception ex)
	{
		logger.debug("Checking if can handle error. Trigger defined error is "+this.TriggeringError+" and it is full name ("+this.IsFullNameOfError+")");
		if(this.TriggeringError==null || this.TriggeringError.trim().length()==0)
		{
			logger.debug("Triger is catch all. returning true");
			return true;
		}
		String IncomingSimpleName = ex.getClass().getSimpleName();
		String IncomingFullName = ex.getClass().getName();
		if (ex instanceof ExecutionRunTimeException)
		{
			IncomingSimpleName = ((ExecutionRunTimeException) ex).GetCauseSimpleName();
			IncomingFullName = ((ExecutionRunTimeException) ex).GetCauseFullName();
		}
		if (this.IsFullNameOfError && this.TriggeringError.equals(IncomingFullName)) return true;
		else if (!this.IsFullNameOfError && this.TriggeringError.equals(IncomingSimpleName)) return true;
		return false;
	}

	public void Validate() throws ExecutionValidationException
	{
		//Triggering Error can be null to be used as catch all
		if(this.Reaction==null) throw new ExecutionValidationException("Contingency trigger reaction not set");
	}
	
	public String ToXML() throws ExecutionSerializationException
	{
		String ter="";
		if(this.TriggeringError!=null && this.TriggeringError.trim().length()!=0) ter="error=\""+this.TriggeringError+"\"";
		StringBuilder buf=new StringBuilder();
		buf.append("<contingency "+ter+" isFullName=\""+ Boolean.toString(this.IsFullNameOfError) +"\">");
		buf.append(this.Reaction.ToXML());
		buf.append("</contingency>");
		return buf.toString();
	}
	
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize the provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}
	
	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try{
			if(!XMLUtils.AttributeExists((Element)XML, "isFullName")) throw new ExecutionSerializationException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists((Element)XML, "error")) this.TriggeringError=null;
			else this.TriggeringError=XMLUtils.GetAttribute((Element)XML, "error");
			this.IsFullNameOfError=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "isFullName"));
			Element react=XMLUtils.GetChildElementWithName(XML, "reaction");
			if(react==null) throw new ExecutionSerializationException("Invalid serialization provided");
			this.Reaction=ContingencyReactionUtils.GetContingencyReaction(react);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize the provided xml serialization", ex);
		}
	}
}
