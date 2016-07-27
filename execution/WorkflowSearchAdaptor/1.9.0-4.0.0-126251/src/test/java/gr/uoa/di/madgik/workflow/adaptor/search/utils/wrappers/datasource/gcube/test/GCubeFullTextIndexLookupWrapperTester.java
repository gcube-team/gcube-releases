package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.gcube.test;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.WSRESTPlanElement;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.DataSourceWrapper.Variables;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.FullTextIndexNodeWrapper;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.gcube.GCubeFullTextIndexNodeWrapper;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.observer.WrapperObserver;

import java.util.ArrayList;

public class GCubeFullTextIndexLookupWrapperTester 
{

	public static void test() throws Exception 
	{
		EnvHintCollection hints = new EnvHintCollection();
		hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint("/d4science.research-infrastructures.eu/Ecosystem/EM")));
		GCubeFullTextIndexNodeWrapper wrapper = new GCubeFullTextIndexNodeWrapper("http://node25.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/indexmanagement/fulltextindexlookup/FullTextIndexLookup", hints);
		wrapper.setQuery("select nothing from nowhere where anything orderby whatever having everything");
		wrapper.setResourceKey("123-456-789-abc");
		wrapper.setVariableName(Variables.OutputLocator, "aVariable");
		WSRESTPlanElement element = wrapper.constructPlanElements()[0];
		System.out.println(element.ToXML());
		
		ExecutionPlan plan = new ExecutionPlan();
		
		wrapper.setVariableName(FullTextIndexNodeWrapper.Variables.Query, "newVariableNameForQuery");
		wrapper.addVariablesToPlan(plan);
		plan.Root = element;
		System.out.println(wrapper.getVariableName(Variables.MessageID));
		System.out.println(wrapper.getVariableName(Variables.Query));
		System.out.println(plan.Serialize());
		
		ExecutionHandle handle = ExecutionEngine.Submit(plan);
		WrapperObserver observer = new WrapperObserver(handle);
		handle.RegisterObserver(observer);
		Object synchCompletion = observer.getSynchCompletionObject();
		synchronized(synchCompletion) 
		{
			ExecutionEngine.Execute(handle);
			try { synchCompletion.wait(); } catch(Exception e) { }
		}
		
		System.out.println(plan.Variables.Get(wrapper.getVariableName(FullTextIndexNodeWrapper.Variables.OutputLocator)).Value.GetStringValue());
		
	}
	
	public static void main(String[] args) throws Exception 
	{
		TCPConnectionManager.Init(new TCPConnectionManagerConfig("localhost",new ArrayList<gr.uoa.di.madgik.commons.server.PortRange>(),true));
		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
		
		test();
	}
}
