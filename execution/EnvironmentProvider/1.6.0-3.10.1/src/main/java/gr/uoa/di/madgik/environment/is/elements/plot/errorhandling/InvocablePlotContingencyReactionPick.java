package gr.uoa.di.madgik.environment.is.elements.plot.errorhandling;

import java.util.ArrayList;
import java.util.List;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;

public class InvocablePlotContingencyReactionPick implements IInvocablePlotContingencyReaction
{
	public boolean ExhaustLists=false;
	public List<String> DefaultList=new ArrayList<String>();
	public String Query=null;
	
	public ReactionType GetReactionType()
	{
		return ReactionType.Pick;
	}

	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<wfprf:reaction type=\""+this.GetReactionType().toString()+"\" exhaust=\""+this.ExhaustLists+"\">");
		buf.append("<wfprf:defaultList>");
		for(String s : this.DefaultList) buf.append("<wfprf:item>"+s+"</wfprf:item>");
		buf.append("</wfprf:defaultList>");
		if(this.Query!=null)buf.append("<wfprf:query>"+this.Query+"</wfprf:query>");
		buf.append("</wfprf:reaction>");
		return buf.toString();
	}
}
