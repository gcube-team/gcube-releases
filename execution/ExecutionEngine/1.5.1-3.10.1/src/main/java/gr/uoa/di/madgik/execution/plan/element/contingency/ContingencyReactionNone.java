package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContingencyReactionNone implements IContingencyReaction
{

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
			if (!XMLUtils.AttributeExists((Element) XML, "type")) throw new ExecutionSerializationException("Invalid serializatiuon provided");
			if (!IContingencyReaction.ReactionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetReactionType())) throw new ExecutionSerializationException("Invalid serializatiuon provided");
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public ReactionType GetReactionType()
	{
		return IContingencyReaction.ReactionType.None;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<reaction type=\"" + this.GetReactionType().toString() + "\"/>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		//Nothing to validate
	}

	public IContingencyReactionHandler GetReactionHandler() throws ExecutionInternalErrorException
	{
		ReactionNoneHandler handler=new ReactionNoneHandler();
		handler.SetReactionToHandle(this);
		return handler;
	}

}
