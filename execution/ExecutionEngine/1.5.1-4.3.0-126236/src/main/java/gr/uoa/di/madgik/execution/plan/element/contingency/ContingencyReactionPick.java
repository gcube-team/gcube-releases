package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContingencyReactionPick implements IContingencyReaction
{
	public Boolean ExhaustPickList = false;
	public List<String> PickList=new ArrayList<String>();
	public String RetrievePickList=null;

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
			if (!XMLUtils.AttributeExists((Element) XML, "type") || !XMLUtils.AttributeExists((Element) XML, "exchaust")) throw new ExecutionSerializationException("Invalid serializatiuon provided");
			if (!IContingencyReaction.ReactionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetReactionType())) throw new ExecutionSerializationException("Invalid serializatiuon provided");
			this.ExhaustPickList = Boolean.parseBoolean(XMLUtils.GetAttribute((Element) XML, "exchaust"));
			Element retr = XMLUtils.GetChildElementWithName(XML, "retrieve");
			if (retr == null) this.RetrievePickList = null;
			else this.RetrievePickList= XMLUtils.GetChildText(retr);
			List<Element> picklst=XMLUtils.GetChildElementsWithName((Element)XML, "pick");
			this.PickList.clear();
			for(Element p : picklst)
			{
				this.PickList.add(XMLUtils.GetChildText(p));
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public ReactionType GetReactionType()
	{
		return IContingencyReaction.ReactionType.Pick;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<reaction type=\"" + this.GetReactionType().toString() + "\" exchaust=\"" + this.ExhaustPickList + "\">");
		if(this.RetrievePickList!=null) buf.append("<retrieve>"+this.RetrievePickList+"</retrieve>");
		if(this.PickList!=null)
		{
			for(String pick : this.PickList)
			{
				buf.append("<pick>"+pick+"</pick>");
			}
		}
		buf.append("</reaction>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if((this.PickList==null || this.PickList.size()==0) && (this.RetrievePickList==null || this.RetrievePickList.trim().length()==0)) throw new ExecutionValidationException("You cannot have a an empty pick list and also not defined a retieval query");
	}

	public IContingencyReactionHandler GetReactionHandler() throws ExecutionInternalErrorException
	{
		ReactionPickHandler handler=new ReactionPickHandler();
		handler.SetReactionToHandle(this);
		return handler;
	}

}
