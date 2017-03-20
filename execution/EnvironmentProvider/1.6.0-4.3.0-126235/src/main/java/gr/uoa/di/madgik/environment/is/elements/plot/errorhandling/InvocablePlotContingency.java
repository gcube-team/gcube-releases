package gr.uoa.di.madgik.environment.is.elements.plot.errorhandling;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;

public class InvocablePlotContingency
{
	public InvocablePlotContingencyTrigger Trigger=null;
	public IInvocablePlotContingencyReaction Reaction=null;
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<wfprf:contingency>");
		if(Trigger.ErrorName!=null)
		{
			buf.append("<wfprf:trigger isFullName=\""+this.Trigger.IsFullName+"\">");
			if(Trigger.ErrorName!=null) buf.append(this.Trigger.ErrorName);
			buf.append("</wfprf:trigger>");
		}
		else
		{
			buf.append("<wfprf:trigger/>");
		}
		buf.append(this.Reaction.ToXML());
		buf.append("</wfprf:contingency>");
		return buf.toString();
	}
}
