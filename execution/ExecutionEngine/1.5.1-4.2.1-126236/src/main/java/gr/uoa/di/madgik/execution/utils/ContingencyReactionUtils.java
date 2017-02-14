package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionNone;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionPick;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionRetry;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction;
import org.w3c.dom.Element;

public class ContingencyReactionUtils
{
	public static IContingencyReaction GetContingencyReaction(Element elem) throws ExecutionSerializationException
	{
		try
		{
			IContingencyReaction.ReactionType reactiontype=IContingencyReaction.ReactionType.valueOf(XMLUtils.GetAttribute(elem, "type"));
			IContingencyReaction react=null;
			switch(reactiontype)
			{
				case None:
				{
					react=new ContingencyReactionNone();
					break;
				}
				case Pick:
				{
					react=new ContingencyReactionPick();
					break;	
				}
				case Retry:
				{
					react=new ContingencyReactionRetry();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized type of reaction provided");
				}
			}
			react.FromXML(elem);
			return react;
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not create contingency reaction element from provided serialization",ex);
		}
	}
}
