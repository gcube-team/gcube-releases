package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import org.w3c.dom.Node;

public interface IContingencyReaction
{
	public enum ReactionType
	{
		None,
		Retry,
		Pick
	}
	
	public void Validate() throws ExecutionValidationException;
	
	public IContingencyReaction.ReactionType GetReactionType();
	
	public String ToXML() throws ExecutionSerializationException;
	
	public void FromXML(String XML) throws ExecutionSerializationException;
	
	public void FromXML(Node XML) throws ExecutionSerializationException;
	
	public IContingencyReactionHandler GetReactionHandler() throws ExecutionInternalErrorException;
}
