package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContingencyReactionRetry implements IContingencyReaction
{
	public int NumberOfRetries = 0;
	public long RetryInterval = 0;

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!XMLUtils.AttributeExists((Element) XML, "type") || !XMLUtils.AttributeExists((Element) XML, "retries") || !XMLUtils.AttributeExists((Element) XML, "interval")) throw new ExecutionSerializationException("Invalid serializatiuon provided");
			if (!IContingencyReaction.ReactionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetReactionType())) throw new ExecutionSerializationException("Invalid serializatiuon provided");
			this.NumberOfRetries = Integer.parseInt(XMLUtils.GetAttribute((Element) XML, "retries"));
			this.RetryInterval = Integer.parseInt(XMLUtils.GetAttribute((Element) XML, "interval"));
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public ReactionType GetReactionType()
	{
		return IContingencyReaction.ReactionType.Retry;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<reaction type=\"" + this.GetReactionType().toString() + "\" retries=\"" + this.NumberOfRetries + "\" interval=\"" + this.RetryInterval + "\" />");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.NumberOfRetries==0) throw new ExecutionValidationException("Consider using ContingencyReactionNone if you want 0 retries");
		if(this.RetryInterval<0) throw new ExecutionValidationException("Retry interval must be non negative");
	}

	public IContingencyReactionHandler GetReactionHandler() throws ExecutionInternalErrorException
	{
		ReactionRetryHandler handler=new ReactionRetryHandler();
		handler.SetReactionToHandle(this);
		return handler;
	}

}
