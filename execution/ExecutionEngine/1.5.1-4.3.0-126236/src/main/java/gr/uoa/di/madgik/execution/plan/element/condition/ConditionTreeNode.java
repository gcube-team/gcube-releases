package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.ConditionUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConditionTreeNode implements IConditionTreeElement
{
	public enum PostVerb
	{
		AsIs,
		Negate
	}
	
	public enum NodeVerb
	{
		AND,
		OR
	}
	
	private static Logger logger=LoggerFactory.getLogger(ConditionTreeNode.class);
	public NodeVerb Verb=NodeVerb.AND;
	public PostVerb Post=PostVerb.AsIs;
	public List<IConditionTreeElement> Childen=new ArrayList<IConditionTreeElement>();

	public void InitializeCondition()
	{
		for(IConditionTreeElement elem : this.Childen)
		{
			elem.InitializeCondition();
		}
	}
	
	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		switch(this.Verb)
		{
			case AND:
			{
				switch(this.Post)
				{
					case AsIs:
					{
						return this.evaluateAND(Handle,Environment);
					}
					case Negate:
					{
						return !(this.evaluateAND(Handle,Environment));
					}
					default:
					{
						throw new ExecutionRunTimeException("Unrecognized condition");
					}
				}
			}
			case OR:
			{
				switch(this.Post)
				{
					case AsIs:
					{
						return this.evaluateOR(Handle,Environment);
					}
					case Negate:
					{
						return !(this.evaluateOR(Handle,Environment));
					}
					default:
					{
						throw new ExecutionRunTimeException("Unrecognized condition");
					}
				}
			}
			default:
			{
				throw new ExecutionRunTimeException("Unrecognized condition");
			}
		}
	}
	
	private boolean evaluateAND(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		for(IConditionTreeElement elem : this.Childen)
		{
			boolean eval=elem.EvaluateCondition(Handle,Environment);
			logger.debug("AND subelement evaluated to "+eval);
			if(!eval) return false;
		}
		return true;
	}
	
	private boolean evaluateOR(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		for(IConditionTreeElement elem : this.Childen)
		{
			boolean eval=elem.EvaluateCondition(Handle,Environment);
			logger.debug("OR subelement evaluated to "+eval);
			if(eval) return true;
		}
		return false;
	}
	
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided serialization of condition tree node", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			this.Childen.clear();
			if (!XMLUtils.AttributeExists(XML, "type")) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree node");
			if(!IConditionTreeElement.TreeElementType.valueOf(XMLUtils.GetAttribute(XML, "type")).equals(this.GetTreeElementType())) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree node");
			if (!XMLUtils.AttributeExists(XML, "verb")) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree node");
			this.Verb=ConditionTreeNode.NodeVerb.valueOf(XMLUtils.GetAttribute(XML, "verb"));
			if (!XMLUtils.AttributeExists(XML, "post")) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree node");
			this.Post=ConditionTreeNode.PostVerb.valueOf(XMLUtils.GetAttribute(XML, "post"));
			List<Element> elems = XMLUtils.GetChildElementsWithName(XML, "treeElement");
			if(elems==null || elems.size()==0) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree node");
			for(Element elem : elems) this.Childen.add(ConditionUtils.GetConditionTreeElement(elem));
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided serialization of condition tree leaf", ex);
		}
	}
	public TreeElementType GetTreeElementType()
	{
		return IConditionTreeElement.TreeElementType.Node;
	}
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<treeElement type=\"" + this.GetTreeElementType().toString() + "\" verb=\""+this.Verb.toString()+"\" post=\""+this.Post.toString()+"\">");
		for(IConditionTreeElement child : this.Childen)
		{
			buf.append(child.ToXML());
		}
		buf.append("</treeElement>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.Childen.size() == 0) throw new ExecutionValidationException("Condition tree node doesn't have a condition set");
		for(IConditionTreeElement child : this.Childen)
		{
			child.Validate();
		}
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		for(IConditionTreeElement child : this.Childen)
		{
			child.ValidatePreExecution(Handle);
		}
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> inputs=new HashSet<String>();
		for(IConditionTreeElement child : this.Childen)
		{
			inputs.addAll(child.GetNeededVariableNames());
		}
		return inputs;
	}

	public Set<String> GetModifiedVariableNames()
	{
		Set<String> outputs=new HashSet<String>();
		for(IConditionTreeElement child : this.Childen)
		{
			outputs.addAll(child.GetModifiedVariableNames());
		}
		return outputs;
	}
}
