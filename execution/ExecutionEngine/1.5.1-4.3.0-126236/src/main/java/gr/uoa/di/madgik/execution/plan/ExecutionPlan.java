package gr.uoa.di.madgik.execution.plan;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentSerializationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.variable.VariableCollection;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import gr.uoa.di.madgik.execution.utils.ValueCollection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class ExecutionPlan.
 * 
 * @author gpapanikos
 */
public class ExecutionPlan implements Serializable
{
	
	private static final long serialVersionUID = 1L;

	/** The Root element of the plan */
	public IPlanElement Root=null;
	
	public EnvHintCollection EnvHints=new EnvHintCollection();
	
	/** The Variables that are shared between all the plan elements */
	public VariableCollection Variables=new VariableCollection();
	
	/** The Configuration for the execution of the plan */
	public PlanConfig Config=new PlanConfig();
	
	/** A list of files that are to be cleaned up after the execution of the plan in the node
	 * that the instance of the sub plan that contains this list is located */
	public List<String> CleanUpLocalFiles=new ArrayList<String>();
	
	public ValueCollection CleanUpSS=new ValueCollection();
	
	public ValueCollection CleanUpSSExclude=new ValueCollection();
	
	/**
	 * Instantiates a new execution plan.
	 */
	public ExecutionPlan(){}

	/**
	 * Locate a plan element that has the provided id
	 * 
	 * @param ID the iD
	 * 
	 * @return the plan element or null if no element found
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.Root == null) return null;
		return this.Root.Locate(ID);
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		if(this.Root==null) return new HashSet<IPlanElement>();
		return this.Root.LocateActionElements();
	}

	/**
	 * Validates the plan element hierarchy
	 * 
	 * @throws ExecutionValidationException A validation error occurred
	 */
	public void Validate() throws ExecutionValidationException
	{
//		if ((!this.Root.GetPlanElementType().equals(IPlanElement.PlanElementType.Sequence)) &&
//				(!this.Root.GetPlanElementType().equals(IPlanElement.PlanElementType.Flow)))
//		{
//			throw new ExecutionValidationException("Root element of execution plan must be one of sequence or flow");
//		}
		this.Root.Validate();
	}

	/**
	 * Serialize the plan
	 * 
	 * @return the xml serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public String Serialize() throws ExecutionSerializationException
	{
		try
		{
			this.Validate();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Serialization is not valid",ex);
		}
		StringBuilder buf = new StringBuilder();
		buf.append("<executionPlan>");
		buf.append(this.Config.ToXML());
		buf.append(this.Variables.ToXML());
		try
		{
			buf.append(this.EnvHints.ToXML());
		}catch(EnvironmentSerializationException ex)
		{
			throw new ExecutionSerializationException("Could not serialize environment variables", ex);
		}
		buf.append("<cleanup>");
		buf.append("<local>");
		for(String f : this.CleanUpLocalFiles) buf.append("<file name=\""+f+"\"/>");
		buf.append("</local>");
		buf.append("<ss>");
		buf.append("<include>");
		buf.append(this.CleanUpSS.ToXML());
		buf.append("</include>");
		buf.append("<exclude>");
		buf.append(this.CleanUpSSExclude.ToXML());
		buf.append("</exclude>");
		buf.append("</ss>");
		buf.append("</cleanup>");
		buf.append("<tree>");
		buf.append(this.Root.ToXML());
		buf.append("</tree>");
		buf.append("</executionPlan>");
		return buf.toString();
	}

	/**
	 * Deserialize the plan
	 * 
	 * @param serialization the serialization as retrieved by {@link ExecutionPlan#Serialize()}
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public void Deserialize(String serialization) throws ExecutionSerializationException
	{
		try
		{
			Document doc = XMLUtils.Deserialize(serialization);
			Node config = XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "config");
			if(config==null) throw new ExecutionSerializationException("Not valid serialization of execution plan");
			this.Config.FromXML(config);
			Node variables = XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "variables");
			if(variables==null) throw new ExecutionSerializationException("Not valid serialization of execution plan");
			this.Variables.FromXML(variables);
			Element envhints = XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "nhints");
			if(envhints==null) throw new ExecutionSerializationException("Not valid serialization of execution plan");
			this.EnvHints.FromXML(envhints);
			Element cleanupElement=XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "cleanup");
			if(cleanupElement==null) throw new ExecutionSerializationException("not valid serialization of element");
			Element cleanupLocalElement=XMLUtils.GetChildElementWithName(cleanupElement, "local");
			if(cleanupLocalElement==null) throw new ExecutionSerializationException("not valid serialization of element");
			List<Element> cleanupFilesElementlst=XMLUtils.GetChildElementsWithName(cleanupLocalElement, "file");
			this.CleanUpLocalFiles.clear();
			for(Element cleanupFileElement : cleanupFilesElementlst)
			{
				if(!XMLUtils.AttributeExists(cleanupFileElement, "value")) throw new ExecutionSerializationException("not valid serialization of element");
				this.CleanUpLocalFiles.add(XMLUtils.GetAttribute(cleanupFileElement, "value"));
			}
			Element cleanupSSElement=XMLUtils.GetChildElementWithName(cleanupElement, "ss");
			if(cleanupSSElement==null) throw new ExecutionSerializationException("not valid serialization of element");
			Element cleanupSSElementInclude=XMLUtils.GetChildElementWithName(cleanupSSElement, "include");
			if(cleanupSSElementInclude==null) throw new ExecutionSerializationException("not valid serialization of element");
			Element cleanupSSElementValColl=XMLUtils.GetChildElementWithName(cleanupSSElementInclude, "valueColl");
			if(cleanupSSElementValColl!=null) this.CleanUpSS.FromXML(cleanupSSElementValColl);
			else this.CleanUpSS=new ValueCollection();
			Element cleanupSSElementExclude=XMLUtils.GetChildElementWithName(cleanupSSElement, "exclude");
			if(cleanupSSElementExclude==null) throw new ExecutionSerializationException("not valid serialization of element");
			Element cleanupSSElementValCollExc=XMLUtils.GetChildElementWithName(cleanupSSElementExclude, "valueColl");
			if(cleanupSSElementValCollExc!=null) this.CleanUpSSExclude.FromXML(cleanupSSElementValCollExc);
			else this.CleanUpSSExclude=new ValueCollection();
			Node rootnode = XMLUtils.GetChildElementWithName(XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "tree"), "planElement");
			this.Root = PlanElementUtils.GetPlanElement((Element)rootnode);
			this.Validate();
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize Plan", ex);
		}
	}
}
