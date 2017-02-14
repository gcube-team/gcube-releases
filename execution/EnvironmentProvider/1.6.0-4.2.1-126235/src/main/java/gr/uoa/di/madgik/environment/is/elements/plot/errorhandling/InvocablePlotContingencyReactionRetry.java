package gr.uoa.di.madgik.environment.is.elements.plot.errorhandling;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;

public class InvocablePlotContingencyReactionRetry implements IInvocablePlotContingencyReaction
{
	public int NumberOfRetries=0;
	public long RetryInterval=0;

	public ReactionType GetReactionType()
	{
		return ReactionType.Retry;
	}

	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<wfprf:reaction type=\""+this.GetReactionType().toString()+"\" retries=\""+this.NumberOfRetries+"\" interval=\""+this.RetryInterval+"\"/>");
		return buf.toString();
	}
}
