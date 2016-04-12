package gr.uoa.di.madgik.environment.is.elements.plot.errorhandling;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;

public class InvocablePlotContingencyReactionNone implements IInvocablePlotContingencyReaction
{
	public ReactionType GetReactionType()
	{
		return ReactionType.None;
	}

	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		return "<wfprf:reaction type=\""+this.GetReactionType().toString()+"\"/>";
	}
}
