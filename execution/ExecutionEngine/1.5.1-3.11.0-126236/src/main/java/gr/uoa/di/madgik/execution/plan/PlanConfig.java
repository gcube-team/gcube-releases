package gr.uoa.di.madgik.execution.plan;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class acts as a configuration placeholder for configuration parameters applicable to a single {@link ExecutionPlan} and 
 * its execution behavior.
 * 
 * @author gpapanikos
 */
public class PlanConfig implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The type of connection mode the remote connections should have.
	 */
	public enum ConnectionMode
	{
		
		/** A connection is opened and remains alive until it no longer needed. */
		KeepAlive,
		
		/** A connection is opened everytime it is needed and then it is closed. */
		Callback
	}
	
	/** The constant placeholder for element for which there can be restriction as to the number active 
	 * instances within a single plan. This set is statically initialized to contains the elements of type
	 * {@link IPlanElement.PlanElementType#Boundary}, {@link IPlanElement.PlanElementType#WSSOAP}, 
	 * {@link IPlanElement.PlanElementType#WSREST}, {@link IPlanElement.PlanElementType#Shell}, 
	 * {@link IPlanElement.PlanElementType#POJO} */
	private static final Set<IPlanElement.PlanElementType> SupportedRestrictActionTypes=new HashSet<IPlanElement.PlanElementType>();
	
	/** The default restriction number for the action elements. Default value is no restriction. */
	public static final int DefaultConcurrentActionsPerBoundary=0;
	
	/** The default utilization percentage of each execution node. Default value is maximum. */
	public static final float DefaultUtilization=1.0f;
	
	/** The default number of times a plan can be passed by without execution. Default value 0. */
	public static final int DefaultPassBy=0;
	
	/** The default connection mode. Currently set to {@link PlanConfig.ConnectionMode#KeepAlive} */
	public static final ConnectionMode DefaultModeOfConnection=ConnectionMode.KeepAlive;
	
	/** The default timeout to use when in {@link PlanConfig.ConnectionMode#Callback}. If after the specified period in 
	 * milliseconds no connection is made back, the connection is considered inactive. Currently set to 1 hour. */
	public static final long DefaultConnectionCallbackTimeout=60*60*1000;//1 hour
	
	/** The Concurrent actions per boundary. */
	public int ConcurrentActionsPerBoundary=PlanConfig.DefaultConcurrentActionsPerBoundary;
	
	/** The utilization percentage of each execution node. */
	public float Utiliaztion=PlanConfig.DefaultUtilization;
	
	/** The times a plan can be passed by in a queue. */
	public int PassedBy=PlanConfig.DefaultPassBy;
	
	/** The Restrict action types. */
	public List<IPlanElement.PlanElementType> RestrictActionTypes=new ArrayList<IPlanElement.PlanElementType>();
	
	/** The Mode of connection. */
	public ConnectionMode ModeOfConnection=PlanConfig.DefaultModeOfConnection;
	
	/** The Connection callback timeout. */
	public long ConnectionCallbackTimeout=PlanConfig.DefaultConnectionCallbackTimeout;
	
	/** Whether progress reporting events should be choked. By default this value is set to false */
	public boolean ChokeProgressReporting=false;
	
	/** Whether performance reporting events should be choked. By default this value is set to false */
	public boolean ChokePerformanceReporting=false;
	
	static
	{
		PlanConfig.SupportedRestrictActionTypes.add(IPlanElement.PlanElementType.Boundary);
		PlanConfig.SupportedRestrictActionTypes.add(IPlanElement.PlanElementType.Shell);
		PlanConfig.SupportedRestrictActionTypes.add(IPlanElement.PlanElementType.WSSOAP);
		PlanConfig.SupportedRestrictActionTypes.add(IPlanElement.PlanElementType.WSREST);
		PlanConfig.SupportedRestrictActionTypes.add(IPlanElement.PlanElementType.POJO);
	}
	
	/**
	 * Instantiates a new plan config.
	 */
	public PlanConfig()
	{
	}
	
	/**
	 * Instantiates a new plan config.
	 * 
	 * @param XML the xML serialization as retrieved by the {@link PlanConfig#ToXML()}
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public PlanConfig(String XML) throws ExecutionSerializationException
	{
		this.FromXML(XML);
	}
	
	/**
	 * Serializes the configuration 
	 * 
	 * @return the serialization
	 * 
	 * @throws ExecutionSerializationException a serialization error occurred
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<config>");
		buf.append("<concurrentActionsPerBoundary value=\""+this.ConcurrentActionsPerBoundary+"\"/>");
		buf.append("<utilization value=\""+this.Utiliaztion+"\"/>");
		buf.append("<passedBy value=\""+this.PassedBy+"\"/>");
		buf.append("<restrictTypes>");
		for(IPlanElement.PlanElementType t : this.RestrictActionTypes)
		{
			if(!PlanConfig.SupportedRestrictActionTypes.contains(t)) continue;
			buf.append("<restrict name=\""+t.toString()+"\"/>");
		}
		buf.append("</restrictTypes>");
		buf.append("<connectionMode value=\""+this.ModeOfConnection+"\"/>");
		buf.append("<chokeProgressReporting value=\""+this.ChokeProgressReporting+"\"/>");
		buf.append("<chokePerformanceReporting value=\""+this.ChokePerformanceReporting+"\"/>");
		buf.append("</config>");
		return buf.toString();
	}
	
	/**
	 * Deserializes the provided serialization as created by {@link PlanConfig#ToXML()} and populates this instance. 
	 * 
	 * @param XML the xML serialization
	 * 
	 * @throws ExecutionSerializationException a serialization error occurred
	 */
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
	
	/**
	 * Deserializes the provided serialization as created by {@link PlanConfig#ToXML()} and populates this instance. 
	 * 
	 * @param XML the xML serialization
	 * 
	 * @throws ExecutionSerializationException a serialization error occurred
	 */
	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			Element acts=XMLUtils.GetChildElementWithName(XML, "concurrentActionsPerBoundary");
			if(acts==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			if(!XMLUtils.AttributeExists(acts, "value")) throw new ExecutionSerializationException("Invalid serailzation provided");
			this.ConcurrentActionsPerBoundary=DataTypeUtils.GetValueAsInteger(XMLUtils.GetAttribute(acts, "value"));
			Element util=XMLUtils.GetChildElementWithName(XML, "utilization");
			if(util==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			if(!XMLUtils.AttributeExists(util, "value")) throw new ExecutionSerializationException("Invalid serailzation provided");
			this.Utiliaztion=DataTypeUtils.GetValueAsFloat(XMLUtils.GetAttribute(util, "value"));
			Element passedBy=XMLUtils.GetChildElementWithName(XML, "passedBy");
			if(passedBy==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			if(!XMLUtils.AttributeExists(passedBy, "value")) throw new ExecutionSerializationException("Invalid serailzation provided");
			this.PassedBy=DataTypeUtils.GetValueAsInteger(XMLUtils.GetAttribute(passedBy, "value"));
			Element restrlstTypesElem=XMLUtils.GetChildElementWithName(XML, "restrictTypes");
			if(restrlstTypesElem==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			List<Element> restrelemlst=XMLUtils.GetChildElementsWithName(restrlstTypesElem, "restrict");
			if(restrelemlst==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			this.RestrictActionTypes.clear();
			for(Element restr : restrelemlst)
			{
				if(!XMLUtils.AttributeExists(restr, "name")) throw new ExecutionSerializationException("Invalid serailzation provided");
				IPlanElement.PlanElementType t=IPlanElement.PlanElementType.valueOf(XMLUtils.GetAttribute(restr, "name"));
				if(!PlanConfig.SupportedRestrictActionTypes.contains(t)) continue;
				this.RestrictActionTypes.add(t);
			}
			Element connectionModeElem=XMLUtils.GetChildElementWithName(XML, "connectionMode");
			if(connectionModeElem==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			if(!XMLUtils.AttributeExists(connectionModeElem, "value")) throw new ExecutionSerializationException("Invalid serailzation provided");
			this.ModeOfConnection=ConnectionMode.valueOf(XMLUtils.GetAttribute(connectionModeElem, "value"));
			Element chokeProgressReportingElem=XMLUtils.GetChildElementWithName(XML, "chokeProgressReporting");
			if(chokeProgressReportingElem==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			if(!XMLUtils.AttributeExists(chokeProgressReportingElem, "value")) throw new ExecutionSerializationException("Invalid serailzation provided");
			this.ChokeProgressReporting=Boolean.parseBoolean(XMLUtils.GetAttribute(chokeProgressReportingElem, "value"));
			Element chokePerformanceReportingElem=XMLUtils.GetChildElementWithName(XML, "chokePerformanceReporting");
			if(chokePerformanceReportingElem==null) throw new ExecutionSerializationException("Invalid serailzation provided");
			if(!XMLUtils.AttributeExists(chokePerformanceReportingElem, "value")) throw new ExecutionSerializationException("Invalid serailzation provided");
			this.ChokePerformanceReporting=Boolean.parseBoolean(XMLUtils.GetAttribute(chokePerformanceReportingElem, "value"));
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve configuration elements", ex);
		}
	}
}
