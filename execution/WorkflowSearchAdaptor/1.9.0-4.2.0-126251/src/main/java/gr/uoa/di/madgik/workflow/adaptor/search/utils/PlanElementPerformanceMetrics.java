package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import gr.uoa.di.madgik.execution.plan.element.IPlanElement;

public class PlanElementPerformanceMetrics
{
	public IPlanElement.PlanElementType Type;
	public int NumberOfEvents=0;
	public long InitilizationTime=0;
	public long FinilizationTime=0;
	public long TotalTime=0;
	public long ChildrenTotalTime=0;
	public long CallsTotalTime=0;
	public int CallsNumber=0;
	
	public String Printout()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Type of element "+this.Type.toString()+"\n");
		buf.append("\tNumber of events : "+this.NumberOfEvents+"\n");
		if(NumberOfEvents>0)
		{
			buf.append("\tTotal Time for each : "+ (this.TotalTime / this.NumberOfEvents)+"\n");
			buf.append("\t\tInitialization : "+ (this.InitilizationTime / this.NumberOfEvents)+"\n");
			buf.append("\t\tFinilization : "+ (this.FinilizationTime / this.NumberOfEvents)+"\n");
			buf.append("\t\tChildren : "+ (this.ChildrenTotalTime / this.NumberOfEvents)+"\n");
			if(CallsNumber>0)
			{
				buf.append("\t\tAvg Number of SubCalls : "+ (this.CallsNumber / this.NumberOfEvents)+"\n");
				buf.append("\t\tAvg Time of SubCall : "+ (this.CallsTotalTime / (this.CallsNumber / this.NumberOfEvents))+"\n");
			}
		}
		return buf.toString();
	}
}
