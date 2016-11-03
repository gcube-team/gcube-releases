package gr.uoa.di.madgik.environment.is.elements.plot.errorhandling;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;

public interface IInvocablePlotContingencyReaction
{
	public enum ReactionType
	{
		None,
		Retry,
		Pick
	}
	
	public ReactionType GetReactionType();
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException;
}
